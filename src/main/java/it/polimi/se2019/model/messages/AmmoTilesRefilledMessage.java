package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoTile;
import it.polimi.se2019.model.Coordinates;

import java.util.Map;

public class AmmoTilesRefilledMessage extends Message {

    private Map<Coordinates, AmmoTile> tiles;

    public AmmoTilesRefilledMessage(Map<Coordinates, AmmoTile> tiles) {
        setMessageType(this.getClass());
        this.tiles = tiles;
    }

    public Map<Coordinates, AmmoTile> getTiles() {
        return this.tiles;
    }
}
