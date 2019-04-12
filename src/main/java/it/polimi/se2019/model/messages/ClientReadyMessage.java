package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class ClientReadyMessage extends Message {

    private GameCharacter character;

    public ClientReadyMessage(GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
