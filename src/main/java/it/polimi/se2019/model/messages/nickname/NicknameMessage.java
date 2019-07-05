package it.polimi.se2019.model.messages.nickname;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

/**
 * Class for handling nickname message
 * @author stefanominotti
 */
public class NicknameMessage extends Message {

    private NicknameMessageType type;
    private String nickname;

    /**
     * Class constructor, it builds a nickname message for handling nickname chosen
     * @param type of the nickname message
     * @param nickname chosen
     */
    public NicknameMessage(NicknameMessageType type, String nickname) {
        setMessageType(MessageType.NICKNAME_MESSAGE);
        this.type = type;
        this.nickname = nickname;
    }

    /**
     * Class constructor, it builds a nickname message
     * @param type of the nickname message
     */
    public NicknameMessage(NicknameMessageType type) {
        setMessageType(MessageType.NICKNAME_MESSAGE);
        this.type = type;
    }

    /**
     * Gets the nickname message type
     * @return type of the nickname message
     */
    public NicknameMessageType getType() {
        return this.type;
    }

    /**
     * Gets the nickname chosen
     * @return the nickname chosen
     */
    public String getNickname() {
        return this.nickname;
    }
}
