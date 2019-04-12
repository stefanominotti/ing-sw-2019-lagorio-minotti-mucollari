package it.polimi.se2019.view;

import it.polimi.se2019.client.ClientInterface;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class View {

    private GameCharacter character;
    private ClientInterface client;
    private BoardView board;
    private List<PlayerBoard> enemyBoards;
    private SelfPlayerBoard selfPlayerBoard;

    public View(ClientInterface client) {
        this.client = client;
        this.enemyBoards = new ArrayList<>();
    }

    public void manageUpdate(Message message) throws RemoteException {
        String messageType = message.getMessageType().getName()
                .replace("it.polimi.se2019.model.messages.", "");;
        switch (messageType) {
            case "PlayerCreatedMessage":
                update((PlayerCreatedMessage) message);
                break;
            case "PlayerReadyMessage":
                update((PlayerReadyMessage) message);
                break;
        }
    }

    public void update(PlayerCreatedMessage message) throws RemoteException {
        showMessage("Insert nickname: ");
        String nickname = receiveTextInput();
        this.character = message.getCharacter();
        this.client.send(new NicknameMessage(this.character, nickname));
    }

    public void update(PlayerReadyMessage message) throws RemoteException {
        if (message.getCharacter() == this.character) {
            this.selfPlayerBoard = new SelfPlayerBoard(message.getCharacter(), message.getNickname());
            showMessage("Nickname " + message.getNickname() + " accepted! You are " + message.getCharacter());
            for (Map.Entry<GameCharacter, String> player : message.getOtherPlayers().entrySet()) {
                this.enemyBoards.add(new PlayerBoard(player.getKey(), player.getValue()));
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        } else {
            this.enemyBoards.add(new PlayerBoard(message.getCharacter(), message.getNickname()));
            showMessage(message.getNickname() + " - " + message.getCharacter() + " connected!");
        }
    }

    public abstract String receiveTextInput();

    public abstract void showMessage(String message);
}
