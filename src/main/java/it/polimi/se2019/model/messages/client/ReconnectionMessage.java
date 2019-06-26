package it.polimi.se2019.model.messages.client;

/**
 * Class for handling reconnection message
 */
public class ReconnectionMessage extends ClientMessage {

    private String token;

    /**
     * Class constructor, it builds a reconnection message
     */
    public ReconnectionMessage() {
        super(ClientMessageType.CLIENT_RECONNECTION);
    }

    /**
     * Class constructor, it builds a reconnection message for handling the token sent by a client which is trying
     * to reconnect
     */
    public ReconnectionMessage(String token) {
        super(ClientMessageType.CLIENT_RECONNECTION);
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
