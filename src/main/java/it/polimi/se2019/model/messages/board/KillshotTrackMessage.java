package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

/**
 * Class for handling killshot track message
 * @author stefanominotti
 */
public class KillshotTrackMessage extends BoardMessage {

    private int skulls;
    private List<GameCharacter> players;

    /**
     * Class constructor, it builds a killshot track message to signal a variation of killshot track
     * @param skulls number of the skull of the killshot track
     * @param players addressee of the message
     */
    public KillshotTrackMessage(int skulls, List<GameCharacter> players) {
        super(BoardMessageType.KILLSHOT_TRACK);
        this.skulls = skulls;
        this.players = players;
    }

    /**
     * Gets the skull number of the skull removed
     * @return the number of the skull of the message
     */
    public int getSkulls() {
        return this.skulls;
    }

    /**
     * Gets the list of characters which of marks was placed on the track
     * @return List of the Game Character which of marks was placed on the track
     */
    public List<GameCharacter> getPlayers() {
        return this.players;
    }
}
