package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

public class RechargeWeaponMessage extends Message {

    private GameCharacter character;
    private Weapon weapon;

    public RechargeWeaponMessage(Weapon weapon, GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
        this.weapon = weapon;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }
}
