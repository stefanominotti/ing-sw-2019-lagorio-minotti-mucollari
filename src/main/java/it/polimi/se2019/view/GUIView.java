package it.polimi.se2019.view;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.arena.RoomColor;
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
import it.polimi.se2019.model.playerassets.AmmoTile;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.PowerupType;
import it.polimi.se2019.model.playerassets.weapons.EffectType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.playerassets.weapons.WeaponEffectOrderType;
import it.polimi.se2019.view.guicontrollers.*;
import it.polimi.se2019.view.modelview.PlayerBoard;
import it.polimi.se2019.view.modelview.SquareView;

import java.util.*;

import static it.polimi.se2019.view.ClientState.*;

/**
 * Class for handling GUI view
 */
public class GUIView extends View {
    
    private static final String LOADING_GIF = "loading.gif";
    private static final String WAITING_FOR_PLAYERS_MESSAGE = "Waiting for players...";
    private static final String WAITING_FOR_PLAYER_TURN_MESSAGE = "Waiting for other players...";
    private static final String CONTINUE_BUTTON = "continue";

    private GUIApp guiApp;
    private AbstractSceneController controller;

    private LinkedList<String> messages;
    private String currentStatus;
    private String currentAction;
    private SceneType currentScene;

    private GameCharacter activePlayer;
    private List<GameCharacter> targetsSelected;
    private int minSelectable;
    private int maxSelectable;

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
        setScene(SceneType.BOARD);
        setArena();
        setPlayerBoard(getCharacter());
        updateTiles();
        updatePlayersPositions();
        updateStores();
        this.currentStatus = "Reconnected!";
        this.currentAction = "You can now keep playing";
        setBanner();
    }

    /**
     * Handles client nickname input
     * @param input nickname of the client
     */
    public void handleNicknameInput(String input) {
        if (input.equalsIgnoreCase("")) {
            this.guiApp.showAlert("Invalid input!");
            return;
        }
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
        setWaitStatus();
    }

    /**
     * Handles client character choice
     * @param character chosen
     */
    public void handleCharacterInput(GameCharacter character) {
        switch (getState()) {
            case CHOOSING_CHARACTER:
                getClient().send(new CharacterMessage(character, generateToken()));
                break;
            case EFFECT_TARGET_SELECTION:
                handleEffectTargetSelect(character);
                return;
            case MULTIPLE_SQUARES_SELECTION:
                handleEffectMultipleSquareSelect(character);
                return;
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
        setWaitStatus();
    }

    /**
     * Handles client skulls number input
     * @param skullsNumber skulls number chosen
     */
    public void handleSkullsInput(int skullsNumber) {
        getClient().send(new SkullsMessage(skullsNumber));
    }

    /**
     * Handles client arena choice
     * @param arenaNumber arena chosen
     */
    public void handleArenaInput(String arenaNumber) {
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
        super.handleConnectionError();
    }

    /**
     * Sets the scene for invalid token
     */
    @Override
    void handleInvalidToken() {
        this.guiApp.setScene(SceneType.INVALID_TOKEN);
    }

    /**
     * Sets the scene for game already started
     */
    @Override
    void handleGameAlreadyStarted() {
        this.guiApp.setScene(SceneType.GAME_ALREADY_STARTED);
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                GUIView.super.handleGameAlreadyStarted();
            }
        }, 2*1000L);
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
     * @param character disconnected
     */
    @Override
    void handleClientDisconnected(GameCharacter character) {
        super.handleClientDisconnected(character);
        if (getState() == WAITING_START || getState() == WAITING_SETUP) {
            ((LobbyController) this.controller).removePlayer(character);
        }
        if (this.currentScene == SceneType.BOARD) {
            addMessage(getBoardByCharacter(character).getNickname() + " (" + character + ") disconnected");
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
        ((LobbyController) this.controller).setMessage(LOADING_GIF, WAITING_FOR_PLAYERS_MESSAGE);
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
        if (this.currentScene == SceneType.BOARD && character != getCharacter()) {
            addMessage(nickname + " (" + character + ") connected");
            showMessage();
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
        ((LobbyController) this.controller).setMessage(LOADING_GIF, WAITING_FOR_PLAYERS_MESSAGE);
    }

    /**
     * Handles game setup timer
     * @param action type of timer message
     * @param duration left
     */
    @Override
    void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (getState() == WAITING_START) {
            switch (action) {
                case START:
                    ((LobbyController) this.controller).setMessage(LOADING_GIF, "Setup will start soon...");
                    break;
                case STOP:
                    ((LobbyController) this.controller).setMessage(LOADING_GIF, WAITING_FOR_PLAYERS_MESSAGE);
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
        if (character != getCharacter()) {
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
        if (character != getCharacter()) {
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
        if (character != getCharacter()) {
            addMessage(character + " used " + weapon);
        }
        showMessage();
        updateWeapons(character);
    }

    /**
     * Shows score changed message
     * @param player of which the score has changed
     * @param score amount of change
     * @param motivation for score change
     * @param killedCharacter who gave the points
     */
    @Override
    void handleScoreChange(GameCharacter player, int score, ScoreMotivation motivation, GameCharacter killedCharacter) {
        super.handleScoreChange(player, score, motivation, killedCharacter);
        StringBuilder text = new StringBuilder();
        String toAppend;
        if (player == getCharacter()) {
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
        addMessage(text.toString());
        showMessage();
        updatePoints();
    }

    /**
     * Continues the current player turn after tagback granade timer
     * @param action type of timer message
     */
    @Override
    void handlePowerupTimer(TimerMessageType action) {
        if (getState() == MULTIPLE_POWERUPS_SELECTION) {
            resetSelections();
            this.secondaryButtons = new ArrayList<>();
            setSecondaryButtons();
            setPowerups();
            this.currentStatus = WAITING_FOR_PLAYER_TURN_MESSAGE;
            this.currentAction = getBoardByCharacter(this.activePlayer).getNickname() + " (" + this.activePlayer +
                    ") is playing";
            setBanner();
        }
        super.handlePowerupTimer(action);
    }

    /**
     * Continues the current player turn after tagback grenade request
     * @param player who is still playing
     */
    @Override
    void handleTurnContinuation(GameCharacter player) {
        resetSelections();
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
        setPowerups();
        this.currentStatus = WAITING_FOR_PLAYER_TURN_MESSAGE;
        this.currentAction = getBoardByCharacter(player).getNickname() + " (" + player + ") is playing";
        setBanner();
        super.handleTurnContinuation(player);
    }

    /**
     * Displays active player message
     * @param message type of turn
     * @param character who is playing
     */
    @Override
    void handleStartTurn(TurnMessage message, GameCharacter character) {
        this.activePlayer = character;
        resetSelections();
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
        setActions();
        setSquares();
        setWeapons();
        setTargets();
        if (character != getCharacter()) {
            this.currentStatus = WAITING_FOR_PLAYER_TURN_MESSAGE;
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
        resetSelections();
        setPowerups();
        setWeapons();
        setSquares();
        setActions();
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
        if (character != getCharacter()) {
            this.currentStatus = WAITING_FOR_PLAYER_TURN_MESSAGE;
            this.currentAction = getBoardByCharacter(character).getNickname() + " (" + character +
                    ") finished his turn";
        } else {
            this.currentStatus = "Turn finished";
            this.currentAction = null;
        }
        setBanner();
    }

    /**
     * Shows Final Frenzy started message
     * @param beforeFirst true if the player is playing before the first player in this turn, else false
     */
    @Override
    void handleFinalFrenzy(boolean beforeFirst) {
        super.handleFinalFrenzy(beforeFirst);
        addMessage("Final frenzy started");
        showMessage();
    }

    /**
     * Shows board flipped message
     * @param player active player
     */
    @Override
    void handleBoardFlip(GameCharacter player) {
        super.handleBoardFlip(player);
        if (player == getCharacter()) {
            addMessage("Your board flipped");
        } else {
            addMessage(player + "'s board flipped");
        }
        showMessage();
        setPlayerBoard(player);
    }

    /**
     * Shows available actions choice request
     * @param actions List of the avilable actions
     */
    @Override
    void handleActionSelectionRequest(List<ActionType> actions) {
        resetSelections();
        super.handleActionSelectionRequest(actions);
        this.currentStatus = "What do you want to do?";
        this.currentAction = "Select an action from the list above";
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
        setBanner();
        setActions();
        setSquares();
        setWeapons();
        setPowerups();
    }

    private void setActions() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setActions(getActionsSelection());
        }
    }

    public void handleActionInput(ActionType action) {
        getClient().send(new SingleSelectionMessage(SelectionMessageType.ACTION, getCharacter(), action));
        resetSelections();
        setActions();
        setWaitStatus();
    }

    /**
     * Shows powerups to discard for spawn
     * @param powerups List of the available powerups to discard for spawn
     */
    @Override
    void handleDiscardPowerupRequest(List<Powerup> powerups) {
        setPlayerBoard(getCharacter());
        super.handleDiscardPowerupRequest(powerups);
        this.currentStatus = "Where do you want to spawn?";
        this.currentAction = "Select a powerup to discard";
        setBanner();
        setPowerups();
    }

    /**
     * Shows powerups choice
     * @param powerups List of powerups to be chosen
     */
    @Override
    void handleUsePowerupRequest(List<Powerup> powerups) {
        setPlayerBoard(getCharacter());
        super.handleUsePowerupRequest(powerups);
        if (getState() == MULTIPLE_POWERUPS_SELECTION) {
            this.currentStatus = "Which powerups do you want to use?";
            this.currentAction = "Select available powerups";
            this.secondaryButtons = new ArrayList<>();
            this.secondaryButtons.add(CONTINUE_BUTTON);
        } else {
            this.secondaryButtons = new ArrayList<>();
            if (powerups.get(0).getType() == PowerupType.TARGETING_SCOPE) {
                this.secondaryButtons.add(CONTINUE_BUTTON);
            } else {
                addActionsSelection(ActionType.CANCEL);
            }
            this.currentStatus = "Which powerup do you want to use?";
            this.currentAction = "Select one of the available powerups";
        }
        setActions();
        setBanner();
        setPowerups();
        setSecondaryButtons();
    }

    void setPowerups() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setPowerups(getPowerupsSelection(), getPaidPowerups());
        }
    }

    public void handlePowerupInput(PowerupType type, AmmoType color) {
        switch (getState()) {
            case DISCARD_SPAWN:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.DISCARD_POWERUP, getCharacter(),
                        new Powerup(type, color)));
                break;
            case PAYMENT:
                handlePowerupPaymentSelect(type, color);
                return;
            case USE_POWERUP:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(),
                        new Powerup(type, color)));
                setActivePowerup(type);
                break;
            case MULTIPLE_POWERUPS_SELECTION:
                handleMultiplePowerupsSelect(type, color);
                return;
            default:
                return;
        }
        this.secondaryButtons = new ArrayList<>();
        resetSelections();
        setSecondaryButtons();
        setActions();
        setPowerups();
        setWaitStatus();
    }

    private void handleMultiplePowerupsSelect(PowerupType type, AmmoType color) {
        for(Powerup powerup: getPowerupsSelection()) {
            if(powerup.getColor() == color && powerup.getType() == type) {
                addPaidPowerup(powerup);
                removePowerupSelection(powerup);
                break;
            }
        }
        if(!getPowerupsSelection().isEmpty()) {
            handleUsePowerupRequest(getPowerupsSelection());
            return;
        }
        getClient().send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP, getCharacter(), getPaidPowerups()));
        this.secondaryButtons = new ArrayList<>();
        setWaitStatus();
        resetSelections();
        setSecondaryButtons();
        setActions();
        setPowerups();
    }

    private void handlePowerupPaymentSelect(PowerupType type, AmmoType color) {
        addPaidPowerup(new Powerup(type, color));
        if(getRequiredPayment().isEmpty()) {
            Map<AmmoType, Integer> ammo = new EnumMap<>(AmmoType.class);
            ammo.put(AmmoType.BLUE, 0);
            ammo.put(AmmoType.YELLOW, 0);
            ammo.put(AmmoType.RED, 0);
            getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                    getPaidPowerups()));
            setWaitStatus();
            resetSelections();
            setSecondaryButtons();
            setActions();
            setPowerups();
            return;
        }

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
            setWaitStatus();
            this.secondaryButtons = new ArrayList<>();
            resetSelections();
            setSecondaryButtons();
            setActions();
            setPowerups();
            return;
        }

        if (!getRequiredPayment().isEmpty()) {
            for (Map.Entry<AmmoType, Integer> ammo : getRequiredPayment().entrySet()) {
                if (ammo.getValue() != 0) {
                    setPowerups();
                    requirePayment();
                    return;
                }
            }
        }
    }

    public void handleContinue() {
        switch (getState()) {
            case PAYMENT:
                if(!getRequiredPayment().isEmpty()) {
                    getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                            getPaidPowerups()));
                    setWaitStatus();
                } else {
                    resetSelections();
                    setPowerups();
                    ammoRequest();
                }
                break;
            case EFFECT_SELECT_SQUARE:
                super.selectionEffectFinish();
                break;
            case EFFECT_TARGET_SELECTION:
                super.setPossibilityCharacters(this.targetsSelected);
                if (!getEffectPossibility().getSquares().isEmpty()) {
                    handleEffectMoveRequest();
                } else {
                    super.selectionEffectFinish();
                }
                break;
            case USE_EFFECT:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT, getCharacter(), null));
                setWaitStatus();
                break;
            case USE_POWERUP:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(), null));
                setWaitStatus();
                break;
            case MULTIPLE_POWERUPS_SELECTION:
                if(getPowerupsSelection().isEmpty()) {
                    getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(), null));
                } else {
                    getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, getCharacter(), getPaidPowerups()));
                }
                setWaitStatus();
                break;
            case RECHARGE_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.RELOAD, getCharacter(), null));
                setWaitStatus();
            case MULTIPLE_SQUARES_SELECTION:
                super.setPossibilityCharacters(this.targetsSelected);
                super.selectionEffectFinish();
                break;
            default:
                break;
        }
        resetSelections();
        setPowerups();
        setSquares();
        setWeapons();
        setTargets();
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
    }

    public void handleEffectInput(WeaponEffectOrderType effect) {
        if (getState() == USE_EFFECT) {
            getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT, getCharacter(), effect));
            setWeaponActivated(true);
            setWaitStatus();
        }
        resetSelections();
        setEffects();
        setActions();
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

    private void setSquares() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setSquares(getCoordinatesSelection());
        }
    }

    /**
     * Shows effect targets choice request message
     */
    @Override
    void handleEffectTargetRequest() {
        super.handleEffectTargetRequest();
        if (getEffectPossibility().getCharacters().size() == 1 &&
                getEffectPossibility().getCharacters().get(0) == getCharacter()) {
            handleEffectMoveRequest();
            return;
        }
        StringBuilder text = new StringBuilder();
        List<String> targetsAmount = getEffectPossibility().getTargetsAmount();
        if (targetsAmount.size() == 1) {
            int amount = Integer.parseInt(targetsAmount.get(0));
            this.minSelectable = amount;
            this.maxSelectable = amount;
            String toAppend = "Select " + amount + " target";
            text.append(toAppend);
            if (amount != 1) {
                text.append("s");
            }
        } else if (targetsAmount.get(1).equals("MAX")) {
            int min = Integer.parseInt(targetsAmount.get(0));
            this.minSelectable = min;
            this.maxSelectable = getEffectPossibility().getCharacters().size();
            String toAppend = "Select at least " + min + " target";
            text.append(toAppend);
        } else {
            int min = Integer.parseInt(targetsAmount.get(0));
            int max = Integer.parseInt(targetsAmount.get(1));
            this.minSelectable = min;
            this.maxSelectable = max;
            String toAppend = "Select from " + min + " to " + max + " targets";
            text.append(toAppend);
        }
        if (getEffectPossibility().getType() == EffectType.MARK) {
            text.append(" to mark");
        } else if (getEffectPossibility().getType() == EffectType.DAMAGE) {
            text.append(" to damage");
        } else if (getEffectPossibility().getType() == EffectType.MOVE) {
            text.append(" to move");
        }
        this.currentStatus = text.toString();
        this.currentAction = "Select one of the available targets";
        setBanner();
        setTargets();
        this.secondaryButtons = new ArrayList<>();
        if (this.targetsSelected.size() >= this.minSelectable) {
            this.secondaryButtons.add(CONTINUE_BUTTON);
        }
        setSecondaryButtons();
    }

    /**
     * Shows movement request message
     */
    @Override
    void handleEffectMoveRequest() {
        super.handleEffectMoveRequest();
        this.currentStatus = "Select a square to perform the movement";
        this.currentAction = "Select one of the available squares";
        setBanner();
        setSquares();
    }

    /**
     * Shows the select of the effect
     */
    @Override
    void handleEffectSelectRequest() {
        super.handleEffectSelectRequest();
        StringBuilder text = new StringBuilder();
        if (getState() == EFFECT_SELECT_SQUARE) {
            text.append("square");
            setSquares();

        } else if (getState() == EFFECT_SELECT_CARDINAL) {
            text.append("cardinal direction");
            setCardinalPoints();
        } else {
            text.append("room");
            setRooms();
        }
        this.currentStatus = "Select a " + text.toString();
        this.currentAction = "Select one of the available " + text.toString();
        setBanner();
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
            this.minSelectable = amount;
            this.maxSelectable = amount;
            toAppend = "Choose " + amount + " players each in different squares";
            text.append(toAppend);
        } else if (targetsAmount.get(1).equals("MAX")) {
            int min = Integer.parseInt(targetsAmount.get(0));
            this.minSelectable = min;
            this.maxSelectable = getEffectPossibility().getMultipleSquares().size();
            toAppend = "Choose at least " + min + " players each in different squares";
            text.append(toAppend);
        } else {
            int min = Integer.parseInt(targetsAmount.get(0));
            int max = Integer.parseInt(targetsAmount.get(1));
            this.minSelectable = min;
            this.maxSelectable = max;
            toAppend = "Choose from " + min + " to " + max + " players each in different squares";
            text.append(toAppend);
        }
        this.currentStatus = text.toString();
        this.currentAction = "Select from the available players";
        this.secondaryButtons = new ArrayList<>();
        if (this.targetsSelected.size() >= this.minSelectable) {
            this.secondaryButtons.add(CONTINUE_BUTTON);
        }
        setSecondaryButtons();
        setBanner();
        setTargets();
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
        this.currentStatus = "Do you want to combo?";
        this.currentAction = description;
        setBanner();
        this.secondaryButtons = Arrays.asList("y", "n");
        setSecondaryButtons();
    }

    /**
     * Shows effect required request message
     */
    @Override
    void handleEffectRequireRequest() {
        super.handleEffectRequireRequest();
        this.currentStatus = "Do you want to perform not mandatory effect?";
        this.currentAction = getEffectPossibility().getDescription();
        setBanner();
        this.secondaryButtons = Arrays.asList("y", "n");
        setSecondaryButtons();
    }

    /**
     * Handles client cardinal point choice
     * @param x x coordinate of the point chosen
     * @param y y coordinate of the point chosen
     */
    public void handleSquareInput(int x, int y) {
        switch (getState()) {
            case SELECT_MOVEMENT:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.MOVE, getCharacter(),
                        new Coordinates(x, y)));
                break;
            case SELECT_POWERUP_POSITION:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.POWERUP_POSITION, getCharacter(),
                        new Coordinates(x, y)));
                break;
            case SELECT_PICKUP:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP, getCharacter(),
                        new Coordinates(x, y)));
                break;
            case EFFECT_SELECT_SQUARE:
                super.setPossibilitySquares(new ArrayList<>(Arrays.asList(new Coordinates(x, y))));
                super.selectionEffectFinish();
                break;
            case EFFECT_SELECT_ROOM:
                super.setPossibilityRooms(new ArrayList<>(Arrays.asList(super.getBoard().getSquareByCoordinates(x, y).getColor())));
                super.selectionEffectFinish();
                break;
            default:
                break;
        }
        setWaitStatus();
        resetSelections();
        setSquares();
        setActions();
    }

    /**
     * Handles clien effect target choice
     * @param character target chosen
     */
    private void handleEffectTargetSelect(GameCharacter character) {
        this.targetsSelected.add(character);
        removeCharacterSelection(character);
        if(this.targetsSelected.size() < this.maxSelectable && !getCharactersSelection().isEmpty()) {
            handleEffectTargetRequest();
            return;
        }
        super.setPossibilityCharacters(this.targetsSelected);
        if (!getEffectPossibility().getSquares().isEmpty()) {
            setCharactersSelection(new ArrayList<>());
            setTargets();
            setActions();
            handleEffectMoveRequest();
        } else {
            resetSelections();
            setTargets();
            setActions();
            super.selectionEffectFinish();
        }
    }
    
    /**
     * Handles client multiple squares target input
     * @param character multiple squares chosen and target chosen
     */
    private void handleEffectMultipleSquareSelect(GameCharacter character) {
        this.targetsSelected.add(character);
        Coordinates toRemove = null;
        List<GameCharacter> removeCharacter = new ArrayList<>();
        for(Coordinates coordinates : getMultipleSquareSelection().keySet()) {
            if(getMultipleSquareSelection().get(coordinates).contains(character)) {
                removeCharacter.addAll(getMultipleSquareSelection().get(coordinates));
                toRemove = coordinates;
            }
        }
        for(GameCharacter c: removeCharacter) {
            removeCharacterSelection(c);
        }
        removeMultipleSquareSelection(toRemove);
        if(this.targetsSelected.size() < this.maxSelectable && !getCharactersSelection().isEmpty()) {
            handleMultipleSquareRequest();
            return;
        }
        super.setPossibilityCharacters(this.targetsSelected);
        super.selectionEffectFinish();
        resetSelections();
        setTargets();
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
        setPlayerBoard(getCharacter());
        super.handleWeaponSwitchRequest(weapons);
        this.currentStatus = "Which weapon do you want to drop?";
        this.currentAction = "Select one of your weapons";
        setBanner();
        setWeapons();
    }

    /**
     * Shows weapons to reload
     * @param weapons List of the available weapons to be reloaded
     */
    @Override
    void handleReloadRequest(List<Weapon> weapons) {
        super.handleReloadRequest(weapons);
        this.currentStatus = "Do you want to reload?";
        this.currentAction = "Select a weapon or continue";
        this.secondaryButtons = new ArrayList<>();
        this.secondaryButtons.add(CONTINUE_BUTTON);
        setBanner();
        setSecondaryButtons();
        setWeapons();
    }

    /**
     * Show weapon effect used
     * @param character that used the effect
     * @param effect used
     */
    void handleEffectSelected(GameCharacter character, WeaponEffectOrderType effect) {
        if (character == getCharacter()) {
            return;
        }
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
        addMessage(text.toString());
        showMessage();
    }

    /**
     * Shows persistence request
     */
    @Override
    void handlePersistenceRequest(GameCharacter character) {
        super.handlePersistenceRequest(character);
        resetSelections();
        setWeapons();
        setActions();
        setPowerups();
        setTargets();
        setSquares();
        if (character == getCharacter()) {
            this.currentStatus = "Do you want to save the game?";
            this.currentAction = "Select one of the options above";
            setBanner();
            this.secondaryButtons = Arrays.asList("y", "n");
            setSecondaryButtons();
        } else {
            this.currentStatus = "Too few players";
            this.currentAction = getBoardByCharacter(character).getNickname()
                    + " (" + character + ") is saving the game";
            setBanner();
        }
    }

    /**
     * Shows game saved message
     */
    @Override
    void handlePersistenceFinish() {
        this.currentStatus = "Game saved";
        this.currentAction = "This window will close";
        setBanner();
        super.handlePersistenceFinish();
    }

    /**
     * Shows player dead
     * @param player dead
     */
    @Override
    void handleDeath(GameCharacter player) {
        super.handleDeath(player);
        if (getCharacter() == player) {
            addMessage("You died");
        } else {
            addMessage(player + " died");
        }
        showMessage();
        updatePlayersPositions();
        setTargets();
    }

    /**
     * Shows kill shot track change
     * @param skulls number
     * @param players of which the kill shot track has changed
     */
    @Override
    void handleKillshotTrackChange(int skulls, List<GameCharacter> players) {
        super.handleKillshotTrackChange(skulls, players);
        String player1;
        if (players.contains(getCharacter())) {
            player1 = "You";
        } else {
            player1 = players.get(0).toString();
        }
        if (players.size() == 1) {
            addMessage(player1 + " got 1 mark on killshot track");
        } else if (players.size() == 2 && players.get(0) == players.get(1)) {
            addMessage(player1 + " got 2 marks on killshot track");
        } else {
            addMessage(player1 + " and " + players.get(1) + " got 1 mark on killshot track");
        }
        showMessage();
        updateKillshotTrack();
    }

    /**
     * Shows kill shot points change
     * @param player of which the kill shot points changed
     */
    @Override
    void handleKillshotPointsChange(GameCharacter player) {
        super.handleKillshotPointsChange(player);
        if (getCharacter() == player) {
            addMessage("Your killshot points have been reduced");
        } else {
            addMessage(player + "'s killshot points have been reduced");
        }
        updateKillshotPoints(player);
    }

    public void handleDecisionInput(String input) {
        input = input.toUpperCase();
        switch (getState()) {
            case PERSISTENCE_SELECTION:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.PERSISTENCE, getCharacter(), input));
                setWaitStatus();
                break;
            case EFFECT_COMBO_SELECTION:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.EFFECT_COMBO, getCharacter(), input));
                setWaitStatus();
                break;
            case EFFECT_REQUIRE_SELECTION:
                super.setPossibilityRequire(input.equals("Y"));
                break;
            default:
                return;
        }
        this.secondaryButtons = new ArrayList<>();
        setSecondaryButtons();
    }

    private void setWeapons() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setWeapons(getWeaponsSelection());
        }
    }

    public void handleWeaponInput(Weapon weapon) {
        switch (getState()) {
            case SELECT_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.PICKUP_WEAPON, getCharacter(), weapon));
                break;
            case SWITCH_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.SWITCH, getCharacter(), weapon));
                break;
            case USE_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.USE_WEAPON, getCharacter(), weapon));
                setCurrentWeapon(weapon);
                setWeaponActivated(false);
                break;
            case RECHARGE_WEAPON:
                getClient().send(new SingleSelectionMessage(SelectionMessageType.RELOAD, getCharacter(), weapon));
                break;
            default:
                break;
        }
        setWaitStatus();
        resetSelections();
        setWeapons();
        setActions();
    }

    /**
     * Shows the effect choice request
     * @param effects List of the available weapon effect macro
     */
    @Override
    void handleEffectRequest(List<WeaponEffectOrderType> effects) {
        super.handleEffectRequest(effects);
        this.targetsSelected = new ArrayList<>();
        this.currentStatus = "Which effect do you want to use?";
        this.currentAction = "Select one from the buttons below";
        setBanner();
        this.secondaryButtons = new ArrayList<>();
        setEffects();
        if (isWeaponActivated()) {
            this.secondaryButtons.add(CONTINUE_BUTTON);
        } else {
            addActionsSelection(ActionType.CANCEL);
        }
        setSecondaryButtons();
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
            text.setLength((text.length() - 2));

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
        if (getPowerupsSelection().isEmpty() && !getRequiredPayment().isEmpty()) {
            getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), getRequiredPayment(),
                    getPaidPowerups()));
            setWaitStatus();
            this.secondaryButtons = new ArrayList<>();
            setSecondaryButtons();
            resetSelections();
            setPowerups();
            return;
        } else if (getPowerupsSelection().isEmpty() && getRequiredPayment().isEmpty()) {
            ammoRequest();
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
            this.secondaryButtons.add(CONTINUE_BUTTON);
        }
        setBanner();
        setPowerups();
        setSecondaryButtons();
    }

    private void ammoRequest() {
        this.currentStatus = "You must pay one ammo of any color";
        this.currentAction = "Select ammo color from buttons below";
        this.secondaryButtons = new ArrayList<>();
        for(AmmoType ammo : getSelfPlayerBoard().getAvailableAmmos().keySet()) {
            if(getSelfPlayerBoard().getAvailableAmmos().get(ammo) > 0) {
                this.secondaryButtons.add(ammo.toString().toLowerCase());
            }
        }
        setBanner();
        setSecondaryButtons();
    }

    public void handleAmmoInput(AmmoType ammo) {
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        for(AmmoType ammoType : AmmoType.values()) {
            if(ammoType == ammo) {
                ammos.put(ammo, 1);
            } else {
                ammos.put(ammo, 0);
            }
        }
        getClient().send(new PaymentSentMessage(getCurrentPayment(), getCharacter(), ammos, new ArrayList<>()));
        setWaitStatus();
        this.secondaryButtons = new ArrayList<>();
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

    public void handleCardinalPointInput(CardinalPoint point) {
        if (getState() == EFFECT_SELECT_CARDINAL) {
            super.setPossibilityCardinal(new ArrayList<>(Arrays.asList(point)));
            super.selectionEffectFinish();
        }
        this.secondaryButtons = new ArrayList<>();
        resetSelections();
        setSecondaryButtons();
    }

    /**
     * Shows weapon choice request
     * @param weapons List of the available weapons to use
     */
    @Override
    void handleWeaponUseRequest(List<Weapon> weapons) {
        setPlayerBoard(getCharacter());
        super.handleWeaponUseRequest(weapons);
        this.currentStatus = "Which weapon do you want to use?";
        this.currentAction = "Select one of your weapons";
        addActionsSelection(ActionType.CANCEL);
        setWeapons();
        setBanner();
    }

    /**
     * Handles game finish setting the ranking scene
     * @param ranking map with game characters and total points raised
     */
    @Override
    void handleGameFinished(Map<GameCharacter, Integer> ranking) {
        this.currentStatus = "Game finished";
        this.currentAction = "Ranking will be shown soon";
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                setScene(SceneType.RANKING);
                ((RankingController) GUIView.this.controller).showMessage(ranking);
                GUIView.super.handleGameFinished(ranking);
            }
        }, 5*1000L);
    }

    /**+
     * Shows a message
     */
    private void showMessage() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).showMessage(this.messages);
        }
    }

    private void setBanner() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setBanner(this.currentStatus, this.currentAction);
        }
    }

    private void addMessage(String message) {
        if (this.messages.size() == 5) {
            this.messages.removeFirst();
        }
        this.messages.addLast(message);
    }

    private void setSecondaryButtons() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setSecondaryButtons(this.secondaryButtons);
        }
    }

    private void setArena() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setArena();
        }
    }

    private void setTargets() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setTargets(getCharactersSelection(), this.targetsSelected);
        }
    }

    public void setPlayerBoard(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).setPlayerBoard(character);
            if (character == getCharacter()) {
                setPowerups();
                setWeapons();
            }
        }
    }

    private void setRooms() {
        if (this.currentScene == SceneType.BOARD) {
            List<Coordinates> squares = new ArrayList<>();
            for (SquareView s : getBoard().getSquares()) {
                if (getRoomsSelection().contains(s.getColor())) {
                    squares.add(new Coordinates(s.getX(), s.getY()));
                }
            }
            ((BoardController) this.controller).setSquares(squares);
        }
    }

    private void setCardinalPoints() {
        if (this.currentScene == SceneType.BOARD) {
            this.secondaryButtons = new ArrayList<>();
            for (CardinalPoint p : getCardinalPointsSelection()) {
                this.secondaryButtons.add(p.toString().toLowerCase());
            }
            setSecondaryButtons();
        }
    }

    private void setEffects() {
        if (this.currentScene == SceneType.BOARD) {
            this.secondaryButtons = new ArrayList<>();
            for (WeaponEffectOrderType p : getEffectsSelection()) {
                this.secondaryButtons.add(p.getIdentifier().toLowerCase());
            }
            setSecondaryButtons();
        }
    }

    private void updateStores() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updateStores();
        }
    }

    private void updateTiles() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updateTiles();
        }
    }

    private void updatePlayersPositions() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updatePlayersPositions();
        }
    }

    private void updateBoardDamages(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateBoardDamages();
        }
    }

    private void updateBoardMarks(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateBoardMarks();
        }
    }

    private void updateAmmo(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateAmmo();
        }
    }

    private void updatePowerups(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updatePowerups();
        }
    }

    private void updateWeapons(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateWeapons();
        }
    }

    private void updateKillshotPoints(GameCharacter character) {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() == character) {
            ((BoardController) this.controller).updateKillshotPoints();
        }
    }

    private void updatePoints() {
        if (this.currentScene == SceneType.BOARD && ((BoardController) this.controller).getActiveBoard() ==
                getCharacter()) {
            ((BoardController) this.controller).updatePoints();
        }
    }

    private void updateKillshotTrack() {
        if (this.currentScene == SceneType.BOARD) {
            ((BoardController) this.controller).updateKillshotTrack();
        }
    }

    @Override
    void resetSelections() {
        super.resetSelections();
        this.targetsSelected = new ArrayList<>();
    }

    private void setWaitStatus() {
        this.currentStatus = "Wait...";
        this.currentAction = "";
        setBanner();
    }
}
