package it.polimi.se2019.model;

/**
 * Enumeration Class for handling game characters
 */
public enum GameCharacter {

    D_STRUCT_OR("\u03B1"), // alfa
    BANSHEE("\u03B2"), // beta
    DOZER(	"\u03B3"), // gamma
    VIOLET(	"\u03B4"), // delta
    SPROG("\u03B5"); // epsilon

    private String identifier;

    /**
     * Class constructor, it builds a game character
     * @param identifier the identifier of the game character
     */
    GameCharacter(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the identifier of the game character
     * @return the identifier of the game character
     */
    public String getIdentifier() {
        return this.identifier;
    }
}