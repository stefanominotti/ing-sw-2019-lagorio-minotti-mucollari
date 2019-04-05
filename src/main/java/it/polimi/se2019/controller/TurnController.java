package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Square;

import java.util.List;

public class TurnController {

    private Board board;
    private Player activePlayer;
    private int movesLeft;
    private ActionType selectedAction;
    private TurnState state;

    public TurnController(Board board) {
        this.selectedAction = null;
        this.state = TurnState.SELECTACTION;
        this.board = board;
        this.activePlayer = board.getPlayers().get(0);
        this.movesLeft = 2;
    }

    public void startTurn() {}

    public Player getActivePlayer() {}

    public void selectAction(ActionSelectedEvent event) {}

    private List<Square> whereCanMove(int maxDistamce) {}

    public void shot(ShotEvent event) {}

    public void pickup(PickupEvent event) {}

    public void move(MoveEvent event) {}

    public void reload(RealoadEvent event) {}

    public void endTurn() {}
}
