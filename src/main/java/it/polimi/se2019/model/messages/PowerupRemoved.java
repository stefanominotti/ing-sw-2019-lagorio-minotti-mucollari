package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

public class PowerupRemoved extends Message {

    GameCharacter character;
    Powerup powerup;

    public PowerupRemoved(GameCharacter character, Powerup powerup) {
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
