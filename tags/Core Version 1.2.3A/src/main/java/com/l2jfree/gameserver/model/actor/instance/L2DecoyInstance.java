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
package com.l2jfree.gameserver.model.actor.instance;

import java.util.concurrent.Future;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Decoy;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.knownlist.DecoyKnownList;
import com.l2jfree.gameserver.taskmanager.DecayTaskManager;
import com.l2jfree.gameserver.templates.L2NpcTemplate;

public class L2DecoyInstance extends L2Decoy
{
    protected static final Log _log = LogFactory.getLog(L2DecoyInstance.class.getName());
    
    private int _totalLifeTime;
    
    private int _timeRemaining;
    
    private Future<?> _DecoyLifeTask;
    
    private Future<?> _HateSpam;
    
    public L2DecoyInstance(int objectId, L2NpcTemplate template, L2PcInstance owner, L2Skill skill)
    {
        super(objectId, template, owner);
        if (skill != null)
        {
            _totalLifeTime = skill.getTotalLifeTime();
        }
        else
        {
            _totalLifeTime = 20000;
        }
        _timeRemaining = _totalLifeTime;
        int delay = 1000;
        int skilllevel = getTemplate().getIdTemplate() - 13070;
        _DecoyLifeTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new DecoyLifetime(getOwner(), this), delay, delay);
        _HateSpam = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new HateSpam(this, SkillTable.getInstance().getInfo(5272, skilllevel)), 2000, 5000);
    }
    
    @Override
    public boolean doDie(L2Character killer)
    {
        if (!super.doDie(killer))
            return false;
        if (_HateSpam != null)
        {
            _HateSpam.cancel(true);
            _HateSpam = null;
        }
        _totalLifeTime = 0;
        DecayTaskManager.getInstance().addDecayTask(this);
        return true;
    }
    
    @Override
    public DecoyKnownList getKnownList()
    {
        if (_knownList == null)
            _knownList = new DecoyKnownList(this);
        
        return (DecoyKnownList)_knownList;
    }
    
    static class DecoyLifetime implements Runnable
    {
        private L2PcInstance _activeChar;
        
        private L2DecoyInstance _Decoy;
        
        DecoyLifetime(L2PcInstance activeChar, L2DecoyInstance Decoy)
        {
            _activeChar = activeChar;
            _Decoy = Decoy;
        }
        
        public void run()
        {
            try
            {
                double newTimeRemaining;
                _Decoy.decTimeRemaining(1000);
                newTimeRemaining = _Decoy.getTimeRemaining();
                if (newTimeRemaining < 0)
                {
                    _Decoy.unSummon(_activeChar);
                }
            }
            catch (Throwable e)
            {
                if (_log.isDebugEnabled())
                    _log.warn("Decoy Error:" + e);
            }
        }
    }
    
    static class HateSpam implements Runnable
    {
        private L2DecoyInstance _activeChar;
        
        private L2Skill _skill;
        
        HateSpam(L2DecoyInstance activeChar, L2Skill Hate)
        {
            _activeChar = activeChar;
            _skill = Hate;
        }
        
        public void run()
        {
            try
            {
                _activeChar.setTarget(_activeChar);
                _activeChar.doCast(_skill);
            }
            catch (Throwable e)
            {
                if (_log.isDebugEnabled())
                    _log.warn("Decoy Error:" + e);
            }
        }
    }
    
    @Override
    public void unSummon(L2PcInstance owner)
    {
        if (_DecoyLifeTask != null)
        {
            _DecoyLifeTask.cancel(true);
            _DecoyLifeTask = null;
        }
        if (_HateSpam != null)
        {
            _HateSpam.cancel(true);
            _HateSpam = null;
        }
        super.unSummon(owner);
    }
    
    public void decTimeRemaining(int value)
    {
        _timeRemaining -= value;
    }
    
    public int getTimeRemaining()
    {
        return _timeRemaining;
    }
    
    public int getTotalLifeTime()
    {
        return _totalLifeTime;
    }
}
