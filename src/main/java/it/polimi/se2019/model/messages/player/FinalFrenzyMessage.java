package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

public class FinalFrenzyMessage extends PlayerMessage {

    private boolean beforeFirstPlayer;

    public FinalFrenzyMessage(GameCharacter character, boolean beforeFirstPlayer) {
        super(PlayerMessageType.FRENZY, character);
        this.beforeFirstPlayer = beforeFirstPlayer;
    }

    public boolean isBeforeFirstPlayer() {
        return this.beforeFirstPlayer;
    }
}
