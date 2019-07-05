package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling movement message
 * @author eknidmucollari
 */
public class MovementMessage extends PlayerMessage {

    private Coordinates coordinates;

    /**
     * Class constructor, it builds a movement message
     * @param character which is going to move
     * @param coordinates where he's going to move
     */
    public MovementMessage(GameCharacter character, Coordinates coordinates) {
        super(PlayerMessageType.MOVE, character);
        this.coordinates = coordinates;
    }

    /**
     * Gets the coordinates of the message
     * @return coordinate of the message
     */
    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
