package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.PowerupType;

import java.util.List;

public class PowerupPositionsAvailableMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private  List<Coordinates> positions;
    private PowerupType powerup;

    public PowerupPositionsAvailableMessage(GameCharacter character, List<Coordinates> positions, PowerupType powerup) {
        setMessageType(this.getClass());
        this.character = character;
        this.positions = positions;
        this.powerup = powerup;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<Coordinates> getPositions() {
        return this.positions;
    }

    public PowerupType getPowerup() {
        return this.powerup;
    }
}
