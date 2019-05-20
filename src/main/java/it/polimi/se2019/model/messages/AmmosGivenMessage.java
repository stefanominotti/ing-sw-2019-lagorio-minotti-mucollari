package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class AmmosGivenMessage extends Message {

    private GameCharacter character;
    private Map<AmmoType, Integer> ammos;

    public AmmosGivenMessage(GameCharacter character, Map<AmmoType, Integer> ammos) {
        setMessageType(this.getClass());
        this.ammos = ammos;
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }
}
