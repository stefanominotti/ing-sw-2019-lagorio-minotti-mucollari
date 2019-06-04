package it.polimi.se2019.view;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.CharacterMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.payment.PaymentSentMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessageType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SelectionReceivedMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.turn.TurnMessage;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.se2019.view.ClientState.*;

public class CLIView extends View {

    private boolean inputEnabled;

    private static final Logger LOGGER = Logger.getLogger(CLIView.class.getName());

    public CLIView(int connection) {
        super(connection);

        Thread inputThread = new Thread() {
            Scanner scanner = new Scanner(System.in);
            boolean read = true;

            @Override
            public void run() {
                while (this.read) {
                    String input = this.scanner.nextLine();
                    try {
                        handleInput(input);
                    } catch (RemoteException e) {
                        LOGGER.log(Level.SEVERE, "Error on managing input", e);
                    }
                }
            }
        };

        inputThread.start();
    }

    @Override
    void manageUpdate(Message message) throws RemoteException {
        super.manageUpdate(message);
        this.inputEnabled = true;
    }

    private void handleInput(String input) throws RemoteException {
        if (!this.inputEnabled || input == null) {
            return;
        }

        if (input.equals("")) {
            showMessage("Invalid username, retry: ");
            return;
        }

        if (getState() == TYPINGNICKNAME) {
            handleNicknameInput(input);
        } else if (getState() == CHOOSINGCHARACTER || getState() == SELECTBOARDTOSHOW
                || getState() == SELECTPOWERUPTARGET) {
            handleCharacterInput(input);
        } else if (getState() == SETTINGSKULLS) {
            handleSkullsInput(input);
        } else if (getState() == SETTINGARENA) {
            handleArenaInput(input);
        } else if (getState() == DISCARDSPAWN || getState() == USEPOWERUP) {
            handlePowerupInput(input);
        } else if (getState() == SELECTACTION) {
            handleActionInput(input);
        } else if (getState() == SELECTMOVEMENT || getState() == SELECTPICKUP || getState() == SELECTPOWERUPPOSITION) {
            handlePositionInput(input);
        } else if (getState() == SELECTWEAPON || getState() == SWITCHWEAPON || getState() == RECHARGEWEAPON) {
            handleWeaponInput(input);
        } else if (getState() == PAYMENT) {
            handlePaymentInput(input);
        }
    }

    private void handleNicknameInput(String input) throws RemoteException {
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
        this.inputEnabled = false;
    }

    private void handleSkullsInput(String input) throws RemoteException {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage("Invalid number, retry: ");
            return;
        }
        if (selection < 3 || selection > 8) {
            showMessage("Skulls number must be between 3 and 8, retry: ");
            return;
        }
        getClient().send(new SkullsMessage(selection));
        this.inputEnabled = false;
    }

    private void handleArenaInput(String input) throws RemoteException {
        if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
            getClient().send(new ArenaMessage(input));
            this.inputEnabled = false;
            return;
        }
        showMessage("Arena must be [1, 2, 3, 4]:, retry:");
    }

    private void handleCharacterInput(String input) throws RemoteException {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage("Invalid number, retry: ");
            return;
        }

        int maxSize = getCharactersSelection().size();
        if (getState() == SELECTBOARDTOSHOW) {
            maxSize = getEnemyBoards().size();
        }
        if (selection > maxSize|| selection <= 0) {
            showMessage("Invalid input, retry: ");
            return;
        }
        selection--;
        if (getState() == CHOOSINGCHARACTER) {
            getClient().send(new CharacterMessage(getCharactersSelection().get(selection), generateToken()));
            this.inputEnabled = false;
        } else if (getState() == SELECTPOWERUPTARGET) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.POWERUP_TARGET, getCharacter(),
                    getCharactersSelection().get(selection)));
            this.inputEnabled = false;
        } else if (getState() == SELECTBOARDTOSHOW) {
            showMessage(getEnemyBoards().get(selection).toString());
            showActions();
            setState(SELECTACTION);
        }
    }

    private void handlePositionInput(String input) throws RemoteException {
        if (input.equalsIgnoreCase("c") && getState() != SELECTPOWERUPPOSITION) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.ACTION, getCharacter(),
                    ActionType.CANCEL));
            this.inputEnabled = false;
            return;
        }
        if (input.split(",").length != 2) {
            showMessage("Invalid input, retry:");
            return;
        }
        int x;
        int y;
        try {
            x = Integer.parseInt(input.split(",")[0]);
            y = Integer.parseInt(input.split(",")[1]);
        } catch(NumberFormatException e) {
            showMessage("Invalid input, retry:");
            return;
        }

        Coordinates toSend = null;
        for (Coordinates c : getCoordinatesSelection()) {
            if (c.getX() == x && c.getY() == y) {
                toSend = c;
                resetSelections();
                break;
            }
        }

        if(toSend == null) {
            showMessage("Invalid input, retry:");
        } else if (getState() == SELECTMOVEMENT) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.MOVE, getCharacter(),
                    new Coordinates(x, y)));
            this.inputEnabled = false;
        } else if (getState() == SELECTPICKUP) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.PICKUP, getCharacter(),
                    new Coordinates(x, y)));
            this.inputEnabled = false;
        } else if (getState() == SELECTPOWERUPPOSITION) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.POWERUP_POSITION, getCharacter(),
                    new Coordinates(x, y)));
            this.inputEnabled = false;
        }

    }

    private void handlePowerupInput(String input) throws RemoteException {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            showMessage("Invalid number, retry:");
            return;
        }

        int maxNumber = getPowerupsSelection().size();
        if (getState() == USEPOWERUP) {
            maxNumber++;
        }

        if (0 >= selection || selection > maxNumber) {
            showMessage("Invalid input, retry:");
            return;
        }

        Powerup toSend;
        if (getState() == USEPOWERUP && selection == getPowerupsSelection().size() + 1) {
            toSend = null;
        } else {
            toSend = getPowerupsSelection().get(selection - 1);
            setActivePowerup(toSend.getType());
            resetSelections();
        }

        if (getState() == USEPOWERUP) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.USE_POWERUP, getCharacter(), toSend));
            this.inputEnabled = false;
        } else if (getState() == DISCARDSPAWN) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.DISCARD_POWERUP, getCharacter(),
                    toSend));
            this.inputEnabled = false;
        }
    }

    private void handleWeaponInput(String input) throws RemoteException {
        int selection;

        try {
            selection = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            showMessage("Invalid number, retry:");
            return;
        }

        int maxNumber = getWeaponsSelection().size();
        if (getState() == RECHARGEWEAPON) {
            maxNumber++;
        }

        if (0 >= selection || selection > maxNumber) {
            showMessage("Invalid input, retry:");
            return;
        }

        Weapon toSend = null;
        if (getState() != RECHARGEWEAPON) {
            toSend = getWeaponsSelection().get(selection - 1);
            resetSelections();
        }

        if (getState() == SELECTWEAPON) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.PICKUP_WEAPON, getCharacter(), toSend));
            this.inputEnabled = false;
        } else if (getState() == SWITCHWEAPON) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.SWITCH, getCharacter(), toSend));
            this.inputEnabled = false;
        } else if (getState() == RECHARGEWEAPON) {
            getClient().send(new SelectionReceivedMessage(SelectionMessageType.RELOAD, getCharacter(), toSend));
            this.inputEnabled = false;
        }
    }

    private void handleActionInput(String input) throws RemoteException {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            showMessage("Invalid input, retry:");
            return;
        }
        if (0 >= selection || selection > getActionsSelection().size() + 3) {
            showMessage("Invalid input, retry:");
            return;
        }

        switch (selection) {
            case 1:
                showMessage(getBoard().killshotTrackToString());
                showMessage(getBoard().arenaToString());
                showActions();
                break;
            case 2:
                showMessage(getSelfPlayerBoard().toString());
                showActions();
                break;
            case 3:
                int index = 1;
                StringBuilder text = new StringBuilder("Select a player to show:\n");
                for (PlayerBoard board : getEnemyBoards()) {
                    String toAppend = "[" + index + "] - " + board.getCharacter() + "\n";
                    text.append(toAppend);
                    index++;
                }
                text.setLength(text.length() - 1);
                showMessage(text.toString());
                setState(SELECTBOARDTOSHOW);
                break;
            default:
                getClient().send(new SelectionReceivedMessage(SelectionMessageType.ACTION, getCharacter(),
                        getActionsSelection().get(selection - 4)));
                this.inputEnabled = false;
                resetSelections();
        }
    }

    private void handlePaymentInput(String input) throws RemoteException {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            showMessage("Invalid input, retry:");
            return;
        }

        List<AmmoType> payableAmmos = new ArrayList<>();
        List<Powerup> payablePowerups = new ArrayList<>();
        for (Map.Entry<AmmoType, Integer> ammo : getAmmosSelection().entrySet()) {
            if (ammo.getValue() != 0 && getRequiredPayment().keySet().contains(ammo.getKey()) &&
                    getRequiredPayment().get(ammo.getKey()) != 0) {
                payableAmmos.add(ammo.getKey());
            }
        }
        for (Powerup p : getPowerupsSelection()) {
            if (getRequiredPayment().keySet().contains(p.getColor()) &&
                    getRequiredPayment().get(p.getColor()) != 0) {
                payablePowerups.add(p);
            }
        }

        if (0 >= selection || selection > payableAmmos.size() + payablePowerups.size() + 1) {
            showMessage("Invalid number, retry:");
            return;
        }
        selection--;

        int newValue;
        if (selection < payableAmmos.size()) {
            newValue = getPaidAmmos().get(payableAmmos.get(selection)) + 1;
            putPaidAmmos(payableAmmos.get(selection), newValue);
            newValue = getAmmosSelection().get(payableAmmos.get(selection)) - 1;
            putAmmosSelection(payableAmmos.get(selection), newValue);
            newValue = getRequiredPayment().get(payableAmmos.get(selection)) - 1;
            putRequiredPayment(payableAmmos.get(selection), newValue);
        } else {
            selection -= payableAmmos.size();
            addPaidPowerup(payablePowerups.get(selection));
            removePowerupSelection(payablePowerups.get(selection));
            newValue = getRequiredPayment().get(payablePowerups.get(selection).getColor()) - 1;
            putRequiredPayment(payablePowerups.get(selection).getColor(), newValue);
        }

        for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
            if (ammo.getValue() != 0) {
                requirePayment();
                return;
            }
        }

        getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getPaidAmmos(),
                getPaidPowerups()));
        this.inputEnabled = false;
        resetSelections();
    }

    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        showMessage("Insert nickname: ");
    }

    @Override
    void handleNicknameDuplicated() {
        super.handleNicknameDuplicated();
        showMessage("Nickname is already in use. Insert another nickname: ");
    }

    @Override
    void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (getState() == WAITINGSTART) {
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

    @Override
    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter,
            String> otherPlayers) {
        super.handlePlayerCreated(character, nickname, otherPlayers);
        showMessage("Nickname " + nickname + " accepted! You are " + character);
        for (PlayerBoard board : getEnemyBoards()) {
            showMessage(board.getNickname() + " - " + board.getCharacter() + " is in!");
        }
    }

    @Override
    void handleReadyPlayer(GameCharacter character, String nickname) {
        super.handleReadyPlayer(character, nickname);
        if(character != getCharacter()) {
            showMessage(nickname + " - " + character + " connected!");
        }
    }

    @Override
    void handleSpawnedPlayer(GameCharacter character, Coordinates coordinates) {
        super.handleSpawnedPlayer(character, coordinates);
        int x = coordinates.getX();
        int y = coordinates.getY();
        if(getCharacter() == character) {
            showMessage("You spawned in [" + x + ", " + y + "]");
        } else {
            showMessage(character + " spawned in [" + x + ", " + y + "]");
        }
    }

    @Override
    void handleSkullsSet() {
        super.handleSkullsSet();
        showMessage("OK, now select the Arena [1, 2, 3, 4]:");
    }

    @Override
    void handleMasterChanged(GameCharacter character) {
        super.handleMasterChanged(character);
        if (character == getCharacter()) {
            showMessage("Master disconnected, you are the new master. Set skull number for the game:");
        } else {
            showMessage("Master disconnected, the new master is setting up the game");
        }
    }

    @Override
    void handleStartSetup(GameCharacter character) {
        super.handleStartSetup(character);
        if (character == getCharacter()) {
            showMessage("You are the master, set Skulls number for the game:");
        } else {
            showMessage("Master player is setting up the game, wait");
        }
    }

    @Override
    void handleMovement(GameCharacter character, Coordinates coordinates) {
        super.handleMovement(character, coordinates);
        int x = coordinates.getX();
        int y = coordinates.getY();
        if(getCharacter() == character) {
            showMessage("You moved in [" + x + ", " + y + "]");
        } else {
            showMessage(character + " moved in [" + x + ", " + y + "]");
        }
    }

    @Override
    void handleInvalidToken() {
        showMessage("Invalid token");
        super.handleInvalidToken();
    }

    @Override
    void handleFullLobby() {
        showMessage("Lobby is full");
        super.handleFullLobby();
    }

    @Override
    void handleReconnectionRequest() throws RemoteException {
        showMessage("A game already exists, trying to reconnect.");
        super.handleReconnectionRequest();
    }

    @Override
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        super.handleCharacterSelectionRequest(availables);
        if(!getCharactersSelection().isEmpty()) {
            showMessage("Character already choosen");
        }
        setCharactersSelection(availables);
        StringBuilder text = new StringBuilder("Choose one of these characters:\n");
        for(GameCharacter character : availables) {
            String toAppend = "[" + (availables.indexOf(character) + 1) + "] - " + character + "\n";
            text.append(toAppend);
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleClientDisconnected(GameCharacter character) {
        super.handleClientDisconnected(character);
        String nickname = "";
        for (PlayerBoard board : getEnemyBoards()) {
            if (board.getCharacter() == character) {
                nickname = board.getNickname();
                break;
            }
        }
        if (getState() == TYPINGNICKNAME || getState() == WAITINGSTART || getState() == WAITINGSETUP ||
                getState() == SETTINGSKULLS || getState() == SETTINGARENA) {
            if (getState() == WAITINGSTART || getState() == WAITINGSETUP || getState() == SETTINGSKULLS ||
                    getState() == SETTINGARENA) {
                showMessage(nickname + " - " + character + " disconnected");
            }
            if (getState() == SETTINGSKULLS) {
                showMessage("Set Skulls number for the game:");
            }
            if (getState() == SETTINGARENA) {
                showMessage("Select the Arena [1, 2, 3, 4]:");
            }
        } else {
            showMessage(nickname + " - " + character + " disconnected");
        }
    }

    @Override
    void handleGameAlreadyStarted() {
        showMessage("Game already started, sorry");
        super.handleGameAlreadyStarted();
    }

    @Override
    void loadView(GameCharacter character, int skulls, List<SquareView> squares,
                  Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                  List<Weapon> weapons, List<Powerup> powerups, int score, Map<GameCharacter, String> others) {
        super.loadView(character, skulls, squares, killshotTrack, playerBoards, weapons, powerups, score, others);
        showMessage("You are " + getCharacter());
        for (Map.Entry<GameCharacter, String> player : others.entrySet()) {
            if(player.getKey() != getCharacter()) {
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        }
    }

    @Override
    void handlePowerupAdded(GameCharacter character, Powerup powerup) {
        super.handlePowerupAdded(character, powerup);
        if(character != getCharacter()) {
            showMessage(character + " has drawn a Powerup");
        } else {
            showMessage("You have drawn " + powerup.getType() + " " + powerup.getColor());
        }
    }

    @Override
    void handlePowerupRemoved(GameCharacter character, Powerup powerup, PowerupMessageType type) {
        super.handlePowerupRemoved(character, powerup, type);
        if(getCharacter() == character) {
            showMessage("You have discarded " + powerup.getType() + " " + powerup.getColor());
        } else {
            showMessage(character + " has discarded " + powerup.getType() + " " + powerup.getColor());
        }
    }

    @Override
    void handleAddAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        super.handleAddAmmos(character, ammos);
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (getCharacter() == character) {
                showMessage("You got " + ammo.getValue() + "x" + ammo.getKey());
            } else {
                showMessage(character + " got " + ammo.getValue() + "x" + ammo.getKey());
            }
        }
    }

    @Override
    void handleRemoveAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        super.handleRemoveAmmos(character, ammos);
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (getCharacter() == character) {
                showMessage("You used " + ammo.getValue() + "x" + ammo.getKey());
            } else {
                showMessage(character + " used " + ammo.getValue() + "x" + ammo.getKey());
            }
        }
    }

    @Override
    void handleWeaponPickup(GameCharacter character, Weapon weapon) {
        super.handleWeaponPickup(character, weapon);
        if (getCharacter() == character) {
            showMessage("You got " + weapon);
        } else {
            showMessage(character + " got " + weapon);
        }
    }

    @Override
    void handleWeaponSwitch(GameCharacter character, Weapon oldWeapon, Weapon newWeapon) {
        super.handleWeaponSwitch(character, oldWeapon, newWeapon);
        if (character == getCharacter()) {
            showMessage("You dropped your " + oldWeapon + " to get a " + newWeapon);
        } else {
            showMessage(character + " dropped a " + oldWeapon + " to get a " + newWeapon);
        }
    }

    @Override
    void handleWeaponReload(GameCharacter character, Weapon weapon) {
        super.handleWeaponReload(character, weapon);
        if (character == getCharacter()) {
            showMessage("Your " + weapon + " is now ready to fire");
        } else {
            showMessage(character + " realoaded " + weapon);
        }
    }

    @Override
    void handleStartTurn(TurnMessage message, GameCharacter character) throws RemoteException {
        if(character != getCharacter()) {
            showMessage(character + " is playing");
        } else {
            showMessage("It's your turn!");
        }
        super.handleStartTurn(message, character);
    }

    @Override
    void handleEndTurn(GameCharacter character) {
        if (character == getCharacter()) {
            showMessage("Turn finished");
        } else {
            showMessage(character + "'s turn finished");
        }
    }

    @Override
    void handleSetupInterrupted() {
        super.handleSetupInterrupted();
        showMessage("Too few players, game setup interrupted");
    }

    @Override
    void handleGameSet(Map<Coordinates, RoomColor> colors, Map<Coordinates, Boolean> spawns,
                       Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility, int skulls, int arena) {
        super.handleGameSet(colors, spawns, nearbyAccessibility, skulls, arena);
        showMessage("Master choose " + skulls + " Skulls and Arena " + arena);
        showMessage("This is the Arena:");
        showMessage(getBoard().arenaToString());
    }

    @Override
    void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        super.handleStoresRefilled(weapons);
        showMessage("Weapon stores filled");
    }

    @Override
    void handleTilesRefilled(Map<Coordinates, AmmoTile> tiles) {
        super.handleTilesRefilled(tiles);
        showMessage("Ammo tiles filled");
    }

    @Override
    void handleWeaponSwitchRequest(List<Weapon> weapons) {
        super.handleWeaponSwitchRequest(weapons);
        StringBuilder text = new StringBuilder("Select a weapon to switch:\n");
        int index = 1;
        for (Weapon weapon : weapons) {
            String toAppend = "[" + index + "] - " + weapon;
            text.append(toAppend);
            if (getSelfPlayerBoard().getReadyWeapons().contains(weapon)) {
                text.append(" [READY]\n");
            } else {
                text.append(" [UNLOADED]\n");
            }
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handlePickupActionRequest(List<Coordinates> coordinates) {
        super.handlePickupActionRequest(coordinates);
        showMessage(getBoard().arenaToString(coordinates));
        showMessage("You can move and pickup in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
    }

    @Override
    void handleMovementActionRequest(List<Coordinates> coordinates) {
        super.handleMovementActionRequest(coordinates);
        showMessage(getBoard().arenaToString(coordinates));
        showMessage("You can move in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
    }

    @Override
    void handlePowerupTargetRequest(List<GameCharacter> targets) {
        super.handlePowerupTargetRequest(targets);
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
    }

    @Override
    void handlePowerupPositionRequest(List<Coordinates> coordinates) {
        super.handlePowerupPositionRequest(coordinates);
        showMessage(getBoard().arenaToString(coordinates));
        switch (getActivePowerup()) {
            case TELEPORTER:
                showMessage("You can move in the squares marked with '***'\nInsert [x,y]:");
                break;
            case NEWTON:
                showMessage("You can move your target in the squares marked with '***'\nInsert [x,y]:");
                break;
        }
    }

    @Override
    void handleReloadRequest(List<Weapon> weapons) {
        super.handleReloadRequest(weapons);
        StringBuilder text = new StringBuilder("Select a weapon to recharge or skip:\n");
        int index = 1;
        for (Weapon weapon : weapons) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        showMessage(text.toString());
    }

    @Override
    void handleDiscardPowerupRequest(List<Powerup> powerups) {
        super.handleDiscardPowerupRequest(powerups);
        StringBuilder text = new StringBuilder("Discard a powerup to spawn:\n");
        int index = 1;
        for(Powerup p : powerups) {
            String toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleUsePowerupRequest(List<Powerup> powerups) {
        super.handleUsePowerupRequest(powerups);
        StringBuilder text = new StringBuilder("Select a powerup to use or skip:\n");
        int index = 1;
        for (Powerup p : powerups) {
            String toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
            text.append(toAppend);
            index++;
        }
        text.append("[" + index + "] - Cancel");
        showMessage(text.toString());
    }

    @Override
    void handleWeaponPickupRequest(List<Weapon> weapons) {
        super.handleWeaponPickupRequest(weapons);
        StringBuilder text = new StringBuilder("Select a weapon to pickup:\n");
        int index = 1;
        for (Weapon weapon : weapons) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleActionSelectionRequest(List<ActionType> actions) {
        super.handleActionSelectionRequest(actions);
        showActions();
    }

    private void showMessage(String message) {
        System.out.println("\n" + message);
    }

    private void showActions() {
        StringBuilder text = new StringBuilder();
        text.append("Select one of these actions:\n");
        text.append("[1] - Show arena and killshot track\n");
        text.append("[2] - Show your board\n");
        text.append("[3] - Show enemy boards\n");
        int number = 4;
        String toAppend;
        for (ActionType action : getActionsSelection()) {
            switch (action) {
                case MOVE:
                    toAppend = "[" + number + "] - Move yourself by up to 3 squares\n";
                    text.append(toAppend);
                    break;
                case PICKUP:
                    if (getSelfPlayerBoard().getDamages().size() < 3) {
                        toAppend = "[" + number + "] - Move yourself by up to 1 square and pickup\n";
                    } else {
                        toAppend = "[" + number + "] - Move yourself by up to 2 squares and pickup\n";
                    }
                    text.append(toAppend);
                    break;
                case SHOT:
                    if (getSelfPlayerBoard().getDamages().size() < 6) {
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

    @Override
    void requirePayment() {
        StringBuilder text = new StringBuilder("You must pay ");
        String toAppend;
        for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            toAppend = ammo.getValue() + "x" + ammo.getKey() + ", ";
            text.append(toAppend);
        }
        text.setLength((text.length() - 1));
        text.append("\nSelect ammos or powerups:\n");
        int index = 1;
        for (Map.Entry<AmmoType, Integer> ammo : getAmmosSelection().entrySet()) {
            if (ammo.getValue() != 0 && getRequiredPayment().keySet().contains(ammo.getKey()) &&
                    getRequiredPayment().get(ammo.getKey()) != 0) {
                toAppend = "[" + index + "] - " + ammo.getKey() + " ammo\n";
                text.append(toAppend);
                index++;
            }
        }
        for (Powerup p : getPowerupsSelection()) {
            if (getRequiredPayment().keySet().contains(p.getColor()) && getRequiredPayment().get(p.getColor()) != 0) {
                toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
                text.append(toAppend);
                index++;
            }
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }
}
