package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

public class PowerupSelectedMessage extends Message {

    Powerup powerup;

    public PowerupSelectedMessage(Powerup powerup) {
        setMessageType(this.getClass());
        this.powerup = powerup;
    }

    public Powerup getPowerup() {
        return powerup;
    }
}
