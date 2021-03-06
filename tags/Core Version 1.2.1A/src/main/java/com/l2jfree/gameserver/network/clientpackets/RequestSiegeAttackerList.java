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

import com.l2jfree.gameserver.instancemanager.CastleManager;
import com.l2jfree.gameserver.instancemanager.FortManager;
import com.l2jfree.gameserver.model.entity.Castle;
import com.l2jfree.gameserver.model.entity.Fort;
import com.l2jfree.gameserver.network.serverpackets.FortSiegeAttackerList;
import com.l2jfree.gameserver.network.serverpackets.SiegeAttackerList;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSiegeAttackerList extends L2GameClientPacket
{
    private static final String _C__A2_RequestSiegeAttackerList = "[C] a2 RequestSiegeAttackerList";
    //private final static Log _log = LogFactory.getLog(RequestJoinParty.class.getName());

    private int _castleId;
    
    @Override
    protected void readImpl()
    {
        _castleId = readD();
    }

    @Override
    protected void runImpl()
    {
        if (_castleId < 100)
        {
            Castle castle = CastleManager.getInstance().getCastleById(_castleId);
            if (castle == null) return;
            SiegeAttackerList sal = new SiegeAttackerList(castle);
            sendPacket(sal);
        }
        else
        {
            Fort fort = FortManager.getInstance().getFortById(_castleId);
            if (fort == null) return;
            FortSiegeAttackerList sal = new FortSiegeAttackerList(fort);
            sendPacket(sal);
        }
    }
    
    @Override
    public String getType()
    {
        return _C__A2_RequestSiegeAttackerList;
    }
}
