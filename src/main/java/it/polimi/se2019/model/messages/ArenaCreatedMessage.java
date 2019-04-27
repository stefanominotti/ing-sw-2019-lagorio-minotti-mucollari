package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.RoomColor;

import java.util.Map;

public class ArenaCreatedMessage extends Message {

    private final Map<Coordinates, RoomColor> arenaColor;
    private final Map<Coordinates, Boolean> arenaSpawn;

    public ArenaCreatedMessage(Map<Coordinates, RoomColor> arenaColor, Map<Coordinates, Boolean> arenaSpawn){
        this.arenaColor = arenaColor;
        this.arenaSpawn = arenaSpawn;
        setMessageType(this.getClass());
    }

    public Map<Coordinates, RoomColor> getArenaColor() {
        return arenaColor;
    }

    public Map<Coordinates, Boolean> getArenaSpawn() {
        return arenaSpawn;
    }
}
