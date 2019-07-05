package it.polimi.se2019.model.messages.weapon;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for handling weapon message
 */
public class WeaponMessage extends Message implements SingleReceiverMessage {

    private WeaponMessageType type;
    private Weapon weapon;
    private GameCharacter character;

    /**
     * Class constructor, it builds a weapon message used to notify an action that involves weapons
     * @param type of the message
     * @param weapon involved
     * @param character who is handling the weapon
     */
    public WeaponMessage(WeaponMessageType type, Weapon weapon, GameCharacter character) {
        setMessageType(MessageType.WEAPON_MESSAGE);
        this.type = type;
        this.weapon = weapon;
        this.character = character;
    }

    /**
     * Gets the type of the message
     * @return the type of the message
     */
    public WeaponMessageType getType() {
        return this.type;
    }

    /** Gets the weapon involved
     * @return the weapon involved
     */
    public Weapon getWeapon() {
        return this.weapon;
    }

    /**
     * Gets the player who is handling the weapon
     * @return the player who is handling the weapon
     */
    public GameCharacter getCharacter() {
        return this.character;
    }
}
