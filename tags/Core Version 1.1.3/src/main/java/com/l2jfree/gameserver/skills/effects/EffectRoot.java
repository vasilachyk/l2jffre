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
package com.l2jfree.gameserver.skills.effects;

import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.skills.Env;

/**
 * @author mkizub
 */
public final class EffectRoot extends L2Effect
{

	public EffectRoot(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectType getEffectType()
	{
		return EffectType.ROOT;
	}

	/** Notify started */
	public void onStart()
	{
		getEffected().startRooted();
	}

	/** Notify exited */
	public void onExit()
	{
		getEffected().stopRooting(this);
	}

	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
