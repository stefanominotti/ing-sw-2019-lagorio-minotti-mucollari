package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

/**
 * Interface for handling messages that can be sent to only one player
 */
public interface SingleReceiverMessage {

    /**
     * Gets the addressee of the message
     * @return the addresse of the message
     */
    GameCharacter getCharacter();
}
