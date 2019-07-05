package it.polimi.se2019.model.playerassets;

/**
 * Enumeration Class for handling ammo tiles
 * @author stefanominotti
 */
public enum AmmoType {

    BLUE('B'),
    RED('R'),
    YELLOW('Y');

    private char identifier;

    /**
     * Class constructor, it builds an ammo tile
     * @param identifier of the ammo type
     */
    AmmoType(char identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the ammo identifier
     * @return char of the ammo identifier
     */
    public char getIdentifier() {
        return this.identifier;
    }
}