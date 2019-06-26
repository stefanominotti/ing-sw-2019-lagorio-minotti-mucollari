package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class EndGameMessage extends BoardMessage {

    private Map<GameCharacter, Integer> ranking;

    public EndGameMessage(Map<GameCharacter, Integer> ranking) {
        super(BoardMessageType.GAME_FINISHED);
        this.ranking = ranking;
    }

    public Map<GameCharacter, Integer> getRanking() {
        return this.ranking;
    }
}
