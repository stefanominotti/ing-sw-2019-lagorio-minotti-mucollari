package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoTile;
import it.polimi.se2019.model.GameCharacter;

import java.util.ArrayList;
import java.util.List;

public class SquareView {

    private int x;
    private int y;
    private List<GameCharacter> activePlayers;
    private AmmoTile availableAmmoTile;

    SquareView(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
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
