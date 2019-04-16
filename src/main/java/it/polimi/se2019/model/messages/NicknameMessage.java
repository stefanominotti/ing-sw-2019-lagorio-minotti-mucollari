package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class NicknameMessage extends Message {

    private final GameCharacter character;
    private final String nickname;

    public NicknameMessage(GameCharacter character, String nickname) {
        setMessageType(this.getClass());
        this.character = character;
        this.nickname = nickname;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public String getNickname() {
        return this.nickname;
    }
}
