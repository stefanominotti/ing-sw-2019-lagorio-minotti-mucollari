package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

/**
 * Class for handling client message pair, used to pair client-message
 */
public class ClientMessagePair {

    private VirtualClientInterface client;
    private Message message;

    /**
     * Class constructor, it builds a client massage pair
     * @param client the client
     * @param message the message to be paired with the client
     */
    ClientMessagePair(VirtualClientInterface client, Message message) {
        this.client = client;
        this.message = message;
    }

    /**
     * Gets the client
     * @return client as a virtual client interface
     */
    public VirtualClientInterface getClient() {
        return this.client;
    }

    /**
     * Gets the message for the client
     * @return the message
     */
    public Message getMessage() {
        return this.message;
    }
}
