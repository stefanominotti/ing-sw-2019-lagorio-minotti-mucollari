package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

public class RequireWeaponSwitchMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private Weapon weapon;

    public RequireWeaponSwitchMessage(GameCharacter character, Weapon weapon) {
        setMessageType(this.getClass());
        this.character = character;
        this.weapon = weapon;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }
}
