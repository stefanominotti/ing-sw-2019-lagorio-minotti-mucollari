package it.polimi.se2019.model.messages.powerups;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

public class PowerupMessage extends Message implements SingleReceiverMessage {

    private PowerupMessageType type;
    private GameCharacter character;
    private Powerup powerup;

    public PowerupMessage(PowerupMessageType type, GameCharacter character, Powerup powerup) {
        setMessageType(MessageType.POWERUP_MESSAGE);
        this.type = type;
        this.character = character;
        this.powerup = powerup;
    }

    public PowerupMessageType getType() {
        return this.type;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Powerup getPowerup() {
        return this.powerup;
    }
}
