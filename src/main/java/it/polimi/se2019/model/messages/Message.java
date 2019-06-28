package it.polimi.se2019.model.messages;

import java.io.Serializable;

/**
 * Class for handling message
 */
public class Message implements Serializable {

    private MessageType messageType;

    /**
     * Sets the message type
     * @param type to be set to the message
     */
    public void setMessageType(MessageType type) {
        this.messageType = type;
    }

    /**
     * Gets the message type
     * @return type of the message
     */
    public MessageType getMessageType() {
        return this.messageType;
    }
}
