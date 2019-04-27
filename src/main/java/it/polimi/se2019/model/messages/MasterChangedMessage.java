package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class MasterChangedMessage extends Message {

    private GameCharacter master;

    public MasterChangedMessage(GameCharacter master) {
        setMessageType(this.getClass());
        this.master = master;
    }

    public GameCharacter getCharacter() {
        return this.master;
    }
}