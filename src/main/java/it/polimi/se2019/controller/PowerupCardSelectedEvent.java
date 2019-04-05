package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.PowerupType;

public class PowerupCardSelectedEvent {

    private PowerupType powerup;
    private Player player;

    public PowerupCardSelectedEvent(PowerupType powerup, Player player) {
        this.powerup = powerup;
        this.player = player;
    }

    public PowerupType getPowerup() {}

    public Player getPlayer() {}
}
