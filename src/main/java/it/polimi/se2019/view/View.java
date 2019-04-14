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
        }
    }

    void update(PlayerCreatedMessage message) throws RemoteException {
        this.state = TYPINGNICKNAME;
        showMessage("Insert nickname: ");
        String nickname = receiveTextInput();
        this.character = message.getCharacter();
        this.client.send(new NicknameMessage(this.character, nickname));
    }

    void update(PlayerReadyMessage message) {
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

    public abstract String receiveTextInput();

    public abstract void showMessage(String message);
}
