package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling client disconnected message
 */
public class ClientDisconnectedMessage extends ClientMessage {

    private boolean reconnectionAllowed;

    /**
     * Class constructor, it builds a client disconnected message to inform that a character has disconnected
     * @param character who has disconnected
     */
    public ClientDisconnectedMessage(GameCharacter character) {
        super(ClientMessageType.DISCONNECTED, character);
    }

    /**
     * Class constructor, it builds a client disconnected message to inform that a character has disconnected with
     * reconnection handling
     * @param character who has disconnected
     * @param reconnectionAllowed true if reconnection is allowed, else false
     */
    public ClientDisconnectedMessage(GameCharacter character, boolean reconnectionAllowed) {
        super(ClientMessageType.DISCONNECTED, character);
        this.reconnectionAllowed = reconnectionAllowed;
    }

    /**
     * Knows if the the reconnection is allowed
     * @return true if it is, else false
     */
    public boolean isReconnectionAllowed() {
        return this.reconnectionAllowed;
    }
}