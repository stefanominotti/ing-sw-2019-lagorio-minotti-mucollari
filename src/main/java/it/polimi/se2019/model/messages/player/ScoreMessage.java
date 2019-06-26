package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling score message
 */
public class ScoreMessage extends PlayerMessage {

    private GameCharacter character;
    private int score;

    /**
     * Class constructor, it builds a score message
     * @param character who is raising score
     * @param score amount to be raised
     */
    public ScoreMessage(GameCharacter character, int score) {
        super(PlayerMessageType.SCORE, character);
        this.score = score;
    }

    /**
     * Gets the score to be raised
     * @return the score to be raised
     */
    public int getScore() {
        return this.score;
    }
}