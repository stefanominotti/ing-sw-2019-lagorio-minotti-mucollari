package it.polimi.se2019.model;

/**
 * Enumeration Class for handling game characters
 */
public enum GameCharacter {

    D_STRUCT_OR("\u03B1", "yellow"), // alfa
    BANSHEE("\u03B2", "blue"), // beta
    DOZER(	"\u03B3", "grey"), // gamma
    VIOLET(	"\u03B4", "purple"), // delta
    SPROG("\u03B5", "green"); // epsilon

    private String identifier;
    private String color;

    /**
     * Class constructor, it builds a game character
     * @param identifier the identifier of the game character
     */
    GameCharacter(String identifier, String color) {
        this.identifier = identifier;
        this.color = color;
    }

    /**
     * Gets the identifier of the game character
     * @return the identifier of the game character
     */
    public String getIdentifier() {
        return this.identifier;
    }

    public String getColor() {
        return this.color;
    }
}