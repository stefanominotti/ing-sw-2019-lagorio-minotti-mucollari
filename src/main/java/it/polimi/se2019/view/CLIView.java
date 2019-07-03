package it.polimi.se2019.view;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.CharacterMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.payment.PaymentSentMessage;
import it.polimi.se2019.model.messages.player.ScoreMotivation;
import it.polimi.se2019.model.messages.powerups.PowerupMessageType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import java.util.*;
import static it.polimi.se2019.view.ClientState.*;
import static org.fusesource.jansi.Ansi.ansi;

import org.fusesource.jansi.AnsiConsole;

/**
 * Class for handling CLI view
 */
public class CLIView extends View {

    private static final String INVALID_INPUT_MESSAGE = "Invalid input, retry:";
    private static final int MIN_SKULLS = 3;
    private static final int MAX_SKULLS = 8;
    
    private boolean inputEnabled;

    /**
     * Class constructor, it builds a CLI view
     * @param connection "0" for socket, "1" for RMI
     * @param ip of the server
     * @param port of the server
     */
    public CLIView(int connection, String ip, int port) {
        super();

        super.connect(connection, ip, port);
        AnsiConsole.systemInstall();
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

    /**
     * Handles client input
     * @param input of the client
     */
    private void handleInput(String input) {
        if (!this.inputEnabled || input == null) {
            return;
        }

        if (input.equals("")) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        if (getState() == TYPING_NICKNAME) {
            handleNicknameInput(input);
        } else if (getState() == CHOOSING_CHARACTER || getState() == SELECT_BOARD_TO_SHOW
                || getState() == SELECT_POWERUP_TARGET) {
            handleCharacterInput(input);
        } else if (getState() == SETTING_SKULLS) {
            handleSkullsInput(input);
        } else if (getState() == SETTING_ARENA) {
            handleArenaInput(input);
        } else if (getState() == DISCARD_SPAWN || getState() == USE_POWERUP) {
            handlePowerupInput(input);
        } else if (getState() == SELECT_ACTION) {
            handleActionInput(input);
        } else if (getState() == SELECT_MOVEMENT || getState() == SELECT_PICKUP ||
                getState() == SELECT_POWERUP_POSITION) {
            handlePositionInput(input);
        } else if (getState() == SELECT_WEAPON || getState() == SWITCH_WEAPON || getState() == RECHARGE_WEAPON ||
                getState() == USE_WEAPON) {
            handleWeaponInput(input);
        } else if (getState() == PAYMENT) {
            handlePaymentInput(input);
        } else if (getState() == USE_EFFECT) {
            handleEffectInput(input);
        } else if (getState() == EFFECT_COMBO_SELECTION) {
            handleDecisionInput(input);
        } else if (getState() == EFFECT_SELECT_SQUARE) {
            handleSelectSquareInput(input);
        } else if (getState() == EFFECT_SELECT_ROOM) {
            handleSelectRoomInput(input);
        } else if (getState() == EFFECT_SELECT_CARDINAL) {
            handleSelectCardinalInput(input);
        } else if (getState() == EFFECT_TARGET_SELECTION) {
            handleEffectTargetInput(input);
        } else if (getState() == EFFECT_REQUIRE_SELECTION) {
            handleDecisionInput(input);
        } else if (getState() == MULTIPLE_SQUARES_SELECTION) {
            handleEffectMultipleSquaresInput(input);
        } else if (getState() == MULTIPLE_POWERUPS_SELECTION) {
            handleMultiplePowerupsInput(input);
        } else if (getState() == PERSISTENCE_SELECTION) {
            handlePersistenceInput(input);
        }
    }

    /**
     * Handles client input on persistence question
     * @param input answer of the client
     */
    private void handlePersistenceInput(String input) {
        if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n")) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.PERSISTENCE, getCharacter(), input));
            this.inputEnabled = false;
            return;
        }
        showMessage(INVALID_INPUT_MESSAGE);
    }

    /**
     * Handles client nickname input
     * @param input nickname of the client
     */
    private void handleNicknameInput(String input) {
        this.inputEnabled = false;
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    /**
     * Handles client skulls number input
     * @param input skulls number chosen
     */
    private void handleSkullsInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        if (selection < MIN_SKULLS || selection > MAX_SKULLS) {
            showMessage("Skulls number must be between 3 and 8, retry: ");
            return;
        }
        this.inputEnabled = false;
        getClient().send(new SkullsMessage(selection));
    }

    /**
     * Handles client arena choice
     * @param input arena chosen
     */
    private void handleArenaInput(String input) {
        if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")) {
            this.inputEnabled = false;
            getClient().send(new ArenaMessage(input));
            return;
        }
        showMessage("Arena must be [1, 2, 3, 4]:, retry:");
    }

    /**
     * Handles client character choice
     * @param input character chosen
     */
    private void handleCharacterInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        int maxSize = getCharactersSelection().size();
        if (getState() == SELECT_BOARD_TO_SHOW) {
            maxSize = getEnemyBoards().size();
        }
        if (selection > maxSize || selection <= 0) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        selection--;
        this.inputEnabled = false;
        if (getState() == CHOOSING_CHARACTER) {
            getClient().send(new CharacterMessage(getCharactersSelection().get(selection), generateToken()));
        } else if (getState() == SELECT_POWERUP_TARGET) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_TARGET, getCharacter(),
                    getCharactersSelection().get(selection)));
        } else if (getState() == SELECT_BOARD_TO_SHOW) {
            showMessage(getEnemyBoards().get(selection).toString());
            showActions();
            setState(SELECT_ACTION);
            this.inputEnabled = true;
        }
    }

    /**
     * Handles client position input
     * @param input position x,y
     */
    private void handlePositionInput(String input) {
        if (input.equalsIgnoreCase("c") && getState() != SELECT_POWERUP_POSITION) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                    ActionType.CANCEL));
            return;
        }
        if (input.split(",").length != 2) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        int x;
        int y;
        try {
            x = Integer.parseInt(input.split(",")[0]);
            y = Integer.parseInt(input.split(",")[1]);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
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
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        this.inputEnabled = false;
        if (getState() == SELECT_MOVEMENT) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.MOVE, getCharacter(),
                    new Coordinates(x, y)));
        } else if (getState() == SELECT_PICKUP) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP, getCharacter(),
                    new Coordinates(x, y)));
        } else if (getState() == SELECT_POWERUP_POSITION) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_POSITION, getCharacter(),
                    new Coordinates(x, y)));
        }

    }

    /**
     * Handles client powerup choice input
     * @param input powerup chosen
     */
    private void handlePowerupInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        int maxNumber = getPowerupsSelection().size();
        if (getState() == USE_POWERUP) {
            maxNumber++;
        }

        if (0 >= selection || selection > maxNumber) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        Powerup toSend;
        if (getState() == USE_POWERUP && selection == getPowerupsSelection().size() + 1) {
            toSend = null;
        } else {
            toSend = getPowerupsSelection().get(selection - 1);
            setActivePowerup(toSend.getType());
            resetSelections();
        }

        this.inputEnabled = false;
        if (getState() == USE_POWERUP) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(), toSend));
        } else if (getState() == DISCARD_SPAWN) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.DISCARD_POWERUP, getCharacter(),
                    toSend));
        }
    }

    /**
     * Handles client multiple powerup choice input
     * @param input client choice
     */
    private void handleMultiplePowerupsInput(String input) {
        String[] inputList = input.split(",");
        List<Powerup> powerups = new ArrayList<>();
        try {
            for (String i : inputList) {
                int index = Integer.parseInt(i);
                if (index == getPowerupsSelection().size() + 1 && inputList.length == 1) {
                    this.inputEnabled = false;
                    setState(OTHER_PLAYER_TURN);
                    getClient().send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP, getCharacter(), null));
                    return;
                }
                if (!powerups.contains(getPowerupsSelection().get(index - 1))) {
                    powerups.add(getPowerupsSelection().get(index - 1));
                } else {
                    showMessage(INVALID_INPUT_MESSAGE);
                    return;
                }
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        this.inputEnabled = false;
        setState(OTHER_PLAYER_TURN);
        getClient().send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP, getCharacter(), powerups));
    }

    /**
     * Handles client weapon choice input
     * @param input weapon chosen
     */
    private void handleWeaponInput(String input) {
        int selection;

        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        int maxNumber = getWeaponsSelection().size();
        if (getState() == RECHARGE_WEAPON || getState() == USE_WEAPON) {
            maxNumber++;
        }

        if (0 >= selection || selection > maxNumber) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        Weapon toSend;
        if (getState() == USE_WEAPON && selection == maxNumber) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                    ActionType.CANCEL));
            return;
        } else if (getState() == RECHARGE_WEAPON && selection == maxNumber) {
            toSend = null;
        } else {
            toSend = getWeaponsSelection().get(selection - 1);
            resetSelections();
        }

        this.inputEnabled = false;
        if (getState() == SELECT_WEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP_WEAPON, getCharacter(), toSend));
        } else if (getState() == SWITCH_WEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.SWITCH, getCharacter(), toSend));
        } else if (getState() == RECHARGE_WEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.RELOAD, getCharacter(), toSend));
        } else if (getState() == USE_WEAPON) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_WEAPON, getCharacter(), toSend));
            setCurrentWeapon(toSend);
            setWeaponActivated(false);
        }
    }

    /**
     * Handles client action input
     * @param input client choice
     */
    private void handleActionInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        if (0 >= selection || selection > getActionsSelection().size() + 3) {
            showMessage(INVALID_INPUT_MESSAGE);
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
                setState(SELECT_BOARD_TO_SHOW);
                break;
            default:
                this.inputEnabled = false;
                getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                        getActionsSelection().get(selection - 4)));
        }
    }

    /**
     * Handles client payment choice
     * @param input payment chosen
     */
    private void handlePaymentInput(String input) {
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        List<AmmoType> payableAmmos = new ArrayList<>();
        List<Powerup> payablePowerups = new ArrayList<>();

        for (Map.Entry<AmmoType, Integer> ammo : getAmmosSelection().entrySet()) {
            if (ammo.getValue() != 0 && (getRequiredPayment().keySet().contains(ammo.getKey()) &&
                    getRequiredPayment().get(ammo.getKey()) != 0 || getRequiredPayment().isEmpty())) {
                payableAmmos.add(ammo.getKey());
            }
        }
        for (Powerup p : getPowerupsSelection()) {
            if (getRequiredPayment().keySet().contains(p.getColor()) &&
                    getRequiredPayment().get(p.getColor()) != 0 || getRequiredPayment().isEmpty()) {
                payablePowerups.add(p);
            }
        }

        if (0 >= selection || selection > payableAmmos.size() + payablePowerups.size()) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        selection--;

        if(getRequiredPayment().isEmpty()) {
            if (selection < payableAmmos.size()) {
                putPaidAmmos(payableAmmos.get(selection), 1);
            } else {
                selection -= payableAmmos.size();
                addPaidPowerup(payablePowerups.get(selection));
            }
        } else {
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
        }


        if (!getRequiredPayment().isEmpty()) {
            for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
                if (ammo.getValue() != 0) {
                    requirePayment();
                    return;
                }
            }
        }

        this.inputEnabled = false;
        getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getPaidAmmos(),
                getPaidPowerups()));
        resetSelections();
    }

    /**
     * Handles client effect choice
     * @param input effect chosen
     */
    private void handleEffectInput(String input) {
        boolean valid = false;
        if (input.equalsIgnoreCase("C") && !isWeaponActivated()) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(),
                    ActionType.CANCEL));
            return;
        }
        if (input.equalsIgnoreCase("S") && isWeaponActivated()) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT, getCharacter(), null));
            return;
        }

        for (WeaponEffectOrderType validInput : getEffectsSelection()) {
            if (validInput.toString().equalsIgnoreCase(input)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        this.inputEnabled = false;
        getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT, getCharacter(),
                WeaponEffectOrderType.valueOf(input.toUpperCase())));
        setWeaponActivated(true);
    }

    /**
     * Handle client decision input
     * @param input client decision
     */
    private void handleDecisionInput(String input) {
        input = input.toUpperCase();
        if (!input.equals("Y") && !input.equals("N")) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }

        if (getState() == EFFECT_COMBO_SELECTION) {
            this.inputEnabled = false;
            getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT_COMBO, getCharacter(), input));
        } else if (getState() == EFFECT_REQUIRE_SELECTION) {
            if (input.equals("Y")) {
                super.setPossibilityRequire(true);
                return;
            }
            super.setPossibilityRequire(false);
        }

    }

    /**
     * Handles client square choice
     * @param input square chosen
     */
    private void handleSelectSquareInput(String input) {
        List<Coordinates> square = new ArrayList<>();
        try {
            int index = Integer.parseInt(input);
            square.add(getEffectPossibility().getSquares().get(index - 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        super.setPossibilitySquares(square);
        this.inputEnabled = false;
        super.selectionEffectFinish();
    }

    /**
     * Handles client room choice
     * @param input room chosen
     */
    private void handleSelectRoomInput(String input) {
        List<RoomColor> room = new ArrayList<>();
        try {
            int index = Integer.parseInt(input);
            room.add(getEffectPossibility().getRooms().get(index - 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        super.setPossibilityRooms(room);
        this.inputEnabled = false;
        super.selectionEffectFinish();
    }

    /**
     * Handles client cardinal point choice
     * @param input cardinal point chosen
     */
    private void handleSelectCardinalInput(String input) {
        List<CardinalPoint> cardinal = new ArrayList<>();
        try {
            int index = Integer.parseInt(input);
            cardinal.add(getEffectPossibility().getCardinalPoints().get(index - 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        super.setPossibilityCardinal(cardinal);
        this.inputEnabled = false;
        super.selectionEffectFinish();
    }

    /**
     * Handles clien effect target choice
     * @param input target chosen
     */
    private void handleEffectTargetInput(String input) {
        String[] inputList = input.split(",");
        List<GameCharacter> characters = new ArrayList<>();
        try {
            for (String i : inputList) {
                int index = Integer.parseInt(i);
                if (!characters.contains(getEffectPossibility().getCharacters().get(index - 1))) {
                    characters.add(getEffectPossibility().getCharacters().get(index - 1));
                } else {
                    showMessage(INVALID_INPUT_MESSAGE);
                    return;
                }
            }
            List<String> targetsAmaunt = getEffectPossibility().getTargetsAmount();
            if (targetsAmaunt.size() == 1 && characters.size() != Integer.parseInt(targetsAmaunt.get(0)) ||
                    targetsAmaunt.size() > 1 && (characters.size() < Integer.parseInt(targetsAmaunt.get(0)) ||
                            (!targetsAmaunt.get(1).equals("MAX") && characters.size() > Integer.parseInt(targetsAmaunt.get(1))))) {
                showMessage(INVALID_INPUT_MESSAGE);
                return;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        super.setPossibilityCharacters(characters);
        if (!getEffectPossibility().getSquares().isEmpty()) {
            handleEffectMoveRequest();
        } else {
            this.inputEnabled = false;
            super.selectionEffectFinish();
        }
    }

    /**
     * Handles client multiple squares target input
     * @param input multiple squares chosen and target chosen
     */
    private void handleEffectMultipleSquaresInput(String input) {
        String[] inputList = input.split(",");
        Map<Coordinates, List<GameCharacter>> availableCharacters = new LinkedHashMap<>(getEffectPossibility().getMultipleSquares());
        List<GameCharacter> selectedCharacters = new ArrayList<>();
        List<Coordinates> availableSquares = new ArrayList<>(getEffectPossibility().getMultipleSquares().keySet());
        try {
            for (String i : inputList) {
                String[] strings = i.split(".");
                int squareIndex = Integer.parseInt(strings[0]) - 1;
                int characterIndex = Integer.parseInt(strings[1]) - 1;
                if (squareIndex < 0 || characterIndex < 0 || squareIndex >= availableCharacters.size()) {
                    showMessage(INVALID_INPUT_MESSAGE);
                    return;
                }
                for (Map.Entry<Coordinates, List<GameCharacter>> characters : availableCharacters.entrySet()) {
                    if (squareIndex == 0 && availableSquares.contains(characters.getKey()) && characterIndex < characters.getValue().size()) {
                        selectedCharacters.add(characters.getValue().get(characterIndex));
                        availableSquares.remove(characters.getKey());
                        break;
                    } else if (squareIndex < 0 || squareIndex == 0 && (!availableSquares.contains(characters.getKey()) || characterIndex >= characters.getValue().size())) {
                        showMessage(INVALID_INPUT_MESSAGE);
                        return;
                    }
                    squareIndex--;
                }
            }
            List<String> targetsAmount = getEffectPossibility().getTargetsAmount();
            if (targetsAmount.size() == 1 && selectedCharacters.size() != Integer.parseInt(targetsAmount.get(0)) ||
                    targetsAmount.size() > 1 && (selectedCharacters.size() < Integer.parseInt(targetsAmount.get(0)) ||
                            (!targetsAmount.get(1).equals("MAX") && selectedCharacters.size() > Integer.parseInt(targetsAmount.get(1))))) {
                showMessage(INVALID_INPUT_MESSAGE);
                return;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showMessage(INVALID_INPUT_MESSAGE);
            return;
        }
        this.inputEnabled = false;
        super.setPossibilityCharacters(selectedCharacters);
        super.selectionEffectFinish();
    }

    /**
     * Shows connection errors message
     */
    @Override
    public void handleConnectionError() {
        showMessage("Connection error, server unreachable or network unavailable\nTry to restart");
        super.handleConnectionError();
    }

    /**
     * Shows nickname request message
     */
    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        showMessage("Insert nickname: ");
        this.inputEnabled = true;
    }

    /**
     * Shows nickname duplicated message
     */
    @Override
    void handleNicknameDuplicated() {
        super.handleNicknameDuplicated();
        showMessage("Nickname is already in use. Insert another nickname: ");
        this.inputEnabled = true;
    }

    /**
     * Shows setup timer message
     * @param action type of timer message
     * @param duration timer for setup
     */
    @Override
    void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (getState() == WAITING_START) {
            switch (action) {
                case START:
                    showMessage("Game setup will start in " + duration + " seconds...");
                    break;
                case STOP:
                    showMessage("Need more players to start the game");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Shows powerup timer message
     * @param action type of timer message
     */
    @Override
    void handlePowerupTimer(TimerMessageType action) {
        if (getState() == MULTIPLE_POWERUPS_SELECTION) {
            showMessage("Time out, sorry");
            this.inputEnabled = false;
        }
        super.handlePowerupTimer(action);
    }

    /**
     * Shows player created message
     * @param character chosen by the player
     * @param nickname chosen by the player
     * @param otherPlayers map with character and its nickname of the other players
     */
    @Override
    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        super.handlePlayerCreated(character, nickname, otherPlayers);
        showMessage("Nickname " + nickname + " accepted! You are " + character);
        for (PlayerBoard board : getEnemyBoards()) {
            showMessage(board.getNickname() + " - " + board.getCharacter() + " is in!");
        }
    }

    /**
     * Shows connected player message
     * @param character of the connected player
     * @param nickname of the connected player
     */
    @Override
    void handleReadyPlayer(GameCharacter character, String nickname) {
        super.handleReadyPlayer(character, nickname);
        if (getCharacter() != null && character != getCharacter()) {
            showMessage(nickname + " - " + character + " connected!");
        }
    }

    /**
     * Shows player spawned message
     * @param character who has spawned
     * @param coordinates where the character has spawned
     */
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

    /**
     * Shows skulls set message
     */
    @Override
    void handleSkullsSet() {
        super.handleSkullsSet();
        showMessage("OK, now select the Arena [1, 2, 3, 4]:");
        this.inputEnabled = true;
    }

    /**
     * Shows master changed message
     * @param character active character
     */
    @Override
    void handleMasterChanged(GameCharacter character) {
        super.handleMasterChanged(character);
        if (character == getCharacter()) {
            showMessage("Master disconnected, you are the new master. Set skull number for the game:");
            this.inputEnabled = true;
        } else {
            showMessage("Master disconnected, the new master is setting up the game");
        }
    }

    /**
     * Shows skull number request message
     * @param character active character
     */
    @Override
    void handleStartSetup(GameCharacter character) {
        super.handleStartSetup(character);
        if (character == getCharacter()) {
            showMessage("You are the master, set Skulls number for the game:");
            this.inputEnabled = true;
        } else {
            showMessage("Master player is setting up the game, wait");
        }
    }

    /**
     * Shows movement message
     * @param character who has moved
     * @param coordinates where the character has moved
     */
    @Override
    void handleMovement(GameCharacter character, Coordinates coordinates) {
        super.handleMovement(character, coordinates);
        int x = coordinates.getX();
        int y = coordinates.getY();
        showMessage(getBoard().arenaToString());
        if (getCharacter() == character) {
            showMessage("You moved in [" + x + ", " + y + "]");
        } else {
            showMessage(character + " moved in [" + x + ", " + y + "]");
        }
    }

    /**
     * Shows attack message
     * @param character who received the attack
     * @param attacker who performed the attack
     * @param amount of damage or marks give
     * @param attackType type of the attack
     */
    @Override
    void handleAttack(GameCharacter character, GameCharacter attacker, int amount, EffectType attackType) {
        super.handleAttack(character, attacker, amount, attackType);
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        StringBuilder text = new StringBuilder();
        if (attacker == getCharacter()) {
            String toAppend = "You dealt " + amount + " ";
            text.append(toAppend);
        } else if (character == getCharacter()) {
            String toAppend = "You received " + amount + " ";
            text.append(toAppend);
        } else {
            String toAppend = attacker + " dealt " + amount + " ";
            text.append(toAppend);
        }

        if (attackType == EffectType.DAMAGE) {
            text.append("damage");
        } else {
            text.append("mark");
        }

        if (amount != 1) {
            text.append("s");
        }

        if (character == getCharacter()) {
            String toAppend = " from " + attacker;
            text.append(toAppend);
        } else {
            String toAppend = " to " + character;
            text.append(toAppend);
        }

        showMessage(text.toString());

        if (getState() == MULTIPLE_POWERUPS_SELECTION && attacker != getCharacter()) {
            handleUsePowerupRequest(getPowerupsSelection());
        }
    }

    /**
     * Shows marks to damages conversion message
     * @param player which the marks has to be converted
     * @param attacker holder of the marks converted
     */
    @Override
    void handleMarksToDamages(GameCharacter player, GameCharacter attacker) {
        super.handleMarksToDamages(player, attacker);
        if (player == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(player).toString());
        }
        if (player == getCharacter()) {
            showMessage(attacker + "'s marks on you converted into damages");
        } else if (attacker == getCharacter()) {
            showMessage("Your marks on " + player + " have been converted into damages");
        } else {
            showMessage(attacker + "'s marks on " + player + " have been converted into damages");
        }
    }

    /**
     * Shows board flipped message
     * @param player active player
     */
    @Override
    void handleBoardFlip(GameCharacter player) {
        super.handleBoardFlip(player);
        if (player == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(player).toString());
        }
        if (player == getCharacter()) {
            showMessage("Your board flipped");
        } else {
            showMessage(player + "'s board flipped");
        }
    }

    /**
     * Shows score changed message
     * @param player of which the score has changed
     * @param score amount of change
     * @param motivation for score change
     * @param killedCharacter who gave the points
     */
    void handleScoreChange(GameCharacter player, int score, ScoreMotivation motivation, GameCharacter killedCharacter) {
        super.handleScoreChange(player, score, motivation, killedCharacter);
        StringBuilder text = new StringBuilder();
        String toAppend;
        if (player == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
            toAppend = "You got " + score + " points from ";
        } else {
            toAppend = player + " got " + score + " points from ";
        }
        text.append(toAppend);
        switch (motivation) {
            case FIRST_BLOOD:
                text.append("first blood on ");
                if (killedCharacter == getCharacter()) {
                    text.append("you");
                } else {
                    text.append(killedCharacter);
                }
                break;
            case PLAYER_BOARD:
                if (killedCharacter == getCharacter()) {
                    text.append("your ");
                } else {
                    toAppend = killedCharacter + "'s ";
                    text.append(toAppend);
                }
                text.append("player board");
                break;
            case KILLSHOT_TRACK:
                text.append("killshot track");
                break;
        }
        showMessage(text.toString());
    }

    /**
     * Shows player dead message
     * @param player dead
     */
    @Override
    void handleDeath(GameCharacter player) {
        super.handleDeath(player);
        if (getCharacter() == player) {
            showMessage("You died");
        } else {
            showMessage(player + " died");
        }
    }

    /**
     * Shows kill shot points change message
     * @param player of which the kill shot points changed
     */
    @Override
    void handleKillshotPointsChange(GameCharacter player) {
        super.handleKillshotPointsChange(player);
        if (player == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(player).toString());
        }
        if (getCharacter() == player) {
            showMessage("Your killshot points have been reduced");
        } else {
            showMessage(player + "'s killshot points have been reduced");
        }
    }

    /**
     * Shows invalid token message
     */
    @Override
    void handleInvalidToken() {
        showMessage("Invalid token");
        super.handleInvalidToken();
    }

    /**
     * Shows full lobby message
     */
    @Override
    void handleFullLobby() {
        showMessage("Lobby is full");
        super.handleFullLobby();
    }

    /**
     * Shows reconnection attempt message
     */
    @Override
    void handleReconnectionRequest() {
        showMessage("A game already exists, trying to reconnect");
        super.handleReconnectionRequest();
    }

    /**
     * Shows character choice request message
     * @param availables List of the available characters
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows client disconnection message, it also handles setup messages in case the game is not yet started
     * @param character who is disconnected
     */
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
        if (getState() == TYPING_NICKNAME || getState() == WAITING_START || getState() == WAITING_SETUP ||
                getState() == SETTING_SKULLS || getState() == SETTING_ARENA) {
            if (getState() == WAITING_START || getState() == WAITING_SETUP || getState() == SETTING_SKULLS ||
                    getState() == SETTING_ARENA) {
                showMessage(nickname + " - " + character + " disconnected");
            }
            if (getState() == SETTING_SKULLS) {
                showMessage("Set Skulls number for the game:");
            }
            if (getState() == SETTING_ARENA) {
                showMessage("Select the Arena [1, 2, 3, 4]:");
            }
        } else {
            showMessage(nickname + " - " + character + " disconnected");
        }
    }

    /**
     * Shows game already started message
     */
    @Override
    void handleGameAlreadyStarted() {
        showMessage("Game already started, sorry");
        super.handleGameAlreadyStarted();
    }

    /**
     * Loads the view
     * @param character active character
     * @param skulls number
     * @param squares list of the squares view
     * @param killshotTrack map with points and list of characters
     * @param playerBoards list of the player boards
     * @param weapons list of the available weapons on stores
     * @param powerups list of the available powerups
     * @param score your raised points
     * @param others map with the other characters
     * @param isFrenzy true if the Final Frenzy mode is active, else false
     * @param isBeforeFirstPlayer true if the player is playing before the first player in this turn, else false
     * @param arena integer representing arena number
     * @param deadPlayers List of dead players
     */
    @Override
    void loadView(GameCharacter character, int skulls, List<SquareView> squares,
                  Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                  List<Weapon> weapons, List<Powerup> powerups, int score, Map<GameCharacter, String> others,
                  boolean isFrenzy, boolean isBeforeFirstPlayer, int arena, List<GameCharacter> deadPlayers) {
        super.loadView(character, skulls, squares, killshotTrack, playerBoards, weapons, powerups, score, others,
                isFrenzy, isBeforeFirstPlayer, arena, deadPlayers);
        showMessage("You are " + getCharacter());
        for (Map.Entry<GameCharacter, String> player : others.entrySet()) {
            if (player.getKey() != getCharacter()) {
                showMessage(player.getKey() + " - " + player.getValue() + " is in!");
            }
        }
    }

    /**
     * Shows powerup drawn message
     * @param character who has drawn a powerup
     * @param powerup drawn
     */
    @Override
    void handlePowerupAdded(GameCharacter character, Powerup powerup) {
        super.handlePowerupAdded(character, powerup);
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        if (character != getCharacter()) {
            showMessage(character + " has drawn a powerup");
        } else {
            showMessage("You have drawn " + powerup.getType() + " " + powerup.getColor());
        }
    }

    /**
     * Shows powerup removed message
     * @param character who has removed a powerup
     * @param powerup removed
     * @param type of the powerup message
     */
    @Override
    void handlePowerupRemoved(GameCharacter character, Powerup powerup, PowerupMessageType type) {
        super.handlePowerupRemoved(character, powerup, type);
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        if (getCharacter() == character) {
            showMessage("You have discarded " + powerup.getType() + " " + powerup.getColor());
        } else {
            showMessage(character + " has discarded " + powerup.getType() + " " + powerup.getColor());
        }
    }

    /**
     * Shows ammo obtained message
     * @param character who got ammo
     * @param ammos obtained
     */
    @Override
    void handleAddAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        super.handleAddAmmos(character, ammos);
        showMessage(getBoard().arenaToString());
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
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

    /**
     * Shows ammo removed message
     * @param character of which the ammo has to be removed
     * @param ammos to be removed
     */
    @Override
    void handleRemoveAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        super.handleRemoveAmmos(character, ammos);
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
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

    /**
     * Shows weapon pickup
     * @param character who pickups the weapon
     * @param weapon picked up
     */
    @Override
    void handleWeaponPickup(GameCharacter character, Weapon weapon) {
        super.handleWeaponPickup(character, weapon);
        showMessage(getBoard().arenaToString());
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        if (getCharacter() == character) {
            showMessage("You got " + weapon);
        } else {
            showMessage(character + " got " + weapon);
        }
    }

    /**
     * Shows weapon switched message
     * @param character who switchs weapons
     * @param oldWeapon to be switched
     * @param newWeapon switched
     */
    @Override
    void handleWeaponSwitch(GameCharacter character, Weapon oldWeapon, Weapon newWeapon) {
        super.handleWeaponSwitch(character, oldWeapon, newWeapon);
        showMessage(getBoard().arenaToString());
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        if (character == getCharacter()) {
            showMessage("You dropped your " + oldWeapon + " to get a " + newWeapon);
        } else {
            showMessage(character + " dropped a " + oldWeapon + " to get a " + newWeapon);
        }
    }

    /**
     * Shows weapon reloaded message
     * @param character who reloaded a weapon
     * @param weapon reloaded
     */
    @Override
    void handleWeaponReload(GameCharacter character, Weapon weapon) {
        super.handleWeaponReload(character, weapon);
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        if (character == getCharacter()) {
            showMessage("Your " + weapon + " is now ready to fire");
        } else {
            showMessage(character + " realoaded " + weapon);
        }
    }

    /**
     * Shows weapon used message
     * @param character who uses weapon
     * @param weapon used
     */
    @Override
    void handleWeaponUnload(GameCharacter character, Weapon weapon) {
        super.handleWeaponUnload(character, weapon);
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        if (character == getCharacter()) {
            showMessage("Your used " + weapon);
        } else {
            showMessage(character + " used " + weapon);
        }
    }

    /**
     * Shows start turn message
     * @param message type of turn
     * @param character who is playing
     */
    @Override
    void handleStartTurn(TurnMessage message, GameCharacter character) {
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        showMessage(getBoard().killshotTrackToString());
        if (character != getCharacter()) {
            showMessage(character + " is playing");
        } else {
            showMessage("It's your turn!");
        }
        super.handleStartTurn(message, character);
    }

    /**
     * Shows turn finished message
     * @param character of which the turn has finished
     */
    @Override
    void handleEndTurn(GameCharacter character) {
        if (character == getCharacter()) {
            showMessage(getSelfPlayerBoard().toString());
        } else {
            showMessage(getBoardByCharacter(character).toString());
        }
        showMessage(getBoard().killshotTrackToString());
        if (character == getCharacter()) {
            showMessage("Turn finished");
            this.inputEnabled = false;
        } else {
            showMessage(character + "'s turn finished");
        }
    }

    /**
     * Shows turn continuation message
     * @param player who is still playing
     */
    @Override
    void handleTurnContinuation(GameCharacter player) {
        super.handleTurnContinuation(player);
        showMessage(player + " is playing...");
    }

    /**
     * Shows kill shot track change message
     * @param skulls number
     * @param players of which the kill shot track has changed
     */
    @Override
    void handleKillshotTrackChange(int skulls, List<GameCharacter> players) {
        super.handleKillshotTrackChange(skulls, players);
        showMessage(getBoard().killshotTrackToString());
        showMessage((skulls - 1) + " skulls left");
        String player1;
        if (players.contains(getCharacter())) {
            player1 = "You";
        } else {
            player1 = players.get(0).toString();
        }
        if (players.size() == 1) {
            showMessage(player1 + " got 1 mark on killshot track");
        } else if (players.size() == 2 && players.get(0) == players.get(1)) {
            showMessage(player1 + " got 2 marks on killshot track");
        } else {
            showMessage(player1 + " and " + players.get(1) + " got 1 mark on killshot track");
        }
    }

    /**
     * Shows Final Frenzy started message
     * @param beforeFirst true if the player is playing before the first player in this turn, else false
     */
    @Override
    void handleFinalFrenzy(boolean beforeFirst) {
        super.handleFinalFrenzy(beforeFirst);
        showMessage("Final frenzy started");
    }

    /**
     * Shows game finished message and buids the ranking
     * @param ranking map with game characters and total points raised
     */
    @Override
    void handleGameFinished(Map<GameCharacter, Integer> ranking) {
        StringBuilder text = new StringBuilder("Game finished, ranking will be shown soon...");
        int index = 1;
        for(Map.Entry<GameCharacter, Integer> character : ranking.entrySet()) {
            String toAppend = "\n" + index + ": " + character.getKey() + " " + character.getValue();
            text.append(toAppend);
            index++;
        }
        showMessage(text.toString());
        super.handleGameFinished(ranking);
    }

    /**
     * Shows game save message
     */
    @Override
    void handlePersistenceFinish() {
        showMessage("Saved completed!");
        super.handlePersistenceFinish();
    }

    /**
     * Shows too few player message
     */
    @Override
    void handleSetupInterrupted() {
        super.handleSetupInterrupted();
        showMessage("Too few players, game setup interrupted");
    }

    /**
     * Shows game settings message
     * @param colors Map with room colors and their coordinates
     * @param spawns Map with coordinates and true if they are a spawn point, else false
     * @param nearbyAccessibility Map with coordinates  and map with cardinal points and their accessibility
     * @param skulls set for the game
     * @param arena chosen for the game
     */
    @Override
    void handleGameSet(Map<Coordinates, RoomColor> colors, Map<Coordinates, Boolean> spawns,
                       Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility, int skulls, int arena) {
        super.handleGameSet(colors, spawns, nearbyAccessibility, skulls, arena);
        showMessage("Master choose " + skulls + " Skulls and Arena " + arena);
        showMessage("This is the Arena:");
        showMessage(getBoard().arenaToString());
    }

    /**
     * Shows store refilled message
     * @param weapons Map with weapon and coordinates of the square where the weapon has to be placed
     */
    @Override
    void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        super.handleStoresRefilled(weapons);
        showMessage("Weapon stores filled");
    }

    /**
     * Shows ammo tiles refilled message
     * @param tiles Map with coordinates and tiles refilled
     */
    @Override
    void handleTilesRefilled(Map<Coordinates, AmmoTile> tiles) {
        super.handleTilesRefilled(tiles);
        showMessage(getBoard().arenaToString());
        showMessage("Ammo tiles filled");
    }

    /**
     * Shows weapon switching request message
     * @param weapons List of the weapon of switching
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows squares choice request message for move and pickup action
     * @param coordinates List of the available squares for move and pickup action
     */
    @Override
    void handlePickupActionRequest(List<Coordinates> coordinates) {
        super.handlePickupActionRequest(coordinates);
        showMessage(getBoard().arenaToString(coordinates));
        showMessage("You can move and pickup in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
        this.inputEnabled = true;
    }

    /**
     * Shows squares choice request message for move action
     * @param coordinates List of the available coordinates for move action
     */
    @Override
    void handleMovementActionRequest(List<Coordinates> coordinates) {
        super.handleMovementActionRequest(coordinates);
        showMessage(getBoard().arenaToString(coordinates));
        showMessage("You can move in the squares marked with '***'\nInsert [x,y] or type 'C' to cancel:");
        this.inputEnabled = true;
    }

    /**
     * Shows targets choice request message for powerup targets
     * @param targets List of the available characters targets
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows squares choice request message for powerup movement
     * @param coordinates List of the available coordinates for move action for powerup
     */
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
            default:
                break;
        }
        this.inputEnabled = true;
    }

    /**
     * Shows weapons to reload choice message
     * @param weapons List of the available weapons to be reloaded
     */
    @Override
    void handleReloadRequest(List<Weapon> weapons) {
        super.handleReloadRequest(weapons);
        StringBuilder text = new StringBuilder("Select a weapon to reload or skip:\n");
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
        this.inputEnabled = true;
    }

    /**
     * Shows powerups to discard for spawn message
     * @param powerups List of the available powerups to discard for spawn
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows powerups choice message
     * @param powerups
     */
    @Override
    void handleUsePowerupRequest(List<Powerup> powerups) {
        super.handleUsePowerupRequest(powerups);
        StringBuilder text = new StringBuilder();
        if (getState() == MULTIPLE_POWERUPS_SELECTION) {
            text.append("Select powerups to use or skip:\n");
        } else {
            text.append("Select a powerup to use or skip:\n");
        }
        int index = 1;
        for (Powerup p : powerups) {
            String toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
            text.append(toAppend);
            index++;
        }

        String toAppend = "[" + index + "] - Cancel";
        text.append(toAppend);
        showMessage(text.toString());
        this.inputEnabled = true;
    }

    /**
     * Shows weapons pickup choice message
     * @param weapons List of the available weapons to pickup
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows available actions choice request
     * @param actions List of the avilable actions
     */
    @Override
    void handleActionSelectionRequest(List<ActionType> actions) {
        super.handleActionSelectionRequest(actions);
        showMessage(getBoard().arenaToString());
        showActions();
        this.inputEnabled = true;
    }

    /**
     * Shows weapon choice request message
     * @param weapons List of the available weapons to use
     */
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
        this.inputEnabled = true;
    }

    /**
     * Show weapon effect used message
     * @param character that used the effect
     * @param effect used
     */
    void handleEffectSelected(GameCharacter character, WeaponEffectOrderType effect) {
        StringBuilder text = new StringBuilder();
        if (character == getCharacter()) {
            text.append("You");
        } else {
            text.append(character);
        }
        text.append(" used ");
        switch (effect) {
            case PRIMARY:
                text.append("primary effect");
                break;
            case ALTERNATIVE:
                text.append("alternative mode");
                break;
            case SECONDARYONE:
                text.append("first secondary effect");
                break;
            case SECONDARYTWO:
                text.append("alternative secondary effect");
                break;
        }
        showMessage(text.toString());
    }

    /**
     * Shows persistence request message
     */
    @Override
    void handlePersistenceRequest(GameCharacter character) {
        super.handlePersistenceRequest(character);
        if (character == getCharacter()) {
            showMessage("Do you want to save game state? [Y/N]");
            this.inputEnabled = true;
        } else {
            showMessage(character + " is saving the game");
            this.inputEnabled = false;
        }
    }

    /**
     * Shows the effect choice request message
     * @param effects List of the available weapon effect macro
     */
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
        if (isWeaponActivated()) {
            text.append("Type 'S' to skip\n");
        } else {
            text.append("Type 'C' to cancel\n");
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.inputEnabled = true;
    }

    /**
     * Shows effect required request message
     */
    @Override
    void handleEffectRequireRequest() {
        super.handleEffectRequireRequest();
        showMessage("Do you want to perform \"" + getEffectPossibility().getDescription() + "\"? [Y / N]");
        this.inputEnabled = true;
    }

    /**
     * Shows effect combo request message
     * @param effect weapon effect macro of the combo
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows effect targets choice request message
     */
    @Override
    void handleEffectTargetRequest() {
        super.handleEffectTargetRequest();
        if (getEffectPossibility().getCharacters().size() == 1 &&
                getEffectPossibility().getCharacters().get(0) == super.getCharacter()) {
            handleEffectMoveRequest();
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
        this.inputEnabled = true;
    }

    /**
     * Shows the select of the effect
     */
    @Override
    void handleEffectSelectRequest() {
        super.handleEffectSelectRequest();
        showMessage(getBoard().arenaToString());
        StringBuilder text = new StringBuilder("Select a ");
        int index = 1;
        if (getState() == EFFECT_SELECT_SQUARE) {
            text.append("square:\n");
            for (Coordinates s : getEffectPossibility().getSquares()) {
                String toAppend = "[" + index + "] - [" + s.getX() + ", " + s.getY() + "]\n";
                text.append(toAppend);
                index++;
            }
        } else if (getState() == EFFECT_SELECT_CARDINAL) {
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
        this.inputEnabled = true;
    }

    /**
     * Shows movement request message
     */
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
        this.inputEnabled = true;
    }

    /**
     * Shows multiple squares choice request message
     */
    @Override
    void handleMultipleSquareRequest() {
        super.handleMultipleSquareRequest();
        List<String> targetsAmount = getEffectPossibility().getTargetsAmount();
        StringBuilder text = new StringBuilder();
        String toAppend;
        if (targetsAmount.size() == 1) {
            int amount = Integer.parseInt(targetsAmount.get(0));
            toAppend = "Choose " + amount + " players each in different squares\n";
            text.append(toAppend);
        } else if (targetsAmount.get(1).equals("MAX")) {
            int min = Integer.parseInt(targetsAmount.get(0));
            toAppend = "Choose at least " + min + " players each in different squares\n";
            text.append(toAppend);
        } else {
            int min = Integer.parseInt(targetsAmount.get(0));
            int max = Integer.parseInt(targetsAmount.get(1));
            toAppend = "Choose from " + min + " to " + max + " players each in different squares\n";
            text.append(toAppend);
        }
        int squareIndex = 1;
        for (Map.Entry<Coordinates, List<GameCharacter>> square :
                getEffectPossibility().getMultipleSquares().entrySet()) {
            toAppend = "\nFrom [" + square.getKey().getX() + ", " + square.getKey().getY() + "]:\n";
            text.append(toAppend);
            int characterIndex = 1;
            for (GameCharacter character : square.getValue()) {
                toAppend = "[" + squareIndex + "." + characterIndex + "] - " + character + "\n";
                text.append(toAppend);
                characterIndex++;
            }
            squareIndex++;
        }
        text.setLength(text.length() - 1);
        text.append("Type [square.character,...]:\n");
        showMessage(text.toString());
        this.inputEnabled = true;
    }

    /**+
     * Shows a message
     * @param message to be shown
     */
    private void showMessage(String message) {
        System.out.println(ansi().a("\n" + message));
    }

    /**
     * Shows available player actions
     */
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
                    if (getBoard().isFrenzy() && getBoard().isBeforeFirstPlayer()) {
                        toAppend = "[" + number + "] - Move yourself by up to 4 squares\n";
                    } else {
                        toAppend = "[" + number + "] - Move yourself by up to 3 squares\n";
                    }
                    text.append(toAppend);
                    break;
                case PICKUP:
                    if (!getBoard().isFrenzy() && getSelfPlayerBoard().getDamages().size() < 3) {
                        toAppend = "[" + number + "] - Move yourself by up to 1 square and pickup\n";
                    } else if (!getBoard().isFrenzy() || (getBoard().isFrenzy() && getBoard().isBeforeFirstPlayer())) {
                        toAppend = "[" + number + "] - Move yourself by up to 2 squares and pickup\n";
                    } else {
                        toAppend = "[" + number + "] - Move yourself by up to 3 squares and pickup\n";
                    }
                    text.append(toAppend);
                    break;
                case SHOOT:
                    if (!getBoard().isFrenzy() && getSelfPlayerBoard().getDamages().size() < 6) {
                        toAppend = "[" + number + "] - Shoot\n";
                    } else if (!getBoard().isFrenzy()) {
                        toAppend = "[" + number + "] - Move yourself by up to 1 square and shoot\n";
                    } else if (getBoard().isFrenzy() && getBoard().isBeforeFirstPlayer()) {
                        toAppend = "[" + number + "] - Move yourself by up to 1 square, reload and shoot\n";
                    } else {
                        toAppend = "[" + number + "] - Move yourself by up to 2 squares, reload and shoot\n";
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
                default:
                    break;
            }
            number++;
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
    }

    /**
     * Shows payment request message
     */
    @Override
    void requirePayment() {
        StringBuilder text = new StringBuilder("You must pay ");
        String toAppend;
        if (getRequiredPayment().isEmpty()) {
            text.append("one ammo of any color");
        } else {
            for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
                if (ammo.getValue() == 0) {
                    continue;
                }
                toAppend = ammo.getValue() + "x" + ammo.getKey() + ", ";
                text.append(toAppend);
            }
            text.setLength((text.length() - 1));

        }
        text.append("\nSelect ammos or powerups:\n");
        int index = 1;
        for (Map.Entry<AmmoType, Integer> ammo : getAmmosSelection().entrySet()) {
            if (ammo.getValue() != 0 && (getRequiredPayment().keySet().contains(ammo.getKey()) &&
                    getRequiredPayment().get(ammo.getKey()) != 0 || getRequiredPayment().isEmpty())) {
                toAppend = "[" + index + "] - " + ammo.getKey() + " ammo\n";
                text.append(toAppend);
                index++;
            }
        }
        for (Powerup p : getPowerupsSelection()) {
            if (getRequiredPayment().keySet().contains(p.getColor()) && getRequiredPayment().get(p.getColor()) != 0
                    || getRequiredPayment().isEmpty()) {
                toAppend = "[" + index + "] - " + p.getType() + " " + p.getColor() + "\n";
                text.append(toAppend);
                index++;
            }
        }
        text.setLength(text.length() - 1);
        showMessage(text.toString());
        this.inputEnabled = true;
    }
}
