package it.polimi.se2019.model.playerassets.weapons;

/**
 * Enumeration Class for handling weapon effects order
 * @author eknidmucollari
 */
public enum WeaponEffectOrderType {

    PRIMARY("P"),
    ALTERNATIVE("A"),
    SECONDARYONE("S1"),
    SECONDARYTWO("S2");

    private String identifier;

    /**
     * Class constructor it builds a weapon effect order type
     * @param identifier of the weapon effect order type
     */
    WeaponEffectOrderType(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the identifier of the effect macro
     * @return
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Gets the effect macro from its identifier
     * @param identifier of the effect macro you want to get
     * @return the weapon effect order type matching with the identifier
     */
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
