package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Square;

public class PickupEvent {

    private Player player;
    private Square square;

    public PickupEvent(Player player, Square square) {
        this.player = player;
        this.square = square;
    }

    public Player getPlayer() {}

    public Square getSquare() {}
}
