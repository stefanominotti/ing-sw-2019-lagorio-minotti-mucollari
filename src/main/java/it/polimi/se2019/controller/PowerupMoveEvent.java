package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Square;

public class PowerupMoveEvent {
    private Square square;
    private Player target;

    public PowerupMoveEvent(Square square, Player target) {
        this.square = square;
        this.target = target;
    }

    public Square getSquare() {}

    public Player getTarget() {}
}
