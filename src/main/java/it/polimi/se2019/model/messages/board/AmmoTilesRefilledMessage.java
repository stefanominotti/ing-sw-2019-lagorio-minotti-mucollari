package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.playerassets.AmmoTile;
import it.polimi.se2019.model.arena.Coordinates;

import java.util.Map;

/**
 * Class for handling ammo tiles refill message
 * @author stefanominotti
 */
public class AmmoTilesRefilledMessage extends BoardMessage {

    private Map<Coordinates, AmmoTile> tiles;

    /**
     * Class constructor, it builds an ammo tiles refill message
     * @param tiles map with coordinates and ammo tiles to refill
     */
    public AmmoTilesRefilledMessage(Map<Coordinates, AmmoTile> tiles) {
        super(BoardMessageType.AMMO_TILES_REFILLED);
        this.tiles = tiles;
    }

    /**
     * Gets the ammo tiles and their coordinates of the message
     * @return map with ammo tiles and their quantities
     */
    public Map<Coordinates, AmmoTile> getTiles() {
        return this.tiles;
    }
}
