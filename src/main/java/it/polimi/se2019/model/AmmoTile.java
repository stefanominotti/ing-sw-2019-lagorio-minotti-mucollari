package it.polimi.se2019.model;

import java.util.EnumMap;
import java.util.Map;

public class AmmoTile {

    private final boolean powerup;
    private final Map<AmmoType, Integer> ammos;

    AmmoTile(boolean powerup, Map<AmmoType, Integer> ammos) {
        this.powerup = powerup;
        this.ammos = new EnumMap<>(AmmoType.class);
        this.ammos.putAll(ammos);
    }

    public Map<AmmoType, Integer> getAmmos() {
        return new EnumMap<>(AmmoType.class);
    }

    public boolean hasPowerup() {
        return this.powerup;
    }
}
