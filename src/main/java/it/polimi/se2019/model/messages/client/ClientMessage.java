package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for handling client message
 * @author stefanominotti
 */
public class ClientMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private ClientMessageType type;

    /**
     * Class constructor, it builds a client message with character handling
     * @param type of the client message
     * @param character addressee if the message has to be sent to a single client
     */
    public ClientMessage(ClientMessageType type, GameCharacter character) {
        setMessageType(MessageType.CLIENT_MESSAGE);
        this.type = type;
        this.character = character;
    }

    /**
     * Class constructor, it builds a client message
     * @param type of the client message
     */
    public ClientMessage(ClientMessageType type) {
        setMessageType(MessageType.CLIENT_MESSAGE);
        this.type = type;
        this.character = null;
    }

    /**
     * Gets the addressee character of the message
     * @return the addressee of the message
     */
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets client message type
     * @return type of the client message
     */
    public ClientMessageType getType() {
        return this.type;
    }
}
