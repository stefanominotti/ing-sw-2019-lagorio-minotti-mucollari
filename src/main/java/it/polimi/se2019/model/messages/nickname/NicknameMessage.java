package it.polimi.se2019.model.messages.nickname;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

public class NicknameMessage extends Message {

    private NicknameMessageType type;
    private String nickname;

    public NicknameMessage(NicknameMessageType type, String nickname) {
        setMessageType(MessageType.NICKNAME_MESSAGE);
        this.type = type;
        this.nickname = nickname;
    }

    public NicknameMessage(NicknameMessageType type) {
        setMessageType(MessageType.NICKNAME_MESSAGE);
        this.type = type;
    }

    public NicknameMessageType getType() {
        return this.type;
    }

    public String getNickname() {
        return this.nickname;
    }
}
