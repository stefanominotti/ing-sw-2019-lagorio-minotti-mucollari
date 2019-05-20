package it.polimi.se2019.model;

import java.io.Serializable;

public class Powerup implements Serializable {

    private PowerupType type;
    private AmmoType color;

    public Powerup(PowerupType type, AmmoType color) {
        this.type = type;
        this.color = color;
    }

    public PowerupType getType() {
        return this.type;
    }

    public AmmoType getColor() {
        return this.color;
    }
}
