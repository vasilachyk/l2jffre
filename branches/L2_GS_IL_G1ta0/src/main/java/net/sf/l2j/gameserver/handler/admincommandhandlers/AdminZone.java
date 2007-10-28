/*
 * $Header: AdminTest.java, 25/07/2005 17:15:21 luisantonioa Exp $ $Author:
 * luisantonioa $ $Date: 25/07/2005 17:15:21 $ $Revision: 1 $ $Log:
 * AdminTest.java,v $ Revision 1 25/07/2005 17:15:21 luisantonioa Added
 * copyright notice This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA. http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.TownManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2WorldRegion;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.IZone;

public class AdminZone implements IAdminCommandHandler
{
	private static final int REQUIRED_LEVEL = Config.GM_TEST;
	private static final String[] ADMIN_COMMANDS = {
			"admin_zone_check", "admin_zone_reload" };

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#useAdminCommand(java.lang.String, net.sf.l2j.gameserver.model.L2PcInstance)
	 */
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if(activeChar == null)
			return false;

		if(!Config.ALT_PRIVILEGES_ADMIN)
			if(activeChar.getAccessLevel() < REQUIRED_LEVEL)
				return false;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		//String val = "";
		//if (st.countTokens() >= 1) {val = st.nextToken();}

		if(actualCommand.equalsIgnoreCase("admin_zone_check"))
		{
			L2WorldRegion region = L2World.getInstance().getRegion(activeChar.getX(), activeChar.getY());
			if(region != null && region.getZones() != null)
			{
				for(IZone zone : region.getZones())
					if(zone.checkIfInZone(activeChar.getX(), activeChar.getY()))
						activeChar.sendMessage("Zone (XY" + (zone.checkIfInZone(activeChar) ? ("Z)") : (")")) + "(" + zone.getZoneType().toString() + "): ID " + zone.getId() + " " + zone.getZoneName() + " Z[" + zone.getMin().getZ() + ":" + zone.getMax().getZ() + "]");
			}
			activeChar.sendMessage("Closest Castle: " + CastleManager.getInstance().getClosestCastle(activeChar).getName());
			activeChar.sendMessage("Closest Town: " + TownManager.getInstance().getClosestTown(activeChar).getName());

			Location loc;

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Castle);
			activeChar.sendMessage("TeleToLocation (Castle): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.ClanHall);
			activeChar.sendMessage("TeleToLocation (ClanHall): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.SiegeFlag);
			activeChar.sendMessage("TeleToLocation (SiegeFlag): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

			loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Town);
			activeChar.sendMessage("TeleToLocation (Town): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());
		}
		else if(actualCommand.equalsIgnoreCase("admin_zone_reload"))
		{
			ZoneManager.getInstance().reload();
			GmListTable.broadcastMessageToGMs("Zones reloaded.");
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#getAdminCommandList()
	 */
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
