package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.se2019.view.ClientState.*;

public abstract class View {

    private GameCharacter character;
    private AbstractClient client;
    private BoardView board;
    private List<PlayerBoard> enemyBoards;
    private SelfPlayerBoard selfPlayerBoard;
    private ClientState state;

    View(AbstractClient client) {
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
            case "LobbyFullMessage":
                update((LobbyFullMessage) message);
                break;
            case "NicknameDuplicatedMessage":
                update((NicknameDuplicatedMessage) message);
                break;
            case "ClientDisconnectedMessage":
                update((ClientDisconnectedMessage) message);
                break;
        }
    }

    private void update(LobbyFullMessage message) {
        showMessage("Lobby is full");
        System.exit(0);
    }

    private void update(NicknameDuplicatedMessage message) throws RemoteException {
        showMessage("Nickname already used. Insert another nickname: ");
        String nickname = receiveTextInput();
        this.client.send(new NicknameMessage(this.character, nickname));
    }

    private void update(PlayerCreatedMessage message) throws RemoteException {
        this.state = TYPINGNICKNAME;
        showMessage("Insert nickname: ");
        String nickname = receiveTextInput();
        this.character = message.getCharacter();
        this.client.send(new NicknameMessage(this.character, nickname));
    }

    private void update(PlayerReadyMessage message) {
        if (message.getCharacter() == this.character) {
            this.state = WAITINGSTART;
            this.selfPlayerBoard = new SelfPlayerBoard(message.getCharacter(), message.getNickname());
            showMessage("Nickname " + message.getNickname() + " accepted! You are " + message.getCharacter());
            for (Map.Entry<GameCharacter, String> player : message.getOtherPlayers().entrySet()) {
                this.enemyBoards.add(new PlayerBoard(player.getKey(), player.getValue()));
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        } else {
            this.enemyBoards.add(new PlayerBoard(message.getCharacter(), message.getNickname()));
            if (this.state == WAITINGSTART) {
                showMessage(message.getNickname() + " - " + message.getCharacter() + " connected!");
            }
        }
    }

    private void update(ClientDisconnectedMessage message) {
        if (this.state == TYPINGNICKNAME || this.state == WAITINGSTART) {
            String nickname = "";
            for (PlayerBoard board : this.enemyBoards) {
                if (board.getCharacter() == message.getCharacter()) {
                    nickname = board.getNickname();
                    this.enemyBoards.remove(board);
                    break;
                }
            }
            if (this.state == WAITINGSTART) {
                showMessage(nickname + " - " + message.getCharacter() + " disconnected");
            }
        }
    }

    public abstract String receiveTextInput();

    public abstract void showMessage(String message);
}
