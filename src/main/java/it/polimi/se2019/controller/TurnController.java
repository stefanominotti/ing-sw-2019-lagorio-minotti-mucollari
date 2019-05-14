package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.DiscardToSpawnMessage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TurnController {

    private GameController controller;
    private Board board;
    private Player activePlayer;
    private int movesLeft;
    private ActionType selectedAction;
    private TurnState state;
    private Player powerupTarget;

    public TurnController(Board board, GameController controller) {
        this.state = TurnState.SELECTACTION;
        this.board = board;
        this.controller = controller;
        this.movesLeft = 2;
    }

    Player getActiveplayer() {
        return null;
    }

    void startTurn(TurnType type, GameCharacter player) throws RemoteException {
        this.activePlayer = this.board.getPlayerByCharacter(player);
        switch (type) {
            case FIRST_TURN:
                this.board.drawPowerup(this.activePlayer);
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
        }
    }

    List<Square> whereCanMove(int maxDistamce) {
        return new ArrayList<>();
    }

    void endTurn() {}
}
