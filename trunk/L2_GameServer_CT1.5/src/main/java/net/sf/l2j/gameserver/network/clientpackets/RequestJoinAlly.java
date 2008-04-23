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
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.AskJoinAlly;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestJoinAlly extends L2GameClientPacket{
	
	private static final String _C__82_REQUESTJOINALLY = "[C] 82 RequestJoinAlly";
	//private static Logger _log = Logger.getLogger(RequestJoinAlly.class.getName());

	private int _id;
	
    @Override
    protected void readImpl()
    {
        _id = readD();
    }

    @Override
    protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
		    return;
		}
		if (!(L2World.getInstance().findObject(_id) instanceof L2PcInstance))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET));
			return;
		}
		if(activeChar.getClan() == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER));
			return;
		}
		L2PcInstance target = (L2PcInstance) L2World.getInstance().findObject(_id);
		L2Clan clan = activeChar.getClan();
		if (!clan.checkAllyJoinCondition(activeChar, target))
		{
			return;
		} 
		if (!activeChar.getRequest().setRequest(target, this))
		{
			return;
		}

		SystemMessage sm = new SystemMessage(SystemMessageId.S2_ALLIANCE_LEADER_OF_S1_REQUESTED_ALLIANCE);
		sm.addString(activeChar.getClan().getAllyName());
		sm.addString(activeChar.getName());
		target.sendPacket(sm);
		AskJoinAlly aja = new AskJoinAlly(activeChar.getObjectId(), activeChar.getClan().getAllyName());
		target.sendPacket(aja);

		sm = new SystemMessage(SystemMessageId.YOU_INVITED_FOR_ALLIANCE);
		activeChar.sendPacket(sm);
		return;
	}
	
	
	@Override
	public String getType()
	{
		return _C__82_REQUESTJOINALLY;
	}
}
