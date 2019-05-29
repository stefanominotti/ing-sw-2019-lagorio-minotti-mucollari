package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.AmmoTile;
import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.messages.Message;

import java.util.Map;

public class AmmoTilesRefilledMessage extends BoardMessage {

    private Map<Coordinates, AmmoTile> tiles;

    public AmmoTilesRefilledMessage(Map<Coordinates, AmmoTile> tiles) {
        super(BoardMessageType.AMMO_TILES_REFILLED);
        this.tiles = tiles;
    }

    public Map<Coordinates, AmmoTile> getTiles() {
        return this.tiles;
    }
}
