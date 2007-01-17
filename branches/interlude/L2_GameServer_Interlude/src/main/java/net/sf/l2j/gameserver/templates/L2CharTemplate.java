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
package net.sf.l2j.gameserver.templates;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.4.6 $ $Date: 2005/04/02 15:57:51 $
 */
public class L2CharTemplate
{
	// BaseStats
	public final int baseSTR;
	public final int baseCON;
	public final int baseDEX;
	public final int baseINT;
	public final int baseWIT;
	public final int baseMEN;
	public final float baseHpMax;
    public final float baseCpMax;
	public final float baseMpMax;
	
	/** HP Regen base */
	public final float baseHpReg;
	
	/** MP Regen base */
	public final float baseMpReg;
       
	public final int basePAtk;
	public final int baseMAtk;
	public final int basePDef;
	public final int baseMDef;
	public final int basePAtkSpd;
	public final int baseMAtkSpd;
	public final float baseMReuseRate;
	public final int baseShldDef;
	public final int baseAtkRange;
	public final int baseShldRate;
	public final int baseCritRate;
	public final int baseRunSpd;
	// SpecialStats
	public final int baseBreath;
	public final int baseAggression;
	public final int baseBleed;
	public final int basePoison;
	public final int baseStun;
	public final int baseRoot;
	public final int baseMovement;
	public final int baseConfusion;
	public final int baseSleep;
	public final int baseFire;
	public final int baseWind;
	public final int baseWater;
	public final int baseEarth;
	public final int baseHoly;
	public final int baseDark;	
	public final int baseAggressionRes;
	public final int baseBleedRes;
	public final int basePoisonRes;
	public final int baseStunRes;
	public final int baseRootRes;
	public final int baseMovementRes;
	public final int baseConfusionRes;
	public final int baseSleepRes;
	public final int baseFireRes;
	public final int baseWindRes;
	public final int baseWaterRes;
	public final int baseEarthRes;
	public final int baseHolyRes;
	public final int baseDarkRes;
    
    //C4 Stats
    public final int baseMpConsumeRate;
    public final int baseHpConsumeRate;
	
	public final int collisionRadius;   
	public final int collisionHeight;
	
	public L2CharTemplate(StatsSet set)
	{
		// Base stats
		baseSTR            = set.getInteger("baseSTR");
		baseCON            = set.getInteger("baseCON");
		baseDEX            = set.getInteger("baseDEX");
		baseINT            = set.getInteger("baseINT");
		baseWIT            = set.getInteger("baseWIT");
		baseMEN            = set.getInteger("baseMEN");
		baseHpMax          = set.getFloat ("baseHpMax");
    	baseCpMax          = set.getFloat("baseCpMax");
		baseMpMax          = set.getFloat ("baseMpMax");
		baseHpReg          = set.getFloat ("baseHpReg");
		baseMpReg          = set.getFloat ("baseMpReg");
		basePAtk           = set.getInteger("basePAtk");
		baseMAtk           = set.getInteger("baseMAtk");
		basePDef           = set.getInteger("basePDef");
		baseMDef           = set.getInteger("baseMDef");
		basePAtkSpd        = set.getInteger("basePAtkSpd");
		baseMAtkSpd        = set.getInteger("baseMAtkSpd");
		baseMReuseRate     = set.getFloat ("baseMReuseDelay", 1.f);
		baseShldDef        = set.getInteger("baseShldDef");
		baseAtkRange       = set.getInteger("baseAtkRange");
		baseShldRate       = set.getInteger("baseShldRate");
		baseCritRate       = set.getInteger("baseCritRate");
		baseRunSpd         = set.getInteger("baseRunSpd");
		// SpecialStats
		baseBreath         = set.getInteger("baseBreath",         100);
		baseAggression     = set.getInteger("baseAggression",     0);
		baseBleed          = set.getInteger("baseBleed",          0);
		basePoison         = set.getInteger("basePoison",         0);
		baseStun           = set.getInteger("baseStun",           0);
		baseRoot           = set.getInteger("baseRoot",           0);
		baseMovement       = set.getInteger("baseMovement",       0);
		baseConfusion      = set.getInteger("baseConfusion",      0);
		baseSleep          = set.getInteger("baseSleep",          0);
		baseFire           = set.getInteger("baseFire",           0);
		baseWind           = set.getInteger("baseWind",           0);
		baseWater          = set.getInteger("baseWater",          0);
		baseEarth          = set.getInteger("baseEarth",          0);
		baseHoly           = set.getInteger("baseHoly",           0);
		baseDark           = set.getInteger("baseDark",           0);
		baseAggressionRes  = set.getInteger("baseAaggressionRes", 1); // setting baseRes stat to 1 for mul type effects in DP
		baseBleedRes       = set.getInteger("baseBleedRes",       1);
		basePoisonRes      = set.getInteger("basePoisonRes",      1);
		baseStunRes        = set.getInteger("baseStunRes",        1);
		baseRootRes        = set.getInteger("baseRootRes",        1);
		baseMovementRes    = set.getInteger("baseMovementRes",    1);
		baseConfusionRes   = set.getInteger("baseConfusionRes",   1);
		baseSleepRes       = set.getInteger("baseSleepRes",       1);
		baseFireRes        = set.getInteger("baseFireRes",        1);
		baseWindRes        = set.getInteger("baseWindRes",        1);
		baseWaterRes       = set.getInteger("baseWaterRes",       1);
		baseEarthRes       = set.getInteger("baseEarthRes",       1);
		baseHolyRes        = set.getInteger("baseHolyRes",        1);
		baseDarkRes        = set.getInteger("baseDarkRes",        1);
        
        //C4 Stats
        baseMpConsumeRate      = set.getInteger("baseMpConsumeRate",        0);
        baseHpConsumeRate      = set.getInteger("baseHpConsumeRate",        0);
		
		// Geometry
		collisionRadius    = set.getInteger("collision_radius");
		collisionHeight    = set.getInteger("collision_height");
	}
}
