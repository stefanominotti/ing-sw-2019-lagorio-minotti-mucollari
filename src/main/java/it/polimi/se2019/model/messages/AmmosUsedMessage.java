package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class AmmosUsedMessage extends Message {

    private GameCharacter character;
    private Map<AmmoType, Integer> ammos;

    public AmmosUsedMessage(GameCharacter character, Map<AmmoType, Integer> usedAmmos) {
        setMessageType(this.getClass());
        this.character = character;
        this.ammos = usedAmmos;
    }

    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
