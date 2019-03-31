package it.polimi.se2019.model;

import java.util.*;

public class Square {

    private final Map<CardinalPoint, Square> nearbySquares;
    private final Map<CardinalPoint, Boolean> nearbyAccessibility;
    private final Room room;
    private final boolean spawn;
    private AmmoTile availableAmmoTile;
    private final List<WeaponCard> weaponsStore;
    private final List<Player> activePlayers;

    public Square(boolean spawn, Room room, Map<CardinalPoint, Square> nearbySquares,
                  Map<CardinalPoint, Boolean> nearbyAccessibility) {
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

    public boolean isSpawn() {
        return this.spawn;
    }

    public List<WeaponCard> getWeaponsStore() {}

    public AmmoTile getAvailableAmmoTile() {}

    public List<Player> getActivePlayers() {}

    public Map<CardinalPoint, Boolean> getNearbyAccessibility() {}

    public Map<CardinalPoint, Square> getNearbySquares() {}

    public Room getRoom() {}

    public int distanceFrom(Square square) {}

    public boolean canSee(Square square) {}

    void addPlayer(Player player) {}

    void removePlayer(Player player) {}

    void addAmmoTile(AmmoTile tile) {}

    void removeAmmoTile(AmmoTile tile) {}

    void addWeapon(WeaponCard weapon) {}

    void removeWeapon(WeaponCard weapon) {}
}
