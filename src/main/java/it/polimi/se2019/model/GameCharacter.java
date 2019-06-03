package it.polimi.se2019.model;

public enum GameCharacter {

    D_STRUCT_OR("\u03B1"), //alfa
    BANSHEE("\u03B2"), //beta
    DOZER(	"\u03B3"), //gamma
    VIOLETTA(	"\u03B4"), //delta
    SPROG("\u03B5"); //epsilon

    private String identifier;

    GameCharacter(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
