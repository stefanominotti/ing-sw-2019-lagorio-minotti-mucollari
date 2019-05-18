package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class ScoreMessage extends Message {

    private GameCharacter character;
    private int score;

    public ScoreMessage(GameCharacter character, int score) {
        setMessageType(this.getClass());
        this.character = character;
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }
}
