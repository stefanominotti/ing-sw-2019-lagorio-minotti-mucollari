package it.polimi.se2019.model.messages.weapon;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

public class WeaponMessage extends Message implements SingleReceiverMessage {

    private WeaponMessageType type;
    private Weapon weapon;
    private GameCharacter character;

    public WeaponMessage(WeaponMessageType type, Weapon weapon, GameCharacter character) {
        setMessageType(MessageType.WEAPON_MESSAGE);
        this.type = type;
        this.weapon = weapon;
        this.character = character;
    }

    public WeaponMessageType getType() {
        return this.type;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
