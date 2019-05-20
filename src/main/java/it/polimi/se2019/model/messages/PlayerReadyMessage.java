package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class PlayerReadyMessage extends Message {

    private GameCharacter character;
    private String nickname;

    public PlayerReadyMessage(GameCharacter character, String nickname) {
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
