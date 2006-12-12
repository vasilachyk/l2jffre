package net.sf.l2j.loginserver.beans;

// Generated 11 d�c. 2006 17:28:16 by Hibernate Tools 3.2.0.beta8

/**
 * CharacterQuests generated by hbm2java
 */
public class CharacterQuests implements java.io.Serializable
{

    // Fields    

    private CharacterQuestsId id;
    private Characters characters;
    private String value;

    // Constructors

    /** default constructor */
    public CharacterQuests()
    {
    }

    /** minimal constructor */
    public CharacterQuests(CharacterQuestsId id, Characters characters)
    {
        this.id = id;
        this.characters = characters;
    }

    /** full constructor */
    public CharacterQuests(CharacterQuestsId id, Characters characters, String value)
    {
        this.id = id;
        this.characters = characters;
        this.value = value;
    }

    // Property accessors
    public CharacterQuestsId getId()
    {
        return this.id;
    }

    public void setId(CharacterQuestsId id)
    {
        this.id = id;
    }

    public Characters getCharacters()
    {
        return this.characters;
    }

    public void setCharacters(Characters characters)
    {
        this.characters = characters;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
