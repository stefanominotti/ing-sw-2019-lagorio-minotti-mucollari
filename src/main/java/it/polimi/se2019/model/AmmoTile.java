package it.polimi.se2019.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class for handling Ammo Tiles
 */
public class AmmoTile implements Serializable {

    private final boolean powerup;
    private final Map<AmmoType, Integer> ammos;

    /**
     * Class constructor, it builds an Ammo Tile
     * @param powerup true, if it's got a Power Up; else false
     * @param ammos Map with Ammo Type and its number on the Ammo Tile
     */
    public AmmoTile(boolean powerup, Map<AmmoType, Integer> ammos) {
        this.powerup = powerup;
        this.ammos = new EnumMap<>(AmmoType.class);
        this.ammos.putAll(ammos);
    }

    /**
     * Gets the Ammos on the Ammo Tile
     * @return Map with Ammo Type and its number on the Ammo Tile
     */
    public Map<AmmoType, Integer> getAmmos() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.ammos);
        return returnMap;
    }

    /**
     * Knows if the Ammo Tile got a Power Up
     * @return true, if it's got a Power Up; else false
     */
    public boolean hasPowerup() {
        return this.powerup;
    }
}
