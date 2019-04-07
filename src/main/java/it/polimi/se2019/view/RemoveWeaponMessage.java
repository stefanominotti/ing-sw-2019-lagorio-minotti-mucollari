package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

public class RemoveWeaponMessage {

    private GameCharacter player;
    private Weapon weapon;

    public RemoveWeaponMessage(GameCharacter player, Weapon weapon) {
        this.player = player;
        this.weapon = weapon;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }
}
