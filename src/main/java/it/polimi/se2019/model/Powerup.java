package it.polimi.se2019.model;

import java.io.Serializable;

/**
 * Class for handling powerups
 */
public class Powerup implements Serializable {

    private PowerupType type;
    private AmmoType color;

    /**
     * Class constructor, it builds a powerup
     * @param type of the powerup you want to build
     * @param color of the powerup you want to build
     */
    public Powerup(PowerupType type, AmmoType color) {
        this.type = type;
        this.color = color;
    }

    /**
     * Gets the powerup type
     * @return the powerup type
     */
    public PowerupType getType() {
        return this.type;
    }

    /**
     * Gets the powerup color
     * @return the powerup type
     */
    public AmmoType getColor() {
        return this.color;
    }
}
