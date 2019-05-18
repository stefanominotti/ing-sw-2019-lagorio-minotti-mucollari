package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.RoomColor;
import it.polimi.se2019.model.TurnType;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.VirtualView;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController implements Observer {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    private Board model;
    private TurnController turnController;
    private VirtualView view;

    public GameController(Board board, VirtualView view) {
        this.model = board;
        this.turnController = new TurnController(this.model, this);
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
            case "NicknameMessage":
                update((NicknameMessage) message);
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
            case "PowerupSelectedMessage":
                update((PowerupSelectedMessage) message);
                break;
            case "ActionSelectedMessage":
                update((ActionSelectedMessage) message);
                break;
        }
    }

    private void update(ClientReadyMessage message) {
        this.model.addPlayer(message.getCharacter());
    }

    private void update(NicknameMessage message) {
        this.model.setPlayerNickname(message.getCharacter(), message.getNickname());
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

    private void update(PowerupSelectedMessage message) {
        this.turnController.handlePowerupDiscarded(message.getPowerup());
    }

    private void update(ActionSelectedMessage message) {
        this.turnController.handleAction(message.getAction());
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
}