package it.polimi.se2019.model;

/**
 * Enumeration Class for handling room colors
 */
public enum RoomColor {

    BLUE('B'),
    RED('R'),
    YELLOW('Y'),
    PURPLE('P'),
    GREEN('G'),
    WHITE('W');

    char identifier;

    /**
     * Class constructor, it builds a room color
     * @param identifier of the room color
     */
    RoomColor(char identifier) {
        this.identifier = identifier;
    }
}