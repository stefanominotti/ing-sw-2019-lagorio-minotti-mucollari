package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;

public class PickupMessage extends Message {

    private GameCharacter character;
    private Coordinates coordinates;

    public PickupMessage(GameCharacter character, Coordinates coordinates) {
        setMessageType(this.getClass());
        this.character = character;
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
