package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling Final Frenzy message
 * @author stefanominotti
 */
public class FinalFrenzyMessage extends PlayerMessage {

    private boolean beforeFirstPlayer;

    /**
     * Class constructor, it builds a Final Frenzy message
     * @param character who has activated the Final Frenzy
     * @param beforeFirstPlayer true if the character plays before the first player in the current turn, else false
     */
    public FinalFrenzyMessage(GameCharacter character, boolean beforeFirstPlayer) {
        super(PlayerMessageType.FRENZY, character);
        this.beforeFirstPlayer = beforeFirstPlayer;
    }

    /**
     * Knows if the character plays before the first player in the current turn
     * @return true if it is, else false
     */
    public boolean isBeforeFirstPlayer() {
        return this.beforeFirstPlayer;
    }
}