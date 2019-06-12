package it.polimi.se2019.model.messages.turn;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

public class TurnContinuationMessage extends TurnMessage implements SingleReceiverMessage {

    private GameCharacter activePlayer;

    public TurnContinuationMessage(GameCharacter character, GameCharacter activePlayer) {
        super(TurnMessageType.CONTINUATION, character);
        this.activePlayer = activePlayer;
    }

    public GameCharacter getActivePlayer() {
        return this.activePlayer;
    }
}
