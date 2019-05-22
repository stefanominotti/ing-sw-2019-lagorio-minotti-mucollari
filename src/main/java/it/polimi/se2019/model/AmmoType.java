package it.polimi.se2019.model;

public enum AmmoType {

    BLUE('B'),
    RED('R'),
    YELLOW('Y');

    private char identifier;

    AmmoType(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return this.identifier;
    }
}
