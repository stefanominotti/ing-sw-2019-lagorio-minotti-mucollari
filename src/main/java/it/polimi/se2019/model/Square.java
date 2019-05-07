package it.polimi.se2019.model;

import java.util.*;

public class Square {

    private final int x;
    private final int y;
    private Arena arena;
    private Map<CardinalPoint, Square> nearbySquares;
    private final Map<CardinalPoint, Boolean> nearbyAccessibility;
    private Room room;
    private final boolean spawn;
    private AmmoTile availableAmmoTile;
    private List<WeaponCard> weaponsStore;
    private List<Player> activePlayers;


    public Square(Room room, boolean spawn, Map<CardinalPoint, Boolean> nearbyAccessibility, int x, int y, Arena arena) {
        this.x = x;
        this.y = y;
        this.room = room;
        this.spawn = spawn;
        this.arena = arena;
        this.nearbySquares = new EnumMap<>(CardinalPoint.class);
        this.nearbyAccessibility = new EnumMap<>(nearbyAccessibility);
        this.availableAmmoTile = null;
        this.activePlayers = new ArrayList<>();
        if (spawn) {
            this.weaponsStore = null;
        } else {
            this.weaponsStore = new ArrayList<>();
        }
    }

    public void setNearbySquares(){
        this.nearbySquares = new EnumMap<>(CardinalPoint.class);
        Square nearSquare;
        for(CardinalPoint cardinal : CardinalPoint.values()){
            switch (cardinal) {
                case EAST:
                    nearSquare = this.arena.getSquareByCoordinate(x+1, y);
                    if(nearSquare != null) {
                        this.nearbySquares.put(cardinal, nearSquare); }
                    else {
                        this.nearbySquares.put(cardinal, null);
                    }
                    break;
                case WEST:
                    nearSquare = this.arena.getSquareByCoordinate(x-1, y);
                    if(nearSquare != null) {
                        this.nearbySquares.put(cardinal, nearSquare);
                    } else {
                        this.nearbySquares.put(cardinal, null);
                    }
                    break;
                case NORTH:
                    nearSquare = this.arena.getSquareByCoordinate(x, y-1);
                    if(nearSquare != null) {
                        this.nearbySquares.put(cardinal, nearSquare);
                    } else {
                        this.nearbySquares.put(cardinal, null);
                    }
                    break;
                case SOUTH:
                    nearSquare = this.arena.getSquareByCoordinate(x, y+1);
                    if(nearSquare != null){
                        this.nearbySquares.put(cardinal, nearSquare);
                    } else {
                        this.nearbySquares.put(cardinal, null);
                    }
                    break;

            }
        }
    }

    public void setRoom(Room room){ this.room = room; }

    public void setArena(Arena arena){ this.arena = arena; }

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

    public Map<CardinalPoint, Boolean> getNearbyAccessibility() { return new EnumMap<>(this.nearbyAccessibility); }

    public Map<CardinalPoint, Square> getNearbySquares() { return new EnumMap<>(this.nearbySquares); }

    public Room getRoom() {
        return this.room;
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
