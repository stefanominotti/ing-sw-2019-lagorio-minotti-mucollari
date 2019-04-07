package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

public class GivePowerupMessage {

    private GameCharacter player;
    private Powerup powerup;

    public GivePowerupMessage(GameCharacter player, Powerup powerup) {
        this.player = player;
        this.powerup = powerup;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }

    public Powerup getPowerup() {
        return this.powerup;
    }
}
