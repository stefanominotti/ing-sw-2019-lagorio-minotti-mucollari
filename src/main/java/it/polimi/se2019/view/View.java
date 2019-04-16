package it.polimi.se2019.view;

import it.polimi.se2019.client.ClientInterface;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.se2019.view.RemoteState.*;

public abstract class View {

    private RemoteState state;
    private GameCharacter character;
    private ClientInterface client;
    private BoardView board;
    private List<PlayerBoard> enemyBoards;
    private SelfPlayerBoard selfPlayerBoard;

    public View(ClientInterface client) {
        this.client = client;
        this.enemyBoards = new ArrayList<>();
    }

    public void manageUpdate(Message message) throws IOException {
        String messageType = message.getMessageType().getName()
                .replace("it.polimi.se2019.model.messages.", "");;
        switch (messageType) {
            case "PlayerCreatedMessage":
                update((PlayerCreatedMessage) message);
                break;
            case "PlayerReadyMessage":
                update((PlayerReadyMessage) message);
                break;
            case "PlayerListMessage":
                update((PlayerListMessage) message);
                break;
            case "GameAlreadyStartedMessage":
                update((GameAlreadyStartedMessage) message);
                break;
            case "ClientDisconnectedMessage":
                update((ClientDisconnectedMessage) message);
                break;
        }
    }

    public void update(ClientDisconnectedMessage message) throws RemoteException {
        if (this.state == SETTINGNICKNAME || this.state == WAITINGSTART ) {
            String nickname = "";
            for (PlayerBoard board : this.enemyBoards) {
                if (board.getCharacter() == message.getCharacter()) {
                    nickname = board.getNickame();
                    this.enemyBoards.remove(board);
                    break;
                }
            }
            if (this.state == WAITINGSTART) {
                showMessage(nickname + " - " + message.getCharacter() + " disconnected");
            }
        }
    }

    public void update(PlayerCreatedMessage message) throws IOException {
        this.state = SETTINGNICKNAME;
        boolean valid = false;
        String nickname = "";
        while (!valid) {
            showMessage("Insert nickname: ");
            nickname = receiveTextInput();
            if (!nickname.equals("") && nickname != null && !message.getNicknames().contains(nickname)) {
                valid = true;
            }
            showMessage("invalid nickname!");
        }
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
            this.state = WAITINGSTART;
        } else {
            this.enemyBoards.add(new PlayerBoard(message.getCharacter(), message.getNickname()));
            if (this.state == WAITINGSTART) {
                showMessage(message.getNickname() + " - " + message.getCharacter() + " connected!");
            }
        }
    }

    public void update(GameAlreadyStartedMessage message) throws RemoteException {
        showMessage("Game already started, sorry");
        System.exit(0);
    }

    public void update(PlayerListMessage message) throws IOException {
        if (message.getCharacters().get(0) == this.character) {
            showMessage("You are the master, insert skulls number: ");
            boolean valid = false;
            String number;
            int skulls = 0;
            int arena = 0;
            while (!valid) {
                number = receiveTextInput();
                try {
                    skulls = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    showMessage("Invalid number, retry: ");
                    continue;
                }
                if (skulls < 3 || skulls > 8) {
                    showMessage("Skulls number must be between 3 and 8, retry: ");
                    continue;
                }
                valid = true;
            }
            valid = false;
            showMessage("Choose the arena {1, 2, 3, 4}");
            while (!valid) {
                number = receiveTextInput();
                try {
                    arena = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    showMessage("Invalid number, retry: ");
                    continue;
                }
                if (arena < 1 || arena > 4) {
                    showMessage("Arena number must be one of these {1, 2, 3, 4}");
                    continue;
                }
                valid = true;
            }
            this.client.send(new SetGameMessage(skulls, arena));
        } else {
            showMessage("Master player is setting game rules, wait");
        }
    }

    public abstract String receiveTextInput();

    public abstract void showMessage(String message);
}
