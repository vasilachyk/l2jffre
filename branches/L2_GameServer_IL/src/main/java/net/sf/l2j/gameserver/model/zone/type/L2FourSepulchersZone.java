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
package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.zone.L2Zone;
import net.sf.l2j.gameserver.model.zone.ZoneSettings;

public class L2FourSepulchersZone extends L2Zone
{
	public L2FourSepulchersZone(ZoneSettings set)
	{
		super(set);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_FOURSEPULCHERS, true);
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_FOURSEPULCHERS, false);
	}
}