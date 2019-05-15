package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.DiscardToSpawnMessage;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return this.activePlayer;
    }

    void startTurn(TurnType type, GameCharacter player) {
        this.activePlayer = this.board.getPlayerByCharacter(player);
        switch (type) {
            case FIRST_TURN:
                this.state = TurnState.RESPAWNING;
                this.board.drawPowerup(this.activePlayer);
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
            case AFTER_DEATH:
                this.state = TurnState.RESPAWNING;
                this.board.drawPowerup(this.getActiveplayer());
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
        }
    }

    void spawnPlayer(RoomColor color) {
        for(Room room : this.board.getArena().getRoomList()){
            if(room.getColor() == color){
                this.board.respawnPlayer(this.activePlayer, room);
                this.state = TurnState.SELECTACTION;
            }
        }
    }

    public TurnState getState() {
        return this.state;
    }

    List<Square> whereCanMove(int maxDistamce) {
        return new ArrayList<>();
    }

    void endTurn() {}
}
