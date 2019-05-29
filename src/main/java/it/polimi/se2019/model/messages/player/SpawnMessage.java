package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;

public class SpawnMessage extends PlayerMessage {

    private Coordinates coordinates;

    public SpawnMessage(GameCharacter character, Coordinates coordinates) {
        super(PlayerMessageType.SPAWNED, character);
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
