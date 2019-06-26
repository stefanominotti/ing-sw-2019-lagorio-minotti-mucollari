package it.polimi.se2019.model;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Class for handling squares
 */
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

    /**
     * Class constructor, it builds a square
     * @param room which the square has to be part of
     * @param spawn true if the square is spawn, else false
     * @param nearbyAccessibility Map with the cardinal point and its accessibility from the square
     * @param x coordinate X of the square
     * @param y coordinate Y of the square
     * @param arena which the square has to be related to
     */
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
        if(spawn) {
            this.weaponsStore = new ArrayList<>();
        } else {
            this.weaponsStore = null;
        }
    }

    /**
     * Sets the nearby squares for the square
     */
    void setNearbySquares(){
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

    /**
     * Sets a room for the square
     * @param room where you want to set the square
     */
    public void setRoom(Room room) { this.room = room; }

    /**
     * Sets an arena for the square
     * @param arena which the square has to be part of
     */
    public void setArena(Arena arena) { this.arena = arena; }

    /**
     * Gets the coordinate X of the square
     * @return the coordinate X of the square
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the coordinate Y of the square
     * @return the coordinate Y of the square
     */
    public int getY() {
        return this.y;
    }

    /**
     * Knows if the square is a spawn point
     * @return true if it is, else false
     */
    public boolean isSpawn() {
        return this.spawn;
    }

    /**
     * Gets the weapon cards of the square
     * @return List of available weapons in the square
     */
    public List<WeaponCard> getWeaponsStore() {
        if (this.weaponsStore == null) {
            return null;
        }
        return new ArrayList<>(this.weaponsStore);
    }

    /**
     * Gets the ammo tile of the square
     * @return the ammo tile of the square
     */
    public AmmoTile getAvailableAmmoTile() {
        return this.availableAmmoTile;
    }

    /**
     * Gets the players in the square
     * @return List of players in the square
     */
    public List<Player> getActivePlayers() {
        return new ArrayList<>(this.activePlayers);
    }

    /**
     * Gets the accessible direction from the square
     * @return Map with cardinal points and its accessibility from the square
     */
    public Map<CardinalPoint, Boolean> getNearbyAccessibility() {
        return new EnumMap<>(this.nearbyAccessibility);
    }

    /**
     * Gets the nearby squares and its direction from the square
     * @return Map with nearby squares and its direction
     */
    public Map<CardinalPoint, Square> getNearbySquares() {
        return new EnumMap<>(this.nearbySquares);
    }

    /**
     * Gets the room where the square is placed
     * @return the room of the square
     */
    public Room getRoom() {
        return this.room;
    }

    /**
     * Knows if a square is visible from the square
     * @param square to know if it is visible
     * @return true if it is, else false
     */
    public boolean canSee(Square square) {
        if(square.getRoom() == this.room) {
            return true;
        }
        for(CardinalPoint point : CardinalPoint.values()) {
            if(!this.nearbyAccessibility.get(point)) {
                continue;
            }
            if(this.nearbySquares.get(point).getRoom() == square.getRoom()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a player to the square
     * @param player to add
     */
    void addPlayer(Player player) {
        this.activePlayers.add(player);
    }

    /**
     * Removes a player from the square
     * @param player to remove
     */
    void removePlayer(Player player) {
        this.activePlayers.remove(player);
    }

    /**
     * Adds an ammo tile to the square
     * @param tile to add
     */
    public void addAmmoTile(AmmoTile tile) {
        this.availableAmmoTile = tile;
    }

    /**
     * Removes the ammo tile from the square
     */
    void removeAmmoTile() {
        this.availableAmmoTile = null;
    }

    /**
     * Adds a weapon card to the weapons store of the square
     * @param weapon to add to the square store
     */
    public void addWeapon(WeaponCard weapon) {
        this.weaponsStore.add(weapon);
    }

    void removeWeapon(WeaponCard weapon) {
        this.weaponsStore.remove(weapon);
    }

    boolean isAtDirection(CardinalPoint direction, Square square) {
        switch(direction) {
            case EAST:
                if(square.getY() == this.y && square.getX() < this.x) {
                    return true;
                }
                break;
            case WEST:
                if(square.getY() == this.y && square.getX() > this.x) {
                    return true;
                }
                break;
            case NORTH:
                if(square.getY() < this.y && square.getX() == this.x) {
                    return true;
                }
                break;
            case SOUTH:
                if(square.getY() > this.y && square.getX() == this.x) {
                    return true;
                }
        }
        return false;
    }


    public List<List<Square>> pathsTo(Square square) {
        return new SquaresGraph(this.arena).findPaths(this, square);
    }

    /**
     * Gets the minimum distance from a square
     * @param square of which you want to calculate the minimum distance
     * @return
     */
    int minimumDistanceFrom(Square square) {
        return new SquaresGraph(this.arena).findMinimumDistance(this, square);
    }

}

