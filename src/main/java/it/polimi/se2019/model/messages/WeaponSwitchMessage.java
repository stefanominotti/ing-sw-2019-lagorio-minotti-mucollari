package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Weapon;

public class WeaponSwitchMessage extends Message {

    private Weapon weapon;

    public WeaponSwitchMessage(Weapon weapon) {
        setMessageType(this.getClass());
        this.weapon = weapon;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }
}
