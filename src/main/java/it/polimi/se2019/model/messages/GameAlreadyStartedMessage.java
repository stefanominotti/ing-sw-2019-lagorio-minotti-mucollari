package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class GameAlreadyStartedMessage extends Message {

    private final GameCharacter character;

    public GameAlreadyStartedMessage(GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
