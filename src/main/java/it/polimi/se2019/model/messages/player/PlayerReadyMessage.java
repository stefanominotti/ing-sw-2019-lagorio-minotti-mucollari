package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;

public class PlayerReadyMessage extends PlayerMessage {

    private String nickname;

    public PlayerReadyMessage(GameCharacter character, String nickname) {
        super(PlayerMessageType.READY, character);
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }
}
