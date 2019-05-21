package it.polimi.se2019.model;

public enum RoomColor {

    BLUE('B'),
    RED('R'),
    YELLOW('Y'),
    PURPLE('P'),
    GREEN('G'),
    WHITE('W');

    private char identifier;

    RoomColor(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return this.identifier;
    }
}
