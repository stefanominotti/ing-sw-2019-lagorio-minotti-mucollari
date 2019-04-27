package it.polimi.se2019.model.messages;

import java.io.Serializable;

public class Message implements Serializable {

    private Class messageType;

    void setMessageType(Class type) {
        this.messageType = type;
    }

    public Class getMessageType() {
        return this.messageType;
    }
}
