package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoTile;

public class RemoveAmmoTileMessage {

    private int x;
    private int y;
    private AmmoTile tile;

    public RemoveAmmoTileMessage(int x, int y, AmmoTile tile) {
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public AmmoTile getTile() {
        return this.tile;
    }
}
