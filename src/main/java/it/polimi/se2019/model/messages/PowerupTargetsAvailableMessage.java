package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.PowerupType;

import java.util.List;

public class PowerupTargetsAvailableMessage extends Message implements SingleReceiverMessage {
    private GameCharacter character;
    private  List<GameCharacter> targets;
    private PowerupType powerup;

    public PowerupTargetsAvailableMessage(GameCharacter character, List<GameCharacter> targets, PowerupType powerup) {
        setMessageType(this.getClass());
        this.character = character;
        this.targets = targets;
        this.powerup = powerup;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<GameCharacter> getTargets() {
        return this.targets;
    }

    public PowerupType getPowerup() {
        return this.powerup;
    }
}
