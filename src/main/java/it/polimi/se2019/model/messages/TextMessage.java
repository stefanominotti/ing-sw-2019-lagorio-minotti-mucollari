package it.polimi.se2019.model.messages;

public class TextMessage extends Message {

    private final String text;

    public TextMessage(String text) {
        setMessageType(this.getClass());
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
