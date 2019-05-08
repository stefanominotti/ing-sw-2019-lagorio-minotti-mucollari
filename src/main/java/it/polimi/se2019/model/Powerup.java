package it.polimi.se2019.model;

public class Powerup {

    private PowerupType type;
    private AmmoType color;

    Powerup(PowerupType type, AmmoType color) {
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
