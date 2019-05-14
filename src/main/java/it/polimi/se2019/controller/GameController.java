package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.TurnType;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.view.VirtualView;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

public class GameController implements Observer {

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
                try {
                    update((StartTurnMessage) message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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

    private void update(StartTurnMessage message) throws RemoteException {
        this.turnController.startTurn(message.getTurnType(), message.getPlayer());
    }

    void send(SingleReceiverMessage message) throws RemoteException {
        this.view.send(message);
    }

    void sendAll(Message message) throws RemoteException {
        this.view.sendAll(message);
    }
}
