package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.*;

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
        String messageType = message.getMessageType().getName()
                .replace("it.polimi.se2019.model.messages.", "");;
        switch (messageType) {
            case "InvalidTokenMessage":
                update((InvalidTokenMessage) message);
                break;
            case "ReconnectionMessage":
                update((ReconnectionMessage) message);
                break;
            case "LoadViewMessage":
                update((LoadViewMessage) message);
                break;
            case "RequireNicknameMessage":
                update((RequireNicknameMessage) message);
                break;
            case "CharacterMessage":
                update((CharacterMessage) message);
                break;
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
            case "StartTurnMessage":
                update((StartTurnMessage) message);
                break;
            case "DiscardToSpawnMessage":
                update((DiscardToSpawnMessage) message);
                break;
            case "PowerupDrawnMessage":
                update((PowerupDrawnMessage) message);
                break;
            case "PlayerSpawnedMessage":
                update((PlayerSpawnedMessage) message);
                break;
            case "PowerupRemovedMessage":
                update((PowerupRemovedMessage) message);
                break;
            case "AvailableActionsMessage":
                update((AvailableActionsMessage) message);
                break;
            case "AvailableMoveActionMessage":
                update((AvailableMoveActionMessage) message);
                break;
            case "AvailablePickupActionMessage":
                update((AvailablePickupActionMessage) message);
                break;
            case "MovementMessage":
                update((MovementMessage) message);
                break;
            case "AmmosGivenMessage":
                update((AmmosGivenMessage) message);
                break;
            case "WeaponPickupSelectionMessage":
                update((WeaponPickupSelectionMessage) message);
                break;
            case "WeaponGivenMessage":
                update((WeaponGivenMessage) message);
                break;
            case "AmmosUsedMessage":
                update((AmmosUsedMessage) message);
                break;
            case "RequireWeaponSwitchMessage":
                update((RequireWeaponSwitchMessage) message);
                break;
            case "WeaponsSwitchedMessage":
                update((WeaponsSwitchedMessage) message);
                break;
            case "RequireWeaponLoadMessage":
                update((RequireWeaponLoadMessage) message);
                break;
            case "RechargeWeaponMessage":
                update((RechargeWeaponMessage) message);
                break;
            case "EndTurnMessage":
                update((EndTurnMessage) message);
                break;
            case "WeaponStoresRefilledMessage":
                update((WeaponStoresRefilledMessage) message);
                break;
            case "AmmoTilesRefilledMessage":
                update((AmmoTilesRefilledMessage) message);
                break;
            case "RequireWeaponPaymentMessage":
                update((RequireWeaponPaymentMessage) message);
                break;
            case "RequirePowerupUseMessage":
                update((RequirePowerupUseMessage) message);
                break;
            case "PowerupPositionsAvailableMessage":
                update((PowerupPositionsAvailableMessage) message);
                break;
            case "PowerupTargetsAvailableMessage":
                update((PowerupTargetsAvailableMessage) message);
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
            case RECONNECTING:
                if (input.equals("")) {
                    showMessage("Invalid username, retry: ");
                    break;
                }
                this.client.send(new NicknameRecconnectingMessage(input));
                break;

            case TYPINGNICKNAME:
                if (input.equals("")) {
                    showMessage("Invalid username, retry: ");
                    break;
                }
                this.client.send(new NicknameMessage(input));
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
                this.client.send(new CharacterMessage(charactersAvailable.get(select-1), generateToken()));
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
                    this.client.send(new PowerupDiscardMessage(this.selfPlayerBoard.getPowerups().get(number - 1)));
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
                    this.client.send(new ActionSelectedMessage(this.turnActions.get(number - 4)));
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
                    this.client.send(new ActionSelectedMessage(ActionType.CANCEL));
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
                            this.client.send(new MovementSelectedMessage(new Coordinates(x, y)));
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
                    this.client.send(new ActionSelectedMessage(ActionType.CANCEL));
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
                            this.client.send(new PickupSelectedMessage(new Coordinates(x, y)));
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
                    this.client.send(new WeaponPickupMessage(this.weaponsSelectionList.get(number - 1)));
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
                    this.client.send(new WeaponSwitchMessage(this.weaponsSelectionList.get(number - 1)));
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
                    this.client.send(new RechargeWeaponMessage(selection, this.character));
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
                        this.client.send(new WeaponPaymentMessage(this.paidPowerups, this.paidAmmos));
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
                        this.client.send(new UsePowerupMessage(this.character, null));
                        this.availablePowerups = new ArrayList<>();
                        break;
                    }
                    this.client.send(new UsePowerupMessage(this.character, this.availablePowerups.get(number - 1)));
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
                            this.client.send(new PowerupPositionMessage(new Coordinates(x, y)));
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
                    this.client.send(new PowerupTargetMessage(this.availableTargets.get(number - 1)));
                    this.availableTargets = new ArrayList<>();
                } catch(NumberFormatException e) {
                    showMessage("Invalid input, retry:");
                    break;
                }
                break;
        }
    }

    private void update(InvalidTokenMessage message) {
        showMessage("Invalid token");
        System.exit(0);
    }

    private void update(ReconnectionMessage message) throws RemoteException {
        showMessage("A game already exists, trying to reconnect.");
        this.state = RECONNECTING;
        client.send(new ReconnectionMessage(getToken()));
    }

    private void update(LoadViewMessage message) {
        this.character = message.getCharacter();
        this.state = WAITINGSETUP;
        this.enemyBoards = new ArrayList<>();
        this.board = new BoardView(message.getSkulls(), message.getSquares(), message.getKillshotTrack());
        for(PlayerBoard playerBoard : message.getPayerBoards()) {
            if(playerBoard.getCharacter() == message.getCharacter()){
                selfPlayerBoard = new SelfPlayerBoard(playerBoard, message.getReadyWeapons(),
                        message.getPowerups(), message.getScore());
            } else {
                this.enemyBoards.add(playerBoard);
            }
        }
        showMessage("You are " + this.character + ", wait other players");
        for (Map.Entry<GameCharacter, String> player : message.getOtherPlayers().entrySet()) {
            if(player.getKey() != this.character) {
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        }
    }

    private void update(RequireNicknameMessage message) {
        showMessage("Insert nickname: ");
        this.state = TYPINGNICKNAME;
    }

    private void update(CharacterMessage message) {
        if(this.charactersAvailable != null) {
            showMessage("Character already choosen");
        }
        StringBuilder builder = new StringBuilder("Choose one of these characters:\n");
        this.charactersAvailable = message.getAvailables();
        for(GameCharacter character : this.charactersAvailable) {
            builder.append("[" + (this.charactersAvailable.indexOf(character) + 1) + "] - " + character + "\n");
        }
        builder.setLength(builder.length() - 1);
        showMessage(builder.toString());
        this.state = CHOOSINGCHARACTER;
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

    private void update(NicknameDuplicatedMessage message){
        showMessage("Nickname is already in use. Insert another nickname: ");
        this.state = TYPINGNICKNAME;
    }

    private void update(PlayerCreatedMessage message){
        this.character = message.getCharacter();
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
    }

    private void update(PlayerReadyMessage message) {
        if (this.state == WAITINGSTART) {
            this.enemyBoards.add(new PlayerBoard(message.getCharacter(), message.getNickname()));
        }
        if(message.getCharacter() != this.character) {
            showMessage(message.getNickname() + " - " + message.getCharacter() + " connected!");
        }
    }

    private void update(ClientDisconnectedMessage message) {
        if (this.state == TYPINGNICKNAME || this.state == WAITINGSTART || this.state == WAITINGSETUP ||
                this.state == SETTINGSKULLS || this.state == SETTINGARENA) {
            String nickname = "";
            for (PlayerBoard board : this.enemyBoards) {
                if (board.getCharacter() == message.getCharacter()) {
                    nickname = board.getNickname();
                    this.enemyBoards.remove(board);
                    break;
                }
            }
            if (this.state == WAITINGSTART || this.state == WAITINGSETUP || this.state == SETTINGSKULLS ||
                    this.state == SETTINGARENA) {
                showMessage(nickname + " - " + message.getCharacter() + " disconnected");
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
                if (board.getCharacter() == message.getCharacter()) {
                    nickname = board.getNickname();
                    break;
                }
            }
            showMessage(nickname + " - " + message.getCharacter() + " disconnected");
        }
    }

    private void update(StartGameSetupMessage message) {
        if (message.getCharacter() == this.character) {
            showMessage("You are the master, set Skulls number for the game:");
            this.state = SETTINGSKULLS;

        } else {
            showMessage("Master player is setting up the game, wait");
            this.state = WAITINGSETUP;
        }
    }

    private void update(SkullsSetMessage message) {
        showMessage("OK, now select the Arena [1, 2, 3, 4]:");
        this.state = SETTINGARENA;
    }

    private void update(GameSetMessage message) {
        List<SquareView> squares = new ArrayList<>();
        for(Coordinates square : message.getArenaColors().keySet()) {
            RoomColor color = message.getArenaColors().get(square);
            Boolean spawn = message.getArenaSpawn().get(square);
            Map<CardinalPoint, Boolean> map = message.getNearbyAccessibility().get(square);
            squares.add(new SquareView(square.getX(), square.getY(), color, spawn, map));
        }
        this.board = new BoardView(message.getSkulls(), squares);

        showMessage("Master choose " + message.getSkulls() + " Skulls and Arena " + message.getArenaNumber());
        showMessage("This is the Arena:");
        showMessage(this.board.arenaToString());
    }

    private void update(ArenaFilledMessage message) {
        showMessage("Ammo tiles and Weapons placed:");
        showMessage(this.board.arenaToString());
    }

    private void update(StartTurnMessage message) throws RemoteException {
        if(message.getPlayer() != this.character) {
            this.state = OTHERTURN;
            showMessage(message.getPlayer() + " is playing");
        } else {
            this.state = YOURTURN;
            showMessage("It's your turn!");
            this.client.send(message);
        }
    }

    private void update(PowerupDrawnMessage message) {
        if(message.getPlayer() != this.character) {
            PlayerBoard playerBoard = getBoardByCharacter(message.getPlayer());
            if(playerBoard != null) {
                playerBoard.addPowerup();
                showMessage(message.getPlayer() + " has drawn a Powerup");
            }
        } else {
            this.selfPlayerBoard.addPowerup(message.getPowerup());
            showMessage("You have drawn " + message.getPowerup().getType() + " " + message.getPowerup().getColor());
        }
    }

    private void update(DiscardToSpawnMessage message) {
        StringBuilder text = new StringBuilder();
        text.append("Discard a Powerup to spawn:\n");
        List<Powerup> powerups = this.selfPlayerBoard.getPowerups();
        for(int i=0; i<powerups.size(); i++) {
            int index = i + 1;
            String toAppend = "[" + index + "] - " + powerups.get(i).getType() + " " + powerups.get(i).getColor() + "\n";
            text.append(toAppend);
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = DISCARDSPAWN;
    }

    private void update(PlayerSpawnedMessage message) {
        int x = message.getCoordinates().getX();
        int y = message.getCoordinates().getY();
        this.board.setPlayerPosition(message.getCharacter(), this.board.getSquareByCoordinates(x, y));
        if(this.character == message.getCharacter()) {
            showMessage("You spawned in [" + x + ", " + y + "]");
        } else {
            showMessage(message.getCharacter() + " spawned in [" + x + ", " + y + "]");
        }
    }

    private void update(PowerupRemovedMessage message) {
        if(this.character == message.getCharacter()) {
            this.selfPlayerBoard.removePowerup(message.getPowerup().getType(), message.getPowerup().getColor());
        } else {
            this.getBoardByCharacter(message.getCharacter()).removePowerup();
        }
        if(this.character == message.getCharacter()) {
            showMessage("You have discarded " +
                    message.getPowerup().getType() + " " + message.getPowerup().getColor());
        } else {
            showMessage(message.getCharacter() + " has discarded " +
                    message.getPowerup().getType() + " " + message.getPowerup().getColor());
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

    private void update(AvailableActionsMessage message) {
        this.turnActions = new ArrayList<>(message.getActions());
        this.turnActions.add(ActionType.ENDTURN);
        showActions();
        this.state = SELECTACTION;
    }

    private void update(AvailableMoveActionMessage message) {
        this.actionCoordinates = message.getMovements();
        showMessage(this.board.arenaToString(message.getMovements()));
        showMessage("You can move in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
        this.state = SELECTMOVEMENT;
    }

    private void update(AvailablePickupActionMessage message) {
        this.actionCoordinates = message.getMovements();
        showMessage(this.board.arenaToString(message.getMovements()));
        showMessage("You can move and pickup in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
        this.state = SELECTPICKUP;
    }

    private void update(MovementMessage message) {
        int x = message.getCoordinates().getX();
        int y = message.getCoordinates().getY();
        this.board.setPlayerPosition(message.getCharacter(), this.board.getSquareByCoordinates(x, y));
        if(this.character == message.getCharacter()) {
            showMessage("You moved in [" + x + ", " + y + "]");
        } else {
            showMessage(message.getCharacter() + " moved in [" + x + ", " + y + "]");
        }
    }

    private void update(AmmosGivenMessage message) {
        this.board.getPlayerPosition(message.getCharacter()).removeAmmoTile();
        if (this.character == message.getCharacter()) {
            this.selfPlayerBoard.addAmmos(message.getAmmos());
        } else {
            this.getBoardByCharacter(message.getCharacter()).addAmmos(message.getAmmos());
        }
        for (Map.Entry<AmmoType, Integer> ammo : message.getAmmos().entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (this.character == message.getCharacter()) {
                showMessage("You got " + ammo.getValue() + "x" + ammo.getKey());
            } else {
                showMessage(message.getCharacter() + " got " + ammo.getValue() + "x" + ammo.getKey());
            }
        }
    }

    private void update(WeaponPickupSelectionMessage message) {
        this.weaponsSelectionList = new ArrayList<>(message.getWeapons());
        StringBuilder text = new StringBuilder();
        text.append("Select a weapon to pickup:\n");
        int index = 1;
        for (Weapon weapon : message.getWeapons()) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = SELECTWEAPON;
    }

    private void update(WeaponGivenMessage message) {
        this.board.getPlayerPosition(message.getCharacter()).removeStoreWeapon(message.getWeapon());
        if (this.character == message.getCharacter()) {
            this.selfPlayerBoard.addWeapon(message.getWeapon());
        } else {
            this.getBoardByCharacter(message.getCharacter()).addWeapon();
        }
        if (this.character == message.getCharacter()) {
            showMessage("You got " + message.getWeapon());
        } else {
            showMessage(message.getCharacter() + " got " + message.getWeapon());
        }
    }

    private void update(AmmosUsedMessage message) {
        if (this.character == message.getCharacter()) {
            this.selfPlayerBoard.useAmmos(message.getAmmos());
        } else {
            this.getBoardByCharacter(message.getCharacter()).useAmmos(message.getAmmos());
        }
        for (Map.Entry<AmmoType, Integer> ammo : message.getAmmos().entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (this.character == message.getCharacter()) {
                showMessage("You used " + ammo.getValue() + "x" + ammo.getKey());
            } else {
                showMessage(message.getCharacter() + " used " + ammo.getValue() + "x" + ammo.getKey());
            }
        }
    }

    private void update(RequireWeaponSwitchMessage message) {
        this.weaponsSelectionList = new ArrayList<>(this.selfPlayerBoard.getReadyWeapons());
        this.weaponsSelectionList.addAll(this.selfPlayerBoard.getUnloadedWeapons());
        StringBuilder text = new StringBuilder();
        String toAppend = "Select a weapon to switch with " + message.getWeapon() + ":\n";
        text.append(toAppend);
        int index = 1;
        for (Weapon weapon : this.weaponsSelectionList) {
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

    private void update(WeaponsSwitchedMessage message) {
        if (message.getCharacter() == this.character) {
            this.selfPlayerBoard.removeWeapon(message.getOldWeapon());
            this.selfPlayerBoard.addWeapon(message.getNewWeapon());
        } else {
            PlayerBoard playerBoard = getBoardByCharacter(message.getCharacter());
            if (playerBoard != null) {
                playerBoard.removeWeapon(message.getOldWeapon());
                playerBoard.addWeapon();
            }
        }
        if (message.getCharacter() == this.character) {
            showMessage("You dropped your " + message.getOldWeapon() + " to get a " + message.getNewWeapon());
        } else {
            showMessage(message.getCharacter() + " dropped a " + message.getOldWeapon() + " to get a " + message.getNewWeapon());
        }
    }

    private void requirePayment() {
        StringBuilder builder = new StringBuilder("Devi pagare ");
        for (Map.Entry<AmmoType, Integer> ammo : this.paymentRequired.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            builder.append(ammo.getValue() + "x" + ammo.getKey() + ", ");
        }
        builder.setLength((builder.length() - 1));
        builder.append("\nSeleziona munizioni o powerup:\n");
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

    private void update(RequireWeaponPaymentMessage message) {
        this.paymentRequired = message.getBuyCost();
        this.availableAmmos = new EnumMap<>(this.selfPlayerBoard.getAvailableAmmos());
        this.availablePowerups = new ArrayList<>(this.selfPlayerBoard.getPowerups());
        requirePayment();
        this.state = PAYMENT;
    }

    private void update(RequireWeaponLoadMessage message) {
        this.weaponsSelectionList = new ArrayList<>(message.getWeapons());
        StringBuilder text = new StringBuilder();
        text.append("Select a weapon to recharge or skip:\n");
        int index = 1;
        for (Weapon weapon : message.getWeapons()) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        showMessage(text.toString());
        this.state = RECHARGEWEAPON;
    }

    private void update(RechargeWeaponMessage message) {
        if (message.getCharacter() == this.character) {
            this.selfPlayerBoard.reloadWeapon(message.getWeapon());
        } else {
            PlayerBoard playerBoard = getBoardByCharacter(message.getCharacter());
            if (playerBoard != null) {
                playerBoard.reloadWeapon(message.getWeapon());
            }
        }
        if (message.getCharacter() == this.character) {
            showMessage("Your " + message.getWeapon() + " is now ready to fire");
        } else {
            showMessage(message.getCharacter() + " realoaded " + message.getWeapon());
        }
    }

    private void update(EndTurnMessage message) {
        if (message.getCharacter() == this.character) {
            showMessage("Turn finished");
        } else {
            showMessage(message.getCharacter() + "'s turn finished");
        }
    }

    private void update(WeaponStoresRefilledMessage message) {
        for (Map.Entry<Coordinates, Weapon> weapon : message.getWeapons().entrySet()) {
            int x = weapon.getKey().getX();
            int y = weapon.getKey().getY();
            this.board.getSquareByCoordinates(x, y).addStoreWeapon(weapon.getValue());
        }
        showMessage("Weapon stores filled");
    }

    private void update(AmmoTilesRefilledMessage message) {
        for (Map.Entry<Coordinates, AmmoTile> tile : message.getTiles().entrySet()) {
            int x = tile.getKey().getX();
            int y = tile.getKey().getY();
            this.board.getSquareByCoordinates(x, y).setAvailableAmmoTile(tile.getValue());
        }
        showMessage("Ammo tiles filled");
    }

    private void update(RequirePowerupUseMessage message) {
        this.availablePowerups = message.getPowerups();
        StringBuilder text = new StringBuilder();
        text.append("Select a powerup to use or skip:\n");
        int index = 1;
        for (Powerup p : message.getPowerups()) {
            String toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
            text.append(toAppend);
            index++;
        }
        text.append("[" + index + "] - Cancel");
        showMessage(text.toString());
        this.state = USEPOWERUP;
    }

    private void update (PowerupPositionsAvailableMessage message) {
        this.actionCoordinates = message.getPositions();
        showMessage(this.board.arenaToString(message.getPositions()));
        switch (message.getPowerup()) {
            case TELEPORTER:
                showMessage("You can move in the squares marked with '***'\nInsert [x,y]:");
                break;
            case NEWTON:
                showMessage("You can move your target in the squares marked with '***'\nInsert [x,y]:");
                break;
        }
        this.state = SELECTPOWERUPPOSITION;
    }

    private void update(PowerupTargetsAvailableMessage message) {
        this.availableTargets = message.getTargets();
        StringBuilder text = new StringBuilder();
        text.append("Select a target:\n");
        int index = 1;
        for (GameCharacter c : message.getTargets()) {
            String toAppend = "[" + index + "] - " + c + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.state = SELECTPOWERUPTARGET;
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
            FileWriter writer = new FileWriter(path + "/" + ".AdrenalinaClient.token");
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
            BufferedReader reader = new BufferedReader(new FileReader(path + "/" + ".AdrenalinaClient.token"));
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