package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;

public class PowerupPositionMessage extends Message {

    private Coordinates position;

    public PowerupPositionMessage(Coordinates position) {
        setMessageType(this.getClass());
        this.position = position;
    }

    public Coordinates getPosition() {
        return this.position;
    }
}
