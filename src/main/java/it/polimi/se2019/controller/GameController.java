package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.BoardMessage;
import it.polimi.se2019.model.messages.board.BoardMessageType;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientReadyMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessageType;
import it.polimi.se2019.model.messages.payment.PaymentSentMessage;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.player.PlayerReadyMessage;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.timer.TimerMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.timer.TimerType;
import it.polimi.se2019.model.messages.turn.TurnContinuationMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.view.VirtualView;
import java.util.*;

/**
 * Class for handling game controller
 */
public class GameController implements Observer {

    private Board model;
    private TurnController turnController;
    private Map<GameCharacter, PowerupsController> powerupsControllers;
    private VirtualView view;
    private boolean gameStarted;
    private EffectsController effectsController;
    private Timer powerupRequestsTimer;
    private int powerupRequests;
    private WeaponEffectOrderType effectSelection;
    private List<GameCharacter> effectTargets;

    /**
     * Class constructor, it builds a game controller
     * @param board the board to handle
     * @param view the view used to send message to clients
     */
    public GameController(Board board, VirtualView view) {
        this.model = board;
        this.effectsController = new EffectsController(this.model, this);
        this.turnController = new TurnController(this.model, this, this.effectsController);
        this.powerupsControllers = new EnumMap<>(GameCharacter.class);
        for (GameCharacter c : GameCharacter.values()) {
            this.powerupsControllers.put(c, new PowerupsController(this.model, this, this.turnController));
        }
        this.view = view;
        this.powerupRequestsTimer = new Timer();
    }


    /**
     * Updates the view with a message
     * @param view the view to notify
     * @param message to be notified
     */
    @Override
    public void update(Observable view, Object message) {
        switch (((Message) message).getMessageType()) {
            case CLIENT_MESSAGE:
                update((ClientMessage) message);
                break;
            case BOARD_MESSAGE:
                update((BoardMessage) message);
                break;
            case TURN_MESSAGE:
                update((TurnMessage) message);
                break;
            case PAYMENT_MESSAGE:
                update((PaymentMessage) message);
                break;
            case SINGLE_SELECTION_MESSAGE:
                update((SingleSelectionMessage) message);
                break;
            case SELECTION_LIST_MESSAGE:
                update((SelectionListMessage) message);
                break;
            default:
                break;
        }
    }

    private void update(ClientMessage message) {
        switch (message.getType()) {
            case READY:
                handleClientReady(message.getCharacter(), ((ClientReadyMessage) message).getNickname(),
                        ((ClientReadyMessage) message).getToken());
                break;
            case RECONNECTED:
                handleClientReconnected(message.getCharacter());
                break;
            case DISCONNECTED:
                handleClientDisconnected(message.getCharacter());
                break;
            default:
                break;
        }

    }

    /**
     * Handles a ready client adding it to the model
     * @param character chosen by the player
     * @param nickname chosen by the player
     * @param token of the client associated to the player
     */
    private void handleClientReady(GameCharacter character, String nickname, String token) {
        this.model.addPlayer(character, nickname, token);
    }

    /**
     * Handles a reconnected client, sending him his view
     * @param character who is reconnected
     */
    private void handleClientReconnected(GameCharacter character) {
        int counter = 0;
        Player player = this.model.getPlayerByCharacter(character);
        player.connect();
        this.model.sendModelView(player);
        sendAll(new PlayerReadyMessage(player.getCharacter(), player.getNickname()));
        for(Player boardPlayer : this.model.getPlayers()) {
            if(boardPlayer.isConnected()) {

               counter++;
            } else {
                return;
            }
        }
        if(counter == this.model.getPlayers().size()) {
            if(this.model.getDeadPlayers().isEmpty()) {
                this.model.startTurn(this.model.getPlayers().get(this.model.getCurrentPlayer()));
            } else {
                this.model.startTurn(this.model.getPlayerByCharacter(this.model.getDeadPlayers().get(0)));
            }
        }
    }

    /**
     * Handles a disconnected client, notify other clients
     * @param character who is disconnected
     */
    private void handleClientDisconnected(GameCharacter character) {
        this.model.handleDisconnection(character);
        if (this.model.getPlayerByCharacter(character) == this.turnController.getActivePlayer() && this.gameStarted) {
            int validPlayers = 0;
            for (Player p : this.model.getPlayers()) {
                if (p.isConnected()) {
                    validPlayers++;
                }
            }
            if(validPlayers >= 3) {
                this.turnController.endTurn();
            }
        }
    }

    /**
     * Updates board with a message
     * @param message to be updated
     */
    private void update(BoardMessage message) {
        switch(message.getType()) {
            case ARENA:
                handleArenaReceived(((ArenaMessage) message).getArena());
                break;
            case SKULLS:
                handleSkullsReceived(((SkullsMessage) message).getSkulls());
                break;
            default:
                break;
        }
    }

    /**
     * Handles chosen arena creating it and finalizing game setup
     * @param arena ID chosen by the master player
     */
    private void handleArenaReceived(String arena) {
        this.model.createArena(arena);
        this.model.finalizeGameSetup();
    }

    /**
     * Handles chosen skulls number setting it in the model and finalizing game setup
     * @param skulls number chosen by the master player
     */
    private void handleSkullsReceived(int skulls) {
        this.model.setSkulls(skulls);
    }

    /**
     * Handles a turn message receiving
     * @param message received
     */
    private void update(TurnMessage message) {
        this.turnController.startTurn(message.getTurnType(), message.getCharacter());
        this.gameStarted = true;
    }

    /**
     * Handles a selection list message receiving
     * @param message received
     */
    private void update(SelectionListMessage message) {
        if (message.getType() == SelectionMessageType.USE_POWERUP) {
                this.powerupRequests--;
                if (this.powerupRequests == 0) {
                    this.powerupRequestsTimer.cancel();
                }
                if (message.getList() == null) {
                    send(new TurnContinuationMessage(message.getCharacter(), this.turnController.getActivePlayer().getCharacter()));
                    checkEnemyTurnPowerup();
                } else {
                    for (Powerup p : (List<Powerup>) message.getList()) {
                        handleUsePowerupSelection(message.getCharacter(), p);
                    }
                }
        }
    }

    /**
     * Handles a single selection message receiving
     * @param message received
     */
    private void update(SingleSelectionMessage message) {
        switch (message.getType()) {
            case SWITCH:
                handleWeaponSwitchSelection((Weapon) message.getSelection());
                break;
            case PICKUP:
                handlePickupSelection((Coordinates) message.getSelection());
                break;
            case MOVE:
                handleMovementSelection((Coordinates) message.getSelection());
                break;
            case POWERUP_TARGET:
                handlePowerupTargetSelection(message.getCharacter(), (GameCharacter) message.getSelection());
                break;
            case POWERUP_POSITION:
                handlePowerupPositionSelection(message.getCharacter(), (Coordinates) message.getSelection());
                break;
            case RELOAD:
                handleReloadSelection((Weapon) message.getSelection());
                break;
            case DISCARD_POWERUP:
                handlePowerupDiscardSelection((Powerup) message.getSelection());
                break;
            case USE_POWERUP:
                if(this.powerupRequests == 1 && message.getSelection() == null) {
                    this.powerupRequests--;
                    this.effectsController.handleEffectsQueue();
                } else {
                    handleUsePowerupSelection(message.getCharacter(), (Powerup) message.getSelection());
                }
                break;
            case PICKUP_WEAPON:
                handleWeaponPickupSelection((Weapon) message.getSelection());
                break;
            case ACTION:
                handleActionSelection((ActionType) message.getSelection());
                break;
            case USE_WEAPON:
                handleWeaponUseSelection((Weapon) message.getSelection());
                break;
            case EFFECT:
                handleEffectSelection((WeaponEffectOrderType) message.getSelection());
                break;
            case EFFECT_COMBO:
                handleEffectComboSelection((String) message.getSelection());
                break;
            case EFFECT_POSSIBILITY:
                handleEffectPossibilitySelection((EffectPossibilityPack) message.getSelection());
                break;
            case PERSISTENCE:
                handlePersistenceSelection((String) message.getSelection());
                break;
        }
    }

    /**
     * Handles persistence selection
     * @param selection chosen by the player
     */
    private void handlePersistenceSelection(String selection) {
        if (selection.equalsIgnoreCase("n")) {
            this.model.endGame();
        } else {
            sendAll(new BoardMessage(BoardMessageType.PERSISTENCE));
        }
    }

    /**
     * Handles powerup discard selection
     * @param powerup chosen to discard
     */
    private void handlePowerupDiscardSelection(Powerup powerup) {
        this.turnController.handlePowerupDiscarded(powerup);
    }

    /**
     * Handles action selection
     * @param action chosen
     */
    private void handleActionSelection(ActionType action) {
        this.turnController.handleAction(action);
    }

    /**
     * Handles movement selection
     * @param coordinates chosen where the player wants to move
     */
    private void handleMovementSelection(Coordinates coordinates) {
        this.turnController.movementAction(coordinates);
    }

    /**
     * Handle pickup selection
     * @param coordinates chosen where the player wants to move and pickup
     */
    private void handlePickupSelection(Coordinates coordinates) {
        this.turnController.pickupAction(coordinates);
    }

    /**
     * Handles weapon pickup selection
     * @param weapon chosen to pickup
     */
    private void handleWeaponPickupSelection(Weapon weapon) {
        this.turnController.pickupWeapon(weapon);
    }

    /**
     * Handles weapon switch selection
     * @param weapon the player wants to switch
     */
    private void handleWeaponSwitchSelection(Weapon weapon) {
        this.turnController.switchWeapon(weapon);
    }

    /**
     * Handled weapon reload selection
     * @param weapon chosen to be reloaded
     */
    private void handleReloadSelection(Weapon weapon) {
        this.turnController.sendReloadPaymentRequest(weapon);
    }

    /**
     * Handles powerup use selection
     * @param player who chose to use the powerup
     * @param powerup chosen to use
     */
    private void handleUsePowerupSelection(GameCharacter player, Powerup powerup) {
        if (powerup == null) {
            this.turnController.handleEndPowerup();
        } else {
            if (powerup.getType() == PowerupType.TAGBACK_GRENADE) {
                this.powerupsControllers.get(player).startEffect(player, powerup,
                        this.turnController.getActivePlayer());
            } else {
                this.powerupsControllers.get(player).startEffect(player, powerup);
            }
        }
    }

    /**
     * Handles powerup position selection
     * @param player who chose to use the powerup
     * @param coordinates chosen by the powerup holder
     */
    private void handlePowerupPositionSelection(GameCharacter player, Coordinates coordinates) {
        this.powerupsControllers.get(player).receivePosition(coordinates);
    }

    /**
     * Handles powerup position selection
     * @param player who chose to use the powerup
     * @param character target chosen by the powerup holder
     */
    private void handlePowerupTargetSelection(GameCharacter player, GameCharacter character) {
        this.powerupsControllers.get(player).receiveTarget(character);
    }

    /**
     * Handles weapon use selection
     * @param weapon chosen to use
     */
    private void handleWeaponUseSelection(Weapon weapon) {
        this.turnController.useWeapon(weapon);
    }

    /**
     * Handles effect selection
     * @param effectSelection effect chosen to be apply
     */
    private void handleEffectSelection(WeaponEffectOrderType effectSelection) {
        if (effectSelection == null) {
            this.turnController.handleEndAction();
            return;
        }

        Map<AmmoType, Integer> effectCost = this.effectsController.getEffectCost(effectSelection);
        if (effectCost != null && (effectCost.get(AmmoType.BLUE) > 0 ||
                effectCost.get(AmmoType.RED) > 0 || effectCost.get(AmmoType.YELLOW) > 0)) {
            this.effectSelection = effectSelection;
            send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.EFFECT,
                    this.turnController.getActivePlayer().getCharacter(), effectCost));
        } else {
            this.effectsController.effectSelected(effectSelection);
        }
    }

    /**
     * Handles effect combo selection
     * @param selection "y" if the player choose to use a combo, else "n"
     */
    private void handleEffectComboSelection(String selection) {
        boolean active = false;
        if (selection.equals("Y")) {
            active = true;
        }
        this.effectsController.activateCombo(active);

    }

    /**
     * Handles effect possibility pack selection
     * @param selection effect possibility pack chosen by the player
     */
    private void handleEffectPossibilitySelection(EffectPossibilityPack selection) {
        this.effectsController.effectApplication(selection);
    }

    void endEffects() {
        this.turnController.handleEndAction();
    }

    private void update(PaymentMessage message) {
        handlePayment(message.getAmmos(), ((PaymentSentMessage) message).getPowerups(), message.getPaymentType());
}

    /**
     * Handles the player payment
     * @param ammos paid
     * @param powerups paid
     * @param type of payment done
     */
    private void handlePayment(Map<AmmoType, Integer> ammos, List<Powerup> powerups, PaymentType type) {
        this.model.useAmmos(this.turnController.getActivePlayer(), ammos);
        for (Powerup p : powerups) {
            this.model.removePowerup(this.turnController.getActivePlayer(), p);
        }
        switch (type) {
            case WEAPON:
                this.turnController.paidWeapon();
                break;
            case EFFECT:
                this.effectsController.effectSelected(effectSelection);
                break;
            case RELOAD:
                this.turnController.reloadWeapon();
                break;
            case POWERUP:
                checkEnemyTurnPowerup();
        }
    }

    /**
     * Send a message to the active player
     * @param message to be sent
     */
    void send(SingleReceiverMessage message) {
        this.view.send(message);
    }

    /**
     * Send a message on broadcast
     * @param message to be sent
     */
    void sendAll(Message message) {
        this.view.sendAll(message);
    }

    /**
     * Knows if the player can pay ammo discarding a powerup
     * @return true if he can, else false
     */
    boolean canPayPowerup() {
        return !(this.turnController.getActivePlayer().getAvailableAmmos().get(AmmoType.YELLOW) == 0 &&
                this.turnController.getActivePlayer().getAvailableAmmos().get(AmmoType.RED) == 0 &&
                this.turnController.getActivePlayer().getAvailableAmmos().get(AmmoType.BLUE) == 0 &&
                this.turnController.getActivePlayer().getPowerups().size() - 1 == 0);
    }

    /**
     * Asks a player if he wants to use a powerup
     * @param players to be asked
     */
    void askPowerup(List<GameCharacter> players) {
        this.effectTargets = players;
        if (checkTagbackGrenadeCharacters(players).isEmpty() &&
                (this.turnController.getActivePlayer().getPowerupsByType(PowerupType.TARGETING_SCOPE).isEmpty() ||
                        !canPayPowerup())) {
            this.effectsController.handleEffectsQueue();
            return;
        } else if (checkTagbackGrenadeCharacters(players).isEmpty() &&
                !this.turnController.getActivePlayer().getPowerupsByType(PowerupType.TARGETING_SCOPE).isEmpty() &&
                canPayPowerup()) {
            checkTargetingScope();
            return;
        }
        checkTagbackGrande();
    }

    void checkTagbackGrande() {
        this.powerupRequests = checkTagbackGrenadeCharacters(this.effectTargets).size();
        for (GameCharacter player : checkTagbackGrenadeCharacters(this.effectTargets)) {
            send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP, player,
                    this.model.getPlayerByCharacter(player).getPowerupsByType(PowerupType.TAGBACK_GRENADE)));
        }
        this.model.pauseTurnTimer();
        this.powerupRequestsTimer = new Timer();
        this.powerupRequestsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                GameController.this.sendAll(new TimerMessage(TimerMessageType.STOP, TimerType.POWERUP));
                GameController.this.model.resumeTurnTimer();
                GameController.this.powerupRequests = 0;
                checkEnemyTurnPowerup();
            }
        }, this.model.getPowerupsTimerDuration());
    }

    void checkTargetingScope() {
        send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP, this.turnController.getActivePlayer().getCharacter(),
                this.turnController.getActivePlayer().getPowerupsByType(PowerupType.TARGETING_SCOPE)));
    }

    void checkEnemyTurnPowerup() {
        if (this.powerupRequests == 0) {
           if(!this.turnController.getActivePlayer().getPowerupsByType(PowerupType.TARGETING_SCOPE).isEmpty() && canPayPowerup()) {
               checkTargetingScope();
               return;
           }
           this.effectsController.handleEffectsQueue();
        }
    }

    /**
     * Gets the Tagback Grenade powerup holders
     * @param players to verify if they got a Tagback Grenade
     * @return List of game characters holders of a Tagback Grenade
     */
    List<GameCharacter> checkTagbackGrenadeCharacters(List<GameCharacter> players) {
        List<GameCharacter> validPlayers = new ArrayList<>();
        for (GameCharacter player : players) {
            Player toVerify = this.model.getPlayerByCharacter(player);
            if (toVerify.isConnected() && !toVerify.getPowerupsByType(PowerupType.TAGBACK_GRENADE).isEmpty() &&
                    toVerify.getPosition().canSee(this.turnController.getActivePlayer().getPosition())) {
                validPlayers.add(player);
            }
        }
        return validPlayers;
    }

    /**
     * Sets the effect chosen by the active player
     * @param effectSelection effect chosen by the active player
     */
    void setEffectSelection(WeaponEffectOrderType effectSelection) {
        this.effectSelection = effectSelection;
    }

    /**
     * Gets the effect targets
     * @return List of the game character targets
     */
    List<GameCharacter> getEffectTargets() {
        return this.effectTargets;
    }

}