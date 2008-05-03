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
package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author  Julian
 */
public class DeluxeKey implements ISkillHandler
{
	private final static Log _log = LogFactory.getLog(BeastFeed.class.getName());
	private static final SkillType[] SKILL_IDS = {SkillType.DELUXE_KEY_UNLOCK};

	public void useSkill(L2Character activeChar, L2Skill skill, @SuppressWarnings("unused") L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;

		L2Object[] targetList = skill.getTargetList(activeChar);

		if (targetList == null)
		{
			return;
		}

		if(_log.isDebugEnabled())
			_log.info("Delux key casting succeded.");

		// This is just a dummy skill handler for the golden food and crystal food skills,
		// since the AI responce onSkillUse handles the rest.
	}

	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
