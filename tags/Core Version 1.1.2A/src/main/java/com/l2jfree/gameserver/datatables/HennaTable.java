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
package com.l2jfree.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.templates.L2Henna;
import com.l2jfree.gameserver.templates.StatsSet;

/**
 * This class ...
 * 
 * @version $Revision$ $Date$
 */
public class HennaTable
{
	private final static Log			_log			= LogFactory.getLog(HennaTable.class.getName());

	private static HennaTable			_instance;

	private final FastMap<Integer, L2Henna> _henna = new FastMap<Integer, L2Henna>();

	public static HennaTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new HennaTable();
		}
		return _instance;
	}

	private HennaTable()
	{
		restoreHennaData();
	}

	/**
	 * 
	 */
	private void restoreHennaData()
	{
		Connection con = null;
		try
		{
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				PreparedStatement statement = con
						.prepareStatement("SELECT symbol_id, symbol_name, dye_id, dye_amount, price, stat_INT, stat_STR, stat_CON, stat_MEM, stat_DEX, stat_WIT FROM henna");
				ResultSet hennadata = statement.executeQuery();

				fillHennaTable(hennadata);
				hennadata.close();
				statement.close();
			}
			catch (Exception e)
			{
				_log.error("error while creating henna table " + e, e);
			}
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	private void fillHennaTable(ResultSet hennaData) throws Exception
	{
		while (hennaData.next())
		{
			int id = hennaData.getInt("symbol_id");
			
			StatsSet hennaDat = new StatsSet();
			hennaDat.set("symbol_id", id);
			hennaDat.set("dye_id", hennaData.getInt("dye_id"));
			hennaDat.set("price", hennaData.getInt("price"));
			hennaDat.set("dye_amount", hennaData.getInt("dye_amount"));
			hennaDat.set("stat_INT", hennaData.getInt("stat_INT"));
			hennaDat.set("stat_STR", hennaData.getInt("stat_STR"));
			hennaDat.set("stat_CON", hennaData.getInt("stat_CON"));
			hennaDat.set("stat_MEM", hennaData.getInt("stat_MEM"));
			hennaDat.set("stat_DEX", hennaData.getInt("stat_DEX"));
			hennaDat.set("stat_WIT", hennaData.getInt("stat_WIT"));
			
			_henna.put(id, new L2Henna(hennaDat));
		}
		_log.info("HennaTable: Loaded " + _henna.size() + " Templates.");
	}
	
	public L2Henna getTemplate(int id)
	{
		return _henna.get(id);
	}
}
