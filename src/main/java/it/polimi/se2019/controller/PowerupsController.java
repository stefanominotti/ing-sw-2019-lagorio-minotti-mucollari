package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.PowerupPositionsAvailableMessage;
import it.polimi.se2019.model.messages.PowerupTargetsAvailableMessage;

import java.util.ArrayList;
import java.util.List;

public class PowerupsController {

    private PowerupType activePowerup;
    private Player activePlayer;
    private Player target;
    private Board board;
    private GameController controller;
    private TurnController turnController;

    public PowerupsController(Board board, GameController controller, TurnController turnController) {
        this.board = board;
        this.controller = controller;
        this.turnController = turnController;
    }

    public void startEffect(GameCharacter player, Powerup powerup, Player target) {
        this.activePlayer = this.board.getPlayerByCharacter(player);
        this.board.removePowerup(this.activePlayer, powerup);
        this.target = target;
        this.activePowerup = powerup.getType();
        switch (powerup.getType()) {
            case TELEPORTER:
                this.target = this.activePlayer;
                sendPositions();
                break;
            case NEWTON:
                requireTarget();
                break;
        }
    }

    public void startEffect(GameCharacter player, Powerup powerup) {
        startEffect(player, powerup, null);
    }

    private void sendPositions() {
        List<Coordinates> positions = new ArrayList<>();
        switch (this.activePowerup) {
            case TELEPORTER:
                for(Square s : this.board.getArena().getAllSquares()) {
                    positions.add(new Coordinates(s.getX(), s.getY()));
                }
                break;
            case NEWTON:
                int x = this.target.getPosition().getX();
                int y = this.target.getPosition().getY();
                for (int i=1; i<3; i++) {
                    if(this.board.getArena().getSquareByCoordinate(x+i, y) != null) {
                        positions.add(new Coordinates(x+i, y));
                    }
                    if(this.board.getArena().getSquareByCoordinate(x-i, y) != null) {
                        positions.add(new Coordinates(x-i, y));
                    }
                    if(this.board.getArena().getSquareByCoordinate(x, y+i) != null) {
                        positions.add(new Coordinates(x, y+i));
                    }
                    if(this.board.getArena().getSquareByCoordinate(x, y-i) != null) {
                        positions.add(new Coordinates(x, y-i));
                    }
                }
                break;
        }
        this.controller.send(new PowerupPositionsAvailableMessage(this.activePlayer.getCharacter(), positions, this.activePowerup));
    }

    private void requireTarget() {
        List<GameCharacter> targets = new ArrayList<>();
        for (Player p : this.board.getPlayers()) {
            if (p.getPosition() != null && p.isConnected() && p != this.activePlayer) {
                targets.add(p.getCharacter());
            }
        }
        this.controller.send(new PowerupTargetsAvailableMessage(this.activePlayer.getCharacter(), targets, this.activePowerup));
    }

    public void receiveTarget(GameCharacter target) {
        this.target = this.board.getPlayerByCharacter(target);
        sendPositions();
    }

    public void receivePosition(Coordinates position) {
        this.board.movePlayer(this.target, this.board.getArena().getSquareByCoordinate(position.getX(), position.getY()));
        this.turnController.handleEndPowerup();
    }
}
