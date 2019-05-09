package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoTile;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.RoomColor;

import java.util.ArrayList;
import java.util.List;

public class SquareView {

    private int x;
    private int y;
    private boolean spawn;
    private RoomColor color;
    private List<GameCharacter> activePlayers;
    private AmmoTile availableAmmoTile;

    SquareView(int x, int y, RoomColor color, boolean spawn) {
        this.x = x;
        this.y = y;
        this.spawn = spawn;
        this.color = color;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    boolean isSpawn() {
        return this.spawn;
    }

    RoomColor color() {
        return this.color;
    }

    AmmoTile getAvailableAmmoTile() {
        return null;
    }

    List<GameCharacter> getActivePlayers() {
        return new ArrayList<>();
    }

    void addActivePlayer(GameCharacter player) {}

    void removeActivePlayer(GameCharacter player) {}

    void setAvailableAmmoTile(AmmoTile tile) {}
}
