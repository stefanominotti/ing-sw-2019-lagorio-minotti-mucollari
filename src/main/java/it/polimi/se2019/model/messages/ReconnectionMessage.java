package it.polimi.se2019.model.messages;

public class ReconnectionMessage extends Message {

    String token;

    public ReconnectionMessage() {
        setMessageType(this.getClass());
    }

    public ReconnectionMessage(String token) {
        setMessageType(this.getClass());
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
