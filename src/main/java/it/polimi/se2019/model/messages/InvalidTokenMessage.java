package it.polimi.se2019.model.messages;

public class InvalidTokenMessage extends Message {

    public InvalidTokenMessage() {
        setMessageType(this.getClass());
    }
}
