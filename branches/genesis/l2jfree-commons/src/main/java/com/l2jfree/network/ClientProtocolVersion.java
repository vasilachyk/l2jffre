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
package com.l2jfree.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.ArrayUtils;

/**
 * An enumeration of known client protocol versions.
 * 
 * @author NB4L1
 */
// dates can be wrong
public enum ClientProtocolVersion implements IGameProtocolVersion
{
	GRACIA_FINAL(87, 0x63, new int[] { 0x12, 0xB1 }, null, true, "2009-10-25"), 
	FREYA(216, true, "2010-08-24"), 
	HIGH_FIVE(267, 0x97, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74 }, false, "2011-02-15"),
	HIGH_FIVE_UPDATE_1(268, 0x97, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74 }, false, "2011-03-15"),
	HIGH_FIVE_UPDATE_2(271, 0x97, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74 }, false, "2011-05-25"),
	HIGH_FIVE_UPDATE_3(273, 0x97, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74 }, true, "2011-06-08"),
	GODDESS_OF_DESTRUCTION(415, 0xC4, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74, 0x73 }, true, "2011-11-30"),
	HARMONY(410, 0xC6, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74, 0x73 }, true, "2012-03-07"),
	GLORY_DAYS_479(479, 0xDE, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74, 0x73 }, true, "2012-10-24"), // ?
	GLORY_DAYS_480(480, 0xDE, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74, 0x73 }, true, "2012-11-21"), 
	GLORY_DAYS_488(488, 0xDE, new int[] { 0x12, 0xB1, 0x11, 0xD0 }, new int[] { 0x74, 0x73 }, true, "2013-03-01"); // ?
	
	private final int _version;
	private final int _op2TableSize;
	private final int[] _ignoredOp1s;
	private final int[] _ignoredOp2s;
	private final boolean _enabled;
	private final long _timestamp;
	
	private ClientProtocolVersion(int version, int op2TableSize, int[] ignoredOp1s, int[] ignoredOp2s, boolean enabled, String date)
	{
		_version = version;
		_op2TableSize = op2TableSize;
		_ignoredOp1s = ArrayUtils.nullToEmpty(ignoredOp1s);
		_ignoredOp2s = ArrayUtils.nullToEmpty(ignoredOp2s);
		_enabled = enabled;
		try
		{
			_timestamp = new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime();
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private ClientProtocolVersion(int version, boolean enabled, String date)
	{
		this(version, 0x97, null, null, enabled, date);
	}
	
	@Override
	public int getVersion()
	{
		return _version;
	}
	
	@Override
	public int getOp2TableSize()
	{
		return _op2TableSize;
	}
	
	@Override
	public int[] getIgnoredOp1s()
	{
		return _ignoredOp1s;
	}
	
	@Override
	public int[] getIgnoredOp2s()
	{
		return _ignoredOp2s;
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	@Override
	public boolean isOlderThan(IProtocolVersion version) throws UnsupportedOperationException
	{
		return getReleaseDate() < version.getReleaseDate();
	}
	
	@Override
	public boolean isOlderThanOrEqualTo(IProtocolVersion version) throws UnsupportedOperationException
	{
		return getReleaseDate() <= version.getReleaseDate();
	}
	
	@Override
	public boolean isNewerThan(IProtocolVersion version) throws UnsupportedOperationException
	{
		return getReleaseDate() > version.getReleaseDate();
	}
	
	@Override
	public boolean isNewerThanOrEqualTo(IProtocolVersion version) throws UnsupportedOperationException
	{
		return getReleaseDate() >= version.getReleaseDate();
	}
	
	@Override
	public long getReleaseDate()
	{
		return _timestamp;
	}
}
