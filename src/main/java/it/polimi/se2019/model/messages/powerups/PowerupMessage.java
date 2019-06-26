package it.polimi.se2019.model.messages.powerups;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for handling power up message
 */
public class PowerupMessage extends Message implements SingleReceiverMessage {

    private PowerupMessageType type;
    private GameCharacter character;
    private Powerup powerup;

    /**
     * Class constructor, it builds a powerup message
     * @param type of the powerup message
     * @param character who is handling the powerup
     * @param powerup handled
     */
    public PowerupMessage(PowerupMessageType type, GameCharacter character, Powerup powerup) {
        setMessageType(MessageType.POWERUP_MESSAGE);
        this.type = type;
        this.character = character;
        this.powerup = powerup;
    }

    /**
     * Gets type of the power up message
     * @return type of the power up message
     */
    public PowerupMessageType getType() {
        return this.type;
    }

    /**
     * Gets the character who is handling the powerup
     * @return
     */
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets the powerup handled
     * @return the powerup handled
     */
    public Powerup getPowerup() {
        return this.powerup;
    }
}