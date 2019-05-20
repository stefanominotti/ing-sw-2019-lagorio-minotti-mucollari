package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class ClientReconnectedMessage extends Message {

    private final GameCharacter character;

    public ClientReconnectedMessage(GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
    }
}
