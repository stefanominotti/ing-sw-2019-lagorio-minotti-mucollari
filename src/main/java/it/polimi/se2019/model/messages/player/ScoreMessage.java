package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

public class ScoreMessage extends PlayerMessage {

    private GameCharacter character;
    private int score;

    public ScoreMessage(GameCharacter character, int score) {
        super(PlayerMessageType.SCORE, character);
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }
}
