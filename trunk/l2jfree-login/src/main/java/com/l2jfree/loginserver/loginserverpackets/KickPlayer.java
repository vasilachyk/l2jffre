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
package com.l2jfree.loginserver.loginserverpackets;

import com.l2jfree.status.LoginStatusServer;

public final class KickPlayer extends LoginToGamePacket
{
	public KickPlayer(String account)
	{
		super(0x04);
		writeS(account);
		LoginStatusServer.tryBroadcast("Player " + account + " was kicked.");
	}
}
