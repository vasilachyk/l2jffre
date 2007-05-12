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
package net.sf.l2j.gameserver.clientpackets;

import net.sf.l2j.gameserver.boat.service.BoatService;
import net.sf.l2j.gameserver.model.actor.instance.L2BoatInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.registry.IServiceRegistry;
import net.sf.l2j.gameserver.serverpackets.GetOnVehicle;
import net.sf.l2j.tools.L2Registry;
import net.sf.l2j.tools.geometry.Point3D;

/**
 * This class ...
 * 
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGetOnVehicle extends L2GameClientPacket
{
    private static final String _C__5C_GETONVEHICLE = "[C] 5C GetOnVehicle";

    private int _id, _x, _y, _z;

    private static BoatService boatService = (BoatService)L2Registry.getBean(IServiceRegistry.BOAT);

    /**
     * packet type id 0x4a
     * 
     * sample
     * 
     * 4b
     * d // unknown
     * d // unknown
     * 
     * format:      cdd
     * @param decrypt
     */
    protected void readImpl()
    {
        _id = readD();
        _x = readD();
        _y = readD();
        _z = readD();
    }

    protected void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        if (activeChar == null) return;
        
        L2BoatInstance boat = boatService.getBoat(_id);
        if (boat == null) return;
        
        GetOnVehicle Gon = new GetOnVehicle(activeChar,boat,_x,_y,_z);
        activeChar.setInBoatPosition(new Point3D(_x,_y,_z));
        activeChar.getPosition().setXYZ(boat.getPosition().getX(),boat.getPosition().getY(),boat.getPosition().getZ());
        activeChar.broadcastPacket(Gon);                
     
    
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
     */
    public String getType()
    {
        return _C__5C_GETONVEHICLE;
    }
}
