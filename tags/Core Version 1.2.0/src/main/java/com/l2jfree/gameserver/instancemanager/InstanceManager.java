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
package com.l2jfree.gameserver.instancemanager;

import java.io.FileNotFoundException;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.model.entity.Instance;

/** 
 * @author evill33t
 * 
 */
public class InstanceManager
{
	private final static Log			_log			= LogFactory.getLog(InstanceManager.class.getName());
	private FastMap<Integer, Instance>	_instanceList	= new FastMap<Integer, Instance>();
	private static InstanceManager		_instance;
	private int							_dynamic		= 300000;

	public static final InstanceManager getInstance()
	{
		if (_instance == null)
		{
			_log.info("Initializing InstanceManager");
			_instance = new InstanceManager();
			_instance.createWorld();
		}
		return _instance;
	}

	private void createWorld()
	{
		Instance themultiverse = new Instance(-1);
		themultiverse.setName("multiverse");
		_instanceList.put(-1, themultiverse);
		_log.info("Multiverse Instance created");

		Instance universe = new Instance(0);
		universe.setName("universe");
		_instanceList.put(0, universe);
		_log.info("Universe Instance created");
	}

	public void destroyInstance(int instanceid)
	{
		if (instanceid == 0)
			return;
		Instance temp = _instanceList.get(instanceid);
		if (temp != null)
		{
			temp.removeNpcs();
			temp.removePlayers();
			_instanceList.remove(instanceid);
		}
	}

	public Instance getInstance(int instanceid)
	{
		return _instanceList.get(instanceid);
	}
	
	public FastMap<Integer,Instance> getInstances()
	{
		return _instanceList;
	}

	public int getPlayerInstance(int objectId)
	{
		for (Instance temp : _instanceList.values())
		{
			// check if the player is in any active instance
			if (temp.containsPlayer(objectId))
				return temp.getId();
		}
		// 0 is default instance aka the world
		return 0;
	}

	public boolean createInstance(int id)
	{
		if (getInstance(id) != null)
			return false;

		Instance instance = new Instance(id);
		_instanceList.put(id, instance);
		return true;
	}

	public boolean createInstanceFromTemplate(int id, String template) throws FileNotFoundException
	{
		if (getInstance(id) != null)
			return false;

		Instance instance = new Instance(id);
		instance.loadInstanceTemplate(template);
		_instanceList.put(id, instance);
		return true;
	}

	public int createDynamicInstance(String template) throws FileNotFoundException
	{

		while (getInstance(_dynamic) != null)
		{
			_dynamic++;
			if (_dynamic == Integer.MAX_VALUE)
			{
				_log.warn("InstanceManager: More then " + (Integer.MAX_VALUE - 300000) + " instances created");
				_dynamic = 300000;
			}
		}
		Instance instance = new Instance(_dynamic);
		instance.loadInstanceTemplate(template);
		_instanceList.put(_dynamic, instance);
		return _dynamic;
	}
}
