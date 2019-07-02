package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling score message
 */
public class ScoreMessage extends PlayerMessage {

    private int score;
    private ScoreMotivation motivation;
    private GameCharacter killedCharacter;

    /**
     * Class constructor, it builds a score message
     * @param character who is raising score
     * @param score amount to be raised
     */
    public ScoreMessage(GameCharacter character, int score, ScoreMotivation motivation, GameCharacter killedCharacter) {
        super(PlayerMessageType.SCORE, character);
        this.score = score;
        this.motivation = motivation;
        this.killedCharacter = killedCharacter;
    }

    /**
     * Gets the score to be raised
     * @return the score to be raised
     */
    public int getScore() {
        return this.score;
    }


    /**
     * Gets the motivation of score raising
     * @return the motivation of score raising
     */
    public ScoreMotivation getMotivation() {
        return this.motivation;
    }

    /**
     * Gets killed character
     * @return killed character
     */
    public GameCharacter getKilledCharacter() {
        return killedCharacter;
    }
}