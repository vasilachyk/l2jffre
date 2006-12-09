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
package net.sf.l2j.gameserver.clientpackets;

import java.nio.ByteBuffer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ClientThread;
import net.sf.l2j.gameserver.SkillTable;
import net.sf.l2j.gameserver.SkillTreeTable;
import net.sf.l2j.gameserver.model.L2EnchantSkillLearn;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2FolkInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.ExEnchantSkillInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Format chdd
 * c: (id) 0xD0
 * h: (subid) 0x06
 * d: skill id
 * d: skill lvl
 * @author -Wooden-
 *
 */
public class RequestExEnchantSkillInfo extends ClientBasePacket
{
	private static final String _C__D0_06_REQUESTEXENCHANTSKILLINFO = "[C] D0:06 RequestExEnchantSkillInfo";
    private final static Log _log = LogFactory.getLog(RequestAquireSkillInfo.class.getName());
	@SuppressWarnings("unused")
	private int _id;
	@SuppressWarnings("unused")
	private int _level;
	/**
	 * @param buf
	 * @param client
	 */
	public RequestExEnchantSkillInfo(ByteBuffer buf, ClientThread client)
	{
		super(buf, client);
		_id = readD();
		_level = readD();
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#runImpl()
	 */
	@Override
	void runImpl()
	{
	    L2PcInstance activeChar = getClient().getActiveChar();
        
        if (activeChar == null) 
            return;
        
        L2FolkInstance trainer = activeChar.getLastFolkNPC();

        if ((trainer == null || !activeChar.isInsideRadius(trainer, L2NpcInstance.INTERACTION_DISTANCE, false, false)) && !activeChar.isGM()) 
            return;

        L2Skill skill = SkillTable.getInstance().getInfo(_id, _level);
        
        boolean canteach = false;
        
        if (skill == null)
        {
            _log.warn("enchant skill id " + _id + " level " + _level
                + " is undefined. aquireEnchantSkillInfo failed.");
            return;
        }

            if (!trainer.getTemplate().canTeach(activeChar.getClassId())) 
                return; // cheater

            L2EnchantSkillLearn[] skills = SkillTreeTable.getInstance().getAvailableEnchantSkills(activeChar);

            for (L2EnchantSkillLearn s : skills)
            {
                if (s.getId() == _id && s.getLevel() == _level)
                {
                    canteach = true;
                    break;
                }
            }
            
            if (!canteach)
                return; // cheater :)
            
            int requiredSp = SkillTreeTable.getInstance().getSkillSpCost(activeChar, skill);
            int requiredExp = SkillTreeTable.getInstance().getSkillExpCost(activeChar, skill);
            int rate = SkillTreeTable.getInstance().getSuccessRate(activeChar, skill);
            ExEnchantSkillInfo asi = new ExEnchantSkillInfo(skill.getId(), skill.getLevel(), requiredSp, requiredExp, rate);
            
            if (Config.SP_BOOK_NEEDED)
            {
                int spbId = 6622;
                
                //if (skill.getLevel() == 1 && spbId > -1)
                    asi.addRequirement(4, spbId, 1, 0);
            }

            sendPacket(asi);
        
		
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__D0_06_REQUESTEXENCHANTSKILLINFO;
	}
	
}