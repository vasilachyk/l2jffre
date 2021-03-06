/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.handler.itemhandlers;

import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Summon;
import com.l2jfree.gameserver.model.actor.instance.L2BabyPetInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ExAutoSoulShot;
import com.l2jfree.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.templates.item.L2Weapon;
import com.l2jfree.gameserver.util.Broadcast;

/**
 * Beast SoulShot Handler
 * 
 * @author Tempy
 */
public class BeastSoulShot implements IItemHandler
{
	// All the item IDs that this handler knows.
	private static final int[]	ITEM_IDS	=
											{ 6645 };

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (playable == null)
			return;

		L2PcInstance activeOwner = null;
		if (playable instanceof L2Summon)
		{
			activeOwner = ((L2Summon) playable).getOwner();
			activeOwner.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
			return;
		}
		else if (playable instanceof L2PcInstance)
		{
			activeOwner = (L2PcInstance) playable;
		}

		if (activeOwner == null)
			return;
		L2Summon activePet = activeOwner.getPet();

		if (activePet == null)
		{
			activeOwner.sendPacket(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return;
		}

		if (activePet.isDead())
		{
			activeOwner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
			return;
		}

		int itemId = 6645;
		int shotConsumption = 1;
		L2ItemInstance weaponInst = null;
		L2Weapon weaponItem = null;

		if ((activePet instanceof L2PetInstance) && !(activePet instanceof L2BabyPetInstance))
		{
			weaponInst = ((L2PetInstance) activePet).getActiveWeaponInstance();
			weaponItem = ((L2PetInstance) activePet).getActiveWeaponItem();

			if (weaponInst == null)
			{
				activeOwner.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
				return;
			}

			if (weaponInst.getChargedSoulshot() != L2ItemInstance.CHARGED_NONE)
			{
				// SoulShots are already active.
				return;
			}

			int shotCount = item.getCount();
			shotConsumption = weaponItem.getSoulShotCount();

			if (shotConsumption == 0)
			{
				activeOwner.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
				return;
			}

			if (!(shotCount > shotConsumption))
			{
				// Not enough Soulshots to use.
				activeOwner.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
				return;
			}

			weaponInst.setChargedSoulshot(L2ItemInstance.CHARGED_SOULSHOT);
		}
		else
		{
			if (activePet.getChargedSoulShot() != L2ItemInstance.CHARGED_NONE)
				return;

			activePet.setChargedSoulShot(L2ItemInstance.CHARGED_SOULSHOT);
		}

		// If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
		if (!activeOwner.destroyItemWithoutTrace("Consume", item.getObjectId(), shotConsumption, null, false))
		{
			if (activeOwner.getAutoSoulShot().containsKey(itemId))
			{
				activeOwner.removeAutoSoulShot(itemId);
				activeOwner.sendPacket(new ExAutoSoulShot(itemId, 0));

				SystemMessage sm = new SystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED);
				sm.addString(item.getItem().getName());
				activeOwner.sendPacket(sm);
				return;
			}

			activeOwner.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
			return;
		}

		// Pet uses the power of spirit.
		activeOwner.sendPacket(SystemMessageId.PET_USE_THE_POWER_OF_SPIRIT);

		Broadcast.toSelfAndKnownPlayersInRadius(activeOwner, new MagicSkillUse(activePet, activePet, 2033, 1, 0, 0), 360000/*600*/);
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}