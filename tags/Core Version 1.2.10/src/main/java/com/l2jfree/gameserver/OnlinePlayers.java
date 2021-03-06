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
package com.l2jfree.gameserver;

import com.l2jfree.Config;
import com.l2jfree.gameserver.model.L2World;

public class OnlinePlayers
{
	private static OnlinePlayers	_instance;

	class AnnounceOnline implements Runnable
	{
		public void run()
		{
			if (L2World.getInstance().getAllPlayers().size() == 1)
				Announcements.getInstance().announceToAll("There is " + L2World.getInstance().getAllPlayers().size() + " online player.");
			else
				Announcements.getInstance().announceToAll("There are " + L2World.getInstance().getAllPlayers().size() + " online players.");
			ThreadPoolManager.getInstance().scheduleGeneral(new AnnounceOnline(), Config.ONLINE_PLAYERS_ANNOUNCE_INTERVAL);
		}
	}

	public static OnlinePlayers getInstance()
	{
		if (_instance == null)
			_instance = new OnlinePlayers();
		return _instance;
	}

	private OnlinePlayers()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new AnnounceOnline(), Config.ONLINE_PLAYERS_ANNOUNCE_INTERVAL);
	}
}