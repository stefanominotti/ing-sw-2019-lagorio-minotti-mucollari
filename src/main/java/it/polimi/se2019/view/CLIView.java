package it.polimi.se2019.view;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.controller.EffectPossibilityPack;
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
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.turn.TurnMessage;

import java.util.*;

import static it.polimi.se2019.view.ClientState.*;

public class CLIView extends View {

    private boolean inputEnabled;

    public CLIView(int connection) {
        super();

        super.connect(connection);

        Thread inputThread = new Thread() {
            Scanner scanner = new Scanner(System.in);
            boolean read = true;

            @Override
            public void run() {
                while (this.read) {
                    String input = this.scanner.nextLine();
                    handleInput(input);
                }
            }
        };
        inputThread.start();
    }

    @Override
    public void manageUpdate(Message message) {
        this.inputEnabled = true;
        super.manageUpdate(message);
    }

    private void handleInput(String input) {
        if (!this.inputEnabled || input == null) {
            return;
        }

        if (input.equals("")) {
            showMessage("Invalid input, retry: ");
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
        } else if (getState() == SELECTWEAPON || getState() == SWITCHWEAPON || getState() == RECHARGEWEAPON || getState() == USEWEAPON) {
            handleWeaponInput(input);
        } else if (getState() == PAYMENT) {
            handlePaymentInput(input);
        } else if (getState() == USEEFFECT) {
            handleEffectInput(input);
        } else if (getState() == EFFECTCOMBO) {
            handleDecisionInput(input);
        } else if (getState() == EFFECTSELECT_SQUARE) {
            handleSelectSquareInput(input);
        } else if (getState() == EFFECTSELECT_ROOM) {
            handleSelectRoomInput(input);
        } else if (getState() == EFFECTSELECT_CARDINAL) {
            handleSelectCardinalInput(input);
        } else if (getState() == EFFECTTARGET) {
            handleEffectTargetInput(input);
        } else if (getState() == EFFECTMOVE) {
            handleSelectSquareInput(input);
        }
    }

    private void handleNicknameInput(String input) {
        this.inputEnabled = false;
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    private void handleSkullsInput(String input) {
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
        this.inputEnabled = false;
        getClient().send(new SkullsMessage(selection));
    }

    private void handleArenaInput(String input) {
        if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
            this.inputEnabled = false;
            getClient().send(new ArenaMessage(input));
            return;
        }
        showMessage("Arena must be [1, 2, 3, 4]:, retry:");
    }

    private void handleCharacterInput(String input) {
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
        if (selection > maxSize || selection <= 0) {
            showMessage("Invalid input, retry: ");
            return;
        }
        selection--;
        this.inputEnabled = false;
        if (getState() == CHOOSINGCHARACTER) {
            getClient().send(new CharacterMessage(getCharactersSelection().get(selection), generateToken()));
        } else if (getState() == SELECTPOWERUPTARGET) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_TARGET, getCharacter(),
                    getCharactersSelection().get(selection)));
        } else if (getState() == SELECTBOARDTOSHOW) {
            showMessage(getEnemyBoards().get(selection).toString());
            showActions();
            setState(SELECTACTION);
        }
    }

    private void handlePositionInput(String input) {
        if (input.equalsIgnoreCase("c") && getState() != SELECTPOWERUPPOSITION) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                    ActionType.CANCEL));
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
        } catch (NumberFormatException e) {
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

        if (toSend == null) {
            showMessage("Invalid input, retry:");
            return;
        }

        this.inputEnabled = false;
        if (getState() == SELECTMOVEMENT) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.MOVE, getCharacter(),
                    new Coordinates(x, y)));
        } else if (getState() == SELECTPICKUP) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP, getCharacter(),
                    new Coordinates(x, y)));
        } else if (getState() == SELECTPOWERUPPOSITION) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_POSITION, getCharacter(),
                    new Coordinates(x, y)));
        }

    }

    private void handlePowerupInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
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

        this.inputEnabled = false;
        if (getState() == USEPOWERUP) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(), toSend));
        } else if (getState() == DISCARDSPAWN) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.DISCARD_POWERUP, getCharacter(),
                    toSend));
        }
    }

    private void handleWeaponInput(String input) {
        int selection;

        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage("Invalid number, retry:");
            return;
        }

        int maxNumber = getWeaponsSelection().size();
        if (getState() == RECHARGEWEAPON || getState() == USEWEAPON) {
            maxNumber++;
        }

        if (0 >= selection || selection > maxNumber) {
            showMessage("Invalid input, retry:");
            return;
        }

        Weapon toSend;
        if (getState() == USEWEAPON && selection == maxNumber) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                    ActionType.CANCEL));
            return;
        } else if (getState() == RECHARGEWEAPON && selection == maxNumber) {
            toSend = null;
        } else {
            toSend = getWeaponsSelection().get(selection - 1);
            resetSelections();
        }

        this.inputEnabled = false;
        if (getState() == SELECTWEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP_WEAPON, getCharacter(), toSend));
        } else if (getState() == SWITCHWEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.SWITCH, getCharacter(), toSend));
        } else if (getState() == RECHARGEWEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.RELOAD, getCharacter(), toSend));
        } else if (getState() == USEWEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_WEAPON, getCharacter(), toSend));
            setCurrentWeapon(toSend);
        }
    }

    private void handleActionInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
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
                this.inputEnabled = false;
                getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                        getActionsSelection().get(selection - 4)));
        }
    }

    private void handlePaymentInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
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

        this.inputEnabled = false;
        getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getPaidAmmos(),
                getPaidPowerups()));
        resetSelections();
    }

    private void handleEffectInput(String input) {
        input = input.toUpperCase();
        boolean valid = false;
        for (WeaponEffectOrderType validInput : getEffectsSelection()) {
            if (validInput.toString().equals(input)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            showMessage("Invalid number, retry:");
            return;
        }

        this.inputEnabled = false;
        getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT, getCharacter(),
                WeaponEffectOrderType.valueOf(input)));
    }

    private void handleDecisionInput(String input) {
        input = input.toUpperCase();
        if (!input.equals("Y") && !input.equals("N")) {
            showMessage("Invalid input, retry:");
            return;
        }

        SelectionMessageType type = SelectionMessageType.EFFECT_COMBO;

        if (getState() == EFFECTCOMBO) {
            type = SelectionMessageType.EFFECT_COMBO;
        }

        this.inputEnabled = false;
        getClient().send(new SingleSelectionMessage(type, getCharacter(), input));
    }

    private void handleSelectSquareInput(String input) {
        List<Coordinates> square = new ArrayList<>();
        try {
            int index = Integer.parseInt(input);
            square.add(getEffectPossibility().getSquares().get(index - 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage("Invalid input, retry: ");
        }
        super.setPossibilitySquares(square);
        this.inputEnabled = false;
        super.selectionEffectFinish();
    }

    private void handleSelectRoomInput(String input) {
        List<RoomColor> room = new ArrayList<>();
        try {
            int index = Integer.parseInt(input);
            room.add(getEffectPossibility().getRooms().get(index - 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage("Invalid input, retry: ");
        }
        super.setPossibilityRooms(room);
        this.inputEnabled = false;
        super.selectionEffectFinish();
    }

    private void handleSelectCardinalInput(String input) {
        List<CardinalPoint> cardinal = new ArrayList<>();
        try {
            int index = Integer.parseInt(input);
            cardinal.add(getEffectPossibility().getCardinalPoints().get(index - 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage("Invalid input, retry: ");
        }
        super.setPossibilityCardinal(cardinal);
        this.inputEnabled = false;
        super.selectionEffectFinish();
    }

    private void handleEffectTargetInput(String input) {
        String[] inputList = input.split(",");
        List<GameCharacter> characters = new ArrayList<>();
        try {
            for (String i : inputList) {
                int index = Integer.parseInt(i);
                if (!characters.contains(getEffectPossibility().getCharacters().get(index - 1))) {
                    characters.add(getEffectPossibility().getCharacters().get(index - 1));
                } else {
                    showMessage("Invalid input, retry: ");
                    return;
                }
            }
            int amount = Integer.parseInt(getEffectPossibility().getTargetsAmount().get(0));
            if(getEffectPossibility().getTargetsAmount().size() == 1 && characters.size() != amount ||
                    getEffectPossibility().getTargetsAmount().size() == 2 && characters.size() < amount) {
                showMessage("Invalid input, retry: ");
                return;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage("Invalid input, retry: ");
        }
        super.setPossibilityCharacters(characters);
        if (!getEffectPossibility().getSquares().isEmpty()) {
            handleEffectMoveRequest();
        } else {
            this.inputEnabled = false;
            super.selectionEffectFinish();
        }
    }

    @Override
    public void handleConnectionError() {
        showMessage("Connection error, server unreachable or network unavailable\nTry to restart");
        super.handleConnectionError();
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
        if (character != getCharacter()) {
            showMessage(nickname + " - " + character + " connected!");
        }
    }

    @Override
    void handleSpawnedPlayer(GameCharacter character, Coordinates coordinates) {
        super.handleSpawnedPlayer(character, coordinates);
        int x = coordinates.getX();
        int y = coordinates.getY();
        if (getCharacter() == character) {
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
        if (getCharacter() == character) {
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
    void handleReconnectionRequest() {
        showMessage("A game already exists, trying to reconnect.");
        super.handleReconnectionRequest();
    }

    @Override
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        super.handleCharacterSelectionRequest(availables);
        if (!getCharactersSelection().isEmpty()) {
            showMessage("Character already choosen");
        }
        setCharactersSelection(availables);
        StringBuilder text = new StringBuilder("Choose one of these characters:\n");
        for (GameCharacter character : availables) {
            String toAppend = "[" + (availables.indexOf(character) + 1) + "] - " + character + "\n";
            text.append(toAppend);
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleClientDisconnected(GameCharacter character) {
        String nickname = "";
        for (PlayerBoard board : getEnemyBoards()) {
            if (board.getCharacter() == character) {
                nickname = board.getNickname();
                break;
            }
        }
        super.handleClientDisconnected(character);
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
            if (player.getKey() != getCharacter()) {
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        }
    }

    @Override
    void handlePowerupAdded(GameCharacter character, Powerup powerup) {
        super.handlePowerupAdded(character, powerup);
        if (character != getCharacter()) {
            showMessage(character + " has drawn a Powerup");
        } else {
            showMessage("You have drawn " + powerup.getType() + " " + powerup.getColor());
        }
    }

    @Override
    void handlePowerupRemoved(GameCharacter character, Powerup powerup, PowerupMessageType type) {
        super.handlePowerupRemoved(character, powerup, type);
        if (getCharacter() == character) {
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
    void handleStartTurn(TurnMessage message, GameCharacter character) {
        if (character != getCharacter()) {
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
        String toAppend = "[" + index + "] - Skip\n";
        text.append(toAppend);
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleDiscardPowerupRequest(List<Powerup> powerups) {
        super.handleDiscardPowerupRequest(powerups);
        showMessage(getBoard().arenaToString());
        StringBuilder text = new StringBuilder("Discard a powerup to spawn:\n");
        int index = 1;
        for (Powerup p : powerups) {
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
        showMessage(getBoard().arenaToString());
        showActions();
    }

    @Override
    void handleWeaponUseRequest(List<Weapon> weapons) {
        super.handleWeaponUseRequest(weapons);
        StringBuilder text = new StringBuilder("Select a weapon to use or cancel:\n");
        int index = 1;
        for (Weapon weapon : weapons) {
            String toAppend = "[" + index + "] - " + weapon + "\n";
            text.append(toAppend);
            index++;
        }
        String toAppend = "[" + index + "] - Cancel\n";
        text.append(toAppend);
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleEffectRequest(List<WeaponEffectOrderType> effects) {
        super.handleEffectRequest(effects);
        StringBuilder text = new StringBuilder("Select an effect to use:\n");
        for (WeaponEffectOrderType effect : effects) {
            String toAppend = "";
            switch (effect) {
                case PRIMARY:
                    toAppend = effect + " - " + getCurrentWeapon().getPrimaryEffect().get(0).getDescription() + "\n";
                    break;
                case ALTERNATIVE:
                    toAppend = effect + " - " + getCurrentWeapon().getAlternativeMode().get(0).getDescription() + "\n";
                    break;
                case SECONDARYONE:
                    toAppend = effect + " - " + getCurrentWeapon().getSecondaryEffectOne().get(0).getDescription() + "\n";
                    break;
                case SECONDARYTWO:
                    toAppend = effect + " - " + getCurrentWeapon().getSecondaryEffectTwo().get(0).getDescription() + "\n";
                    break;
            }
            text.append(toAppend);
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleEffectComboRequest(WeaponEffectOrderType effect) {
        super.handleEffectComboRequest(effect);
        String description;
        if (effect == WeaponEffectOrderType.SECONDARYONE) {
            description = getCurrentWeapon().getSecondaryEffectOne().get(0).getDescription();
        } else {
            description = getCurrentWeapon().getSecondaryEffectTwo().get(0).getDescription();
        }
        showMessage("Do you want to apply \"" + description + "\"? [Y / N]");
    }

    @Override
    void handleEffectTargetRequest() {
        super.handleEffectTargetRequest();
        if(getEffectPossibility().getCharacters().size() == 1 &&
                getEffectPossibility().getCharacters().get(0) == super.getCharacter()) {
            List<GameCharacter> characters = new ArrayList<>();
            characters.add(super.getCharacter());
            super.setPossibilityCharacters(characters);
            return;
        }
        StringBuilder text = new StringBuilder();
        List<String> targetsAmount = getEffectPossibility().getTargetsAmount();
        if (targetsAmount.size() == 1) {
            int amount = Integer.parseInt(targetsAmount.get(0));
            String toAppend = "Select " + amount + " target";
            text.append(toAppend);
            if (amount != 1) {
                text.append("s");
            }
        } else if (targetsAmount.get(1).equals("MAX")) {
            int min = Integer.parseInt(targetsAmount.get(0));
            String toAppend = "Select at least " + min + " target";
            text.append(toAppend);
        } else {
            int min = Integer.parseInt(targetsAmount.get(0));
            int max = Integer.parseInt(targetsAmount.get(1));
            String toAppend = "Select from " + min + " to " + max + " targets";
            text.append(toAppend);
        }
        if (getEffectPossibility().getType() == EffectType.MARK) {
            text.append(" to mark:\n");
        } else if (getEffectPossibility().getType() == EffectType.DAMAGE) {
            text.append(" to damage:\n");
        } else if (getEffectPossibility().getType() == EffectType.MOVE) {
            text.append(" to move:\n");
        }
        int index = 1;
        for (GameCharacter character : getEffectPossibility().getCharacters()) {
            String toAppend = "[" + index + "] - " + character + "\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleEffectSelectRequest() {
        super.handleEffectSelectRequest();
        showMessage(getBoard().arenaToString());
        StringBuilder text = new StringBuilder("Select a ");
        int index = 1;
        if (getState() == EFFECTSELECT_SQUARE) {
            text.append("square:\n");
            for (Coordinates s : getEffectPossibility().getSquares()) {
                String toAppend = "[" + index + "] - [" + s.getX() + ", " + s.getY() + "]\n";
                text.append(toAppend);
                index++;
            }
        } else if (getState() == EFFECTSELECT_CARDINAL) {
            text.append("cardinal direction:\n");
            for (CardinalPoint p : getEffectPossibility().getCardinalPoints()) {
                String toAppend = "[" + index + "] - " + p + "\n";
                text.append(toAppend);
                index++;
            }
        } else {
            text.append("room:\n");
            for (RoomColor room : getEffectPossibility().getRooms()) {
                String toAppend = "[" + index + "] - " + room + "\n";
                text.append(toAppend);
                index++;
            }
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleEffectMoveRequest() {
        super.handleEffectMoveRequest();
        StringBuilder text = new StringBuilder("Select a square to perform the movement:\n");
        int index = 1;
        for (Coordinates s : getEffectPossibility().getSquares()) {
            String toAppend = "[" + index + "] - [" + s.getX() + ", " + s.getY() + "]\n";
            text.append(toAppend);
            index++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    @Override
    void handleMultipleSquareRequest() {
        super.handleMultipleSquareRequest();
        StringBuilder text = new StringBuilder("Select a square:\n");
        for (Map.Entry<Coordinates, List<GameCharacter>> square : getEffectPossibility().getMultipleSquares().entrySet()) {

        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());

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
