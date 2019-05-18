package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.DiscardToSpawnMessage;
import it.polimi.se2019.model.messages.Message;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.rmi.RemoteException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

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
                this.state = TurnState.FIRST_RESPAWNING;
                this.board.drawPowerup(this.activePlayer);
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
            case AFTER_DEATH:
                this.state = TurnState.DEATH_RESPAWNING;
                countScore();
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
        }
    }

    private void countScore() {
        Map<Player, Integer> playersOrder = new LinkedHashMap<>();
        int points;
        List<Player> damages = this.activePlayer.getDamages();
        damages.get(0).raiseScore(1);
        for(Player player : this.board.getPlayers()) {
            points = 0;
            for(Player present : damages){
                if(present == player) {
                    points++;
                }
            }
            if(points > 0) {
                playersOrder.put(player, points);
            }
        }
        playersOrder.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        int index = 0;
        for(Player player : playersOrder.keySet()) {
            player.raiseScore(this.getActiveplayer().getKillshotPoints().get(index));
            this.controller.sendAll(new Message()/*aggiorna giocatori sui punteggi*/);
            index++;
        }
    }

    void spawnPlayer(RoomColor color) {
        for(Room room : this.board.getArena().getRoomList()){
            if(room.getColor() == color){
                this.board.respawnPlayer(this.activePlayer, room);
                if(this.state == TurnState.FIRST_RESPAWNING) {
                    this.state = TurnState.SELECTACTION;
                }
                if (this.state == TurnState.DEATH_RESPAWNING){
                    this.board.endTurn();
                }
            }
        }
    }

    public TurnState getState() {
        return this.state;
    }

    List<Square> whereCanMove(int maxDistamce) {
        return new ArrayList<>();
    }

}
