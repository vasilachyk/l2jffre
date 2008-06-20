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
package com.l2jfree.gameserver.network.clientpackets;

import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

/**
 * @author -Wooden-
 *
 */
public class RequestExOustFromMPCC extends L2GameClientPacket
{
	//private final static Log _log = LogFactory.getLog(RequestExOustFromMPCC.class.getName());
	private static final String _C__D0_0F_REQUESTEXOUSTFROMMPCC = "[C] D0:0F RequestExOustFromMPCC";
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS();
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.clientpackets.ClientBasePacket#runImpl()
	 */
	@Override
	protected void runImpl()
	{
		L2PcInstance target = L2World.getInstance().getPlayer(_name);
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (target != null && target.isInParty() && activeChar.isInParty() && activeChar.getParty().isInCommandChannel() 
				&& target.getParty().isInCommandChannel() 
				&& activeChar.getParty().getCommandChannel().getChannelLeader().equals(activeChar))
		{
			target.getParty().getCommandChannel().removeParty(target.getParty());
			
			SystemMessage sm = SystemMessage.sendString("Your party was dismissed from the CommandChannel.");
			target.getParty().broadcastToPartyMembers(sm);
			
			sm = SystemMessage.sendString(target.getParty().getPartyMembers().get(0).getName() 
					+ "'s party was dismissed from the CommandChannel.");
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
		}
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__D0_0F_REQUESTEXOUSTFROMMPCC;
	}
}
