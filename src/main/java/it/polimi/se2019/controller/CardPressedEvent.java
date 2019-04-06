package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;

public class CardPressedEvent {

    private GameCharacter player;
    private Weapon weapon;
    private Powerup powerup;

    public CardPressedEvent(GameCharacter player, Weapon weapon, Powerup powerup) {
        this.player = player;
        this.weapon = weapon;
        this.powerup = powerup;
    }

    public GameCharacter getPlayer() {}

    public Weapon getWeapon() {}

    public Powerup getPowerup() {}
}
