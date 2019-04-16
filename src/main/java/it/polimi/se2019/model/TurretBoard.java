package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurretBoard extends Board {

    private List<AmmoTile> availableAmmos;
    private Map<Square, Player> turrets;

    public TurretBoard() {
        super();
        this.availableAmmos = new ArrayList<>();
        this.turrets = new HashMap<>();
    }

    @Override
    public void initializeGame(int skulls, int arenaNumber) {}

    @Override
    public void handleEndTurn() {}

    @Override
    public void giveAmmoTile(Player player, AmmoTile tile) {}

    @Override
    protected void fillAmmosDeck() {}

    @Override
    public void handleDeadPlayer(Player player) {}

    public void addTurret(Player player, Square square) {}

    public void removeTurret(Player player, Square square) {}

    public Map<Square, Player> getTurrets() {
        return new HashMap<>();
    }

    public List<AmmoTile> getAvailableAmmos() {
        return new ArrayList<>();
    }
}
