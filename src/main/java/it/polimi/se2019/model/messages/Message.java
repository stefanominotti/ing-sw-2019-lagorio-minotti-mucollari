package it.polimi.se2019.model.messages;

import java.io.Serializable;

public class Message implements Serializable {

    private MessageType messageType;

    public void setMessageType(MessageType type) {
        this.messageType = type;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }
}
