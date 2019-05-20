package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Weapon;

public class WeaponPickupMessage extends Message {

    private Weapon weapon;

    public WeaponPickupMessage(Weapon weapon) {
        setMessageType(this.getClass());
        this.weapon = weapon;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }
}
