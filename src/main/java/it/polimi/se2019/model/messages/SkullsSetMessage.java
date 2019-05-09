package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class SkullsSetMessage extends Message implements SingleReceiverMessage{

    private GameCharacter master;

    public SkullsSetMessage(GameCharacter master) {
        setMessageType(this.getClass());
        this.master = master;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.master;
    }
}
