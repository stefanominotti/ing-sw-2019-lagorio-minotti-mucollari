package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.playerassets.weapons.Weapon;

import java.util.Map;

/**
 * Class for handling weapon store refilled message
 * @author stefanominotti
 */
public class WeaponStoresRefilledMessage extends BoardMessage {

    private Map<Coordinates, Weapon> weapons;

    /**
     * Class constructor, it builds a weapon store refilled message
     * @param weapons and the coordinates of the square where they are placed
     */
    public WeaponStoresRefilledMessage(Map<Coordinates, Weapon> weapons) {
        super(BoardMessageType.WEAPON_STORES_REFILLED);
        this.weapons = weapons;
    }

    /**
     * Gets the weapons refilled and their positions
     * @return Map with weapon and the coordinates of the square where it is be placed
     */
    public Map<Coordinates, Weapon> getWeapons() {
        return this.weapons;
    }
}