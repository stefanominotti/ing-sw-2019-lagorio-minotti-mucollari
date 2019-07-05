package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling player spawn message
 * @author stefanominotti
 */
public class SpawnMessage extends PlayerMessage {

    private Coordinates coordinates;

    /**
     * Class constructor, it builds a spawn message
     * @param character who has spawned
     * @param coordinates where the characters has spawned
     */
    public SpawnMessage(GameCharacter character, Coordinates coordinates) {
        super(PlayerMessageType.SPAWNED, character);
        this.coordinates = coordinates;
    }

    /**
     * Gets the coordinates where the characters has spawned
     * @return the coordinates where the characters has spawned
     */
    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
