/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.model.actor.instance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.Inventory;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2PetData;
import net.sf.l2j.gameserver.model.L2PetDataTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Summon;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.PcInventory;
import net.sf.l2j.gameserver.model.PetInventory;
import net.sf.l2j.gameserver.model.actor.stat.PetStat;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.ItemList;
import net.sf.l2j.gameserver.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.serverpackets.PetInventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.PetItemList;
import net.sf.l2j.gameserver.serverpackets.PetStatusShow;
import net.sf.l2j.gameserver.serverpackets.PetStatusUpdate;
import net.sf.l2j.gameserver.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.serverpackets.StopMove;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.templates.L2Item;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;
import net.sf.l2j.gameserver.templates.L2Weapon;

/**
 * 
 * This class ...
 * 
 * @version $Revision: 1.15.2.10.2.16 $ $Date: 2005/04/06 16:13:40 $
 */
public final class L2PetInstance extends L2Summon
{
    protected static Logger _logPet = Logger.getLogger(L2PetInstance.class.getName());
    
    //private byte _pvpFlag;
    private int _curFed;
    private PetInventory _inventory;
    private final int _controlItemId;
    private boolean _respawned;
    private boolean _mountable;
    
    private Future _feedTask;
    private int _feedTime;
    protected boolean _feedMode;
    
    private L2PetData _Data;

    /** The Experience before the last Death Penalty */
	private long _expBeforeDeath = 0; 
    private final int FOOD_ITEM_CONSUME_COUNT = 5;
    
    public final L2PetData getPetData()
    {
        if (_Data == null) 
            _Data = L2PetDataTable.getInstance().getPetData(getTemplate().npcId, getStat().getLevel());
        
        return _Data;
    }
    
    public final void setPetData(L2PetData value) { _Data = value; }
    
    /**
     * Manage Feeding Task.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR>
     * <li>Feed or kill the pet depending on hunger level</li>
     * <li>If pet has food in inventory and feed level drops below 55% then consume food from inventory</li> 
     * <li>Send a broadcastStatusUpdate packet for this L2PetInstance</li><BR><BR>
     *
     */
    
    class FeedTask implements Runnable
    {
        public void run()
        {
            try
            {
                // if pet is attacking
                if (isAttackingNow())
                    // if its not already on battleFeed mode
                    if (!_feedMode)
                        startFeed(true); //switching to battle feed
                else
                    // if its on battleFeed mode
                    if (_feedMode)
                        startFeed(false); // normal feed
                
                if (getCurrentFed() > FOOD_ITEM_CONSUME_COUNT)
                {
                    // eat
                    setCurrentFed(getCurrentFed() - FOOD_ITEM_CONSUME_COUNT);
                }
                else
                {
                    // go back to pet control item, or simply said, unsummon it
                    setCurrentFed(0);
                    stopFeed();
                    unSummon(getOwner());
                    getOwner().sendMessage("Your pet is too hungry to stay summoned.");
                }
                
                int foodId = L2PetDataTable.getFoodItemId(getTemplate().npcId);
                if (foodId == 0) return;

                L2ItemInstance food = null;
                food = getInventory().getItemByItemId(foodId);
                
                if ((food != null) && (getCurrentFed() < (0.55 * getMaxFed())))
                {
					if (destroyItem("Feed", food.getObjectId(), 1, null, false)) 
                    {
						setCurrentFed(getCurrentFed() + (100));
                        if (getOwner() != null)
                        {
                            SystemMessage sm = new SystemMessage(SystemMessage.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY);
                            sm.addItemName(foodId);
                            getOwner().sendPacket(sm);
                        }
                    }
                }
                
                broadcastStatusUpdate();
            }
            catch (Throwable e) 
            {
                if (Config.DEBUG) 
                    _logPet.warning("Pet [#"+getObjectId()+"] a feed task error has occurred: "+e);
            }
        }
    }

    public static L2PetInstance spawnPet(L2NpcTemplate template, L2PcInstance owner, L2ItemInstance control)
    {
         L2PetInstance pet = restore(control, template, owner);
         // add the pet instance to world
         if (pet != null)
             L2World.getInstance().addPet(owner.getObjectId(), pet);
         
         return pet;
    }
    
    public L2PetInstance(int objectId, L2NpcTemplate template, L2PcInstance owner, L2ItemInstance control)
    {
        super(objectId, template, owner);
        super.setStat(new PetStat(new L2PetInstance[] {this}));

        _controlItemId = control.getObjectId();
        
        // Pet's initial level is supposed to be read from DB
        // Pets start at :
        // Wolf : Level 15
        // Hatcling : Level 35
        // Tested and confirmed on official servers
        getStat().setLevel(template.level);

        _inventory = new PetInventory(this);
        
        int npcId = template.npcId;
        _mountable = L2PetDataTable.isMountable(npcId);
    }
    
    public PetStat getStat() { return (PetStat)super.getStat(); }
    
    public double getLevelMod() { return (100.0 - 11 + getLevel()) / 100.0; }
    
    public boolean isRespawned() { return _respawned; }
    
    public int getSummonType() { return 2; }
    
    public void onAction(L2PcInstance player)
    {
        boolean isOwner = player.getObjectId() == getOwner().getObjectId();
        boolean thisIsTarget = player.getTarget() != null && player.getTarget().getObjectId() == this.getObjectId();
        
        if (isOwner && thisIsTarget)
        {
            if(isOwner && player != getOwner())
            {
                // update owner
                updateRefOwner(player);
            }
            player.sendPacket(new PetStatusShow(this));
            player.sendPacket(new ActionFailed());
        }
        else
        {
            if (Config.DEBUG) _logPet.fine("new target selected:"+getObjectId());
            player.setTarget(this);
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
        }
    }
    
    public int getControlItemId() { return _controlItemId; }
    
    public L2ItemInstance getControlItem() 
    {
        return getOwner().getInventory().getItemByObjectId(_controlItemId); 
    }
    
    public int getCurrentFed() { return _curFed; }
    
	public void setCurrentFed(int num) { _curFed = num > getMaxFed() ? getMaxFed() : num; }
    
    //public void setPvpFlag(byte pvpFlag) { _pvpFlag = pvpFlag; }
    
    public void setPkKills(int pkKills) { _pkKills = pkKills; }
    
    /**
     * Returns the pet's currently equipped weapon instance (if any). 
     */
    public L2ItemInstance getActiveWeaponInstance()
    {
        for (L2ItemInstance item : getInventory().getItems())
            if (item.getLocation() == L2ItemInstance.ItemLocation.PET_EQUIP &&
                    item.getItem().getType1() == L2Item.TYPE2_WEAPON)
                return item;
        
        return null;
    }
    
    /**
     * Returns the pet's currently equipped weapon (if any). 
     */
    public L2Weapon getActiveWeaponItem()
    {
        L2ItemInstance weapon = getActiveWeaponInstance();
        
        if (weapon == null)
            return null;
        
        return (L2Weapon)weapon.getItem();
    }
    
    public L2ItemInstance getSecondaryWeaponInstance()
    {
        // temporary? unavailable
        return null;
    }
    
    public L2Weapon getSecondaryWeaponItem()
    {
        // temporary? unavailable
        return null;
    }
    
    public PetInventory getInventory() 
    { 
        return _inventory; 
    }

    /**
     * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
     * @param process : String Identifier of process triggering this action
     * @param objectId : int Item Instance identifier of the item to be destroyed
     * @param count : int Quantity of items to be destroyed
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successfull
     */
    public boolean destroyItem(String process, int objectId, int count, L2Object reference, boolean sendMessage)
    {
        L2ItemInstance item = _inventory.destroyItem(process, objectId, count, getOwner(), reference);
        
        if (item == null)
        {
            if (sendMessage) 
                getOwner().sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_ITEMS));
            
            return false;
        }

        // Send Pet inventory update packet
        PetInventoryUpdate petIU = new PetInventoryUpdate();
        petIU.addItem(item);
        getOwner().sendPacket(petIU);

        if (sendMessage)
        {
            SystemMessage sm = new SystemMessage(SystemMessage.DISSAPEARED_ITEM);
            sm.addNumber(count);
            sm.addItemName(item.getItemId());
            getOwner().sendPacket(sm);
        }
        return true;
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the L2PcInstance.
     * @param process : String Identifier of process triggering this action
     * @param itemId : int Item identifier of the item to be destroyed
     * @param count : int Quantity of items to be destroyed
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successfull
     */
    public boolean destroyItemByItemId(String process, int itemId, int count, L2Object reference, boolean sendMessage)
    {
        L2ItemInstance item = _inventory.destroyItemByItemId(process, itemId, count, getOwner(), reference);
        
        if (item == null)
        {
            if (sendMessage) getOwner().sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_ITEMS));
            return false;
        }

        // Send Pet inventory update packet
        PetInventoryUpdate petIU = new PetInventoryUpdate();
        petIU.addItem(item);
        getOwner().sendPacket(petIU);

        if (sendMessage)
        {
            SystemMessage sm = new SystemMessage(SystemMessage.DISSAPEARED_ITEM);
            sm.addNumber(count);
            sm.addItemName(itemId);
            getOwner().sendPacket(sm);
        }
        
        return true;
    }

    protected void doPickupItem(L2Object object)
    {
        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        StopMove sm = new StopMove(getObjectId(), getX(), getY(), getZ(), getHeading());
        
        if (Config.DEBUG)
            _logPet.fine("Pet pickup pos: "+ object.getX() + " "+object.getY()+ " "+object.getZ() );
        
        broadcastPacket(sm);
        
        if (!(object instanceof L2ItemInstance))
        {
            // dont try to pickup anything that is not an item :)
            _logPet.warning("trying to pickup wrong target."+object);
            getOwner().sendPacket(new ActionFailed());
            return;
       }
       // Herbs
       if ( ((L2ItemInstance)object).getItemId() > 8599 && ((L2ItemInstance)object).getItemId() < 8615 )   
       {
           SystemMessage smsg = new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1);
            smsg.addItemName(((L2ItemInstance)object).getItemId());
            getOwner().sendPacket(smsg);            
            return;
        }
        
        L2ItemInstance target = (L2ItemInstance) object;
        
        synchronized (target)
        {
            if (!target.isVisible())
            {
                getOwner().sendPacket(new ActionFailed());
                return;
            }
            
            if (target.getOwnerId() != 0 && target.getOwnerId() != getOwner().getObjectId() && !getOwner().isInLooterParty(target.getOwnerId()))
            {
                getOwner().sendPacket(new ActionFailed());
                
                if (target.getItemId() == 57)
                {
                    SystemMessage smsg = new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1_ADENA);
                    smsg.addNumber(target.getCount());
                    getOwner().sendPacket(smsg);
                }
                else if (target.getCount() > 1)
                {
                    SystemMessage smsg = new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S2_S1_s);
                    smsg.addItemName(target.getItemId());
                    smsg.addNumber(target.getCount());
                    getOwner().sendPacket(smsg);
                }
                else
                {
                    SystemMessage smsg = new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1);
                    smsg.addItemName(target.getItemId());
                    getOwner().sendPacket(smsg);
                }
                
                return;
            }
            if(target.getItemLootShedule() != null
                   && (target.getOwnerId() == getOwner().getObjectId() || getOwner().isInLooterParty(target.getOwnerId())))
               target.resetOwnerTimer();

            target.pickupMe(this);
        }
        
        getInventory().addItem("Pickup", target, getOwner(), this);
        //FIXME Just send the updates if possible (old way wasn't working though)
        PetItemList iu = new PetItemList(this);
        getOwner().sendPacket(iu);
        
        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        
        if (getFollowStatus())
            followOwner();
    }

    public void deleteMe(L2PcInstance owner)
    {
        super.deleteMe(owner);
        destroyControlItem(owner); //this should also delete the pet from the db
    }
    
    public void reduceCurrentHp(double damage, L2Character attacker)
    {
        // called in combat
        if (!isDead() && attacker != null)
        {
            SystemMessage sm = new SystemMessage(SystemMessage.S1_GAME_PET_S2_DMG);
            if (attacker instanceof L2NpcInstance)
            {
                sm.addNpcName( ((L2NpcInstance)attacker).getTemplate().npcId);
            }
            else
            {
                sm.addString(attacker.getName());
            }
            sm.addNumber((int)damage);
            
            getOwner().sendPacket(sm);
            getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
        }
        if (isPetrified())
        {damage=0;}
        super.reduceCurrentHp(damage, attacker);
    }
    
    public synchronized void doDie(L2Character killer) {
        stopFeed();
        DecayTaskManager.getInstance().addDecayTask(this,1200000);
        super.doDie(killer,true);
        deathPenalty();
    }

    public void doRevive()
    {
        if (_curFed > (getMaxFed() / 10))
            _curFed = getMaxFed() / 10;

        super.doRevive();

        // stopDecay
        DecayTaskManager.getInstance().cancelDecayTask(this);
        startFeed(false);
    }

    public void doRevive(L2Skill skill)
    {
        // Restore the pet's lost experience, 
        // depending on the % return of the skill used (based on its power).
        restoreExp(skill.getPower());
        doRevive();
    }

    /**
     * Transfers item to another inventory
     * @param process : String Identifier of process triggering this action
     * @param itemId : int Item Identifier of the item to be transfered
     * @param count : int Quantity of items to be transfered
     * @param actor : L2PcInstance Player requesting the item transfer
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item or the updated item in inventory
     */
    public L2ItemInstance transferItem(String process, int objectId, int count, Inventory target, L2PcInstance actor, L2Object reference)
    {
        L2ItemInstance oldItem = getInventory().getItemByObjectId(objectId); 
        L2ItemInstance newItem = getInventory().transferItem(process, objectId, count, target, actor, reference);

        if (newItem == null) return null;

        // Send inventory update packet
        PetInventoryUpdate petIU = new PetInventoryUpdate();
        if (oldItem.getCount() > 0 && oldItem != newItem) petIU.addModifiedItem(oldItem);
        else petIU.addRemovedItem(oldItem);
        getOwner().sendPacket(petIU);

        // Send target update packet
        if (target instanceof PcInventory)
        {
            L2PcInstance targetPlayer = ((PcInventory)target).getOwner();
            InventoryUpdate playerUI = new InventoryUpdate();
            if (newItem.getCount() > count) playerUI.addModifiedItem(newItem);
            else playerUI.addNewItem(newItem);
            targetPlayer.sendPacket(playerUI);

            // Update current load as well
            StatusUpdate playerSU = new StatusUpdate(targetPlayer.getObjectId());
            playerSU.addAttribute(StatusUpdate.CUR_LOAD, targetPlayer.getCurrentLoad());
            targetPlayer.sendPacket(playerSU);
        }
        else if (target instanceof PetInventory)
        {
            petIU = new PetInventoryUpdate();
            if (newItem.getCount() > count) petIU.addRemovedItem(newItem);
            else petIU.addNewItem(newItem);
            ((PetInventory)target).getOwner().getOwner().sendPacket(petIU);
        }
        return newItem;
    }

    public void giveAllToOwner()
    {
        try
        {
            Inventory petInventory = getInventory();
            L2ItemInstance[] items = petInventory.getItems();
            for (int i = 0; (i < items.length); i++)
            { 
                L2ItemInstance giveit = items[i];
                if (((giveit.getItem().getWeight() * giveit.getCount())
                        + getOwner().getInventory().getTotalWeight()) 
                        < getOwner().getMaxLoad())
                {
                    // If the owner can carry it give it to them
                    giveItemToOwner(giveit);
                }
                else 
                {
                    // If they can't carry it, chuck it on the floor :)
                    dropItemHere(giveit);
                }
            }
        }
        catch(Exception e)
        {
            _logPet.warning("Give all items error " + e);
        }
    }
    
    public void giveItemToOwner(L2ItemInstance item)
    {
        try
        {
            getInventory().transferItem("PetTransfer", item.getObjectId(), item.getCount(), getOwner().getInventory(), getOwner(), this);   
            PetInventoryUpdate petiu = new PetInventoryUpdate();
            ItemList PlayerUI = new ItemList(getOwner(), false);
            petiu.addRemovedItem(item);
            getOwner().sendPacket(petiu);
            getOwner().sendPacket(PlayerUI);    
        }
        catch (Exception e)
        {
            _logPet.warning("Error while giving item to owner: " + e);
        }
    }
    
    public void broadcastStatusUpdate()
    {
        super.broadcastStatusUpdate();
        if (getOwner() != null)
        {
            getOwner().sendPacket(new PetStatusUpdate(this));
        }
    }
    
    /**
     * Remove the Pet from DB and its associated item from the player inventory
     * @param owner The owner from whose invenory we should delete the item
     */
    public void destroyControlItem(L2PcInstance owner)
    {
        // remove the pet instance from world
        L2World.getInstance().removePet(owner.getObjectId());

        // delete from inventory
        try 
        {
            L2ItemInstance removedItem = owner.getInventory().destroyItem("PetDestroy", getControlItemId(), 1, getOwner(), this);
            
            InventoryUpdate iu = new InventoryUpdate();
            iu.addRemovedItem(removedItem);
            
            owner.sendPacket(iu);
            
            StatusUpdate su = new StatusUpdate(owner.getObjectId());
            su.addAttribute(StatusUpdate.CUR_LOAD, owner.getCurrentLoad());
            owner.sendPacket(su);
            
            owner.broadcastUserInfo();
            
            L2World world = L2World.getInstance();
            world.removeObject(removedItem);
        }
        catch (Exception e){
            _logPet.warning("Error while destroying control item: " + e);
        }
        
        // pet control item no longer exists, delete the pet from the db
        java.sql.Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
            statement.setInt(1, getControlItemId());
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _logPet.warning("could not delete pet:"+e);
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }
    
    public void dropAllItems()
    {
        try
        {
            L2ItemInstance[] items = getInventory().getItems();
            for (int i = 0; (i < items.length); i++)
            { 
                dropItemHere(items[i]);
            }
        }
        catch(Exception e)
        {
            _logPet.warning("Pet Drop Error: " + e);
        }
    }
    
    public void dropItemHere(L2ItemInstance dropit)
    {
        dropit = getInventory().dropItem("Drop", dropit.getObjectId(), dropit.getCount(), getOwner(), this);
        
        if (dropit != null)
        {
            _logPet.finer("Item id to drop: "+dropit.getItemId()+" amount: "+dropit.getCount());
            dropit.dropMe(this, getX(), getY(), getZ()+100);
        }
    }
    
//  public void startAttack(L2Character target)
//  {  
//      if (!knownsObject(target))
//      {
//          target.addKnownObject(this);
//          this.addKnownObject(target);
//      }
//        if (!target.knownsObject(this))
//        {
//            target.addKnownObject(this);
//            this.addKnownObject(target);
//        }
//      
//      if (!isRunning())
//      {
//          setRunning(true);
//          ChangeMoveType move = new ChangeMoveType(this, ChangeMoveType.RUN);
//          broadcastPacket(move);
//      }
//      
//      super.startAttack(target);
//  }
//      
    /** @return Returns the mountable. */
    public boolean isMountable() { return _mountable; }

    private static L2PetInstance restore(L2ItemInstance control, L2NpcTemplate template, L2PcInstance owner)
    {
        java.sql.Connection con = null;
        try
        {
            L2PetInstance pet = new L2PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT item_obj_id, objId, name, level, maxHp, curHp, maxMp, curMp, acc, crit, evasion, mAtk, mDef, mSpd, pAtk, pDef, pSpd, str, con, dex, _int, men, wit, exp, sp, karma, pkkills, maxload, fed, max_fed FROM pets WHERE item_obj_id=?");
            statement.setInt(1, control.getObjectId());
            ResultSet rset = statement.executeQuery();
            if (!rset.next()) return pet;

            pet._respawned = true;
            pet.setName(rset.getString("name"));

            pet.getStat().setLevel(rset.getInt("level"));
            pet.getStat().setExp(rset.getLong("exp"));
            pet.getStat().setSp(rset.getInt("sp"));

            pet.getStatus().setCurrentHp(rset.getDouble("curHp"));
            if(pet.getStatus().getCurrentHp()==0){
                pet.stopFeed();
                pet.getStatus().stopHpMpRegeneration();
                DecayTaskManager.getInstance().addDecayTask(pet,1200000);                
            }
            pet.getStatus().setCurrentMp(rset.getDouble("curMp"));
            pet.getStatus().setCurrentCp(pet.getMaxCp());

            pet.setKarma(rset.getInt("karma"));
            pet.setPkKills(rset.getInt("pkkills"));
            pet.setCurrentFed(rset.getInt("fed"));

            rset.close();
            statement.close();
            return pet;
        } catch (Exception e) {
            _logPet.warning("could not restore pet data: "+ e);
            return null;
        } finally {
            try { con.close(); } catch (Exception e) {}
        }
    }
   
    public void store()
    {
        if (getControlItemId() == 0)
        {
            // this is a summon, not a pet, don't store anything
            return;
        }
        
        String req;
        if (!isRespawned())
            req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,karma,pkkills,fed,item_obj_id) "+
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        else
            req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,karma=?,pkkills=?,fed=? "+
                "WHERE item_obj_id = ?";
        java.sql.Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(req);
            statement.setString(1, getName());
            statement.setInt(2, getStat().getLevel());
            statement.setDouble(3, getStatus().getCurrentHp());
            statement.setDouble(4, getStatus().getCurrentMp());
			statement.setLong(5, getStat().getExp());
            statement.setInt(6, getStat().getSp());
            statement.setInt(7, getKarma());
            statement.setInt(8, getPkKills());
            statement.setInt(9, getCurrentFed());
            statement.setInt(10, getControlItemId());
            statement.executeUpdate();
            statement.close();
            _respawned = true;
        } catch (Exception e) {
            _logPet.warning("could not store pet data: "+e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }
        
        L2ItemInstance itemInst = getControlItem();
        if (itemInst != null && itemInst.getEnchantLevel() != getStat().getLevel())
        {
            itemInst.setEnchantLevel(getStat().getLevel());
            itemInst.updateDatabase();
        }
    }
    
    public synchronized void stopFeed()
    {
        if (_feedTask != null)
        {
            _feedTask.cancel(false);
            _feedTask = null;
            if (Config.DEBUG) _logPet.fine("Pet [#"+getObjectId()+"] feed task stop");
        }
    }
    
    public synchronized void startFeed( boolean battleFeed )
    {
        // stop feeding task if its active
        if (_feedTask != null) { stopFeed(); }
        
        if (_feedTask == null && !isDead())
        {
            if (battleFeed)
            {
                _feedMode = true;
                _feedTime = _Data.getPetFeedBattle();
            }
            else
            {
                _feedMode = false;
                _feedTime = _Data.getPetFeedNormal();
            }
            //  pet feed time must be different than 0. Changing time to bypass divide by 0
            if (_feedTime <= 0) { _feedTime = 1; }
            
            _feedTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new FeedTask(), 60000/_feedTime, 60000/_feedTime);
        }
    }
    
    public synchronized void unSummon (L2PcInstance owner)
    {
        stopFeed();
        stopHpMpRegeneration();
        super.unSummon(owner);
        
        /* if dead - will be restored with hp=0
        if(!isDead())
        */
            L2World.getInstance().removePet(owner.getObjectId());
    }

    /**
     * Restore the specified % of experience this L2PetInstance has lost.<BR><BR>
     */
    public void restoreExp(double restorePercent)
    { 
        if (_expBeforeDeath > 0)
        {   
            // Restore the specified % of lost experience.
			getStat().addExp(Math.round((_expBeforeDeath - getStat().getExp()) * restorePercent / 100));
            _expBeforeDeath = 0;
        }
    }
    

    private void deathPenalty()
    {
        // TODO Need Correct Penalty

        int lvl = getStat().getLevel();
        double percentLost = -0.07 * lvl + 6.5;
        
        // Calculate the Experience loss
        long lostExp = Math.round((getStat().getExpForLevel(lvl+1) - getStat().getExpForLevel(lvl)) * percentLost /100);

        // Get the Experience before applying penalty
        _expBeforeDeath = getStat().getExp();
        
        // Set the new Experience value of the L2PetInstance
        getStat().addExp(-lostExp);
    }

    public void addExpAndSp(long addToExp, int addToSp) 
    {
        getStat().addExpAndSp(Math.round(addToExp * Config.PET_XP_RATE), addToSp);
    }
    public long getExpForThisLevel() { return getStat().getExpForLevel(getLevel()); }
    public long getExpForNextLevel() { return getStat().getExpForLevel(getLevel() + 1); }
    public final int getLevel() { return getStat().getLevel(); }
    public int getMaxFed() { return getStat().getMaxFeed(); }
    public int getAccuracy() { return getStat().getAccuracy(); }
    public int getCriticalHit(L2Character target, L2Skill skill) { return getStat().getCriticalHit(target, skill); }
    public int getEvasionRate(L2Character target) { return getStat().getEvasionRate(target); }
    public int getRunSpeed() { return getStat().getRunSpeed(); }
    public int getPAtkSpd() { return getStat().getPAtkSpd(); }
    public int getMAtkSpd() { return getStat().getMAtkSpd(); }
    public int getMAtk(L2Character target, L2Skill skill) { return getStat().getMAtk(target, skill); }
    public int getMDef(L2Character target, L2Skill skill) { return getStat().getMDef(target, skill); }
    public int getPAtk(L2Character target) { return getStat().getPAtk(target); }
    public int getPDef(L2Character target) { return getStat().getPDef(target); }
    
    public final int getSkillLevel(int skillId)
    {
        if (_Skills == null || _Skills.get(skillId) == null) return -1;
        int lvl = getLevel();
        return lvl > 70 ? 7 + (lvl - 70) / 5 : lvl / 10;
    }
    
    public void updateRefOwner(L2PcInstance owner)
    {
        int oldOwnerId = getOwner().getObjectId();
        
        setOwner(owner);
        L2World.getInstance().removePet(oldOwnerId);
        L2World.getInstance().addPet(oldOwnerId, this);
    }
}

