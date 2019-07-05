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
     * Class constructor, it builds a killshot track message
     * @param skulls number of the skull of the killshot track
     * @param players addressee of the message
     */
    public KillshotTrackMessage(int skulls, List<GameCharacter> players) {
        super(BoardMessageType.KILLSHOT_TRACK);
        this.skulls = skulls;
        this.players = players;
    }

    /**
     * Gets the skull number of the message
     * @return the number of the skull of the message
     */
    public int getSkulls() {
        return this.skulls;
    }

    /**
     * Gets the addressee characters of the message
     * @return List of the Game Character addressee of the message
     */
    public List<GameCharacter> getPlayers() {
        return this.players;
    }
}
