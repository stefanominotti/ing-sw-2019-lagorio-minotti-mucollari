package it.polimi.se2019.model.messages.ammos;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

import java.util.Map;

public class AmmosMessage extends Message {

    private AmmosMessageType type;
    private GameCharacter character;
    private Map<AmmoType, Integer> ammos;

    public AmmosMessage(AmmosMessageType type, GameCharacter character, Map<AmmoType, Integer> ammos) {
        setMessageType(MessageType.AMMOS_MESSAGE);
        this.type = type;
        this.character = character;
        this.ammos = ammos;
    }

    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public AmmosMessageType getType() {
        return this.type;
    }
}
