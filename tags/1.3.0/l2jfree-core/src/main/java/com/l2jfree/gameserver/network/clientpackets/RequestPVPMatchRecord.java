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

import com.l2jfree.gameserver.network.SystemMessageId;

/**
 *
 * @author  KenM
 */
public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger, no data
	}

	@Override
	protected void runImpl()
	{
		requestFailed(SystemMessageId.NOT_WORKING_PLEASE_TRY_AGAIN_LATER);
	}

	@Override
	public String getType()
	{
		return "[C] D0:4C RequestPVPMatchRecord";
	}
}
