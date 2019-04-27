package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class StartGameSetupMessage extends Message {

    private GameCharacter master;

    public StartGameSetupMessage(GameCharacter master) {
        setMessageType(this.getClass());
        this.master = master;
    }

    public GameCharacter getCharacter() {
        return this.master;
    }
}
