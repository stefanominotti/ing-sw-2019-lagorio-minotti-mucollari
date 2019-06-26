package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;

/**
 * Class for handling player spawn message
 */
public class SpawnMessage extends PlayerMessage {

    private Coordinates coordinates;

    /**
     * Class constructor, it builds a spawn message
     * @param character who has spawn
     * @param coordinates where the characters has spawn
     */
    public SpawnMessage(GameCharacter character, Coordinates coordinates) {
        super(PlayerMessageType.SPAWNED, character);
        this.coordinates = coordinates;
    }

    /**
     * Gets the coordinates where the characters has spawn
     * @return the coordinates where the characters has spawn
     */
    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
