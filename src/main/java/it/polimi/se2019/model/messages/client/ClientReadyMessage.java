package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;

public class ClientReadyMessage extends ClientMessage {

    private String nickname;
    private String token;

    public ClientReadyMessage(GameCharacter character, String nickname, String token) {
        super(ClientMessageType.READY, character);
        this.nickname = nickname;
        this.token = token;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getToken() {
        return this.token;
    }
}
