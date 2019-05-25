package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

public class UsePowerupMessage extends Message {

    private Powerup powerup;
    private GameCharacter player;

    public UsePowerupMessage(GameCharacter player, Powerup powerup) {
        setMessageType(this.getClass());
        this.powerup = powerup;
        this.player = player;
    }

    public Powerup getPowerup() {
        return this.powerup;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }
}
