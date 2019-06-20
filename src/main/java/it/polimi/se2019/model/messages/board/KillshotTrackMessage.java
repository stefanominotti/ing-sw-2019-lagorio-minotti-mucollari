package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class KillshotTrackMessage extends BoardMessage {

    private int skulls;
    private List<GameCharacter> players;

    public KillshotTrackMessage(int skulls, List<GameCharacter> players) {
        super(BoardMessageType.KILLSHOT_TRACK);
        this.skulls = skulls;
        this.players = players;
    }

    public int getSkulls() {
        return this.skulls;
    }

    public List<GameCharacter> getPlayers() {
        return this.players;
    }
}
