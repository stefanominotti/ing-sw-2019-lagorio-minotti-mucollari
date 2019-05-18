package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.AvailableActionsMessage;
import it.polimi.se2019.model.messages.AvailableMoveActionMessage;
import it.polimi.se2019.model.messages.AvailablePickupActionMessage;
import it.polimi.se2019.model.messages.DiscardToSpawnMessage;

import java.util.*;

import static it.polimi.se2019.controller.TurnState.MOVING;
import static it.polimi.se2019.controller.TurnState.PICKINGUP;
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
                this.movesLeft = 2;
                this.state = TurnState.FIRST_RESPAWNING;
                this.board.drawPowerup(this.activePlayer);
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
            case AFTER_DEATH:
                this.movesLeft = 0;
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
            this.board.raisePlayerScore(player, this.getActiveplayer().getKillshotPoints().get(index));
            index++;
        }
    }

    void handlePowerupDiscarded(Powerup powerup) {
        this.board.removePowerup(this.activePlayer, powerup);

        if(this.state == TurnState.FIRST_RESPAWNING || this.state == TurnState.DEATH_RESPAWNING) {
            spawnPlayer(RoomColor.valueOf(powerup.getColor().toString()));
        } else {
            //usa powerup
        }
    }

    void spawnPlayer(RoomColor color) {
        for(Room room : this.board.getArena().getRoomList()){
            if(room.getColor() == color){
                this.board.respawnPlayer(this.activePlayer, room);
                if(this.state == TurnState.FIRST_RESPAWNING) {
                    List<ActionType> availableActions = Arrays.asList(ActionType.MOVE, ActionType.PICKUP, ActionType.SHOT);
                    this.controller.send(new AvailableActionsMessage(this.activePlayer.getCharacter(), availableActions));
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

    void calculateMovementAction() {
        Square position = this.activePlayer.getPosition();
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s == position) {
                continue;
            }
            List<List<Square>> paths = position.pathsTo(s);
            for (List<Square> path : paths) {
                if (path.size() - 1 <= 3) {
                    movements.add(new Coordinates(s.getX(), s.getY()));
                }
            }
        }

        this.controller.send(new AvailableMoveActionMessage(this.activePlayer.getCharacter(), movements));
    }

    void calculatePickupAction() {
        Square position = this.activePlayer.getPosition();
        int maxDistance = 1;
        if (this.activePlayer.getDamages().size() > 2) {
            maxDistance = 2;
        }
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s == position || s.getAvailableAmmoTile() == null) {
                continue;
            }
            List<List<Square>> paths = position.pathsTo(s);
            for (List<Square> path : paths) {
                if (path.size() - 1 <= maxDistance) {
                    movements.add(new Coordinates(s.getX(), s.getY()));
                }
            }
        }

        this.controller.send(new AvailablePickupActionMessage(this.activePlayer.getCharacter(), movements));
    }

    void handleAction(ActionType action) {
        switch (action) {
            case MOVE:
                this.state = MOVING;
                calculateMovementAction();
                break;
            case PICKUP:
                this.state = PICKINGUP;
                calculatePickupAction();
                break;
        }
    }

}
