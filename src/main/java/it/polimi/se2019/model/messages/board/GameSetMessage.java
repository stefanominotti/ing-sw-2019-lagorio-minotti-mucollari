package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.CardinalPoint;
import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.RoomColor;
import it.polimi.se2019.model.messages.Message;

import java.util.Map;

public class GameSetMessage extends BoardMessage {

    private int skulls;
    private int arenaNumber;
    private final Map<Coordinates, RoomColor> squareColors;
    private final Map<Coordinates, Boolean> spawnPoints;
    private final Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility;
    public GameSetMessage(int skulls, int arenaNumber, Map<Coordinates, RoomColor> squareColors,
                          Map<Coordinates, Boolean> spawnPoints, Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility) {
        super(BoardMessageType.GAME_SET);
        this.skulls = skulls;
        this.arenaNumber = arenaNumber;
        this.squareColors = squareColors;
        this.spawnPoints = spawnPoints;
        this.nearbyAccessibility = nearbyAccessibility;
    }

    public int getSkulls() {
        return this.skulls;
    }

    public int getArenaNumber() {
        return this.arenaNumber;
    }

    public Map<Coordinates, RoomColor> getSquareColors() {
        return this.squareColors;
    }

    public Map<Coordinates, Boolean> getSpawnPoints() {
        return this.spawnPoints;
    }

    public Map<Coordinates, Map<CardinalPoint, Boolean>> getNearbyAccessibility() {
        return this.nearbyAccessibility;
    }
}
