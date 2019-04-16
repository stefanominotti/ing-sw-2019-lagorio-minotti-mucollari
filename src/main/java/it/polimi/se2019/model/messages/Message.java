package it.polimi.se2019.model.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    Class messageType;

    void setMessageType(Class type) {
        this.messageType = type;
    }

    public Class getMessageType() {
        return this.messageType;
    }

}
