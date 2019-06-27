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

/**
 * Class for handling powerups
 */
public class PowerupsController {

    private PowerupType activePowerup;
    private Player activePlayer;
    private Player target;
    private Board board;
    private GameController controller;
    private TurnController turnController;

    /**
     * Class constructor, it builds a powerup controller
     * @param board in which the controller has to be used
     * @param controller the game controller
     * @param turnController the turn controller
     */
    PowerupsController(Board board, GameController controller, TurnController turnController) {
        this.board = board;
        this.controller = controller;
        this.turnController = turnController;
    }

    /**
     * Starts performing powerup effect
     * @param player who is using the powerup
     * @param powerup which powerup is wanted to use
     * @param target of the powerup effect
     */
    void startEffect(GameCharacter player, Powerup powerup, Player target) {
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

    /**
     * Starts performing powerup effect, for powerups with no targets
     * @param player who is using the powerup
     * @param powerup which powerup is wanted to use
     */
    void startEffect(GameCharacter player, Powerup powerup) {
        startEffect(player, powerup, null);
    }

    /**
     * Asks to the poweup holder to choose positions
     */
    private void sendPositions() {
        List<Coordinates> positions = new ArrayList<>();
        if (this.activePowerup == PowerupType.TELEPORTER) {
            for (Square s : this.board.getArena().getAllSquares()) {
                positions.add(new Coordinates(s.getX(), s.getY()));
            }
        } else if (this.activePowerup == PowerupType.NEWTON) {
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
        }
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.POWERUP_POSITION,
                this.activePlayer.getCharacter(), positions));
    }

    /**
     * Asks to the powerup holder to choose targets
     */
    private void requireTarget() {
        List<GameCharacter> availableTargets = new ArrayList<>();
        if(this.activePowerup == PowerupType.TARGETING_SCOPE) {
            availableTargets = this.controller.getEffectTargets();
        } else {
            for (Player p : this.board.getPlayers()) {
                if (p.getPosition() != null && p != this.activePlayer) {
                    availableTargets.add(p.getCharacter());
                }
            }
        }

        this.controller.send(new SelectionListMessage<>(SelectionMessageType.POWERUP_TARGET,
                this.activePlayer.getCharacter(), availableTargets));
    }

    /**
     * Handles target chosen
     * @param target chosen by the powerup holder
     */
    void receiveTarget(GameCharacter target) {
        this.target = this.board.getPlayerByCharacter(target);
        if (this.activePowerup == PowerupType.TARGETING_SCOPE) {
            this.board.attackPlayer(activePlayer.getCharacter(), this.target.getCharacter(), 1, EffectType.DAMAGE);
            this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.POWERUP,
                    this.turnController.getActivePlayer().getCharacter(), new HashMap<>()));
        } else {
            sendPositions();
        }
    }

    /**
     * Handles the position chosen
     * @param position coordinates chosen by the powerup holder
     */
    void receivePosition(Coordinates position) {
        this.board.movePlayer(this.target, this.board.getArena().getSquareByCoordinate(position.getX(), position.getY()));
        this.turnController.handleEndPowerup();
    }

    /**
     * Gets the available targets for the Newton powerup application
     * @param p who is using the powerup
     * @return List of the available target players
     */
    public List<Player> getNewtonTargets(Player p) {
        List<Player> targets = new ArrayList<>();
        for (Player possibleTarget : this.board.getPlayers()) {
            if (possibleTarget.getPosition() == null || possibleTarget == p) {
                continue;
            }
            int x = possibleTarget.getPosition().getX();
            int y = possibleTarget.getPosition().getY();
            Square current = this.board.getArena().getSquareByCoordinate(x + 1, y);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.WEST)) {
                targets.add(possibleTarget);
                continue;
            }
            current = this.board.getArena().getSquareByCoordinate(x - 1, y);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.EAST)) {
                targets.add(possibleTarget);
                continue;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y + 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.SOUTH)) {
                targets.add(possibleTarget);
                continue;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y - 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.NORTH)) {
                targets.add(possibleTarget);
            }
        }
        return targets;
    }

    /**
     * Marks a player
     */
    private void markPlayer() {
        this.board.attackPlayer(this.activePlayer.getCharacter(), this.target.getCharacter(), 1, EffectType.MARK);
        this.controller.checkEnemyTurnPowerup();
    }

}
