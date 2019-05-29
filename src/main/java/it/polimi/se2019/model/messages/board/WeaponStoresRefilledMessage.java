package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.model.messages.Message;

import java.util.Map;

public class WeaponStoresRefilledMessage extends BoardMessage {

    Map<Coordinates, Weapon> weapons;

    public WeaponStoresRefilledMessage(Map<Coordinates, Weapon> weapons) {
        super(BoardMessageType.WEAPON_STORES_REFILLED);
        this.weapons = weapons;
    }

    public Map<Coordinates, Weapon> getWeapons() {
        return this.weapons;
    }
}
