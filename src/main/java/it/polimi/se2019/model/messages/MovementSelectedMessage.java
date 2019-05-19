package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;

public class MovementSelectedMessage extends Message {

    private Coordinates coordinates;

    public MovementSelectedMessage(Coordinates coordinates) {
        setMessageType(this.getClass());
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
