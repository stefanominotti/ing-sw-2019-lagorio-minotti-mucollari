package it.polimi.se2019.model;

public enum GameCharacter {

    D_STRUCT_OR('α'),
    BANSHEE('β'),
    DOZER('γ'),
    VIOLETTA('δ'),
    SPROG('ε');

    private char identifier;

    GameCharacter(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return this.identifier;
    }
}
