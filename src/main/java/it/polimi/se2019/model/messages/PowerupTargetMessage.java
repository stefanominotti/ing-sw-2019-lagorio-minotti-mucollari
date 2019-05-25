package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class PowerupTargetMessage extends Message {

    private GameCharacter target;

    public PowerupTargetMessage(GameCharacter target) {
        setMessageType(this.getClass());
        this.target = target;
    }

    public GameCharacter getTarget() {
        return this.target;
    }
}
