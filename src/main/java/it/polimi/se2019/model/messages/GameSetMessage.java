package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.CardinalPoint;
import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.RoomColor;

import java.util.Map;

public class GameSetMessage extends Message {

    private int skulls;
    private int arenaNumber;
    private final Map<Coordinates, RoomColor> arenaColors;
    private final Map<Coordinates, Boolean> arenaSpawn;
    private final Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility;
    public GameSetMessage(int skulls, int arenaNumber, Map<Coordinates, RoomColor> arenaColors,
                          Map<Coordinates, Boolean> arenaSpawn, Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility) {
        setMessageType(this.getClass());
        this.skulls = skulls;
        this.arenaNumber = arenaNumber;
        this.arenaColors = arenaColors;
        this.arenaSpawn = arenaSpawn;
        this.nearbyAccessibility = nearbyAccessibility;
    }

    public int getSkulls() {
        return this.skulls;
    }

    public int getArenaNumber() {
        return this.arenaNumber;
    }

    public Map<Coordinates, RoomColor> getArenaColors() {
        return this.arenaColors;
    }

    public Map<Coordinates, Boolean> getArenaSpawn() {
        return this.arenaSpawn;
    }

    public Map<Coordinates, Map<CardinalPoint, Boolean>> getNearbyAccessibility() {
        return this.nearbyAccessibility;
    }
}
