package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.PowerupType;

public class PowerupCardSelectedEvent {

    private PowerupType powerup;
    private GameCharacter player;

    public PowerupCardSelectedEvent(PowerupType powerup, GameCharacter player) {
        this.powerup = powerup;
        this.player = player;
    }

    public PowerupType getPowerup() {}

    public GameCharacter getPlayer() {}
}
