package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.messages.ClientReadyMessage;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.NicknameMessage;

import java.util.Observable;
import java.util.Observer;

public class GameController implements Observer {

    private Board model;

    public GameController(Board board) {
        this.model = board;
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
        }
    }

    private void update(ClientReadyMessage message) {
        this.model.addPlayer(message.getCharacter());
    }

    private void update(NicknameMessage message) {
        this.model.setPlayerNickname(message.getCharacter(), message.getNickname());
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
