package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class PlayerCreatedMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;

    public PlayerCreatedMessage(GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }
}
