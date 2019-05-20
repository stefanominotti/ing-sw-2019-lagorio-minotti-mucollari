package it.polimi.se2019.model.messages;

public class NicknameMessage extends Message {

    private String nickname;

    public NicknameMessage(String nickname) {
        setMessageType(this.getClass());
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }
}
