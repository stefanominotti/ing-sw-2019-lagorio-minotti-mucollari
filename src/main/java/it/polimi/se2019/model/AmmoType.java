package it.polimi.se2019.model;

/**
 * Enumeration Class for handling Ammo Tiles
 */
public enum AmmoType {

    BLUE('B'),
    RED('R'),
    YELLOW('Y');

    private char identifier;

    /**
     * Class constructor
     * @param identifier the identifier of the Ammo Type
     */
    AmmoType(char identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the Ammo identifier
     * @return char of Ammo identifier
     */
    public char getIdentifier() {
        return this.identifier;
    }
}
