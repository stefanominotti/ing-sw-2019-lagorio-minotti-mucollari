package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

/**
 *
 */
public class MarksToDamagesMessage extends PlayerMessage {

    private GameCharacter attacker;

    /**
     * Class constructor, it builds a marks to damage message
     * @param player whose marks need to be converted
     * @param attacker of which marks are converted
     */
    public MarksToDamagesMessage(GameCharacter player, GameCharacter attacker) {
        super(PlayerMessageType.MARKS_TO_DAMAGES, player);
        this.attacker = attacker;
    }

    /**
     * Gets the game character of the attacker
     * @return the game character of the attacker
     */
    public GameCharacter getAttacker() {
        return this.attacker;
    }
}
