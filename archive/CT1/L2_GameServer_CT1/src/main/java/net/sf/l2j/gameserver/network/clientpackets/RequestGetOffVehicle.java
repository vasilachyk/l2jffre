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

import net.sf.l2j.gameserver.boat.service.BoatService;

import net.sf.l2j.gameserver.model.actor.instance.L2BoatInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.GetOffVehicle;
import net.sf.l2j.gameserver.registry.IServiceRegistry;
import net.sf.l2j.tools.L2Registry;


/**
 * @author Maktakien
 *
 */
public class RequestGetOffVehicle extends L2GameClientPacket
{
    private int _id, _x, _y, _z;
    
    private static BoatService boatService = (BoatService)L2Registry.getBean(IServiceRegistry.BOAT);

    /**
     * @param buf
     * @param client
    */
    @Override
    protected void readImpl()
    {
        _id  = readD();
        _x  = readD();
        _y  = readD();
        _z  = readD();
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#runImpl()
     */
    @Override
    protected void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        if(activeChar == null)
            return;
        L2BoatInstance boat = boatService.getBoat(_id);
        GetOffVehicle Gon = new GetOffVehicle(activeChar,boat,_x,_y,_z);
        activeChar.broadcastPacket(Gon);
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.BasePacket#getType()
     */
    @Override
    public String getType()
    {
        return "[S] 5d GetOffVehicle";
    }
}
