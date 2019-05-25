package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

import java.util.List;

public class RequirePowerupUseMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private List<Powerup> powerups;

    public RequirePowerupUseMessage(GameCharacter character, List<Powerup> powerups) {
        setMessageType(this.getClass());
        this.powerups = powerups;
        this.character = character;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<Powerup> getPowerups() {
        return this.powerups;
    }
}
