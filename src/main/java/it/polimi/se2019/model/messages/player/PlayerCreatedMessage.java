package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;

import java.util.Map;

public class PlayerCreatedMessage extends PlayerMessage {

    private String nickname;
    private Map<GameCharacter, String> otherPlayers;

    public PlayerCreatedMessage(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        super(PlayerMessageType.CREATED, character);
        this.nickname = nickname;
        this.otherPlayers = otherPlayers;
    }

    public String getNickname() {
        return this.nickname;
    }

    public Map<GameCharacter, String> getOtherPlayers() {
        return this.otherPlayers;
    }
}
