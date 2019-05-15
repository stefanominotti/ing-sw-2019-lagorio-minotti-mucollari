package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Player;

public class PlayerSpawnedMessage extends Message {

    GameCharacter character;
    Coordinates coordinates;

    public PlayerSpawnedMessage(GameCharacter character, Coordinates coordinates) {
        setMessageType(this.getClass());
        this.character = character;
        this.coordinates = coordinates;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
