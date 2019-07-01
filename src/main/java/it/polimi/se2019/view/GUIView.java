package it.polimi.se2019.view;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
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
import javafx.application.Platform;
import javafx.scene.Scene;

import java.util.*;

import static it.polimi.se2019.view.ClientState.*;

/**
 * Class for handling GUI view
 */
public class GUIView extends View {

    private GUIApp guiApp;
    private AbstractSceneController controller;

    private LinkedList<String> messages;
    private String currentStatus;
    private String currentAction;
    private SceneType currentScene;

    private List<String> secondaryButtons;

    /**
     * Class constructor, it builds a CLI view
     * @param connection "0" for socket, "1" for RMI
     * @param ip of the server
     * @param port of the server
     * @param guiApp the gui app
     */
    GUIView(int connection, String ip, int port,  GUIApp guiApp) {
        super();
        this.guiApp = guiApp;
        super.connect(connection, ip, port);
        this.messages = new LinkedList<>();
        this.secondaryButtons = new ArrayList<>();
    }

    /**
     * Sets the controller for GUI scene
     * @param controller to be set
     */
    void setActiveController(AbstractSceneController controller) {
        this.controller = controller;
    }

    /**
     * Sets the scene to be shown
     * @param scene to bne shown
     */
    private void setScene(SceneType scene) {
        if (this.currentScene != scene) {
            this.controller = null;
            this.guiApp.setScene(scene);
            synchronized (this) {
                while(this.controller == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            this.currentScene = scene;
        }
    }

    /**
     * Handles client nickname input
     * @param input nickname of the client
     */
    void handleNicknameInput(String input) {
        if (input.equalsIgnoreCase("")) {
            this.guiApp.showAlert("Invalid input!");
            return;
        }
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    /**
     * Handles client character choice
     * @param character chosen
     */
    void handleCharacterInput(GameCharacter character) {
        if (getState() == CHOOSING_CHARACTER) {
            getClient().send(new CharacterMessage(character, generateToken()));
        }
    }

    /**
     * Handles client skulls number input
     * @param skullsNumber skulls number chosen
     */
    void handleSkullsInput(int skullsNumber) {
        getClient().send(new SkullsMessage(skullsNumber));
    }

    /**
     * Handles client arena choice
     * @param arenaNumber arena chosen
     */
    void handleArenaInput(String arenaNumber) {
        getClient().send(new ArenaMessage(arenaNumber));
    }

    /**
     * Sets the scene for reconnection attempt
     */
    @Override
    void handleReconnectionRequest() {
        super.handleReconnectionRequest();
        setScene(SceneType.RELOAD_GAME);
    }

    /**
     * Sets the scene for connection error
     */
    @Override
    public void handleConnectionError() {
        this.guiApp.setScene(SceneType.CONNECTION_ERROR);
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
               GUIView.super.handleConnectionError();
            }
        }, 7*1000L);
    }

    /**
     * Sets the scene for invalid token
     */
    @Override
    void handleInvalidToken() {
        this.guiApp.setScene(SceneType.INVALID_TOKEN);
    }

    /**
     * Sets the scene for nickname request
     */
    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        setScene(SceneType.SELECT_NICKNAME);
    }

    /**
     * Handles case of nickname duplicated, resetting the scene
     */
    @Override
    void handleNicknameDuplicated() {
        if (getState() == CHOOSING_CHARACTER) {
            setScene(SceneType.SELECT_NICKNAME);
            super.resetSelections();
        }
        super.handleNicknameDuplicated();
        this.guiApp.showAlert("Nickname duplicated!");
    }

    /**
     * Sets the scene for character selection request
     */
    @Override
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        super.handleCharacterSelectionRequest(availables);
        if(!getCharactersSelection().isEmpty()) {
            this.guiApp.showAlert("Character already choosen!");
            ((SelectCharacterController) this.controller).enableCharacters(availables);
            return;
        }
        setCharactersSelection(availables);
        setScene(SceneType.SELECT_CHARACTER);
        ((SelectCharacterController) this.controller).enableCharacters(availables);
    }

    /**
     * Handles a player disconnection fromt the lobby when a game is not yet started
     * @param character
     */
    @Override
    void handleClientDisconnected(GameCharacter character) {
        super.handleClientDisconnected(character);
        if (getState() == WAITING_START || getState() == WAITING_SETUP) {
            ((LobbyController) this.controller).removePlayer(character);
        }
    }

    /**
     * Sets lobby scene when a player setup is finished
     * @param character chosen
     * @param nickname chosen
     * @param otherPlayers Map with game characters and their nicknames
     */
    @Override
    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        super.handlePlayerCreated(character, nickname, otherPlayers);
        Map<GameCharacter, String> players = new LinkedHashMap<>(otherPlayers);
        players.put(character, nickname);
        setScene(SceneType.LOBBY);
        ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
        ((LobbyController) this.controller).setPlayers(players);
    }

    /**
     * Handles player ready, adding it to the the controller
     * @param character chosen
     * @param nickname chosen
     */
    @Override
    void handleReadyPlayer(GameCharacter character, String nickname) {
        super.handleReadyPlayer(character, nickname);
        if (getState() == WAITING_START) {
            ((LobbyController) this.controller).addPlayer(character, nickname);
        }
    }

    /**
     * Handles case of game setup aborting, resetting the scene to "lobby" and "waiting for players" state
     */
    @Override
    void handleSetupInterrupted() {
        if(getState() == SETTING_SKULLS || getState() == SETTING_ARENA) {
            setScene(SceneType.LOBBY);
            Map<GameCharacter, String> players = new LinkedHashMap<>();
            players.put(getCharacter(), getSelfPlayerBoard().getNickname());
            for (PlayerBoard player : getEnemyBoards()) {
                players.put(player.getCharacter(), player.getNickname());
            }
            ((LobbyController) this.controller).setPlayers(players);
        }
        super.handleSetupInterrupted();
        ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
    }

    /**
     * Handles game setup timer
     * @param action type of timer message
     * @param duration
     */
    @Override
    void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (getState() == WAITING_START) {
            switch (action) {
                case START:
                    ((LobbyController) this.controller).setMessage("loading.gif", "Setup will start soon...");
                    break;
                case STOP:
                    ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Handles start setup, setting the scene
     * @param character
     */
    @Override
    void handleStartSetup(GameCharacter character) {
        super.handleStartSetup(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        } else {
            ((LobbyController) this.controller).setMessage("setting.gif", "Master player is setting up the game...");
        }
    }

    /**
     * Sets select arena scene when skulls number is set
     */
    @Override
    void handleSkullsSet() {
        super.handleSkullsSet();
        setScene(SceneType.SELECT_ARENA);
    }

    /**
     * Handles case of master changed resetting the scene to select skulls
     * @param character new master
     */
    @Override
    void handleMasterChanged(GameCharacter character) {
        super.handleMasterChanged(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        }
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
        setScene(SceneType.BOARD);
        setArena();
        setPlayerBoard(getCharacter());
    }

    /**
     * Shows store refilled
     * @param weapons Map with weapon and coordinates of the square where the weapon has to be placed
     */
    @Override
    void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        super.handleStoresRefilled(weapons);
        updateStores();
    }

    /**
     * Shows ammo tiles refilled
     * @param tiles Map with coordinates and tiles refilled
     */
    @Override
    void handleTilesRefilled(Map<Coordinates, AmmoTile> tiles) {
        super.handleTilesRefilled(tiles);
        updateTiles();
    }

    /**
     * Shows player spawned message
     * @param character who has spawned
     * @param coordinates where the character has spawned
     */
    @Override
    void handleSpawnedPlayer(GameCharacter character, Coordinates coordinates) {
        super.handleSpawnedPlayer(character, coordinates);
        if (getCharacter() != character) {
            addMessage(character + " spawned");
        }
        showMessage();
        updatePlayersPositions();
    }

    /**
     * Shows movement
     * @param character who has moved
     * @param coordinates where the character has moved
     */
    @Override
    void handleMovement(GameCharacter character, Coordinates coordinates) {
        super.handleMovement(character, coordinates);
        if (getCharacter() != character) {
            addMessage(character + " moved");
        }
        showMessage();
        updatePlayersPositions();
    }

    /**
     * Shows attack
     * @param character who received the attack
     * @param attacker who performed the attack
     * @param amount of damage or marks give
     * @param attackType type of the attack
     */
    @Override
    void handleAttack(GameCharacter character, GameCharacter attacker, int amount, EffectType attackType) {
        super.handleAttack(character, attacker, amount, attackType);
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

        addMessage(text.toString());
        showMessage();
        if (attackType == EffectType.DAMAGE) {
            updateBoardDamages(character);
        } else if (attackType == EffectType.MARK) {
            updateBoardMarks(character);
        }

        if (getState() == MULTIPLE_POWERUPS_SELECTION && attacker != getCharacter()) {
            handleUsePowerupRequest(getPowerupsSelection());
        }
    }

    /**
     * Shows marks to damages
     * @param player which the marks has to be converted
     * @param attacker holder of the marks converted
     */
    @Override
    void handleMarksToDamages(GameCharacter player, GameCharacter attacker) {
        super.handleMarksToDamages(player, attacker);
        if (player == getCharacter()) {
            addMessage(attacker + "'s marks on you converted");
        } else if (attacker == getCharacter()) {
            addMessage("Your marks on " + player + " have been converted into damages");
        } else {
            addMessage(attacker + "'s marks on " + player + " converted");
        }
        showMessage();
        updateBoardMarks(player);
        updateBoardDamages(player);
    }

    /**
     * Shows ammo obtained
     * @param character who got ammo
     * @param ammos obtained
     */
    @Override
    void handleAddAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        super.handleAddAmmos(character, ammos);
        if (character != getCharacter()) {
            addMessage(character + " picked up an ammo tile");
        }
        showMessage();
        updateTiles();
        updateAmmo(character);
        updatePowerups(character);
    }

    /**
     * Shows ammo removed message
     * @param character of which the ammo has to be removed
     * @param ammos to be removed
     */
    @Override
    void handleRemoveAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        super.handleRemoveAmmos(character, ammos);
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            if (ammo.getValue() == 0) {
                continue;
            }
            if (getCharacter() != character) {
                addMessage(character + " used " + ammo.getValue() + "x" + ammo.getKey());
            }
            showMessage();
        }
        updateAmmo(character);
    }

    /**
     * Shows powerup drawn
     * @param character who has drawn a powerup
     * @param powerup drawn
     */
    @Override
    void handlePowerupAdded(GameCharacter character, Powerup powerup) {
        super.handlePowerupAdded(character, powerup);
        if (character != getCharacter()) {
            addMessage(character + " has drawn a powerup");
        }
        showMessage();
        updatePowerups(character);
    }

    /**
     * Shows powerup removed
     * @param character who has removed a powerup
     * @param powerup removed
     * @param type of the powerup message
     */
    @Override
    void handlePowerupRemoved(GameCharacter character, Powerup powerup, PowerupMessageType type) {
        super.handlePowerupRemoved(character, powerup, type);
        if (getCharacter() != character) {
            addMessage(character + " has discarded " + powerup.getType() + " " + powerup.getColor());
        }
        showMessage();
        updatePowerups(character);
    }

    /**
     * Shows weapon pickup
     * @param character who pickups the weapon
     * @param weapon picked up
     */
    @Override
    void handleWeaponPickup(GameCharacter character, Weapon weapon) {
        super.handleWeaponPickup(character, weapon);
        if (character != getCharacter()) {
            addMessage(character + " got " + weapon);
        }
        showMessage();
        updateStores();
        updateWeapons(character);
    }

    /**
     * Shows weapon switched
     * @param character who switchs weapons
     * @param oldWeapon to be switched
     * @param newWeapon switched
     */
    @Override
    void handleWeaponSwitch(GameCharacter character, Weapon oldWeapon, Weapon newWeapon) {
        super.handleWeaponSwitch(character, oldWeapon, newWeapon);
        if (character == getCharacter()) {
            addMessage(character + " dropped a " + oldWeapon + " to get a " + newWeapon);
        }
        showMessage();
        updateStores();
        updateWeapons(character);
    }

    /**
     * Shows weapon reloaded
     * @param character who reloaded a weapon
     * @param weapon reloaded
     */
    @Override
    void handleWeaponReload(GameCharacter character, Weapon weapon) {
        super.handleWeaponReload(character, weapon);
        if (character == getCharacter()) {
            addMessage(character + " realoaded " + weapon);
        }
        showMessage();
        updateWeapons(character);
    }

    /**
     * Shows weapon used
     * @param character who uses weapon
     * @param weapon used
     */
    @Override
    void handleWeaponUnload(GameCharacter character, Weapon weapon) {
        super.handleWeaponUnload(character, weapon);
        if (character == getCharacter()) {
            addMessage(character + " used " + weapon);
        }
        showMessage();
        updateWeapons(character);
    }

    /**
     * Displays active player message
     * @param message type of turn
     * @param character who is playing
     */
    @Override
    void handleStartTurn(TurnMessage message, GameCharacter character) {
        resetSelections();
        if (character != getCharacter()) {
            this.currentStatus = "Waiting for other players...";
            this.currentAction = getBoardByCharacter(character).getNickname() + " (" + character + ") is playing";
        } else {
            this.currentStatus = "It's your turn!";
            this.currentAction = null;
        }
        setBanner();
        super.handleStartTurn(message, character);
    }

    /**
     * Handles end turn of a player
     * @param character who ends the turn
     */
    @Override
    void handleEndTurn(GameCharacter character) {
        if (character != getCharacter()) {
            this.currentStatus = "Waiting for other players...";
            this.currentAction = getBoardByCharacter(character).getNickname() + " (" + character +
                    ") finished his turn";
        } else {
            this.currentStatus = "Turn finished";
            this.currentAction = null;
        }
        setBanner();
    }

    /**
     * Shows available actions choice request
     * @param actions List of the avilable actions
     */
    @Override
    void handleActionSelectionRequest(List<ActionType> actions) {
        super.handleActionSelectionRequest(actions);
        showBoard();
        this.currentStatus = "What do you want to do?";
        this.currentAction = "Select an action from the list above";
        setBanner();
    }

    void setActions() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setActions(getActionsSelection());
        }
    }

    void handleActionInput(ActionType action) {
        getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(), action));
        resetSelections();
        setActions();
    }

    /**
     * Shows powerups to discard for spawn
     * @param powerups List of the available powerups to discard for spawn
     */
    @Override
    void handleDiscardPowerupRequest(List<Powerup> powerups) {
        super.handleDiscardPowerupRequest(powerups);
        showBoard();
        this.currentStatus = "Where do you want to spawn?";
        this.currentAction = "Select a powerup to discard";
        setBanner();
        setPowerups();
    }

    void setPowerups() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setPowerups(getPowerupsSelection(), getPaidPowerups());
        }
    }

    void handlePowerupInput(PowerupType type, AmmoType color) {
        switch (getState()) {
            case DISCARD_SPAWN:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.DISCARD_POWERUP, getCharacter(),
                        new Powerup(type, color)));
                break;
            case PAYMENT:
                addPaidPowerup(new Powerup(type, color));

                int newValue = getRequiredPayment().get(color) - 1;
                putRequiredPayment(color, newValue);

                List<Powerup> toRemove = new ArrayList<>();
                for (Powerup p : getPowerupsSelection()) {
                    if (getRequiredPayment().keySet().contains(p.getColor()) && getRequiredPayment().get(p.getColor()) != 0
                            || getRequiredPayment().isEmpty()) {
                        continue;
                    }
                    toRemove.add(p);
                }
                for (Powerup p : toRemove) {
                    removePowerupSelection(p);
                }

                if (getPowerupsSelection().isEmpty()) {
                    getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                            getPaidPowerups()));
                    this.secondaryButtons = new ArrayList<>();
                    setSecondaryButtons();
                    resetSelections();
                    setPowerups();
                    return;
                }

                if (!getRequiredPayment().isEmpty()) {
                    for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
                        if (ammo.getValue() != 0) {
                            requirePayment();
                            return;
                        }
                    }
                }

                getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                        getPaidPowerups()));
                break;
            case USE_POWERUP:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(),
                        new Powerup(type, color)));
                setActivePowerup(type);
                break;

        }
        resetSelections();
        setPowerups();
        setActions();
    }

    void handleConfirmation() {
        switch (getState()) {
            case PAYMENT:
                getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                        getPaidPowerups()));
                resetSelections();
                setPowerups();
                break;
        }
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
    }

    /**
     * Shows squares choice request message for move action
     * @param coordinates List of the available coordinates for move action
     */
    @Override
    void handleMovementActionRequest(List<Coordinates> coordinates) {
        super.handleMovementActionRequest(coordinates);
        addActionsSelection(ActionType.CANCEL);
        setActions();
        this.currentStatus = "Where do you want to move?";
        this.currentAction = "Select one of the available squares or cancel";
        setBanner();
        setSquares();
    }

    void setSquares() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setSquares(getCoordinatesSelection());
        }
    }

    void handleSquareInput(int x, int y) {
        switch (getState()) {
            case SELECT_MOVEMENT:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.MOVE, getCharacter(),
                        new Coordinates(x, y)));
                break;
            case SELECT_PICKUP:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP, getCharacter(),
                        new Coordinates(x, y)));
                break;
            case SELECT_POWERUP_POSITION:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_POSITION, getCharacter(),
                        new Coordinates(x, y)));
                break;
            default:
                break;
        }
        resetSelections();
        setSquares();
        setActions();
    }

    /**
     * Shows squares choice request for move and pickup action
     * @param coordinates List of the available squares for move and pickup action
     */
    @Override
    void handlePickupActionRequest(List<Coordinates> coordinates) {
        super.handlePickupActionRequest(coordinates);
        addActionsSelection(ActionType.CANCEL);
        setActions();
        this.currentStatus = "Where do you want to pickup?";
        this.currentAction = "Select one of the available squares or cancel";
        setBanner();
        setSquares();
    }

    /**
     * Shows weapons pickup choice
     * @param weapons List of the available weapons to pickup
     */
    @Override
    void handleWeaponPickupRequest(List<Weapon> weapons) {
        super.handleWeaponPickupRequest(weapons);
        this.currentStatus = "Which weapon do you want?";
        this.currentAction = "Select one of the available weapons";
        setBanner();
        setWeapons();
    }

    /**
     * Shows weapon switching request message
     * @param weapons List of the weapon of switching
     */
    @Override
    void handleWeaponSwitchRequest(List<Weapon> weapons) {
        super.handleWeaponSwitchRequest(weapons);
        this.currentStatus = "Which weapon do you want to drop?";
        this.currentAction = "Select one of your weapons";
        setBanner();
        setWeapons();
    }

    void setWeapons() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setWeapons(getWeaponsSelection());
        }
    }

    void handleWeaponInput(Weapon weapon) {
        switch (getState()) {
            case SELECT_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP_WEAPON, getCharacter(), weapon));
                break;
            case SWITCH_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.SWITCH, getCharacter(), weapon));
                break;
            default:
                break;
        }
        resetSelections();
        setWeapons();
        setActions();
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
        List<Powerup> toRemove = new ArrayList<>();
        for (Powerup p : getPowerupsSelection()) {
            if (getRequiredPayment().keySet().contains(p.getColor()) && getRequiredPayment().get(p.getColor()) != 0
                    || getRequiredPayment().isEmpty()) {
                continue;
            }
            toRemove.add(p);
        }
        for (Powerup p : toRemove) {
            removePowerupSelection(p);
        }
        if (getPowerupsSelection().isEmpty()) {
            getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                    getPaidPowerups()));
            this.secondaryButtons = new ArrayList<>();
            setSecondaryButtons();
            resetSelections();
            setPowerups();
            return;
        }
        this.currentStatus = text.toString();
        boolean powerupsMandatory = false;
        for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
            if (ammo.getValue() > getSelfPlayerBoard().getAvailableAmmos().get(ammo.getKey())) {
                powerupsMandatory = true;
                break;
            }
        }
        this.secondaryButtons = new ArrayList<>();
        if (powerupsMandatory) {
            this.currentAction = "You must pay with powerups, select desired ones";
        } else {
            this.currentAction = "Select powerups if you want, then confirm";
            this.secondaryButtons.add("continue");
        }
        setBanner();
        setPowerups();
        setSecondaryButtons();
    }

    /**
     * Shows powerups choice
     * @param powerups
     */
    @Override
    void handleUsePowerupRequest(List<Powerup> powerups) {
        super.handleUsePowerupRequest(powerups);
        if (getState() == MULTIPLE_POWERUPS_SELECTION) {
            this.currentStatus = "Which powerups do you want to use?";
            this.currentAction = "Select available powerups";
            this.secondaryButtons = new ArrayList<>();
            this.secondaryButtons.add("continue");
        } else {
            addActionsSelection(ActionType.CANCEL);
            this.currentStatus = "Which powerup do you want to use?";
            this.currentAction = "Select one of the available powerups";
        }
        setActions();
        setBanner();
        setPowerups();
        setSecondaryButtons();
    }

    /**
     * Shows squares choice request message for powerup movement
     * @param coordinates List of the available coordinates for move action for powerup
     */
    @Override
    void handlePowerupPositionRequest(List<Coordinates> coordinates) {
        super.handlePowerupPositionRequest(coordinates);
        switch (getActivePowerup()) {
            case TELEPORTER:
                this.currentStatus = "Where do you want to move?";
                this.currentAction = "Select one of the available squares";
                break;
            case NEWTON:
                this.currentStatus = "Where do you want to move your target?";
                this.currentAction = "Select one of the available squares";
                break;
            default:
                break;
        }
        setBanner();
        setSquares();
    }

    /**
     * Shows targets choice request for powerup targets
     * @param targets List of the available characters targets
     */
    @Override
    void handlePowerupTargetRequest(List<GameCharacter> targets) {
        super.handlePowerupTargetRequest(targets);
        this.currentStatus = "Who do you want to use tour powerup on?";
        this.currentAction = "Select one of the available targets";
        setBanner();
        setTargets();
    }

    void handleTargetInput(GameCharacter character) {
        switch (getState()) {
            case SELECT_POWERUP_TARGET:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_TARGET, getCharacter(),
                        character));
                break;
            default:
                break;

        }
        resetSelections();
        setTargets();
        setActions();
    }

    /**
     * Handles game finish setting the ranking scene
     * @param ranking map with game characters and total points raised
     */
    @Override
    void handleGameFinished(Map<GameCharacter, Integer> ranking) {

    }

    /**
     * Handles persistence finish
     */
    @Override
    void handlePersistenceFinish() {

    }

    /**+
     * Shows a message
     */
    void showMessage() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).showMessage(this.messages);
        }
    }

    void setBanner() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setBanner(this.currentStatus, this.currentAction);
        }
    }

    void addMessage(String message) {
        if (this.messages.size() == 5) {
            this.messages.removeFirst();
        }
        this.messages.addLast(message);
    }

    void setSecondaryButtons() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setSecondaryButtons(this.secondaryButtons);
        }
    }

    void showBoard() {
        setScene(SceneType.BOARD);
        setArena();
        setPlayerBoard(getCharacter());
        updateTiles();
        updatePlayersPositions();
        updateStores();
        showMessage();
        setBanner();
        setActions();
        setSquares();
        setWeapons();
        setSecondaryButtons();
    }

    void showWeaponInfo(Weapon weapon) {
        setScene(SceneType.WEAPON_INFO);
        ((CardDetailController) this.controller).setWeapon(weapon);
    }

    void setArena() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setArena();
        }
    }

    void setTargets() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setTargets(getCharactersSelection());
        }
    }

    void setPlayerBoard(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setPlayerBoard(character);
            if (character == getCharacter()) {
                setPowerups();
                setWeapons();
            }
        }
    }

    void updateStores() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updateStores();
        }
    }

    void updateTiles() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updateTiles();
        }
    }

    void updatePlayersPositions() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updatePlayersPositions();
        }
    }

    void updateBoardDamages(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateBoardDamages();
        }
    }

    void updateBoardMarks(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateBoardMarks();
        }
    }

    void updateAmmo(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateAmmo();
        }
    }

    void updatePowerups(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updatePowerups();
        }
    }

    void updateWeapons(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateWeapons();
        }
    }
}
