package it.polimi.se2019.model;

import java.util.*;

public class Square {

    private final int x;
    private final int y;
    private final Map<CardinalPoint, Square> nearbySquares;
    private final Map<CardinalPoint, Boolean> nearbyAccessibility;
    private final Room room;
    private final boolean spawn;
    private AmmoTile availableAmmoTile;
    private final List<WeaponCard> weaponsStore;
    private final List<Player> activePlayers;

    public Square(boolean spawn, Room room, Map<CardinalPoint, Square> nearbySquares,
                  Map<CardinalPoint, Boolean> nearbyAccessibility, int x, int y) {
        this.x = x;
        this.y = y;
        this.room = room;
        this.spawn = spawn;
        this.nearbySquares = new EnumMap<>(nearbySquares);
        this.nearbyAccessibility = new EnumMap<>(nearbyAccessibility);
        this.availableAmmoTile = null;
        this.activePlayers = new ArrayList<>();
        if (spawn) {
            this.weaponsStore = null;
        } else {
            this.weaponsStore = new ArrayList<>();
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isSpawn() {
        return this.spawn;
    }

    public List<WeaponCard> getWeaponsStore() {
        return new ArrayList<>();
    }

    public AmmoTile getAvailableAmmoTile() {
        return null;
    }

    public List<Player> getActivePlayers() {
        return new ArrayList<>();
    }

    public Map<CardinalPoint, Boolean> getNearbyAccessibility() {
        return new EnumMap<>(CardinalPoint.class);
    }

    public Map<CardinalPoint, Square> getNearbySquares() {
        return new EnumMap<>(CardinalPoint.class);
    }

    public Room getRoom() {
        return null;
    }

    public int distanceFrom(Square square) {
        return 0;
    }

    public boolean canSee(Square square) {
        return true;
    }

    void addPlayer(Player player) {}

    void removePlayer(Player player) {}

    void addAmmoTile(AmmoTile tile) {}

    void removeAmmoTile(AmmoTile tile) {}

    void addWeapon(WeaponCard weapon) {}

    void removeWeapon(WeaponCard weapon) {}
}
