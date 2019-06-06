package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

public class ClientMessagePair {

    private VirtualClientInterface client;
    private Message message;

    public ClientMessagePair(VirtualClientInterface client, Message message) {
        this.client = client;
        this.message = message;
    }

    public VirtualClientInterface getClient() {
        return this.client;
    }

    public Message getMessage() {
        return this.message;
    }
}
