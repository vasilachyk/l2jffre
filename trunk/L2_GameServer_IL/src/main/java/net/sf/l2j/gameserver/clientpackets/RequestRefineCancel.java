/* This program is free software; you can redistribute it and/or modify
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
package net.sf.l2j.gameserver.clientpackets;

import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.ExVariationCancelResult;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.L2Item;

/**
 * Format(ch) d
 * @author  -Wooden-
 */
public class RequestRefineCancel extends L2GameClientPacket
{
	private static final String _C__D0_2E_REQUESTREFINECANCEL = "[C] D0:2E RequestRefineCancel";
	private int _targetItemObjId;
	
    protected void readImpl()
    {
		_targetItemObjId = readD();
    }

	/**
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#runImpl()
	 */
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		L2ItemInstance targetItem = (L2ItemInstance)L2World.getInstance().findObject(_targetItemObjId);

		// cannot remove augmentation from a not augmented item
		if (!targetItem.isAugmented())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM));
			return;
		}
		
		// get the price
		int price=0;
		switch (targetItem.getItem().getItemGrade())
		{
			//TODO: C: low:95k, med:150k, high:210k
			case L2Item.CRYSTAL_C:
				price = 150000;
				break;
			//TODO: B: low:240k, med:270k
			case L2Item.CRYSTAL_B:
				price = 240000;
				break;
			//TODO: A: low:330k, med:390k, high:420k
			case L2Item.CRYSTAL_A:
				price = 390000;
				break;
			case L2Item.CRYSTAL_S:
				price = 480000;
				break;
			// any other item type is not augmentable
			default:
				return;
		}
		
		// try to reduce the players adena
		if (!activeChar.reduceAdena("RequestRefineCancel", price, null, true)) return;
		
		// unequip item
		if (targetItem.isEquipped()) activeChar.disarmWeapons();
		
		// cancel boni
		targetItem.getAugmentation().removeBoni(activeChar);
		
		// remove the augmentation
		targetItem.removeAugmentation();
		
		// send ExVariationCancelResult
		activeChar.sendPacket(new ExVariationCancelResult());
		
		// send inventory update
		InventoryUpdate iu = new InventoryUpdate();
		iu.addItem(targetItem);
		activeChar.sendPacket(iu);
		
		// send system message
		SystemMessage sm = new SystemMessage(SystemMessage.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
		sm.addString(targetItem.getItemName());
		activeChar.sendPacket(sm);
	}

	/**
	 * @see net.sf.l2j.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__D0_2E_REQUESTREFINECANCEL;
	}

}
