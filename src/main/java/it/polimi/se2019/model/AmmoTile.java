package it.polimi.se2019.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class for handling ammo tiles
 */
public class AmmoTile implements Serializable {

    private final boolean powerup;
    private final Map<AmmoType, Integer> ammos;

    /**
     * Class constructor, it builds an ammo tile
     * @param powerup true if it's got a powerup, else false
     * @param ammos Map with ammo type and its quantity on the ammo tile
     */
    public AmmoTile(boolean powerup, Map<AmmoType, Integer> ammos) {
        this.powerup = powerup;
        this.ammos = new EnumMap<>(AmmoType.class);
        this.ammos.putAll(ammos);
    }

    /**
     * Gets the ammos on the ammo tile
     * @return Map with ammo type and its quantity on the ammo tile
     */
    public Map<AmmoType, Integer> getAmmos() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.ammos);
        return returnMap;
    }

    /**
     * Knows if the ammo tile got a powerup
     * @return true if it's got a powerup, else false
     */
    public boolean hasPowerup() {
        return this.powerup;
    }
}
