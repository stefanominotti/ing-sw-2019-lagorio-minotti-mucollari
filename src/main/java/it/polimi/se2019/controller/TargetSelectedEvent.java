package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Square;

import java.util.List;

public class TargetSelectedEvent {

    private Player player;
    private List<Player> targets;
    private Square square;

    public TargetSelectedEvent(Player player, List<Player> targets, Square square) {
        this.player = player;
        this.targets = targets;
        this.square = square;
    }

    public Player getPlayer() {}

    public Player getTarget() {}

    public Square getSquare() {}
}
