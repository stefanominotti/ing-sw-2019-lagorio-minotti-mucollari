package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class GiveAmmoMessage {

    private GameCharacter player;
    private Map<AmmoType, Integer> ammos;

    public GiveAmmoMessage(GameCharacter player, Map<AmmoType, Integer> ammos) {
        this.player = player;
        this.ammos = ammos;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }

    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }
}
