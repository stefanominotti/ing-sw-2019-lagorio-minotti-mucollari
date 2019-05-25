package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

public class PowerupRemovedMessage extends Message {

    GameCharacter character;
    Powerup powerup;

    public PowerupRemovedMessage(GameCharacter character, Powerup powerup) {
        setMessageType(this.getClass());
        this.character = character;
        this.powerup = powerup;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Powerup getPowerup() {
        return powerup;
    }
}
