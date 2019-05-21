package it.polimi.se2019.model.messages;

public class NotAvailableNameMessage extends Message {

    private final String nickname;
    private final boolean isPresent;

    public NotAvailableNameMessage(String nickname, boolean isPresent) {
        setMessageType(this.getClass());
        this.nickname = nickname;
        this.isPresent = isPresent;
    }

    public String getNickname() {
        return this.nickname;
    }

    public Boolean isPresent() {
        return this.isPresent;
    }
}
