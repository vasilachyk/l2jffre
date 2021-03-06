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
package com.l2jfree.gameserver.model.entity;

import com.l2jfree.gameserver.model.L2Clan;
import com.l2jfree.gameserver.model.L2SiegeClan;

/**
 * @author NB4L1
 */
public abstract class AbstractSiege
{
	public abstract Siegeable<?> getSiegeable();
	
	public abstract boolean getIsInProgress();

	public abstract boolean checkIsAttacker(L2Clan clan);

	public abstract boolean checkIsDefender(L2Clan clan);

	public abstract L2SiegeClan getAttackerClan(L2Clan clan);

	public abstract L2SiegeClan getDefenderClan(L2Clan clan);
}
