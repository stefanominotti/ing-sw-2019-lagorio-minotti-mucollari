package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.RoomColor;

import java.util.Map;

public class GameSetMessage extends Message {

    private int skulls;
    private int arenaNumber;
    private final Map<Coordinates, RoomColor> arenaColors;
    private final Map<Coordinates, Boolean> arenaSpawn;

    public GameSetMessage(int skulls, int arenaNumber, Map<Coordinates, RoomColor> arenaColors,
                          Map<Coordinates, Boolean> arenaSpawn) {
        this.skulls = skulls;
        this.arenaNumber = arenaNumber;
        this.arenaColors = arenaColors;
        this.arenaSpawn = arenaSpawn;
        setMessageType(this.getClass());
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
}
