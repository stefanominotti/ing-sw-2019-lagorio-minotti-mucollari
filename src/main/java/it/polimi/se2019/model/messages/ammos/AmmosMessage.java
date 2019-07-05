package it.polimi.se2019.model.messages.ammos;

import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

import java.util.Map;

/**
 * Class for handling ammo message
 * @author stefanominotti
 */
public class AmmosMessage extends Message {

    private AmmosMessageType type;
    private GameCharacter character;
    private Map<AmmoType, Integer> ammos;

    /**
     * Class constructor, it builds an ammo message
     * @param type of the ammo message
     * @param character of which the message is referred to
     * @param ammos map with ammo and its quantity to be sent
     */
    public AmmosMessage(AmmosMessageType type, GameCharacter character, Map<AmmoType, Integer> ammos) {
        setMessageType(MessageType.AMMOS_MESSAGE);
        this.type = type;
        this.character = character;
        this.ammos = ammos;
    }

    /**
     * Gets the ammo of the message
     * @return Map with ammo and its quantity
     */
    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }

    /**
     * Gets the addressee of the message
     * @return the Game Character of the addressee
     */
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets the type of the ammo message
     * @return the type of the ammo message
     */
    public AmmosMessageType getType() {
        return this.type;
    }
}
