package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoTile;
import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.Weapon;

import java.util.List;
import java.util.Map;

public class ArenaFilledMessage extends Message {

    private Map<Coordinates, AmmoTile> ammos;
    private Map<Coordinates, List<Weapon>> stores;

    public ArenaFilledMessage(Map<Coordinates, AmmoTile> ammos, Map<Coordinates, List<Weapon>> stores) {
        setMessageType(this.getClass());
        this.ammos = ammos;
        this.stores = stores;
    }

    public Map<Coordinates, AmmoTile> getAmmos() {
        return this.ammos;
    }

    public Map<Coordinates, List<Weapon>> getStores() {
        return this.stores;
    }
}
