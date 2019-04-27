package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class SkullsSettedMessage extends Message implements SingleReceiverMessage{

    private GameCharacter master;

    public SkullsSettedMessage(GameCharacter master) {
        setMessageType(this.getClass());
        this.master = master;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.master;
    }
}
