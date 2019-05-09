package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.model.*;
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
            case "GameAlreadyStartedMessage":
                update((GameAlreadyStartedMessage) message);
                break;
            case "NicknameDuplicatedMessage":
                update((NicknameDuplicatedMessage) message);
                break;
            case "ClientDisconnectedMessage":
                update((ClientDisconnectedMessage) message);
                break;
            case "StartGameSetupMessage":
                update((StartGameSetupMessage) message);
                break;
            case "GameSetupTimerStartedMessage":
                update((GameSetupTimerStartedMessage) message);
                break;
            case "GameSetupTimerResetMessage":
                update((GameSetupTimerResetMessage) message);
                break;
            case "SkullsSetMessage":
                update((SkullsSetMessage) message);
                break;
            case "GameSetMessage":
                update((GameSetMessage) message);
                break;
            case "GameSetupInterruptedMessage":
                update((GameSetupInterruptedMessage) message);
                break;
            case "MasterChangedMessage":
                update((MasterChangedMessage) message);
                break;
            case "ArenaFilledMessage":
                update((ArenaFilledMessage) message);
                break;
        }
    }

    void handleInput(String input) throws RemoteException {
        switch (this.state) {
            case TYPINGNICKNAME:
                this.client.send(new NicknameMessage(this.character, input));
                break;
            case SETTINGSKULLS:
                int skulls;
                try {
                    skulls = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    showMessage("Invalid number, retry: ");
                    break;
                }
                if (skulls < 3 || skulls > 8) {
                    showMessage("Skulls number must be between 3 and 8, retry: ");
                    break;
                }
                this.client.send(new SkullsMessage(skulls));
                break;
            case SETTINGARENA:
                if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
                    this.client.send(new ArenaMessage(input));
                    break;
                }
                else{
                    showMessage("Arena must be one of {1, 2, 3, 4}, retry:");
                    break;
                }
        }
    }

    private void update(MasterChangedMessage message) {
        if (message.getCharacter() == this.character) {
            showMessage("Master disconnected, you are the new master. Set skull number for the game:");
            this.state = SETTINGSKULLS;
        } else {
            showMessage("Master disconnected, the new master is setting up the game");
        }
    }

    private void update(GameSetupInterruptedMessage message) {
        showMessage("Too few players, game setup interrupted");
        this.state = WAITINGSTART;
    }

    private void update(GameSetupTimerResetMessage message) {
        if (this.state == WAITINGSTART) {
            showMessage("Need more players to start the game");
        }
    }

    private void update(GameSetupTimerStartedMessage message) {
        if (this.state == WAITINGSTART) {
            showMessage("Game setup will start in " + message.getTime() + " seconds...");
        }
    }

    private void update(LobbyFullMessage message) {
        showMessage("Lobby is full");
        System.exit(0);
    }

    private void update(GameAlreadyStartedMessage message) {
        showMessage("Game already started, sorry");
        System.exit(0);
    }

    private void update(NicknameDuplicatedMessage message) throws RemoteException {
        showMessage("Nickname already used. Insert another nickname: ");
    }

    private void update(PlayerCreatedMessage message) throws RemoteException {
        this.character = message.getCharacter();
        showMessage("Insert nickname: ");
        this.state = TYPINGNICKNAME;
    }

    private void update(PlayerReadyMessage message) {
        if (message.getCharacter() == this.character) {
            this.selfPlayerBoard = new SelfPlayerBoard(message.getCharacter(), message.getNickname());
            showMessage("Nickname " + message.getNickname() + " accepted! You are " + message.getCharacter());
            for (Map.Entry<GameCharacter, String> player : message.getOtherPlayers().entrySet()) {
                boolean present = false;
                for (PlayerBoard p : this.enemyBoards) {
                    if (p.getCharacter() == player.getKey()) {
                        present = true;
                        break;
                    }
                }
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
                if (present) {
                    continue;
                }
                this.enemyBoards.add(new PlayerBoard(player.getKey(), player.getValue()));
            }
            this.state = WAITINGSTART;
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

    private void update(StartGameSetupMessage message) {
        if (message.getCharacter() == this.character) {
            showMessage("You are the master, set skull number for the game:");
            this.state = SETTINGSKULLS;

        } else {
            showMessage("Master player is setting up the game, wait");
            this.state = WAITINGSETUP;
        }
    }
    private void update(SkullsSetMessage message) {
        showMessage("OK, now select the arena between {1, 2, 3, 4}:");
        this.state = SETTINGARENA;
    }

    private void update(GameSetMessage message) {
        showMessage("Master choose " + message.getSkulls() + " skulls and arena number " + message.getArenaNumber());
        showMessage("This is the arena:");
        StringBuilder builder = new StringBuilder();
        List<SquareView> squares = new ArrayList<>();
        for(Coordinates square : message.getArenaColors().keySet()){
            RoomColor color = message.getArenaColors().get(square);
            Boolean spawn = message.getArenaSpawn().get(square);
            squares.add(new SquareView(square.getX(), square.getY(), color, spawn));
            String text = "Square [" + square.getX() + ", " + square.getY() + "] is " + color;
            builder.append(text);
            if(spawn) {
                builder.append(" [Spawn]");
            }
            builder.append("\n");
        }
        this.board = new BoardView(message.getSkulls(), squares);
        showMessage(builder.toString());
    }

    private void update(ArenaFilledMessage message) {
        for(Map.Entry<Coordinates, AmmoTile> square: message.getAmmos().entrySet()) {
            this.board.getSquareByCoordinates(square.getKey().getX(), square.getKey().getY())
                    .setAvailableAmmoTile(square.getValue());
        }
        for(Map.Entry<Coordinates, List<Weapon>> store: message.getStores().entrySet()) {
            SquareView square = this.board.getSquareByCoordinates(store.getKey().getX(), store.getKey().getY());
            for(Weapon weapon : store.getValue()) {
                square.addStoreWeapon(weapon);
            }
        }
        showMessage("Ammo tiles and weapons placed:");
        StringBuilder builder = new StringBuilder();
        for(SquareView square : this.board.getSquares()) {
            String text = "[" + square.getX() + ", " + square.getY() + "], " + square.getColor();
            builder.append(text);
            if(!square.isSpawn()) {
                builder.append( "\nTile: [");
                if(square.getAvailableAmmoTile().hasPowerup()) {
                    builder.append("POWERUP, ");
                }
                for(Map.Entry<AmmoType, Integer> ammos : square.getAvailableAmmoTile().getAmmos().entrySet()) {
                    if(ammos.getValue() != 0) {
                        String toAppend = ammos.getKey() + "x" + ammos.getValue() + ", ";
                        builder.append(toAppend);
                    }
                }
            } else {
                builder.append( "\nStore: [");
                for(Weapon weapon : square.getStore()) {
                    String toAppend = weapon + ", ";
                    builder.append(toAppend);
                }
            }
            builder.setLength(builder.length() - 1);
            builder.append("]");
            showMessage(builder.toString());
            builder = new StringBuilder();
        }

    }

    public abstract void showMessage(String message);
}
