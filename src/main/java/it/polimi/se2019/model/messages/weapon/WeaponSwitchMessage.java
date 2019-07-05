package it.polimi.se2019.model.messages.weapon;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.playerassets.weapons.Weapon;

/**
 * Class for handling weapon switch message
 */
public class WeaponSwitchMessage extends WeaponMessage {

    private Weapon switchWeapon;

    /**
     * Class constructor, it builds a weapon switch message used to notify a weapon switch
     * @param weapon left by the character
     * @param switchWeapon weapon picked up
     * @param character who is handling the weapons
     */
    public WeaponSwitchMessage(Weapon weapon, Weapon switchWeapon, GameCharacter character) {
        super(WeaponMessageType.SWITCH, weapon, character);
        this.switchWeapon = switchWeapon;
    }

    /**
     * Gets the weapon picked up
     * @return the weapon picked up
     */
    public Weapon getSwitchWeapon() {
        return this.switchWeapon;
    }
}
