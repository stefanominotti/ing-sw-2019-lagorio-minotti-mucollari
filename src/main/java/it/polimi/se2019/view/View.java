package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.ammos.AmmosMessage;
import it.polimi.se2019.model.messages.board.*;
import it.polimi.se2019.model.messages.client.*;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentSentMessage;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.player.*;
import it.polimi.se2019.model.messages.powerups.PowerupMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessageType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SelectionReceivedMessage;
import it.polimi.se2019.model.messages.selections.SelectionSentMessage;
import it.polimi.se2019.model.messages.timer.TimerMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.weapon.WeaponMessage;
import it.polimi.se2019.model.messages.weapon.WeaponSwitchMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.se2019.view.ClientState.*;

public abstract class View {

    private static final Logger LOGGER = Logger.getLogger(View.class.getName());

    private GameCharacter character;
    private AbstractClient client;
    private BoardView board;
    private List<PlayerBoard> enemyBoards;
    private SelfPlayerBoard selfPlayerBoard;
    private ClientState state;
    private List<ActionType> turnActions;
    private List<Coordinates> actionCoordinates;
    private List<Weapon> weaponsSelectionList;
    private List<GameCharacter> charactersAvailable;
    private Map<AmmoType, Integer> paymentRequired;
    private Map<AmmoType, Integer> paidAmmos;
    private List<Powerup> paidPowerups;
    private Map<AmmoType, Integer> availableAmmos;
    private List<Powerup> availablePowerups;
    private List<GameCharacter> availableTargets;
    private PaymentType currentPayment;
    private PowerupType activePowerup;

    View(AbstractClient client) {
        this.client = client;
        this.enemyBoards = new ArrayList<>();
        this.paidAmmos = new EnumMap<>(AmmoType.class);
        for (AmmoType type : AmmoType.values()) {
            this.paidAmmos.put(type, 0);
        }
        this.paidPowerups = new ArrayList<>();
    }

    public void manageUpdate(Message message) throws RemoteException {
        switch (message.getMessageType()) {
            case NICKNAME_MESSAGE:
                update((NicknameMessage) message);
                break;
            case PLAYER_MESSAGE:
                update((PlayerMessage) message);
                break;
            case CLIENT_MESSAGE:
                update((ClientMessage) message);
                break;
            case TIMER_MESSAGE:
                update((TimerMessage) message);
                break;
            case BOARD_MESSAGE:
                update((BoardMessage) message);
                break;
            case POWERUP_MESSAGE:
                update((PowerupMessage) message);
                break;
            case AMMOS_MESSAGE:
                update((AmmosMessage) message);
                break;
            case WEAPON_MESSAGE:
                update((WeaponMessage) message);
                break;
            case TURN_MESSAGE:
                update((TurnMessage) message);
                break;
            case PAYMENT_MESSAGE:
                update((PaymentMessage) message);
                break;
            case SELECTION_SENT_MESSAGE:
                update((SelectionSentMessage) message);
                break;
        }
    }

    private PlayerBoard getBoardByCharacter(GameCharacter character) {
        for(PlayerBoard board : this.enemyBoards) {
            if(board.getCharacter() == character) {
                return board;
            }
        }
        return null;
    }

    void handleInput(String input) throws RemoteException {
        if (input == null) {
            return;
        }
        switch (this.state) {
            case TYPINGNICKNAME:
                if (input.equals("")) {
                    showMessage("Invalid username, retry: ");
                    break;
                }
                this.client.send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
                break;
            case CHOOSINGCHARACTER:
                int select;
                try {
                    select = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    showMessage("Invalid number, retry: ");
                    break;
                }
                if (select > this.charactersAvailable.size() || select <= 0) {
                    showMessage("Invalid number, retry: ");
                    break;
                }
                this.client.send(new CharacterMessage(this.charactersAvailable.get(select-1), generateToken()));
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
                showMessage("Arena must be [1, 2, 3, 4]:, retry:");
                break;
            case DISCARDSPAWN:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.selfPlayerBoard.getPowerups().size()) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.DISCARD_POWERUP, this.character,
                            this.selfPlayerBoard.getPowerups().get(number - 1)));
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
            case SELECTACTION:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.turnActions.size() + 3) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    if (number == 1) {
                        showMessage(this.board.killshotTrackToString());
                        showMessage(this.board.arenaToString());
                        showActions();
                        break;
                    }
                    if (number == 2) {
                        showMessage(this.selfPlayerBoard.toString());
                        showActions();
                        break;
                    }
                    if (number == 3) {
                        int index = 1;
                        StringBuilder builder = new StringBuilder("Select a player to show:\n");
                        for (PlayerBoard board : this.enemyBoards) {
                            builder.append("[" + index + "] - " + board.getCharacter() + "\n");
                            index++;
                        }
                        builder.setLength(builder.length() - 1);
                        showMessage(builder.toString());
                        this.state = SELECTBOARDTOSHOW;
                        break;
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.ACTION, this.character,
                            this.turnActions.get(number - 4)));
                    this.turnActions = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
            case SELECTBOARDTOSHOW:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.enemyBoards.size()) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    showMessage(this.enemyBoards.get(number - 1).toString());
                    showActions();
                    this.state = SELECTACTION;
                    break;
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
            case SELECTMOVEMENT:
                if (input.toUpperCase().equals("C")) {
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.ACTION, this.character,
                            ActionType.CANCEL));
                    break;
                }
                if (input.split(",").length != 2) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                try {
                    int x = Integer.parseInt(input.split(",")[0]);
                    int y = Integer.parseInt(input.split(",")[1]);
                    boolean valid = false;
                    for (Coordinates c : this.actionCoordinates) {
                        if (c.getX() == x && c.getY() == y) {
                            this.client.send(new SelectionReceivedMessage(SelectionMessageType.MOVE, this.character,
                                    new Coordinates(x, y)));
                            this.actionCoordinates = new ArrayList<>();
                            valid = true;
                            break;
                        }
                    }
                    if(!valid) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    break;

                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
            case SELECTPICKUP:
                if (input.toUpperCase().equals("C")) {
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.ACTION, this.character,
                            ActionType.CANCEL));
                    break;
                }
                if (input.split(",").length != 2) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                try {
                    int x = Integer.parseInt(input.split(",")[0]);
                    int y = Integer.parseInt(input.split(",")[1]);
                    boolean valid = false;
                    for (Coordinates c : this.actionCoordinates) {
                        if (c.getX() == x && c.getY() == y) {
                            this.client.send(new SelectionReceivedMessage(SelectionMessageType.PICKUP, this.character,
                                    new Coordinates(x, y)));
                            this.actionCoordinates = new ArrayList<>();
                            valid = true;
                            break;
                        }
                    }
                    if(!valid) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    break;

                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
            case SELECTWEAPON:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.weaponsSelectionList.size()) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.PICKUP_WEAPON, this.character,
                            this.weaponsSelectionList.get(number - 1)));
                    this.weaponsSelectionList = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
            case SWITCHWEAPON:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.weaponsSelectionList.size()) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.SWITCH, this.character,
                            this.weaponsSelectionList.get(number - 1)));
                    this.weaponsSelectionList = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
            case RECHARGEWEAPON:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.weaponsSelectionList.size() + 1) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    number = number - 1;
                    Weapon selection;
                    if (number == this.weaponsSelectionList.size()) {
                        selection = null;
                    } else {
                        selection = this.weaponsSelectionList.get(number);
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.RELOAD, this.character,
                            selection));
                    this.weaponsSelectionList = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
            case PAYMENT:
                try {
                    int number = Integer.parseInt(input);
                    List<AmmoType> payableAmmos = new ArrayList<>();
                    List<Powerup> payablePowerups = new ArrayList<>();
                    for (Map.Entry<AmmoType, Integer> ammo : this.availableAmmos.entrySet()) {
                        if (ammo.getValue() != 0 && this.paymentRequired.keySet().contains(ammo.getKey()) &&
                                this.paymentRequired.get(ammo.getKey()) != 0) {
                            payableAmmos.add(ammo.getKey());
                        }
                    }
                    for (Powerup p : this.availablePowerups) {
                        if (this.paymentRequired.keySet().contains(p.getColor()) &&
                                this.paymentRequired.get(p.getColor()) != 0) {
                            payablePowerups.add(p);
                        }
                    }

                    if (0 >= number || number > payableAmmos.size() + payablePowerups.size() + 1) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    number = number - 1;
                    int newValue;
                    if (number < payableAmmos.size()) {
                        newValue = this.paidAmmos.get(payableAmmos.get(number)) + 1;
                        this.paidAmmos.put(payableAmmos.get(number), newValue);
                        newValue = this.availableAmmos.get(payableAmmos.get(number)) - 1;
                        this.availableAmmos.put(payableAmmos.get(number), newValue);
                        newValue = this.paymentRequired.get(payableAmmos.get(number)) - 1;
                        this.paymentRequired.put(payableAmmos.get(number), newValue);
                    } else {
                        number -= payableAmmos.size();
                        this.paidPowerups.add(payablePowerups.get(number));
                        this.availablePowerups.remove(payablePowerups.get(number));
                        newValue = this.paymentRequired.get(payablePowerups.get(number).getColor()) - 1;
                        this.paymentRequired.put(payablePowerups.get(number).getColor(), newValue);
                    }

                    boolean paid = true;
                    for (Map.Entry<AmmoType, Integer> ammo : this.paymentRequired.entrySet()) {
                        if (ammo.getValue() != 0) {
                            paid = false;
                            break;
                        }
                    }

                    if(paid) {
                        this.client.send(new PaymentSentMessage(this.currentPayment, this.character, this.paidAmmos,
                                this.paidPowerups));
                        this.paymentRequired = new EnumMap<>(AmmoType.class);
                        this.paidAmmos = new EnumMap<>(AmmoType.class);
                        for (AmmoType type : AmmoType.values()) {
                            this.paidAmmos.put(type, 0);
                        }
                        this.paidPowerups = new ArrayList<>();
                    } else {
                        requirePayment();
                    }
                    break;
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
            case USEPOWERUP:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.availablePowerups.size() + 1) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    if (number == this.availablePowerups.size() + 1) {
                        this.client.send(new SelectionReceivedMessage(SelectionMessageType.USE_POWERUP, this.character,
                                null));
                        this.availablePowerups = new ArrayList<>();
                        break;
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.USE_POWERUP, this.character,
                            this.availablePowerups.get(number - 1)));
                    this.activePowerup = this.availablePowerups.get(number - 1).getType();
                    this.availablePowerups = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
            case SELECTPOWERUPPOSITION:
                if (input.split(",").length != 2) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                try {
                    int x = Integer.parseInt(input.split(",")[0]);
                    int y = Integer.parseInt(input.split(",")[1]);
                    boolean valid = false;
                    for (Coordinates c : this.actionCoordinates) {
                        if (c.getX() == x && c.getY() == y) {
                            this.client.send(new SelectionReceivedMessage(SelectionMessageType.POWERUP_POSITION,
                                    this.character, new Coordinates(x, y)));
                            this.actionCoordinates = new ArrayList<>();
                            valid = true;
                            break;
                        }
                    }
                    if(!valid) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    break;

                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
            case SELECTPOWERUPTARGET:
                try {
                    int number = Integer.parseInt(input);
                    if (0 >= number || number > this.availableTargets.size()) {
                        showMessage("Invalid input, retry:");
                        break;
                    }
                    this.client.send(new SelectionReceivedMessage(SelectionMessageType.POWERUP_TARGET, this.character,
                            this.availableTargets.get(number - 1)));
                    this.availableTargets = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
        }
    }

    private void update(NicknameMessage message) {
        switch (message.getType()) {
            case REQUIRE:
                handleNicknameRequest();
                break;
            case DUPLICATED:
                handleNicknameDuplicated();
                break;
        }
    }

    private void handleNicknameRequest() {
        showMessage("Insert nickname: ");
        this.state = TYPINGNICKNAME;
    }

    private void handleNicknameDuplicated() {
        showMessage("Nickname is already in use. Insert another nickname: ");
        this.state = TYPINGNICKNAME;
    }

    private void update(TimerMessage message) {
        switch(message.getTimerType()) {
            case SETUP:
                handleGameSetupTimer(message.getType(), message.getTime());
        }
    }

    private void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (this.state == WAITINGSTART) {
            switch (action) {
                case START:
                    showMessage("Game setup will start in " + duration + " seconds...");
                    break;
                case STOP:
                    showMessage("Need more players to start the game");
                    break;
            }
        }
    }

    private void update(PlayerMessage message) {
        switch (message.getType()) {
            case CREATED:
                handlePlayerCreated(message.getCharacter(), ((PlayerCreatedMessage) message).getNickname(),
                        ((PlayerCreatedMessage) message).getOtherPlayers());
                break;
            case READY:
                handleReadyPlayer(message.getCharacter(), ((PlayerReadyMessage) message).getNickname());
                break;
            case SPAWNED:
                handleSpawnedPlayer(message.getCharacter(), ((SpawnMessage) message).getCoordinates());
                break;
            case SKULLS_SET:
                handleSkullsSet();
                break;
            case MASTER_CHANGED:
                handleMasterChanged(message.getCharacter());
                break;
            case START_SETUP:
                handleStartSetup(message.getCharacter());
                break;
            case MOVE:
                handleMovement(message.getCharacter(), ((MovementMessage) message).getCoordinates());
                break;
        }
    }

    private void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter,
            String> otherPlayers) {
        this.character = character;
        this.selfPlayerBoard = new SelfPlayerBoard(character, nickname);
        showMessage("Nickname " + nickname + " accepted! You are " + character);
        for (Map.Entry<GameCharacter, String> player : otherPlayers.entrySet()) {
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
    }

    private void handleReadyPlayer(GameCharacter character, String nickname) {
        if (this.state == WAITINGSTART) {
            this.enemyBoards.add(new PlayerBoard(character, nickname));
        }
        if(character != this.character) {
            showMessage(nickname + " - " + character + " connected!");
        }
    }

    private void handleSpawnedPlayer(GameCharacter character, Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        this.board.setPlayerPosition(character, this.board.getSquareByCoordinates(x, y));
        if(this.character == character) {
            showMessage("You spawned in [" + x + ", " + y + "]");
        } else {
            showMessage(character + " spawned in [" + x + ", " + y + "]");
        }
    }

    private void handleSkullsSet() {
        showMessage("OK, now select the Arena [1, 2, 3, 4]:");
        this.state = SETTINGARENA;
    }

    private void handleMasterChanged(GameCharacter character) {
        if (character == this.character) {
            showMessage("Master disconnected, you are the new master. Set skull number for the game:");
            this.state = SETTINGSKULLS;
        } else {
            showMessage("Master disconnected, the new master is setting up the game");
        }
    }

    private void handleStartSetup(GameCharacter character) {
        if (character == this.character) {
            showMessage("You are the master, set Skulls number for the game:");
            this.state = SETTINGSKULLS;

        } else {
            showMessage("Master player is setting up the game, wait");
            this.state = WAITINGSETUP;
        }
    }

    private void handleMovement(GameCharacter character, Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        this.board.setPlayerPosition(character, this.board.getSquareByCoordinates(x, y));
        if(this.character == character) {
            showMessage("You moved in [" + x + ", " + y + "]");
        } else {
            showMessage(character + " moved in [" + x + ", " + y + "]");
        }
    }

    private void update(ClientMessage message) throws RemoteException {
        switch (message.getType()) {
            case INVALID_TOKEN:
                handleInvalidToken();
                break;
            case LOBBY_FULL:
                handleFullLobby();
                break;
            case CLIENT_RECONNECTION:
                handleReconnectionRequest();
                break;
            case CHARACTER_SELECTION:
                handleCharacterSelectionRequest(((CharacterMessage) message).getAvailables());
                break;
            case DISCONNECTED:
                handleClientDisconnected(message.getCharacter());
                break;
            case GAME_ALREADY_STARTED:
                handleGameAlreadyStarted();
                break;
            case LOAD_VIEW:
                loadView(message.getCharacter(), ((LoadViewMessage) message).getSkulls(),
                        ((LoadViewMessage) message).getSquares(), ((LoadViewMessage) message).getKillshotTrack(),
                        ((LoadViewMessage) message).getPayerBoards(), ((LoadViewMessage) message).getReadyWeapons(),
                        ((LoadViewMessage) message).getPowerups(), ((LoadViewMessage) message).getScore(),
                        ((LoadViewMessage) message).getOtherPlayers());
                break;
        }
    }

    private void handleInvalidToken() {
        showMessage("Invalid token");
        System.exit(0);
    }

    private void handleFullLobby() {
        showMessage("Lobby is full");
        System.exit(0);
    }

    private void handleReconnectionRequest() throws RemoteException {
        showMessage("A game already exists, trying to reconnect.");
        this.state = RECONNECTING;
        this.client.send(new ReconnectionMessage(getToken()));
    }

    private void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        if(this.charactersAvailable != null) {
            showMessage("Character already choosen");
        }
        StringBuilder builder = new StringBuilder("Choose one of these characters:\n");
        this.charactersAvailable = availables;
        for(GameCharacter character : this.charactersAvailable) {
            builder.append("[" + (this.charactersAvailable.indexOf(character) + 1) + "] - " + character + "\n");
        }
        builder.setLength(builder.length() - 1);
        showMessage(builder.toString());
        this.state = CHOOSINGCHARACTER;
    }

    private void handleClientDisconnected(GameCharacter character) {
        if (this.state == TYPINGNICKNAME || this.state == WAITINGSTART || this.state == WAITINGSETUP ||
                this.state == SETTINGSKULLS || this.state == SETTINGARENA) {
            String nickname = "";
            for (PlayerBoard board : this.enemyBoards) {
                if (board.getCharacter() == character) {
                    nickname = board.getNickname();
                    this.enemyBoards.remove(board);
                    break;
                }
            }
            if (this.state == WAITINGSTART || this.state == WAITINGSETUP || this.state == SETTINGSKULLS ||
                    this.state == SETTINGARENA) {
                showMessage(nickname + " - " + character + " disconnected");
            }
            if (this.state == SETTINGSKULLS) {
                showMessage("Set Skulls number for the game:");
            }
            if (this.state == SETTINGARENA) {
                showMessage("Select the Arena [1, 2, 3, 4]:");
            }
        } else {
            String nickname = "";
            for (PlayerBoard board : this.enemyBoards) {
                if (board.getCharacter() == character) {
                    nickname = board.getNickname();
                    break;
                }
            }
            showMessage(nickname + " - " + character + " disconnected");
        }
    }

    private void handleGameAlreadyStarted() {
        showMessage("Game already started, sorry");
        System.exit(0);
    }

    private void loadView(GameCharacter character, int skulls, List<SquareView> squares,
                        Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                        List<Weapon> weapons, List<Powerup> powerups, int score, Map<GameCharacter, String> others) {
        this.character = character;
        this.state = WAITINGSETUP;
        this.enemyBoards = new ArrayList<>();
        this.board = new BoardView(skulls, squares, killshotTrack);
        for(PlayerBoard playerBoard : playerBoards) {
            if(playerBoard.getCharacter() == character){
                this.selfPlayerBoard = new SelfPlayerBoard(playerBoard, weapons, powerups, score);
            } else {
                this.enemyBoards.add(playerBoard);
            }
        }
        showMessage("You are " + this.character);
        for (Map.Entry<GameCharacter, String> player : others.entrySet()) {
            if(player.getKey() != this.character) {
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        }
    }

    private void update(PowerupMessage message) {
        switch(message.getType()) {
            case ADD:
                handlePowerupAdded(message.getCharacter(), message.getPowerup());
                break;
            default:
                handlePowerupRemoved(message.getCharacter(), message.getPowerup(), message.getType());
        }
    }

    private void handlePowerupAdded(GameCharacter character, Powerup powerup) {
        if(character != this.character) {
            PlayerBoard playerBoard = getBoardByCharacter(character);
            if(playerBoard != null) {
                playerBoard.addPowerup();
                showMessage(character + " has drawn a Powerup");
            }
        } else {
            this.selfPlayerBoard.addPowerup(powerup);
            showMessage("You have drawn " + powerup.getType() + " " + powerup.getColor());
        }
    }

    private void handlePowerupRemoved(GameCharacter character, Powerup powerup, PowerupMessageType type) {
        if(this.character == character) {
            this.selfPlayerBoard.removePowerup(powerup.getType(), powerup.getColor());
        } else {
            this.getBoardByCharacter(character).removePowerup();
        }
        if(this.character == character) {
            showMessage("You have discarded " +
                    powerup.getType() + " " + powerup.getColor());
        } else {
            showMessage(character + " has discarded " +
                    powerup.getType() + " " + powerup.getColor());
        }
    }

    private void showActions() {
        StringBuilder text = new StringBuilder();
        text.append("Select one of these actions:\n");
        text.append("[1] - Show arena and killshot track\n");
        text.append("[2] - Show your board\n");
        text.append("[3] - Show enemy boards\n");
        int number = 4;
        String toAppend;
        for (ActionType action : this.turnActions) {
            switch (action) {
                case MOVE:
                    toAppend = "[" + number + "] - Move yourself by up to 3 squares\n";
                    text.append(toAppend);
                    break;
                case PICKUP:
                    if (this.selfPlayerBoard.getDamages().size() < 3) {
                        toAppend = "[" + number + "] - Move yourself by up to 1 square and pickup\n";
                    } else {
                        toAppend = "[" + number + "] - Move yourself by up to 2 squares and pickup\n";
                    }
                    text.append(toAppend);
                    break;
                case SHOT:
                    if (this.selfPlayerBoard.getDamages().size() < 6) {
                        toAppend = "[" + number + "] - Shoot\n";
                    } else {
                        toAppend = "[" + number + "] - Move yourself by up to 1 square and shoot\n";
                    }
                    text.append(toAppend);
                    break;
                case ENDTURN:
                    toAppend = "[" + number + "] - End your turn\n";
                    text.append(toAppend);
                    break;
                case POWERUP:
                    toAppend = "[" + number + "] - Use a powerup\n";
                    text.append(toAppend);
                    break;
                case RELOAD:
                    toAppend = "[" + number + "] - Reload weapons\n";
                    text.append(toAppend);
                    break;
            }
            number++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    private void update(AmmosMessage message) {
        switch (message.getType()) {
            case ADD:
                handleAddAmmos(message.getCharacter(), message.getAmmos());
                break;
            case REMOVE:
                handleRemoveAmmos(message.getCharacter(), message.getAmmos());
        }
    }

    private void handleAddAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        this.board.getPlayerPosition(character).removeAmmoTile();
        if (this.character == character) {
            this.selfPlayerBoard.addAmmos(ammos);
        } else {
            this.getBoardByCharacter(character).addAmmos(ammos);
        }
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (this.character == character) {
                showMessage("You got " + ammo.getValue() + "x" + ammo.getKey());
            } else {
                showMessage(character + " got " + ammo.getValue() + "x" + ammo.getKey());
            }
        }
    }

    private void handleRemoveAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        if (this.character == character) {
            this.selfPlayerBoard.useAmmos(ammos);
        } else {
            this.getBoardByCharacter(character).useAmmos(ammos);
        }
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (this.character == character) {
                showMessage("You used " + ammo.getValue() + "x" + ammo.getKey());
            } else {
                showMessage(character + " used " + ammo.getValue() + "x" + ammo.getKey());
            }
        }
    }

    private void update(WeaponMessage message) {
        switch(message.getType()) {
            case PICKUP:
                handleWeaponPickup(message.getCharacter(), message.getWeapon());
                break;
            case SWITCH:
                handleWeaponSwitch(message.getCharacter(), ((WeaponSwitchMessage) message).getSwitchWeapon(),
                        message.getWeapon());
                break;
            case RELOAD:
                handleWeaponReload(message.getCharacter(), message.getWeapon());
                break;
        }
    }

    private void handleWeaponPickup(GameCharacter character, Weapon weapon) {
        this.board.getPlayerPosition(character).removeStoreWeapon(weapon);
        if (this.character == character) {
            this.selfPlayerBoard.addWeapon(weapon);
        } else {
            this.getBoardByCharacter(character).addWeapon();
        }
        if (this.character == character) {
            showMessage("You got " + weapon);
        } else {
            showMessage(character + " got " + weapon);
        }
    }

    private void handleWeaponSwitch(GameCharacter character, Weapon oldWeapon, Weapon newWeapon) {
        if (character == this.character) {
            this.selfPlayerBoard.removeWeapon(oldWeapon);
            this.selfPlayerBoard.addWeapon(newWeapon);
        } else {
            PlayerBoard playerBoard = getBoardByCharacter(character);
            if (playerBoard != null) {
                playerBoard.removeWeapon(oldWeapon);
                playerBoard.addWeapon();
            }
        }
        if (character == this.character) {
            showMessage("You dropped your " + oldWeapon + " to get a " + newWeapon);
        } else {
            showMessage(character + " dropped a " + oldWeapon + " to get a " + newWeapon);
        }
    }

    private void handleWeaponReload(GameCharacter character, Weapon weapon) {
        if (character == this.character) {
            this.selfPlayerBoard.reloadWeapon(weapon);
        } else {
            PlayerBoard playerBoard = getBoardByCharacter(character);
            if (playerBoard != null) {
                playerBoard.reloadWeapon(weapon);
            }
        }
        if (character == this.character) {
            showMessage("Your " + weapon + " is now ready to fire");
        } else {
            showMessage(character + " realoaded " + weapon);
        }
    }

    private void requirePayment() {
        StringBuilder builder = new StringBuilder("You must pay ");
        for (Map.Entry<AmmoType, Integer> ammo : this.paymentRequired.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            builder.append(ammo.getValue() + "x" + ammo.getKey() + ", ");
        }
        builder.setLength((builder.length() - 1));
        builder.append("\nSelect ammos or powerups:\n");
        int index = 1;
        for (Map.Entry<AmmoType, Integer> ammo : this.availableAmmos.entrySet()) {
            if (ammo.getValue() != 0 && this.paymentRequired.keySet().contains(ammo.getKey()) &&
                    this.paymentRequired.get(ammo.getKey()) != 0) {
                builder.append("[" + index + "] - " + ammo.getKey() + " ammo\n");
                index++;
            }
        }
        for (Powerup p : this.availablePowerups) {
            if (this.paymentRequired.keySet().contains(p.getColor()) && this.paymentRequired.get(p.getColor()) != 0) {
                builder.append("[" + index + "] - " + p.getType() + " " + p.getColor() + "\n");
                index++;
            }
        }
        builder.setLength(builder.length() - 1);
        showMessage(builder.toString());
    }

    private void update(PaymentMessage message) {
        handlePayment(message.getPaymentType(), message.getAmmos());
    }

    private void handlePayment(PaymentType type, Map<AmmoType, Integer> request) {
        this.paymentRequired = request;
        this.currentPayment = type;
        this.availableAmmos = new EnumMap<>(this.selfPlayerBoard.getAvailableAmmos());
        this.availablePowerups = new ArrayList<>(this.selfPlayerBoard.getPowerups());
        requirePayment();
        this.state = PAYMENT;
    }

    private void update(TurnMessage message) throws RemoteException {
        switch(message.getType()) {
            case START:
                handleStartTurn(message, message.getCharacter());
                break;
            case END:
                handleEndTurn(message.getCharacter());
                break;
        }
    }

    private void handleStartTurn(TurnMessage message, GameCharacter character) throws RemoteException {
        if(character != this.character) {
            this.state = OTHERTURN;
            showMessage(character + " is playing");
        } else {
            this.state = YOURTURN;
            showMessage("It's your turn!");
            this.client.send(message);
        }
    }

    private void handleEndTurn(GameCharacter character) {
        if (character == this.character) {
            showMessage("Turn finished");
        } else {
            showMessage(character + "'s turn finished");
        }
    }

    private void update(BoardMessage message) {
        switch (message.getType()) {
            case SETUP_INTERRUPTED:
                handleSetupInterrupted();
                break;
            case GAME_SET:
                handleGameSet(((GameSetMessage) message).getSquareColors(), ((GameSetMessage) message).getSpawnPoints(),
                        ((GameSetMessage) message).getNearbyAccessibility(), ((GameSetMessage) message).getSkulls(),
                        ((GameSetMessage) message).getArenaNumber());
                break;
            case WEAPON_STORES_REFILLED:
                handleStoresRefilled(((WeaponStoresRefilledMessage) message).getWeapons());
                break;
            case AMMO_TILES_REFILLED:
                handleTilesRefilled(((AmmoTilesRefilledMessage) message).getTiles());
                break;
        }
    }

    private void handleSetupInterrupted() {
        showMessage("Too few players, game setup interrupted");
        this.state = WAITINGSTART;
    }

    private void handleGameSet(Map<Coordinates, RoomColor> colors, Map<Coordinates, Boolean> spawns,
                        Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility, int skulls, int arena) {
        List<SquareView> squares = new ArrayList<>();
        for(Coordinates square : colors.keySet()) {
            RoomColor color = colors.get(square);
            Boolean spawn = spawns.get(square);
            Map<CardinalPoint, Boolean> map = nearbyAccessibility.get(square);
            squares.add(new SquareView(square.getX(), square.getY(), color, spawn, map));
        }
        this.board = new BoardView(skulls, squares);

        showMessage("Master choose " + skulls + " Skulls and Arena " + arena);
        showMessage("This is the Arena:");
        showMessage(this.board.arenaToString());
    }

    private void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        for (Map.Entry<Coordinates, Weapon> weapon : weapons.entrySet()) {
            int x = weapon.getKey().getX();
            int y = weapon.getKey().getY();
            this.board.getSquareByCoordinates(x, y).addStoreWeapon(weapon.getValue());
        }
        showMessage("Weapon stores filled");
    }

    private void handleTilesRefilled(Map<Coordinates, AmmoTile> tiles) {
        for (Map.Entry<Coordinates, AmmoTile> tile : tiles.entrySet()) {
            int x = tile.getKey().getX();
            int y = tile.getKey().getY();
            this.board.getSquareByCoordinates(x, y).setAvailableAmmoTile(tile.getValue());
        }
        showMessage("Ammo tiles filled");
    }

    private void update(SelectionSentMessage message) {
        switch (message.getType()) {
            case SWITCH:
                handleWeaponSwitchRequest((List<Weapon>) message.getList());
                break;
            case PICKUP:
                handlePickupActionRequest((List<Coordinates>) message.getList());
                break;
            case MOVE:
                handleMovementActionRequest((List<Coordinates>) message.getList());
                break;
            case POWERUP_TARGET:
                handlePowerupTargetRequest((List<GameCharacter>) message.getList());
                break;
            case POWERUP_POSITION:
                handlePowerupPositionRequest((List<Coordinates>) message.getList());
                break;
            case RELOAD:
                handleReloadRequest((List<Weapon>) message.getList());
                break;
            case DISCARD_POWERUP:
                handleDiscardPowerupRequest((List<Powerup>) message.getList());
                break;
            case USE_POWERUP:
                handleUsePowerupRequest((List<Powerup>) message.getList());
                break;
            case PICKUP_WEAPON:
                handleWeaponPickupRequest((List<Weapon>) message.getList());
                break;
            case ACTION:
                handleActionSelectionRequest((List<ActionType>) message.getList());
                break;
        }
    }

    private void handleWeaponSwitchRequest(List<Weapon> weapons) {
        this.weaponsSelectionList = weapons;
        StringBuilder text = new StringBuilder();
        String toAppend = "Select a weapon to switch:\n";
        text.append(toAppend);
        int index = 1;
        for (Weapon weapon : weapons) {
            toAppend = "[" + index + "] - " + weapon;
            text.append(toAppend);
            if (this.selfPlayerBoard.getReadyWeapons().contains(weapon)) {
                text.append(" [READY]\n");
            } else {
                text.append(" [UNLOADED]\n");
            }
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = SWITCHWEAPON;
    }

    private void handlePickupActionRequest(List<Coordinates> coordinates) {
        this.actionCoordinates = coordinates;
        showMessage(this.board.arenaToString(coordinates));
        showMessage("You can move and pickup in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
        this.state = SELECTPICKUP;
    }

    private void handleMovementActionRequest(List<Coordinates> coordinates) {
        this.actionCoordinates = coordinates;
        showMessage(this.board.arenaToString(coordinates));
        showMessage("You can move in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
        this.state = SELECTMOVEMENT;
    }

    private void handlePowerupTargetRequest(List<GameCharacter> targets) {
        this.availableTargets = targets;
        StringBuilder text = new StringBuilder();
        text.append("Select a target:\n");
        int index = 1;
        for (GameCharacter c : targets) {
            String toAppend = "[" + index + "] - " + c + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = SELECTPOWERUPTARGET;
    }

    private void handlePowerupPositionRequest(List<Coordinates> coordinates) {
        this.actionCoordinates = coordinates;
        showMessage(this.board.arenaToString(coordinates));
        switch (this.activePowerup) {
            case TELEPORTER:
                showMessage("You can move in the squares marked with '***'\nInsert [x,y]:");
                break;
            case NEWTON:
                showMessage("You can move your target in the squares marked with '***'\nInsert [x,y]:");
                break;
        }
        this.state = SELECTPOWERUPPOSITION;
    }

    private void handleReloadRequest(List<Weapon> weapons) {
        this.weaponsSelectionList = new ArrayList<>(weapons);
        StringBuilder text = new StringBuilder();
        text.append("Select a weapon to recharge or skip:\n");
        int index = 1;
        for (Weapon weapon : weapons) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        showMessage(text.toString());
        this.state = RECHARGEWEAPON;
    }

    private void handleDiscardPowerupRequest(List<Powerup> powerups) {
        StringBuilder text = new StringBuilder();
        text.append("Discard a Powerup to spawn:\n");
        for(int i=0; i<powerups.size(); i++) {
            int index = i + 1;
            String toAppend = "[" + index + "] - " + powerups.get(i).getType() + " " + powerups.get(i).getColor() + "\n";
            text.append(toAppend);
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = DISCARDSPAWN;
    }

    private void handleUsePowerupRequest(List<Powerup> powerups) {
        this.availablePowerups = powerups;
        StringBuilder text = new StringBuilder();
        text.append("Select a powerup to use or skip:\n");
        int index = 1;
        for (Powerup p : powerups) {
            String toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
            text.append(toAppend);
            index++;
        }
        text.append("[" + index + "] - Cancel");
        showMessage(text.toString());
        this.state = USEPOWERUP;
    }

    private void handleWeaponPickupRequest(List<Weapon> weapons) {
        this.weaponsSelectionList = new ArrayList<>(weapons);
        StringBuilder text = new StringBuilder();
        text.append("Select a weapon to pickup:\n");
        int index = 1;
        for (Weapon weapon : weapons) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = SELECTWEAPON;
    }

    private void handleActionSelectionRequest(List<ActionType> actions) {
        this.turnActions = new ArrayList<>(actions);
        this.turnActions.add(ActionType.ENDTURN);
        showActions();
        this.state = SELECTACTION;
    }

    public abstract void showMessage(String message);

    private String generateToken() {

        String message = UUID.randomUUID().toString();

        //Creating the MessageDigest object
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE,"Error SHA-256 algorithm", e);
        }
        //Passing data to the created MessageDigest Object
        md.update(message.getBytes());
        //Compute the message digest
        byte[] digest = md.digest();
        //Converting the byte array in to HexString format
        StringBuffer hexString = new StringBuffer();
        for (int i = 0;i<digest.length;i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        try {
            String path = System.getProperty("user.home");
            FileWriter writer = new FileWriter(path + "/" + "AdrenalinaClient.token");
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error writing data", e);
        }
        return hexString.toString();
    }

    private String getToken() {
        String path = System.getProperty("user.home");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "/" + "AdrenalinaClient.token"));
            String message = reader.readLine();
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                LOGGER.log(Level.SEVERE,"Error SHA-256 algorithm", e);
            }
            //Passing data to the created MessageDigest Object
            md.update(message.getBytes());
            //Compute the message digest
            byte[] digest = md.digest();
            //Converting the byte array in to HexString format
            StringBuffer hexString = new StringBuffer();
            for (int i = 0;i<digest.length;i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            return hexString.toString();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error corrupt data", e);
        }
        return null;
    }
}