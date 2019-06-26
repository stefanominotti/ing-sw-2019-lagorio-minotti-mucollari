package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for handling player message
 */
public class PlayerMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private PlayerMessageType type;

    /**
     * Class constructor, it builds a player message
     * @param type of the player message
     * @param character of the message
     */
    public PlayerMessage(PlayerMessageType type, GameCharacter character) {
        setMessageType(MessageType.PLAYER_MESSAGE);
        this.type = type;
        this.character = character;
    }

    /**
     * Gets the character of the message
     * @return the game character of the message
     */
    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets type of the player message
     * @return type of the player message
     */
    public PlayerMessageType getType() {
        return this.type;
    }
}
