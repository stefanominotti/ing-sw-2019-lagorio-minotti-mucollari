package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

public class PowerupDrawnMessage extends Message {

    private GameCharacter player;
    private Powerup powerup;

    public PowerupDrawnMessage(GameCharacter character, Powerup powerup) {
        setMessageType(this.getClass());
        this.player = character;
        this.powerup = powerup;
    }

    public Powerup getPowerup() {
        return this.powerup;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }
}
