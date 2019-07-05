package it.polimi.se2019.model.arena;

import it.polimi.se2019.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling rooms
 * @author stefanominotti
 */
public class Room {

    private final RoomColor color;
    private List<Square> squares;

    /**
     * Class constructor, it builds a room
     * @param color of the room you want to build
     * @param squares with which you want to compose the room
     */
    Room(RoomColor color , List<Square> squares) {
        this.color = color;
        this.squares = squares;
    }

    /**
     * Gets the room color
     * @return the color of the room
     */
    public RoomColor getColor() {
        return color;
    }

    /**
     * Gets the squares of the room
     * @return List of the square of the room
     */
    public List<Square> getSquares() {
        return new ArrayList<>(this.squares);
    }

    /**
     * Knows if the room has any spawn point
     * @return true if it has, else false
     */
    public boolean hasSpawn() {
        for(Square square : this.squares) {
            if (square.isSpawn()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the spawn square of the room
     * @return the spawn square of the room, null if it doesn't have it
     */
    public Square getSpawn() {
        for(Square square : this.squares) {
            if(square.isSpawn()) {
                return square;
            }
        }
        return null;
    }

    /**
     * Gets players of the room
     * @return List of players of the room
     */
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for(Square square : getSquares()) {
            players.addAll(square.getActivePlayers());
        }
        return players;
    }
}