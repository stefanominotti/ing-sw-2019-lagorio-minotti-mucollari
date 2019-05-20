package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class PlayerCreatedMessage extends Message {

    private GameCharacter character;
    private String nickname;
    private Map<GameCharacter, String> otherPlayers;

    public PlayerCreatedMessage(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        setMessageType(this.getClass());
        this.character = character;
        this.nickname = nickname;
        this.otherPlayers = otherPlayers;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public String getNickname() {
        return this.nickname;
    }

    public Map<GameCharacter, String> getOtherPlayers() {
        return this.otherPlayers;
    }
}
