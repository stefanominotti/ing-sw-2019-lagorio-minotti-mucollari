package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.arena.RoomColor;

import java.util.Map;

/**
 * Class for handling game setup message
 */
public class GameSetMessage extends BoardMessage {

    private int skulls;
    private int arenaNumber;
    private final Map<Coordinates, RoomColor> squareColors;
    private final Map<Coordinates, Boolean> spawnPoints;
    private final Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility;

    /**
     * Class constructor, it builds a board message
     * @param skulls number of skull set for the game
     * @param arenaNumber ID of the arena set for the game
     * @param squareColors colors and positions of the squares of the arena
     * @param spawnPoints coordinates of spawn points
     * @param nearbyAccessibility map of accessible directions from the squares of the arena
     */
    public GameSetMessage(int skulls, int arenaNumber, Map<Coordinates, RoomColor> squareColors,
                          Map<Coordinates, Boolean> spawnPoints, Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility) {
        super(BoardMessageType.GAME_SET);
        this.skulls = skulls;
        this.arenaNumber = arenaNumber;
        this.squareColors = squareColors;
        this.spawnPoints = spawnPoints;
        this.nearbyAccessibility = nearbyAccessibility;
    }

    /**
     * Gets the skulls number set for the game
     * @return the number of skulls set for the game
     */
    public int getSkulls() {
        return this.skulls;
    }

    /**
     * Gets the arena ID set for the game
     * @return the ID of the arena set for the game
     */
    public int getArenaNumber() {
        return this.arenaNumber;
    }

    /**
     * Gets the squares and their colors
     * @return Map with the coordinates and its color of the squares of the arena
     */
    public Map<Coordinates, RoomColor> getSquareColors() {
        return this.squareColors;
    }

    /**
     * Gets the spawn points
     * @return Map with the coordinates and true if it is a spawn point, else false
     */
    public Map<Coordinates, Boolean> getSpawnPoints() {
        return this.spawnPoints;
    }

    /**
     * Gets the accessible directions from the squares of the board
     * @return Map with the coordinates of the squares and map with their accessible directions, and true if they are, else false
     */
    public Map<Coordinates, Map<CardinalPoint, Boolean>> getNearbyAccessibility() {
        return this.nearbyAccessibility;
    }
}
