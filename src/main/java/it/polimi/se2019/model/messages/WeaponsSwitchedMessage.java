package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

public class WeaponsSwitchedMessage extends Message {

    private GameCharacter character;
    private Weapon oldWeapon;
    private Weapon newWeapon;

    public WeaponsSwitchedMessage(GameCharacter character, Weapon oldWeapon, Weapon newWeapon) {
        setMessageType(this.getClass());
        this.character = character;
        this.oldWeapon = oldWeapon;
        this.newWeapon = newWeapon;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Weapon getOldWeapon() {
        return this.oldWeapon;
    }

    public Weapon getNewWeapon() {
        return this.newWeapon;
    }
}
