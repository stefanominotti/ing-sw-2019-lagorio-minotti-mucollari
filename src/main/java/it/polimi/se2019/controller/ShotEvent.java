package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

public class ShotEvent {
    private GameCharacter player;
    private Weapon weapon;

    public ShotEvent(GameCharacter player, Weapon weapon) {
        this.player = player;
        this.weapon = weapon;
    }

    public GameCharacter getPlayer() {
        return null;
    }

    public Weapon getWeapon() {
        return null;
    }
}
