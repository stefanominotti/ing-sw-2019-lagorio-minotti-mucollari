package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.messages.*;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class GameController implements Observer {


    private Board model;
    Timer timer;

    public GameController(Board board) {
        this.model = board;
        this.timer = new Timer();
    }

    void newGame(int playersNumber) {}

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
            case "SetGameMessage":
                update((SetGameMessage) message);
                break;
        }
    }

    public void update(ClientReadyMessage message) {
        this.model.addPlayer(message.getCharacter());
    }

    public void update(ClientDisconnectedMessage message) {
        this.model.handleDisconnection(message.getCharacter());
    }

    public void update(NicknameMessage message) {
        this.model.setPlayerNickname(message.getCharacter(), message.getNickname());
        if (this.model.getPlayersCount() == 3) {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finalizePlayersCreation();
                }
            }, 10*1000);
        }
    }

    public void update(SetGameMessage message){
        this.model.initializeGame(message.getSkulls(), message.getArena());
    }

    private void finalizePlayersCreation() {
        if(this.model.getPlayersCount() >= 3) {
            this.model.finalizePlayersCreation();
        }
    }

    public void update(CardPressedEvent event) {}

    public void update(ActionSelectedEvent event) {}

    public void update(ShotEvent event) {}

    public void update(PickupEvent event) {}

    public void update(MoveEvent event) {}

    public void update(RealoadEvent event) {}

    public void update(EffectSelectedEvent event) {}

    public void update(TargetSelectedEvent event) {}

    public void update(PowerupCardSelectedEvent event) {}
}
