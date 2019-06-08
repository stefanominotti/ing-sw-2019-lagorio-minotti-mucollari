package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.BoardMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientReadyMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentSentMessage;
import it.polimi.se2019.model.messages.player.PlayerReadyMessage;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.view.VirtualView;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

public class GameController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private Board model;
    private TurnController turnController;
    private PowerupsController powerupsController;
    private VirtualView view;
    private boolean gameStarted;
    private EffectsController effectsController;

    public GameController(Board board, VirtualView view) {
        this.model = board;
        this.effectsController = new EffectsController(this.model, this);
        this.turnController = new TurnController(this.model, this, this.effectsController);
        this.powerupsController = new PowerupsController(this.model, this, this.turnController);
        this.view = view;
    }

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
        }

    }

    private void handleClientReady(GameCharacter character, String nickname, String token) {
        this.model.addPlayer(character, nickname, token);
    }

    private void handleClientReconnected(GameCharacter character) {
        int counter = 0;
        Player player = this.model.getPlayerByCharacter(character);
        player.connect();
        this.model.createModelView(player);
        sendAll(new PlayerReadyMessage(player.getCharacter(), player.getNickname()));
        for(Player boardPlayer : this.model.getPlayers()) {
            if(boardPlayer.isConnected()) {

               counter++;
            } else {
                return;
            }
        }
        if(counter == this.model.getPlayers().size()) {
            this.model.startTurn(this.model.getPlayers().get(this.model.getCurrentPlayer()));
        }
        //continuare la partita
    }

    private void handleClientDisconnected(GameCharacter character) {
        this.model.handleDisconnection(character);
        if (this.model.getPlayerByCharacter(character) == this.turnController.getActivePlayer() && this.gameStarted) {
            this.turnController.endTurn();
        }
    }

    private void update(BoardMessage message) {
        switch(message.getType()) {
            case ARENA:
                handleArenaReceived(((ArenaMessage) message).getArena());
                break;
            case SKULLS:
                handleSkullsReceived(((SkullsMessage) message).getSkulls());
                break;
        }
    }

    private void handleArenaReceived(String arena) {
        this.model.createArena(arena);
    }

    private void handleSkullsReceived(int skulls) {
        this.model.setSkulls(skulls);
    }

    private void update(TurnMessage message) {
        this.turnController.startTurn(message.getTurnType(), message.getCharacter());
        this.gameStarted = true;
    }

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
                handlePowerupTargetSelection((GameCharacter) message.getSelection());
                break;
            case POWERUP_POSITION:
                handlePowerupPositionSelection((Coordinates) message.getSelection());
                break;
            case RELOAD:
                handleReloadSelection((Weapon) message.getSelection());
                break;
            case DISCARD_POWERUP:
                handlePowerupDiscardSelection((Powerup) message.getSelection());
                break;
            case USE_POWERUP:
                handleUsePowerupSelection(message.getCharacter(), (Powerup) message.getSelection());
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
        }
    }

    private void handlePowerupDiscardSelection(Powerup powerup) {
        this.turnController.handlePowerupDiscarded(powerup);
    }

    private void handleActionSelection(ActionType action) {
        this.turnController.handleAction(action);
    }

    private void handleMovementSelection(Coordinates coordinates) {
        this.turnController.movementAction(coordinates);
    }

    private void handlePickupSelection(Coordinates coordinates) {
        this.turnController.pickupAction(coordinates);
    }

    private void handleWeaponPickupSelection(Weapon weapon) {
        this.turnController.pickupWeapon(weapon);
    }

    private void handleWeaponSwitchSelection(Weapon weapon) {
        this.turnController.switchWeapon(weapon);
    }

    private void handleReloadSelection(Weapon weapon) {
        this.turnController.reloadWeapon(weapon);
    }

    private void handleUsePowerupSelection(GameCharacter player, Powerup powerup) {
        if (powerup == null && this.turnController.getActivePlayer().getCharacter() == player) {
            this.turnController.cancelPowerup();
        } else if (powerup == null && this.turnController.getActivePlayer().getCharacter() != player) {
            // powerup usati nel turno avversario
        } else {
            this.powerupsController.startEffect(player, powerup);
        }
    }

    private void handlePowerupPositionSelection(Coordinates coordinates) {
        this.powerupsController.receivePosition(coordinates);
    }

    private void handlePowerupTargetSelection(GameCharacter character) {
        this.powerupsController.receiveTarget(character);
    }

    private void handleWeaponUseSelection(Weapon weapon) {
        this.turnController.useWeapon(weapon);
    }

    private void handleEffectSelection(WeaponEffectOrderType effectSelection) {
        this.effectsController.effectSelected(effectSelection);
    }

    private void handleEffectComboSelection(String selection) {
        if (selection.equals("Y")) {
            this.effectsController.activateCombo();
        }
    }

    private void handleEffectPossibilitySelection(EffectPossibilityPack selection) {
        this.effectsController.effectApplication(selection);
    }

    private void update(PaymentMessage message) {
        switch (message.getPaymentType()) {
            case WEAPON:
                this.turnController.payWeapon(message.getAmmos(), ((PaymentSentMessage) message).getPowerups());
                break;
        }

    }

    void send(SingleReceiverMessage message) {
        this.view.send(message);
    }

    void sendAll(Message message) {
        this.view.sendAll(message);
    }

    void sendOthers(GameCharacter character, Message message) {
        this.view.sendOthers(character, message);
    }
}