package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.ArrayList;
import java.util.List;

public class SquareView {

    private int x;
    private int y;
    private boolean spawn;
    private RoomColor color;
    private List<GameCharacter> activePlayers;
    private AmmoTile availableAmmoTile;
    private List<Weapon> store;

    SquareView(int x, int y, RoomColor color, boolean spawn) {
        this.x = x;
        this.y = y;
        this.spawn = spawn;
        this.color = color;
        this.activePlayers = new ArrayList<>();
        if(spawn) {
            this.store = new ArrayList<>();
        } else {
            this.store = null;
        }
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
        return this.availableAmmoTile;
    }

    List<GameCharacter> getActivePlayers() {
        return new ArrayList<>(this.activePlayers);
    }

    void addStoreWeapon(Weapon weapon) {
        this.store.add(weapon);
    }

    void removeStoreWeapon(Weapon weapon) {
        this.store.remove(weapon);
    }

    List<Weapon> getStore() {
        return new ArrayList<>(this.store);
    }

    RoomColor getColor() {
        return this.color;
    }

    void addActivePlayer(GameCharacter player) {
        this.activePlayers.add(player);
    }

    void removeActivePlayer(GameCharacter player) {
        this.activePlayers.remove(player);
    }

    void setAvailableAmmoTile(AmmoTile tile) {
        this.availableAmmoTile = tile;
    }

    void removeAmmoTile() {
        this.availableAmmoTile = null;
    }
}
