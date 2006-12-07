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
package net.sf.l2j.gameserver.skills;


import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.geodata.FarPoint;
import net.sf.l2j.gameserver.model.entity.geodata.GeoDataRequester;

/**
 * @author littlecrow
 *
 * Implementation of the Fear Effect
 */
final class EffectFear extends L2Effect {

	public static final int FEAR_RANGE = 500;
	
	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectType getEffectType()
	{
		return EffectType.FEAR;
	}
	
	/** Notify started */
	public void onStart() {
        if (!(getEffected() instanceof L2PcInstance))
		getEffected().startFear();
        if (!(getEffected() instanceof L2PcInstance))
		onActionTime();
	}
	
	/** Notify exited */
	public void onExit() {
		
		getEffected().stopFear(this);
	}
		
	
	
    public boolean onActionTime()
    {
        // Fear skills cannot be used l2pcinstance to l2pcinstance. Heroic Dread is the exception.
        if(getEffected() instanceof L2PcInstance && getEffector() instanceof L2PcInstance && getSkill().getId() != 1376) return false;
        int posX = getEffected().getX();
		int posY = getEffected().getY();
		int posZ = getEffected().getZ();
		
//		Random r = L2Character.getRnd();
        int signx=-1;
		int signy=-1;
		if (getEffected().getX()>getEffector().getX())
			signx=1;
		if (getEffected().getY()>getEffector().getY())
			signy=1;

        posX += signx*FEAR_RANGE;
		posY += signy*FEAR_RANGE;

        getEffected().setRunning();
        
        FarPoint fp;
        fp = GeoDataRequester.getInstance().hasMovementLoS(getEffected(), posX,posY,(short)getEffected().getZ());
        
        getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,
                 new L2CharPosition(fp.x,fp.y,fp.z,0));
        
        return true;
    }
}

