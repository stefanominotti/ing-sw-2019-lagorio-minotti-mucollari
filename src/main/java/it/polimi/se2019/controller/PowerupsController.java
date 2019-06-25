package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessageType;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.board.removePowerup(this.board.getPlayerByCharacter(player), powerup);
        this.target = target;
        this.activePowerup = powerup.getType();
        switch (powerup.getType()) {
            case TELEPORTER:
                this.target = this.activePlayer;
                sendPositions();
                break;
            case TAGBACK_GRENADE:
                markPlayer();
                break;
            default:
                requireTarget();
        }
    }

    public void startEffect(GameCharacter player, Powerup powerup) {
        startEffect(player, powerup, null);
    }

    private void sendPositions() {
        List<Coordinates> positions = new ArrayList<>();
        switch (this.activePowerup) {
            case TELEPORTER:
                for (Square s : this.board.getArena().getAllSquares()) {
                    positions.add(new Coordinates(s.getX(), s.getY()));
                }
                break;
            case NEWTON:
                int x = this.target.getPosition().getX();
                int y = this.target.getPosition().getY();
                Square current = this.board.getArena().getSquareByCoordinate(x + 1, y);
                if (current != null && current.getNearbyAccessibility().get(CardinalPoint.WEST)) {
                    positions.add(new Coordinates(x + 1, y));
                    current = this.board.getArena().getSquareByCoordinate(x + 2, y);
                    if (current != null && current.getNearbyAccessibility().get(CardinalPoint.WEST)) {
                        positions.add(new Coordinates(x + 2, y));
                    }
                }
                current = this.board.getArena().getSquareByCoordinate(x - 1, y);
                if (current != null && current.getNearbyAccessibility().get(CardinalPoint.EAST)) {
                    positions.add(new Coordinates(x - 1, y));
                    current = this.board.getArena().getSquareByCoordinate(x - 2, y);
                    if (current != null && current.getNearbyAccessibility().get(CardinalPoint.EAST)) {
                        positions.add(new Coordinates(x - 2, y));
                    }
                }
                current = this.board.getArena().getSquareByCoordinate(x, y - 1);
                if (current != null && current.getNearbyAccessibility().get(CardinalPoint.SOUTH)) {
                    positions.add(new Coordinates(x, y - 1));
                    current = this.board.getArena().getSquareByCoordinate(x, y - 2);
                    if (current != null && current.getNearbyAccessibility().get(CardinalPoint.SOUTH)) {
                        positions.add(new Coordinates(x, y - 2));
                    }
                }
                current = this.board.getArena().getSquareByCoordinate(x, y + 1);
                if (current != null && current.getNearbyAccessibility().get(CardinalPoint.NORTH)) {
                    positions.add(new Coordinates(x, y + 1));
                    current = this.board.getArena().getSquareByCoordinate(x, y + 2);
                    if (current != null && current.getNearbyAccessibility().get(CardinalPoint.NORTH)) {
                        positions.add(new Coordinates(x, y + 2));
                    }
                }
                break;
        }
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.POWERUP_POSITION,
                this.activePlayer.getCharacter(), positions));
    }

    private void requireTarget() {
        List<GameCharacter> avialableTargets = new ArrayList<>();
        if(this.activePowerup == PowerupType.TARGETING_SCOPE) {
            avialableTargets = this.controller.getEffectTargets();
        } else {
            for (Player p : this.board.getPlayers()) {
                if (p.getPosition() != null && p != this.activePlayer) {
                    avialableTargets.add(p.getCharacter());
                }
            }
        }

        this.controller.send(new SelectionListMessage<>(SelectionMessageType.POWERUP_TARGET,
                this.activePlayer.getCharacter(), avialableTargets));
    }

    public void receiveTarget(GameCharacter target) {
        this.target = this.board.getPlayerByCharacter(target);
        if (this.activePowerup == PowerupType.TARGETING_SCOPE) {
            this.board.attackPlayer(activePlayer.getCharacter(), this.target.getCharacter(), 1, EffectType.DAMAGE);
            this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.POWERUP,
                    this.turnController.getActivePlayer().getCharacter(), new HashMap<>()));
        } else {
            sendPositions();
        }
    }

    public void receivePosition(Coordinates position) {
        this.board.movePlayer(this.target, this.board.getArena().getSquareByCoordinate(position.getX(), position.getY()));
        this.turnController.handleEndPowerup();
    }

    public List<Player> getNewtonTargets(Player p) {
        List<Player> targets = new ArrayList<>();
        for (Player target : this.board.getPlayers()) {
            if (target.getPosition() == null || target == p) {
                continue;
            }
            int x = target.getPosition().getX();
            int y = target.getPosition().getY();
            Square current = this.board.getArena().getSquareByCoordinate(x + 1, y);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.WEST)) {
                targets.add(target);
                continue;
            }
            current = this.board.getArena().getSquareByCoordinate(x - 1, y);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.EAST)) {
                targets.add(target);
                continue;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y + 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.SOUTH)) {
                targets.add(target);
                continue;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y - 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.NORTH)) {
                targets.add(target);
            }
        }
        return targets;
    }

    private void markPlayer() {
        this.board.attackPlayer(this.activePlayer.getCharacter(), this.target.getCharacter(), 1, EffectType.MARK);
        this.controller.checkEnemyTurnPowerup();
    }

    public List<Player> getNewtonTargets() {
        return getNewtonTargets(this.activePlayer);
    }

}
