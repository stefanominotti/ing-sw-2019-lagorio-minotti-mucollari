package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class EndTurnMessage extends Message {

    private GameCharacter character;

    public EndTurnMessage(GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
