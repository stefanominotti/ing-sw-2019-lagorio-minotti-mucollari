package it.polimi.se2019.model.messages;

public class NicknameRecconnectingMessage extends Message {

    private final String nickname;

    public NicknameRecconnectingMessage(String input) {
        setMessageType(this.getClass());
        this.nickname = input;
    }

    public String getNickname() {
        return nickname;
    }
}
