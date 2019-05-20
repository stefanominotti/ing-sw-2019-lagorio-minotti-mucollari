package it.polimi.se2019.model.messages;

public class NotFoundNameMessage extends Message {

    private final String nickname;

    public NotFoundNameMessage(String nickname) {
        setMessageType(this.getClass());
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }
}
