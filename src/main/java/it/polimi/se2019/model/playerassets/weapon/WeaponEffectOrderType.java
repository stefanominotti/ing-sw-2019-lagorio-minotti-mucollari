package it.polimi.se2019.model.playerassets.weapon;

/**
 * Enumeration Class for handling weapon effects order
 */
public enum WeaponEffectOrderType {

    PRIMARY("P"),
    ALTERNATIVE("A"),
    SECONDARYONE("S1"),
    SECONDARYTWO("S2");

    private String identifier;

    WeaponEffectOrderType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public static WeaponEffectOrderType getFromIdentifier(String identifier) {
        switch (identifier) {
            case "P":
                return PRIMARY;
            case "A":
                return ALTERNATIVE;
            case "S1":
                return SECONDARYONE;
            case "S2":
                return SECONDARYTWO;
            default:
                return null;
        }
    }
}
