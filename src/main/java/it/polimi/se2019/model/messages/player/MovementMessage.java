package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling movement message
 */
public class MovementMessage extends PlayerMessage {

    private Coordinates coordinates;

    public MovementMessage(GameCharacter character, Coordinates coordinates) {
        super(PlayerMessageType.MOVE, character);
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
