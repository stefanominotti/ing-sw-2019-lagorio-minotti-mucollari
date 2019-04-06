package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Square;

import java.util.ArrayList;
import java.util.List;

public class TurnController {

    private Board board;
    private Player activePlayer;
    private int movesLeft;
    private ActionType selectedAction;
    private TurnState state;
    private Player powerupTarget;

    public TurnController(Board board) {
        this.selectedAction = null;
        this.state = TurnState.SELECTACTION;
        this.board = board;
        this.activePlayer = board.getPlayers().get(0);
        this.movesLeft = 2;
    }

    Player getActiveplayer() {
        return null;
    }

    void startTurn() {}

    void powerupTargetSelected(PowerupTargetSelectedEvent event) {}

    void powerupMove(PowerupMoveEvent event) {}

    void selectPowerup(PowerupCardSelectedEvent event) {}

    void selectAction(ActionSelectedEvent event) {}

    List<Square> whereCanMove(int maxDistamce) {
        return new ArrayList<>();
    }

    void shot(ShotEvent event) {}

    void pickup(PickupEvent event) {}

    void move(MoveEvent event) {}

    void reload(RealoadEvent event) {}

    void endTurn() {}
}
