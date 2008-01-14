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
package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.model.L2Attackable;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.Env;

class EffectDamOverTime extends L2Effect
{		
	public EffectDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME;
	}

	public boolean onActionTime()
	{	
		if (getEffected().isDead())
			return false;
		
		double damage = calc();
		if(getSkill().getId() < 2000) { // fix for players' poison and bleed weak effect 
		    if(getSkill().getSkillType() == SkillType.POISON)
            {
                if (getEffected().isPetrified())
                    damage= 0;
                else
                damage = damage * 2; 
            }
		    if(getSkill().getSkillType() == SkillType.BLEED)
            {
                if (getEffected().isPetrified())
                    damage= 0;
                else
                damage = damage * 2; 
            }
                         if(damage > 300) damage = 300; 
		} 
		if (damage >= getEffected().getStatus().getCurrentHp())
		{
			if (getSkill().isToggle())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
				getEffected().sendPacket(sm);
				return false;
			}
            
            // ** This is just hotfix, needs better solution **
            // 1947: "DOT skills shouldn't kill"
            // Well, some of them should ;-)
            if (getSkill().getId() != 4082) damage = getEffected().getStatus().getCurrentHp() - 1;
		}

        boolean awake = !(getEffected() instanceof L2Attackable)
        					&& !(getSkill().getTargetType() == SkillTargetType.TARGET_SELF 
        							&& getSkill().isToggle());
        
        if(getSkill().getSkillType() == SkillType.POISON &&
                getEffected().getStatus().getCurrentHp() > damage)
        {
            if (getEffected().isPetrified())
            {damage= 0;}
            getEffected().reduceCurrentHp(damage, getEffector(),awake);
        }
        else
        {
            if (getEffected().isPetrified())
            {damage= 0;}
            getEffected().reduceCurrentHp(damage, getEffector(),awake);
        }
		
		return true;
	}
}
