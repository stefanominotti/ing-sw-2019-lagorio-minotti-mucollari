package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.Weapon;

import java.util.Map;

public class WeaponStoresRefilledMessage extends Message {

    Map<Coordinates, Weapon> weapons;

    public WeaponStoresRefilledMessage(Map<Coordinates, Weapon> weapons) {
        setMessageType(this.getClass());
        this.weapons = weapons;
    }

    public Map<Coordinates, Weapon> getWeapons() {
        return this.weapons;
    }
}
