/*
 * $Header: PlayerClass.java, 24/11/2005 12:56:01 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 24/11/2005 12:56:01 $
 * $Revision: 1 $
 * $Log: PlayerClass.java,v $
 * Revision 1  24/11/2005 12:56:01  luisantonioa
 * Added copyright notice
 *
 * 
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
package net.sf.l2j.gameserver.model.base;

import static net.sf.l2j.gameserver.model.base.ClassLevel.First;
import static net.sf.l2j.gameserver.model.base.ClassLevel.Second;
import static net.sf.l2j.gameserver.model.base.ClassLevel.Third;
import static net.sf.l2j.gameserver.model.base.ClassType.Fighter;
import static net.sf.l2j.gameserver.model.base.ClassType.Mystic;
import static net.sf.l2j.gameserver.model.base.ClassType.Priest;
import static net.sf.l2j.gameserver.model.base.PlayerRace.DarkElf;
import static net.sf.l2j.gameserver.model.base.PlayerRace.Dwarf;
import static net.sf.l2j.gameserver.model.base.PlayerRace.Human;
import static net.sf.l2j.gameserver.model.base.PlayerRace.LightElf;
import static net.sf.l2j.gameserver.model.base.PlayerRace.Orc;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

/**
 * This class ...
 * 
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public enum PlayerClass
{
    HumanFighter    (Human, Fighter, First),
    Warrior         (Human, Fighter, Second),
    Gladiator       (Human, Fighter, Third),
    Warlord         (Human, Fighter, Third),
    HumanKnight     (Human, Fighter, Second),
    Paladin         (Human, Fighter, Third),
    DarkAvenger     (Human, Fighter, Third),
    Rogue           (Human, Fighter, Second),
    TreasureHunter  (Human, Fighter, Third),
    Hawkeye         (Human, Fighter, Third),
    
    HumanMystic     (Human, Mystic, First),
    HumanWizard     (Human, Mystic, Second),
    Sorceror        (Human, Mystic, Third),
    Necromancer     (Human, Mystic, Third),
    Warlock         (Human, Mystic, Third),
    Cleric          (Human, Priest, Second),
    Bishop          (Human, Priest, Third),
    Prophet         (Human, Priest, Third),
    
    ElvenFighter        (LightElf, Fighter, First),
    ElvenKnight         (LightElf, Fighter, Second),
    TempleKnight        (LightElf, Fighter, Third),
    Swordsinger         (LightElf, Fighter, Third),
    ElvenScout          (LightElf, Fighter, Second),
    Plainswalker        (LightElf, Fighter, Third),
    SilverRanger        (LightElf, Fighter, Third),
    ElvenMystic         (LightElf, Mystic, First),
    ElvenWizard         (LightElf, Mystic, Second),
    Spellsinger         (LightElf, Mystic, Third),
    ElementalSummoner   (LightElf, Mystic, Third),
    ElvenOracle         (LightElf, Priest, Second),
    ElvenElder          (LightElf, Priest, Third),
   
    DarkElvenFighter    (DarkElf, Fighter, First),
    PalusKnight         (DarkElf, Fighter, Second),
    ShillienKnight      (DarkElf, Fighter, Third),
    Bladedancer         (DarkElf, Fighter, Third),
    Assassin            (DarkElf, Fighter, Second),
    AbyssWalker         (DarkElf, Fighter, Third),
    PhantomRanger       (DarkElf, Fighter, Third),
    DarkElvenMystic     (DarkElf, Mystic, First),
    DarkElvenWizard     (DarkElf, Mystic, Second),
    Spellhowler         (DarkElf, Mystic, Third),
    PhantomSummoner     (DarkElf, Mystic, Third),
    ShillienOracle      (DarkElf, Priest, Second),
    ShillienElder       (DarkElf, Priest, Third),
    
    OrcFighter  (Orc, Fighter, First),
    OrcRaider   (Orc, Fighter, Second),
    Destroyer   (Orc, Fighter, Third),
    OrcMonk     (Orc, Fighter, Second),
    Tyrant      (Orc, Fighter, Third),
    OrcMystic   (Orc, Mystic, First),
    OrcShaman   (Orc, Mystic, Second),
    Overlord    (Orc, Mystic, Third),
    Warcryer    (Orc, Mystic, Third),
    
    DwarvenFighter      (Dwarf, Fighter, First),
    DwarvenScavenger    (Dwarf, Fighter, Second),
    BountyHunter        (Dwarf, Fighter, Third),
    DwarvenArtisan      (Dwarf, Fighter, Second),
    Warsmith            (Dwarf, Fighter, Third);
    
    private PlayerRace race;
    private ClassLevel level;
    private ClassType type;
    
    private static final Set<PlayerClass> mainSubclassSet;
    private static final Set<PlayerClass> neverSubclassed   = EnumSet.of(Overlord, Warsmith);

    private static final Set<PlayerClass> subclasseSet1     = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
    private static final Set<PlayerClass> subclasseSet2     = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
    private static final Set<PlayerClass> subclasseSet3     = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
    private static final Set<PlayerClass> subclasseSet4     = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
    private static final Set<PlayerClass> subclasseSet5     = EnumSet.of(Sorceror, Spellsinger, Spellhowler);

    private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap  = new EnumMap<PlayerClass, Set<PlayerClass>>(PlayerClass.class);
    
    static
    {
        Set<PlayerClass> subclasses = getSet(null, Third);
        subclasses.removeAll(neverSubclassed);
        
        mainSubclassSet = subclasses;
        
        subclassSetMap.put(DarkAvenger,     subclasseSet1);
        subclassSetMap.put(Paladin,         subclasseSet1);
        subclassSetMap.put(TempleKnight,    subclasseSet1);
        subclassSetMap.put(ShillienKnight,  subclasseSet1);

        
        subclassSetMap.put(TreasureHunter,  subclasseSet2);
        subclassSetMap.put(AbyssWalker,     subclasseSet2);
        subclassSetMap.put(Plainswalker,    subclasseSet2);
        
        subclassSetMap.put(Hawkeye,         subclasseSet3);
        subclassSetMap.put(SilverRanger,    subclasseSet3);
        subclassSetMap.put(PhantomRanger,   subclasseSet3);
        
        subclassSetMap.put(Warlock,             subclasseSet4);
        subclassSetMap.put(ElementalSummoner,   subclasseSet4);
        subclassSetMap.put(PhantomSummoner,     subclasseSet4);
        
        subclassSetMap.put(Sorceror,    subclasseSet5);
        subclassSetMap.put(Spellsinger, subclasseSet5);
        subclassSetMap.put(Spellhowler, subclasseSet5);
    }
    
    PlayerClass(PlayerRace race, ClassType type, ClassLevel level)
    {
        this.race   = race;
        this.level  = level;
        this.type   = type;
    }
    
    public final Set<PlayerClass> getAvaliableSubclasses()
    {
        Set<PlayerClass> subclasses = null;
        
        if (this.level == Third)
        {
            subclasses  = EnumSet.copyOf(mainSubclassSet);
            
            subclasses.removeAll(neverSubclassed);
            subclasses.remove(this);
            
            switch (this.race)
            {
                case LightElf:
                    subclasses.removeAll(getSet(DarkElf, Third));
                    break;
                case DarkElf:
                    subclasses.removeAll(getSet(LightElf, Third));
                    break;
            }
            
            Set<PlayerClass> unavaliableClasses = subclassSetMap.get(this);
            
            if (unavaliableClasses != null)
            {
                subclasses.removeAll(unavaliableClasses);
            }
        }
      
        return subclasses;
    }
    
    public static final EnumSet<PlayerClass> getSet(PlayerRace race, ClassLevel level)
    {
        EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);
        
        for (PlayerClass playerClass : EnumSet.allOf(PlayerClass.class))
        {
            if (race == null || playerClass.isOfRace(race))
            {
                if (level == null || playerClass.isOfLevel(level))
                {
                    allOf.add(playerClass);
                }
            }
        }
        
        return allOf;
    }
    
    public final boolean isOfRace(PlayerRace _race)
    {
        return this.race == _race;
    }
    
    public final boolean isOfType(ClassType _type)
    {
        return this.type == _type;
    }
    
    public final boolean isOfLevel(ClassLevel _level)
    {
        return this.level == _level;
    }

    public final ClassLevel getLevel()
    {
        return level;
    }    
}
