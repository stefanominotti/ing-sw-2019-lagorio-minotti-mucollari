package it.polimi.se2019.model.messages.turn;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for handling turn continuation message
 */
public class TurnContinuationMessage extends TurnMessage implements SingleReceiverMessage {

    private GameCharacter activePlayer;

    /**
     * Class constructor, it builds a turn continuation message used when a player continues his turn after powerup
     * usage
     * @param character which the message is addressed to
     * @param activePlayer of the turn
     */
    public TurnContinuationMessage(GameCharacter character, GameCharacter activePlayer) {
        super(TurnMessageType.CONTINUATION, character);
        this.activePlayer = activePlayer;
    }

    /**
     * Gets the active player of the turn
     * @return the active player of the turn
     */
    public GameCharacter getActivePlayer() {
        return this.activePlayer;
    }
}
