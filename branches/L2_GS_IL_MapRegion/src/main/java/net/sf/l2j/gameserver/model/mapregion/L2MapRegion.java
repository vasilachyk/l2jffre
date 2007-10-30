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
package net.sf.l2j.gameserver.model.mapregion;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.entity.Town;
import net.sf.l2j.gameserver.model.world.L2Polygon;

import org.w3c.dom.Node;

/**
 * @author Noctarius
 *
 */
public class L2MapRegion
{
	private int _id = -1;
	private L2Polygon _polygon = new L2Polygon();
	private Map<Race, Integer> _restarts = new FastMap<Race, Integer>();
	private int _zMin = -999999999;
	private int _zMax = 999999999;
	private Town _town = null;

	public L2MapRegion(Node node)
	{
		Node e = node.getAttributes().getNamedItem("id");
		if (e != null)
			_id = Integer.parseInt(e.getNodeValue());
		
		parseRangeData(node);
	}
	
	private void parseRangeData(Node node)
	{
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("zHeight".equalsIgnoreCase(n.getNodeName()))
			{
				Node d = n.getAttributes().getNamedItem("min");
				if (d != null)
					_zMin = Integer.parseInt(d.getTextContent());

				d = n.getAttributes().getNamedItem("max");
				if (d != null)
					_zMax = Integer.parseInt(d.getTextContent());
			}
			else if ("point".equalsIgnoreCase(n.getNodeName()))
			{
				int X = 0;
				int Y = 0;
				
    			Node d = n.getAttributes().getNamedItem("X");
    			if (d != null)
    				X = Integer.parseInt(d.getTextContent());
    			
    			d = n.getAttributes().getNamedItem("Y");
    			if (d != null)
    				Y = Integer.parseInt(d.getTextContent());

    			_polygon.addPoint(X, Y);
			}
			else if ("restart".equalsIgnoreCase(n.getNodeName()))
			{
				Race race = Race.human;
				int restartId = 0;
				
    			Node d = n.getAttributes().getNamedItem("race");
    			if (d != null)
    				race= Race.getRaceByName(d.getTextContent());
    			
    			d = n.getAttributes().getNamedItem("restartId");
    			if (d != null)
    				restartId = Integer.parseInt(d.getTextContent());
    			
    			if (!_restarts.containsKey(race))
    				_restarts.put(race, restartId);
			}
		}
	}
	
	public int getRestartId(Race race)
	{
		return _restarts.get(race);
	}
	
	public int[] getZ()
	{
		int[] z = { _zMin, _zMax };
		
		return z;
	}
	
	public L2Polygon getRegionPolygon()
	{
		return _polygon;
	}
	
	public int getId()
	{
		return _id;
	}

	public final boolean checkIfInRegion(int x, int y, int z)
    {
    	if (!quickIsInsideRegion(x, y, z)) 
    		return false;
    	
    	return _polygon.contains(x, y);
    }
	
    private final boolean quickIsInsideRegion(int x, int y, int z)
    {
    	int[] xPoints = _polygon.getXPoints();
    	int[] yPoints = _polygon.getYPoints();
    	
    	int xMax = xPoints[0];
    	int xMin = xPoints[0];
    	int yMax = yPoints[0];
    	int yMin = yPoints[0];
    	
    	for(int in: xPoints)
	        if(in > xMax) 
    			xMax = in;
    		else if(in < xMin) 
    			xMin = in;
    	
    	for(int in: yPoints)
	        if(in > yMax) 
    			yMax = in;
    		else if(in < yMin) 
    			yMin = in;
    	
    	if (!(x > xMin && x < xMax && y > yMin && y < yMax))
    		return false;
    	
    	if (_zMin == -999999999 && _zMax == 999999999) 
    		return true;
    	else
    		return z > _zMin && z < _zMax;
    }
    
    public void setTown(Town town)
    {
    	_town = town;
    }
    
    public Town getTown()
    {
    	return _town;
    }
}
