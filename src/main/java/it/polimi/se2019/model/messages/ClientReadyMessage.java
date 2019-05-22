package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class ClientReadyMessage extends Message {

    private final GameCharacter character;
    private final String nickname;
    private final String token;

    public ClientReadyMessage(GameCharacter character, String nickname, String token) {
        setMessageType(this.getClass());
        this.character = character;
        this.nickname = nickname;
        this.token = token;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getToken() {
        return this.token;
    }
}
