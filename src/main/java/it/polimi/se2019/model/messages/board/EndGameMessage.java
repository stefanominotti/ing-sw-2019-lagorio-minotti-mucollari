package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

/**
 * Class for handling End Game Message, it is used to send that game has finished
 * @author stefanominotti
 */
public class EndGameMessage extends BoardMessage {

    private Map<GameCharacter, Integer> ranking;

    /**
     * Class constructor, it builds and end game message
     * @param ranking Map with characters and their relative points raised during the game
     */
    public EndGameMessage(Map<GameCharacter, Integer> ranking) {
        super(BoardMessageType.GAME_FINISHED);
        this.ranking = ranking;
    }

    /**
     * Gets the game ranking
     * @return Map with characters and their relative points raised during the game
     */
    public Map<GameCharacter, Integer> getRanking() {
        return this.ranking;
    }
}
