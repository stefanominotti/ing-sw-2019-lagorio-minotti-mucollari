package it.polimi.se2019.model.messages.weapon;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.playerassets.weapon.Weapon;

public class WeaponSwitchMessage extends WeaponMessage {

    private Weapon switchWeapon;

    public WeaponSwitchMessage(Weapon weapon, Weapon switchWeapon, GameCharacter character) {
        super(WeaponMessageType.SWITCH, weapon, character);
        this.switchWeapon = switchWeapon;
    }

    public Weapon getSwitchWeapon() {
        return this.switchWeapon;
    }
}
