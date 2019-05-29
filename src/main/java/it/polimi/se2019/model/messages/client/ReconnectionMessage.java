package it.polimi.se2019.model.messages.client;

public class ReconnectionMessage extends ClientMessage {

    private String token;

    public ReconnectionMessage() {
        super(ClientMessageType.CLIENT_RECONNECTION);
    }

    public ReconnectionMessage(String token) {
        super(ClientMessageType.CLIENT_RECONNECTION);
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
