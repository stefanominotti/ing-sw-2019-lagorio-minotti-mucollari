package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.view.VirtualView;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private Board model;
    private TurnController turnController;
    private PowerupsController powerupsController;
    private VirtualView view;

    public GameController(Board board, VirtualView view) {
        this.model = board;
        this.turnController = new TurnController(this.model, this);
        this.powerupsController = new PowerupsController(this.model, this, this.turnController);
        this.view = view;
    }

    @Override
    public void update(Observable view, Object message) {
        String messageType = ((Message) message).getMessageType().getName()
                .replace("it.polimi.se2019.model.messages.", "");
        switch (messageType) {
            case "ClientReadyMessage":
                update((ClientReadyMessage) message);
                break;
            case "ClientReconnectedMessage":
                update((ClientReconnectedMessage) message);
                break;
            case "ClientDisconnectedMessage":
                update((ClientDisconnectedMessage) message);
                break;
            case "SkullsMessage":
                update((SkullsMessage) message);
                break;
            case "ArenaMessage":
                update((ArenaMessage) message);
                break;
            case "StartTurnMessage":
                update((StartTurnMessage) message);
                break;
            case "PowerupDiscardMessage":
                update((PowerupDiscardMessage) message);
                break;
            case "ActionSelectedMessage":
                update((ActionSelectedMessage) message);
                break;
            case "MovementSelectedMessage":
                update((MovementSelectedMessage) message);
                break;
            case "PickupSelectedMessage":
                update((PickupSelectedMessage) message);
                break;
            case "WeaponPickupMessage":
                update((WeaponPickupMessage) message);
                break;
            case "WeaponSwitchMessage":
                update((WeaponSwitchMessage) message);
                break;
            case "RechargeWeaponMessage":
                update((RechargeWeaponMessage) message);
                break;
            case "WeaponPaymentMessage":
                update((WeaponPaymentMessage) message);
                break;
            case "UsePowerupMessage":
                update((UsePowerupMessage) message);
                break;
            case "PowerupPositionMessage":
                update((PowerupPositionMessage) message);
                break;
            case "PowerupTargetMessage":
                update((PowerupTargetMessage) message);
                break;
        }
    }

    private void update(ClientReadyMessage message) {
        this.model.addPlayer(message.getCharacter(), message.getNickname(), message.getToken());
    }

    private void update(ClientReconnectedMessage message) {
        int counter = 0;
        Player player = this.model.getPlayerByCharacter(message.getCharacter());
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

    private void update(ClientDisconnectedMessage message) {
        this.model.handleDisconnection(message.getCharacter());
    }

    private void update(SkullsMessage message) {
        this.model.setSkulls(message.getSkulls());
    }

    private void update(ArenaMessage message) {
        this.model.createArena(message.getArena());
    }

    private void update(StartTurnMessage message) {
        this.turnController.startTurn(message.getTurnType(), message.getPlayer());
    }

    private void update(PowerupDiscardMessage message) {
        this.turnController.handlePowerupDiscarded(message.getPowerup());
    }

    private void update(ActionSelectedMessage message) {
        this.turnController.handleAction(message.getAction());
    }

    private void update(MovementSelectedMessage message) {
        this.turnController.movementAction(message.getCoordinates());
    }

    private void update(PickupSelectedMessage message) {
        this.turnController.pickupAction(message.getCoordinates());
    }

    private void update(WeaponPickupMessage message) {
        this.turnController.pickupWeapon(message.getWeapon());
    }

    private void update(WeaponSwitchMessage message) {
        this.turnController.switchWeapon(message.getWeapon());
    }

    private void update(RechargeWeaponMessage message) {
        this.turnController.rechargeWeapon(message.getWeapon());
    }

    private void update(WeaponPaymentMessage message) {
        this.turnController.payWeapon(message.getPaidAmmos(), message.getPaidPowerups());
    }

    private void update(UsePowerupMessage message) {
        if (message.getPowerup() == null && this.turnController.getActiveplayer().getCharacter() == message.getPlayer()) {
            this.turnController.cancelPowerup();
        } else if (message.getPowerup() == null && this.turnController.getActiveplayer().getCharacter() != message.getPlayer()) {
            // powerup usati nel turno avversario
        } else {
            this.powerupsController.startEffect(message.getPlayer(), message.getPowerup());
        }
    }

    private void update(PowerupPositionMessage message) {
        this.powerupsController.receivePosition(message.getPosition());
    }

    private void update(PowerupTargetMessage message) {
        this.powerupsController.receiveTarget(message.getTarget());
    }

    void send(SingleReceiverMessage message) {
        try {
            this.view.send(message);
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error on sending message", e);
        }
    }

    void sendAll(Message message) {
        try {
            this.view.sendAll(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void sendOthers(GameCharacter character, Message message) {
        try {
            this.view.sendOther(character, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}