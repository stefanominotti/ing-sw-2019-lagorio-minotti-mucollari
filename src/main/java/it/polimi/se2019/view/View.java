package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.client.RMIProtocolClient;
import it.polimi.se2019.client.SocketClient;
import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.controller.EffectPossibilityPack;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.ammos.AmmosMessage;
import it.polimi.se2019.model.messages.board.*;
import it.polimi.se2019.model.messages.client.*;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.player.*;
import it.polimi.se2019.model.messages.powerups.PowerupMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessageType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.timer.TimerMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.turn.TurnContinuationMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.weapon.WeaponMessage;
import it.polimi.se2019.model.messages.weapon.WeaponSwitchMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.se2019.view.ClientState.*;

/**
 * Abstract class for handling view
 */
public abstract class View {

    private static final Logger LOGGER = Logger.getLogger(View.class.getName());

    private static final String PATH = System.getProperty("user.home");
    private static final String FILE_NAME = "/AdrenalinaClient.token";

    private GameCharacter character;
    private AbstractClient client;
    private BoardView board;
    private List<PlayerBoard> enemyBoards;
    private SelfPlayerBoard selfPlayerBoard;
    private ClientState state;

    private List<ActionType> actionsSelection;
    private List<Coordinates> coordinatesSelection;
    private List<Weapon> weaponsSelection;
    private List<GameCharacter> charactersSelection;
    private List<Powerup> powerupsSelection;
    private Map<AmmoType, Integer> ammosSelection;
    private List<WeaponEffectOrderType> effectsSelection;
    private Map<Coordinates, List<GameCharacter>> multipleSquareSelection;
    private List<RoomColor> roomsSelection;
    private List<CardinalPoint> cardinalPointsSelection;


    private Weapon currentWeapon;
    private boolean weaponActivated;
    private PaymentType currentPayment;
    private Map<AmmoType, Integer> requiredPayment;
    private Map<AmmoType, Integer> paidAmmos;
    private List<Powerup> paidPowerups;

    private EffectPossibilityPack effectPossibility;
    private EffectPossibilityPack possibilitySelections;

    private PowerupType activePowerup;

    /**
     * Class constructor, it builds a view
     */
    View() {
        this.enemyBoards = new ArrayList<>();
        this.paidAmmos = new EnumMap<>(AmmoType.class);
        for (AmmoType type : AmmoType.values()) {
            this.paidAmmos.put(type, 0);
        }
        this.paidPowerups = new ArrayList<>();
        this.charactersSelection = new ArrayList<>();
        resetSelections();
    }

    /**
     * Connects the view to the server
     * @param connectionType "0" for socket, "1" for RMI
     * @param ip of the server
     * @param port of the server
     */
    void connect(int connectionType, String ip, int port) {
        if (connectionType == 0) {
            SocketClient s = new SocketClient(this, ip, port);
            (new Thread(s)).start();
            this.client = s;
        } else {
            try {
                this.client = new RMIProtocolClient(this, ip);
            } catch (IllegalStateException e) {
                handleConnectionError();
            }
        }
    }

    /**
     * Gets the client associated to the view
     * @return the abstract associated
     */
    public AbstractClient getClient() {
        return this.client;
    }

    /**
     * Gets the client state
     * @return client state
     */
    ClientState getState() {
        return this.state;
    }

    /**
     * Get the client character
     * @return the character
     */
    GameCharacter getCharacter() { return this.character; }

    /**
     * Gets the enemy boards
     * @return List of the enemy player boards
     */
    List<PlayerBoard> getEnemyBoards() {
        return new ArrayList<>(this.enemyBoards);
    }

    /**
     * Gets the player board
     * @return player board
     */
    SelfPlayerBoard getSelfPlayerBoard() {
        return this.selfPlayerBoard;
    }

    /**
     * Gets the board view
     * @return the board view
     */
    BoardView getBoard() {
        return this.board;
    }

    List<RoomColor> getRoomsSelection() {
        return this.roomsSelection;
    }

    List<CardinalPoint> getCardinalPointsSelection() {
        return this.cardinalPointsSelection;
    }

    /**
     * Gets selected characters
     * @return List of the selected characters
     */
    List<GameCharacter> getCharactersSelection() {
        return new ArrayList<>(this.charactersSelection);
    }

    /**
     * Gets selected actions
     * @return List of the selected actions
     */
    List<ActionType> getActionsSelection() {
        return new ArrayList<>(this.actionsSelection);
    }

    void addActionsSelection(ActionType action) {
        this.actionsSelection.add(action);
    }

    /**
     * Gets selected actions
     * @return Map with ammo type and its quantity selected
     */
    Map<AmmoType, Integer> getAmmosSelection() {
        return new EnumMap<>(this.ammosSelection);
    }

    /**
     * Gets selected powerups
     * @return List of the selected powerups
     */
    List<Powerup> getPowerupsSelection() {
        return new ArrayList<>(this.powerupsSelection);
    }

    /**
     * Gets selected effects
     * @return List of the selected effects
     */
    List<WeaponEffectOrderType> getEffectsSelection() {
        return new ArrayList<>(this.effectsSelection);
    }

    /**
     * Gets selected coordinates
     * @return List of the selected coordinates
     */
    List<Coordinates> getCoordinatesSelection() {
        return this.coordinatesSelection;
    }

    /**
     * Gets selected weapon
     * @return List of the selected weapons
     */
    List<Weapon> getWeaponsSelection() {
        return new ArrayList<>(this.weaponsSelection);
    }

    /**
     * Gets a ammo of a required payment
     * @return Map with ammo type and its quantity to be paid
     */
    Map<AmmoType, Integer> getRequiredPayment() {
        return this.requiredPayment;
    }

    /**
     * Gets the active powerup
     * @return the active powerup
     */
    PowerupType getActivePowerup() {
        return this.activePowerup;
    }

    /**
     * Gets the current weapon in use
     * @return the weapon in use
     */
    Weapon getCurrentWeapon() {
        return this.currentWeapon;
    }

    /**
     * Sets a weapon as current
     * @param currentWeapon the weaopon you want to set on use
     */
    void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    /**
     * Knows if a weapon is activated
     * @return true if it is, else false
     */
    boolean isWeaponActivated() {
        return this.weaponActivated;
    }

    /**
     * Sets a weapon as activated
     * @param weaponActivated true if you want to set as activated, else false
     */
    void setWeaponActivated(boolean weaponActivated) {
        this.weaponActivated = weaponActivated;
    }

    /**
     * Gets the payment type
     * @return type of the payment
     */
    PaymentType getCurrentPayment() {
        return this.currentPayment;
    }

    /**
     * Gets paid ammo
     * @return Map with ammo type and its quantity paid
     */
    Map<AmmoType, Integer> getPaidAmmos() {
        return new EnumMap<>(this.paidAmmos);
    }

    /**
     * Gets the powerups paid
     * @return List of the powerups paid
     */
    List<Powerup> getPaidPowerups() {
        return new ArrayList<>(this.paidPowerups);
    }

    /**
     * Gets effect possibilities
     * @return an effect possibility pack
     */
    EffectPossibilityPack getEffectPossibility() {
        return this.effectPossibility;
    }

    /**
     * Sets client state
     * @param state which you want to set to the client
     */
    void setState(ClientState state) {
        this.state = state;
    }

    /**
     * Sets a powerup active
     * @param powerup you want to set active
     */
    void setActivePowerup(PowerupType powerup) {
        this.activePowerup = powerup;
    }

    /**
     * Sets the character selection
     * @param characters to be set
     */
    void setCharactersSelection(List<GameCharacter> characters) {
        this.charactersSelection = new ArrayList<>(characters);
    }

    /**
     * Removes a character from the characters selection list
     * @param character you want to remove
     */
    void removeCharacterSelection(GameCharacter character) {
        this.charactersSelection.remove(character);
    }

    void setMultipleSquareSelection(Map<Coordinates, List<GameCharacter>> selection) {
        this.multipleSquareSelection = selection;
    }

    Map<Coordinates, List<GameCharacter>> getMultipleSquareSelection() {
        return this.multipleSquareSelection;
    }

    void removeMultipleSquareSelection(Coordinates coordinates) {
        this.multipleSquareSelection.remove(coordinates);
    }

    /**
     * Adds paid ammo to paid ammo list
     * @param type of the ammo paid
     * @param amount of the ammo paid
     */
    void putPaidAmmos(AmmoType type, int amount) {
        this.paidAmmos.put(type, amount);
    }


    /**
     * Add a paid powerup to the powerup paid list
     * @param powerup paid
     */
    void addPaidPowerup(Powerup powerup) {
        this.paidPowerups.add(powerup);
    }

    /**
     * Puts ammo into ammo selection list
     * @param type of the ammo
     * @param amount of the ammo
     */
    void putAmmosSelection(AmmoType type, int amount) {
        this.ammosSelection.put(type, amount);
    }

    /**
     * Removes a powerup from the powerup selection list
     * @param powerup you want to remove
     */
    void removePowerupSelection(Powerup powerup) {
        this.powerupsSelection.remove(powerup);
    }


    /**
     * Adds ammo into a list of the ammo to be paid request
     * @param type of ammo
     * @param amount of ammo
     */
    void putRequiredPayment(AmmoType type, int amount) {
        this.requiredPayment.put(type, amount);
    }

    /**
     * Resets all possible selections
     */
    void resetSelections() {
        this.charactersSelection = new ArrayList<>();
        this.actionsSelection = new ArrayList<>();
        this.ammosSelection = new EnumMap<>(AmmoType.class);
        this.coordinatesSelection = new ArrayList<>();
        this.weaponsSelection = new ArrayList<>();
        this.powerupsSelection = new ArrayList<>();
        this.requiredPayment = new EnumMap<>(AmmoType.class);
        this.paidPowerups = new ArrayList<>();
        for (AmmoType type : AmmoType.values()) {
            this.paidAmmos.put(type, 0);
        }
        this.cardinalPointsSelection = new ArrayList<>();
        this.roomsSelection = new ArrayList<>();
    }

    /**
     * Uses to sort message type based and forward it
     * @param message to be sort and forward
     */
    public void manageUpdate(Message message) {
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
            case SELECTION_LIST_MESSAGE:
                update((SelectionListMessage) message);
                break;
            case SINGLE_SELECTION_MESSAGE:
                update((SingleSelectionMessage) message);
                break;
        }
    }

    /**
     * Handles connection error. Ends the app
     */
    public void handleConnectionError() {
        System.exit(0);
    }

    /**
     * Gets a character player board
     * @param character of which you want to get the player board
     * @return player board of that character
     */
    PlayerBoard getBoardByCharacter(GameCharacter character) {
        for (PlayerBoard playerBoard : this.enemyBoards) {
            if (playerBoard.getCharacter() == character) {
                return playerBoard;
            }
        }
        return null;
    }

    /**
     * Uses to forward a nickname message to client
     * @param message to be forwarded
     */
    private void update(NicknameMessage message) {
        switch (message.getType()) {
            case REQUIRE:
                handleNicknameRequest();
                break;
            case DUPLICATED:
                handleNicknameDuplicated();
                break;
            default:
                break;
        }
    }

    /**
     * Sets the client state on typing nickname
     */
    void handleNicknameRequest() {
        this.state = TYPING_NICKNAME;
    }

    /**
     * Sets again the client state on typing nickname in case of nickname duplicated
     */
    void handleNicknameDuplicated() {
        this.state = TYPING_NICKNAME;
    }

    /**
     * Uses to forward a timer message to client
     * @param message to be forwarded
     */
    private void update(TimerMessage message) {
        switch (message.getTimerType()) {
            case SETUP:
                handleGameSetupTimer(message.getType(), message.getTime());
                break;
            case POWERUP:
                handlePowerupTimer(message.getType());
                break;
            default:
                break;
        }
    }

    /**
     * Handles game setup timer
     * @param action type of the timer message
     * @param duration setup timer
     */
    abstract void handleGameSetupTimer(TimerMessageType action, long duration);

    /**
     * Handles powerup timer
     * @param action type of the timer message
     */
    void handlePowerupTimer(TimerMessageType action) {
        if (this.state == MULTIPLE_POWERUPS_SELECTION) {
            this.state = OTHER_PLAYER_TURN;
        }
    }
    /**
     * Uses to forward a player message to client
     * @param message to be forwarded
     */
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
            case ATTACK:
                handleAttack(message.getCharacter(), ((AttackMessage) message).getAttacker(),
                        ((AttackMessage) message).getAmount(), ((AttackMessage) message).getAttackType());
                break;
            case MARKS_TO_DAMAGES:
                handleMarksToDamages(message.getCharacter(), ((MarksToDamagesMessage) message).getAttacker());
                break;
            case FIRST_BLOOD:
                handleFirstBlood(message.getCharacter());
                break;
            case KILLSHOT_POINTS:
                handleKillshotPointsChange(message.getCharacter());
                break;
            case DEATH:
                handleDeath(message.getCharacter());
                break;
            case SCORE:
                handleScoreChange(message.getCharacter(), ((ScoreMessage) message).getScore());
                break;
            case BOARD_FLIP:
                handleBoardFlip(message.getCharacter());
                break;
            case FRENZY:
                handleFinalFrenzy(((FinalFrenzyMessage) message).isBeforeFirstPlayer());
                break;
        }
    }

    /**
     * Handles Final Frenzy mode start
     * @param beforeFirst if the player is playing before the first player during this turn, else false
     */
    void handleFinalFrenzy(boolean beforeFirst) {
        this.board.startFrenzy();
        this.board.setBeforeFirstPlayer(beforeFirst);
    }

    /**
     * Handles board flipping to Final Frenzy mode
     * @param player to check if flipping
     */
    void handleBoardFlip(GameCharacter player) {
        PlayerBoard playerBoard;
        if (player == this.character) {
            playerBoard = this.selfPlayerBoard;
        } else {
            playerBoard = getBoardByCharacter(player);
        }
        if (playerBoard != null) {
            playerBoard.flipBoard();
        }
    }

    /**
     * Shows score changed message
     * @param player of which the score has changed
     * @param score amount of change
     */
    void handleScoreChange(GameCharacter player, int score) {
        if (player == this.character) {
            this.selfPlayerBoard.raiseScore(score);
        }
    }

    /**
     * Handles a player death
     * @param player character of which you want to handle the death
     */
    void handleDeath(GameCharacter player) {
        PlayerBoard playerBoard;
        if (player == this.character) {
            playerBoard = this.selfPlayerBoard;
        } else {
            playerBoard = getBoardByCharacter(player);
        }
        if (playerBoard != null) {
            playerBoard.resetDamages();
        }
    }

    /**
     * Handles kill shot points change message
     * @param player of which the kill shot points has changed
     */
    void handleKillshotPointsChange(GameCharacter player) {
        PlayerBoard board;
        if (player == this.character) {
            board = this.selfPlayerBoard;
        } else {
            board = getBoardByCharacter(player);
        }
        if (board != null) {
            board.reduceKillshotPoints();
        }
    }

    /**
     * Handles first blood shot
     * @param player who dealt the first blood shot
     */
    void handleFirstBlood(GameCharacter player) {
        if (this.character == player) {
            this.selfPlayerBoard.raiseScore(1);
        }
    }


    /**
     * Handles marks to damages conversion
     * @param player of which the marks has to be converted
     * @param attacker player of which marks need to be converted
     */
    void handleMarksToDamages(GameCharacter player, GameCharacter attacker) {
        PlayerBoard playerBoard;
        if (player == this.character) {
            playerBoard = this.selfPlayerBoard;
        } else {
            playerBoard = getBoardByCharacter(player);
        }

        if (playerBoard == null) {
            return;
        }

        for(GameCharacter c : playerBoard.getRevengeMarks()) {
            if(playerBoard.getDamages().size() < Player.MAX_DAMAGES && c == attacker) {
                playerBoard.addDamages(attacker, 1);
            }
        }
        playerBoard.resetMarks(attacker);
    }

    /**
     * Handles an attack
     * @param character who received the attack
     * @param attacker who performed the attack
     * @param amount of damage or marks give
     * @param attackType type of the attack
     */
    void handleAttack(GameCharacter character, GameCharacter attacker, int amount, EffectType attackType) {
        PlayerBoard playerBoard;
        if (character == this.character) {
            playerBoard = this.selfPlayerBoard;
        } else {
            playerBoard = getBoardByCharacter(character);
        }

        if (playerBoard == null) {
            return;
        }

        if (attackType == EffectType.DAMAGE) {
            playerBoard.addDamages(attacker, amount);
        } else {
            playerBoard.addMarks(attacker, amount);
        }
    }

    /**
     * Handles player created, changing state to waiting start
     * @param character chosen
     * @param nickname chosen
     * @param otherPlayers Map with game characters and their nicknames
     */
    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        this.state = WAITING_START;
        this.character = character;
        this.selfPlayerBoard = new SelfPlayerBoard(character, nickname);
        for (Map.Entry<GameCharacter, String> player : otherPlayers.entrySet()) {
            boolean present = false;
            for (PlayerBoard p : this.enemyBoards) {
                if (p.getCharacter() == player.getKey()) {
                    present = true;
                    break;
                }
            }
            if (present) {
                continue;
            }
            this.enemyBoards.add(new PlayerBoard(player.getKey(), player.getValue()));
        }
    }

    /**
     * Handles ready player, adding player board
     * @param character of the ready player
     * @param nickname of the ready player
     */
    void handleReadyPlayer(GameCharacter character, String nickname) {
        if (this.state == WAITING_START) {
            this.enemyBoards.add(new PlayerBoard(character, nickname));
        }
    }

    /**
     * Handles a player spawn
     * @param character who has spawn
     * @param coordinates where he has spawn
     */
    void handleSpawnedPlayer(GameCharacter character, Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        this.board.setPlayerPosition(character, this.board.getSquareByCoordinates(x, y));
    }

    /**
     * Handles skulls set complete, changing state to setting arena
     */
    void handleSkullsSet() {
        this.state = SETTING_ARENA;
    }

    /**
     * Handles master changed, changing state to setting skulls
     * @param character active character
     */
    void handleMasterChanged(GameCharacter character) {
        if (character == this.character) {
            this.state = SETTING_SKULLS;
        }
    }

    /**
     * Handles start setup, changing state
     * @param character who is starting setup
     */
    void handleStartSetup(GameCharacter character) {
        if (character == this.character) {
            this.state = SETTING_SKULLS;
        } else {
            this.state = WAITING_SETUP;
        }
    }

    /**
     * Handles movement action
     * @param character who has to move
     * @param coordinates chosen where the player wants to move
     */
    void handleMovement(GameCharacter character, Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        this.board.setPlayerPosition(character, this.board.getSquareByCoordinates(x, y));
    }

    /**
     * Uses to forward a client message to client
     * @param message to be forwarded
     */
    private void update(ClientMessage message) {
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
                        ((LoadViewMessage) message).getPlayerBoards(), ((LoadViewMessage) message).getReadyWeapons(),
                        ((LoadViewMessage) message).getPowerups(), ((LoadViewMessage) message).getScore(),
                        ((LoadViewMessage) message).getOtherPlayers(), ((LoadViewMessage) message).isFrenzy(),
                        ((LoadViewMessage) message).isBeforeFirstPlayer());
                break;
            default:
                break;
        }
    }

    /**
     * Handles invalid token, after the app is closed
     */
    void handleInvalidToken() {
        System.exit(0);
    }

    /**
     * Handles full lobby, after the app is closed
     */
    void handleFullLobby() {
        System.exit(0);
    }

    /**
     * Handles a reconnection request, changing state to reconnecting
     */
    void handleReconnectionRequest() {
        this.state = RECONNECTING;
        this.client.send(new ReconnectionMessage(getToken()));
    }

    /**
     * Handles characters selection, changing state to choosing characters
     * @param availables List of the available game characters
     */
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        this.state = CHOOSING_CHARACTER;
    }

    /**
     * Hanfles client disconnection
     * @param character who has disconnected
     */
    void handleClientDisconnected(GameCharacter character) {
        if (this.state == TYPING_NICKNAME || this.state == WAITING_START || this.state == WAITING_SETUP ||
                this.state == SETTING_SKULLS || this.state == SETTING_ARENA) {
            for (PlayerBoard playerBoard : this.enemyBoards) {
                if (playerBoard.getCharacter() == character) {
                    this.enemyBoards.remove(playerBoard);
                    break;
                }
            }
        }
    }

    /**
     * Handles game already start, in case an another player try to join when the game is already started
     * after the app is closed
     */
    void handleGameAlreadyStarted() {
        System.exit(0);
    }

    void loadView(GameCharacter character, int skulls, List<SquareView> squares,
                  Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                  List<Weapon> weapons, List<Powerup> powerups, int score, Map<GameCharacter, String> others,
                  boolean isFrenzy, boolean isBeforeFirstPlayer) {
        this.character = character;
        this.state = WAITING_SETUP;
        this.enemyBoards = new ArrayList<>();
        this.board = new BoardView(skulls, squares, killshotTrack, isFrenzy, isBeforeFirstPlayer);
        for (PlayerBoard playerBoard : playerBoards) {
            if (playerBoard.getCharacter() == character) {
                this.selfPlayerBoard = new SelfPlayerBoard(playerBoard, weapons, powerups, score);
            } else {
                this.enemyBoards.add(playerBoard);
            }
        }
    }

    /**
     * Uses to forward a powerup message to client
     * @param message to be forwarded
     */
    private void update(PowerupMessage message) {
        if (message.getType() == PowerupMessageType.ADD) {
            handlePowerupAdded(message.getCharacter(), message.getPowerup());
        } else {
            handlePowerupRemoved(message.getCharacter(), message.getPowerup(), message.getType());
        }
    }

    /**
     * Handles a powerup added
     * @param character who add a powerup
     * @param powerup added
     */
    void handlePowerupAdded(GameCharacter character, Powerup powerup) {
        if (character != this.character) {
            PlayerBoard playerBoard = getBoardByCharacter(character);
            if (playerBoard != null) {
                playerBoard.addPowerup();
            }
        } else {
            this.selfPlayerBoard.addPowerup(powerup);
        }
    }

    /**
     * Handles powerup removed
     * @param character who removed a powerup
     * @param powerup removed
     * @param type of the powerup message
     */
    void handlePowerupRemoved(GameCharacter character, Powerup powerup, PowerupMessageType type) {
        if (this.character == character) {
            this.selfPlayerBoard.removePowerup(powerup.getType(), powerup.getColor());
        } else {
            PlayerBoard enemyBoard = getBoardByCharacter(character);
            if (enemyBoard != null) {
                enemyBoard.removePowerup();
            }
        }
    }

    /**
     * Uses to forward an ammo message to client
     * @param message to be forwarded
     */
    private void update(AmmosMessage message) {
        switch (message.getType()) {
            case ADD:
                handleAddAmmos(message.getCharacter(), message.getAmmos());
                break;
            case REMOVE:
                handleRemoveAmmos(message.getCharacter(), message.getAmmos());
                break;
            default:
                break;
        }
    }

    /**
     * Handles add ammo
     * @param character who has added ammo
     * @param ammos Map with ammo type and its quantity added
     */
    void handleAddAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        this.board.getPlayerPosition(character).removeAmmoTile();
        if (this.character == character) {
            this.selfPlayerBoard.addAmmos(ammos);
        } else {
            PlayerBoard enemyBoard = getBoardByCharacter(character);
            if (enemyBoard != null) {
                enemyBoard.addAmmos(ammos);
            }
        }
    }

    /**
     * Handles remove ammo
     * @param character who has removed ammo
     * @param ammos Map with ammo type and its quantity removed
     */
    void handleRemoveAmmos(GameCharacter character, Map<AmmoType, Integer> ammos) {
        if (this.character == character) {
            this.selfPlayerBoard.useAmmos(ammos);
        } else {
            PlayerBoard enemyBoard = getBoardByCharacter(character);
            if (enemyBoard != null) {
                enemyBoard.useAmmos(ammos);
            }
        }
    }

    /**
     * Uses to forward a weapon message to client
     * @param message to be forwarded
     */
    private void update(WeaponMessage message) {
        switch (message.getType()) {
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
            case UNLOAD:
                handleWeaponUnload(message.getCharacter(), message.getWeapon());
                break;
        }
    }

    /**
     * Handles weapon pickup
     * @param character who has picked up the weapon
     * @param weapon picked up
     */

    void handleWeaponPickup(GameCharacter character, Weapon weapon) {
        this.board.getPlayerPosition(character).removeStoreWeapon(weapon);
        if (this.character == character) {
            this.selfPlayerBoard.addWeapon(weapon);
        } else {
            PlayerBoard enemyBoard = getBoardByCharacter(character);
            if (enemyBoard != null) {
                enemyBoard.addWeapon();
            }
        }
    }

    /**
     * Handles weapon switched
     * @param character who has switched the weapon
     * @param oldWeapon to be switched
     * @param newWeapon switched
     */

    void handleWeaponSwitch(GameCharacter character, Weapon oldWeapon, Weapon newWeapon) {
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
        this.board.getPlayerPosition(character).removeStoreWeapon(newWeapon);
        this.board.getPlayerPosition(character).addStoreWeapon(oldWeapon);
    }

    /**
     * Handles weapon reload
     * @param character who has reload weapon
     * @param weapon reloaded
     */
    void handleWeaponReload(GameCharacter character, Weapon weapon) {
        if (character == this.character) {
            this.selfPlayerBoard.reloadWeapon(weapon);
        } else {
            PlayerBoard playerBoard = getBoardByCharacter(character);
            if (playerBoard != null) {
                playerBoard.reloadWeapon(weapon);
            }
        }
    }


    /**
     * Handles weapon unload
     * @param character who unloaded the weapon
     * @param weapon unloaded
     */
    void handleWeaponUnload(GameCharacter character, Weapon weapon) {
        if (character == this.character) {
            this.selfPlayerBoard.unloadWeapon(weapon);
        } else {
            PlayerBoard playerBoard = getBoardByCharacter(character);
            if (playerBoard != null) {
                playerBoard.unloadWeapon(weapon);
            }
        }
    }

    /**
     * Uses to forward a payment message to client
     * @param message to be forwarded
     */
    private void update(PaymentMessage message) {
        handlePayment(message.getPaymentType(), message.getAmmos());
    }

    /**
     * Handles the player payment
     * @param type of payment to be done
     * @param ammoRequested ammo requested
     */
    private void handlePayment(PaymentType type, Map<AmmoType, Integer> ammoRequested) {
        this.state = PAYMENT;
        this.requiredPayment = ammoRequested;
        this.currentPayment = type;
        this.ammosSelection = new EnumMap<>(this.selfPlayerBoard.getAvailableAmmos());
        this.powerupsSelection = new ArrayList<>(this.selfPlayerBoard.getPowerups());
        requirePayment();
    }

    /**
     * Uses to forward a turn message to client
     * @param message to be forwarded
     */
    private void update(TurnMessage message) {
        switch (message.getType()) {
            case START:
                handleStartTurn(message, message.getCharacter());
                break;
            case END:
                handleEndTurn(message.getCharacter());
                break;
            case CONTINUATION:
                handleTurnContinuation(((TurnContinuationMessage) message).getActivePlayer());
                break;
        }
    }

    /**
     * Handles start turm
     * @param message type of turn
     * @param character of the turn
     */
    void handleStartTurn(TurnMessage message, GameCharacter character) {
        if (character != this.character) {
            this.state = OTHER_PLAYER_TURN;
        } else {
            this.state = YOUR_TURN;
            this.client.send(message);
        }
    }

    /**
     * Handles end turn o a player
     * @param character who has to end his turn
     */
    abstract void handleEndTurn(GameCharacter character);

    void handleTurnContinuation(GameCharacter player) {
        this.state = OTHER_PLAYER_TURN;
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
            case KILLSHOT_TRACK:
                handleKillshotTrackChange(((KillshotTrackMessage) message).getSkulls(),
                        ((KillshotTrackMessage) message).getPlayers());
                break;
            case GAME_FINISHED:
                handleGameFinished(((EndGameMessage) message).getRanking());
                break;
            case PERSISTENCE:
                handlePersistenceFinish();
            default:
                break;
        }
    }

    /**
     * Closes the app after persistence choice
     */
    void handlePersistenceFinish(){
        System.exit(0);
    }

    /**
     * Closes the app after the game has finished and the ranking has shown
     */
    void handleGameFinished(Map<GameCharacter, Integer> ranking) {
        try {
            removeToken();
        } catch (IOException e) {
            // Ignore
        }
        System.exit(0);
    }

    /**
     * Handles change on killshot track
     * @param skulls current number
     * @param players of which the kill shot track has to be changed
     */
    void handleKillshotTrackChange(int skulls, List<GameCharacter> players) {
        this.board.setSkulls(skulls - 1);
        this.board.addKillshotPoints(players, skulls);
    }

    /**
     * Handles setup interrupted setting the game state on waiting start
     */
    void handleSetupInterrupted() {
        this.state = WAITING_START;
    }

    /**
     * Handles
     * @param colors Map with room colors and their coordinates
     * @param spawns Map with coordinates and true if they are a spawn point, else false
     * @param nearbyAccessibility Map with coordinates  and map with cardinal points and their accessibility
     * @param skulls set for the game
     * @param arena chosen for the game
     */
    void handleGameSet(Map<Coordinates, RoomColor> colors, Map<Coordinates, Boolean> spawns,
                       Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility, int skulls, int arena) {
        List<SquareView> squares = new ArrayList<>();
        for (Map.Entry<Coordinates, RoomColor> square : colors.entrySet()) {
            RoomColor color = square.getValue();
            Boolean spawn = spawns.get(square.getKey());
            Map<CardinalPoint, Boolean> accessibilityMap = nearbyAccessibility.get(square.getKey());
            squares.add(new SquareView(square.getKey().getX(), square.getKey().getY(), color, spawn, accessibilityMap));
        }
        this.board = new BoardView(skulls, squares, arena);
    }

    /**
     * Handles store refilled
     * @param weapons Map with coordinates of the store and weapons to placed in
     */
    void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        for (Map.Entry<Coordinates, Weapon> weapon : weapons.entrySet()) {
            int x = weapon.getKey().getX();
            int y = weapon.getKey().getY();
            this.board.getSquareByCoordinates(x, y).addStoreWeapon(weapon.getValue());
        }
    }

    /**
     * Handles tiles refilled
     * @param tiles Map with ammo and its quantity refilled
     */
    void handleTilesRefilled(Map<Coordinates, AmmoTile> tiles) {
        for (Map.Entry<Coordinates, AmmoTile> tile : tiles.entrySet()) {
            int x = tile.getKey().getX();
            int y = tile.getKey().getY();
            this.board.getSquareByCoordinates(x, y).setAvailableAmmoTile(tile.getValue());
        }
    }

    /**
     * Uses to sort request messages
     * @param message to be sort
     */
    private void update(SelectionListMessage message) {
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
            case USE_WEAPON:
                handleWeaponUseRequest((List<Weapon>) message.getList());
                break;
            case EFFECT:
                handleEffectRequest((List<WeaponEffectOrderType>) message.getList());
                break;
            default:
                break;
        }
    }

    /**
     * Handles weapon switch request, setting game state on switch weapon
     * @param weapons
     */
    void handleWeaponSwitchRequest(List<Weapon> weapons) {
        this.weaponsSelection = weapons;
        this.state = SWITCH_WEAPON;
    }

    /**
     * Handles pickup request, setting game state on select pickup
     * @param coordinates of the available squares where you can pickup
     */
    void handlePickupActionRequest(List<Coordinates> coordinates) {
        this.coordinatesSelection = coordinates;
        this.state = SELECT_PICKUP;
    }

    /**
     * Handles movement request, setting game state on select movement
     * @param coordinates of the available squares where you can move
     */
    void handleMovementActionRequest(List<Coordinates> coordinates) {
        this.coordinatesSelection = coordinates;
        this.state = SELECT_MOVEMENT;
    }

    /**
     * Handles targets request for powerup, setting game state on select powerup targer
     * @param targets available for that powerup
     */
    void handlePowerupTargetRequest(List<GameCharacter> targets) {
        this.charactersSelection = targets;
        this.state = SELECT_POWERUP_TARGET;
    }

    /**
     * Handles targets request for position, setting game state on select powerup position
     * @param coordinates of the squares available for that powerup
     */
    void handlePowerupPositionRequest(List<Coordinates> coordinates) {
        this.coordinatesSelection = coordinates;
        this.state = SELECT_POWERUP_POSITION;
    }

    /**
     * Handles reload request, setting game state on recharge weapon
     * @param weapons that can be reloaded
     */
    void handleReloadRequest(List<Weapon> weapons) {
        this.weaponsSelection = new ArrayList<>(weapons);
        this.state = RECHARGE_WEAPON;
    }

    /**
     * Handles discard powerup to spawn request, setting game state on discard spawn
     * @param powerups that can be discarded to spawn
     */
    void handleDiscardPowerupRequest(List<Powerup> powerups) {
        this.powerupsSelection = powerups;
        this.state = DISCARD_SPAWN;
    }

    /**
     * Handles discard powerup to spawn request, setting game state on discard spawn
     * @param powerups
     */
    void handleUsePowerupRequest(List<Powerup> powerups) {
        this.powerupsSelection = powerups;
        if (powerups.get(0).getType() == PowerupType.TAGBACK_GRENADE) {
            this.state = MULTIPLE_POWERUPS_SELECTION;
        } else {
            this.state = USE_POWERUP;
        }
    }

    /**
     * Handles weapon pickup request, setting game state on select weapon
     * @param weapons List of the available weapons to pickup
     */
    void handleWeaponPickupRequest(List<Weapon> weapons) {
        this.weaponsSelection = new ArrayList<>(weapons);
        this.state = SELECT_WEAPON;
    }

    /**
     * Handles action selection request, setting game state on select action
     * @param actions List of the available actions
     */
    void handleActionSelectionRequest(List<ActionType> actions) {
        this.actionsSelection = new ArrayList<>(actions);
        this.actionsSelection.add(ActionType.ENDTURN);
        this.state = SELECT_ACTION;
    }

    /**
     * Handles weapons use request, setting game state on use weapon
     * @param weapons List of the available weapons to use
     */
    void handleWeaponUseRequest(List<Weapon> weapons) {
        this.weaponsSelection = new ArrayList<>(weapons);
        this.state = USE_WEAPON;
    }

    /**
     * Handles effect use request, settin game state on use weapon
     * @param effects List of the available effect to use
     */
    void handleEffectRequest(List<WeaponEffectOrderType> effects) {
        this.effectsSelection = new ArrayList<>(effects);
        this.state = USE_EFFECT;
    }

    /**
     * Forward client choices for combo request, effects request, persistence request
     * @param message client choice
     */
    private void update(SingleSelectionMessage message) {
        switch (message.getType()) {
            case EFFECT_COMBO:
                handleEffectComboRequest((WeaponEffectOrderType) message.getSelection());
                break;
            case EFFECT_POSSIBILITY:
                this.effectPossibility = (EffectPossibilityPack) message.getSelection();
                this.possibilitySelections = new EffectPossibilityPack(true, this.effectPossibility.getType());
                if(this.effectPossibility.isRequire()) {
                    handleEffectSelections();
                } else {
                    handleEffectRequireRequest();
                }
                break;
            case PERSISTENCE:
                handlePersistenceRequest(message.getCharacter());
                break;
            default:
                break;
        }
    }

    /**
     * Sets game state on persitence selection
     * @param character which has to answer to persitence request
     */
    void handlePersistenceRequest(GameCharacter character) {
        this.state = PERSISTENCE_SELECTION;
    }

    /**
     * Sets game state on effect required selection
     */
    void handleEffectRequireRequest() {
        this.state = EFFECT_REQUIRE_SELECTION;
    }

    /**
     * Handles effect selection based on effect type
     */
    private void handleEffectSelections() {
        this.roomsSelection = this.effectPossibility.getRooms();
        this.cardinalPointsSelection = this.effectPossibility.getCardinalPoints();
        this.charactersSelection = this.effectPossibility.getCharacters();
        this.coordinatesSelection = this.effectPossibility.getSquares();
        this.multipleSquareSelection = this.effectPossibility.getMultipleSquares();
        if (this.effectPossibility.getType() == EffectType.SELECT) {
            handleEffectSelectRequest();
        } else if (!this.effectPossibility.getMultipleSquares().isEmpty()) {
            handleMultipleSquareRequest();
        } else {
            handleEffectTargetRequest();
        }
    }

    /**
     * Handles effect combo request, setting game state on effect combo selection
     * @param effect of the combo
     */
    void handleEffectComboRequest(WeaponEffectOrderType effect) {
        this.state = EFFECT_COMBO_SELECTION;
    }

    /**
     * Handles the effect which has the select of objects inside, setting game state based on the select
     */
    void handleEffectSelectRequest() {
        if(!this.effectPossibility.getSquares().isEmpty()) {
            this.state = EFFECT_SELECT_SQUARE;
        } else if(!this.effectPossibility.getRooms().isEmpty()) {
            this.state = EFFECT_SELECT_ROOM;
        } else if(!this.effectPossibility.getCardinalPoints().isEmpty()) {
            this.state = EFFECT_SELECT_CARDINAL;
        }
    }

    /**
     * Handles move selection request, setting state to effect move selection
     */
    void handleEffectMoveRequest() {
        this.state = EFFECT_SELECT_SQUARE;
    }

    /**
     * Handles target selection request, setting state to effect target selection
     */
    void handleEffectTargetRequest() {
        this.state = EFFECT_TARGET_SELECTION;
    }

    /**
     * Handles effect multiple squares request, setting state to multiple squares selection
     */
    void handleMultipleSquareRequest() {
        this.state = MULTIPLE_SQUARES_SELECTION;
    }

    /**
     * Requires a payment
     */
    abstract void requirePayment();

    /**
     * Sets the list of the available characters
     * @param characters list of the available ones
     */
    void setPossibilityCharacters(List<GameCharacter> characters) {
        this.possibilitySelections.setCharacters(characters);
    }

    /**
     * Sets the list of the available squares
     * @param squares List of the coordinates of the available squares
     */
    void setPossibilitySquares(List<Coordinates> squares) {
        this.possibilitySelections.setSquares(squares);
    }

    /**
     * Sets the list of the available rooms
     * @param rooms List of the available rooms color
     */
    void setPossibilityRooms(List<RoomColor> rooms) {
        this.possibilitySelections.setRooms(rooms);
    }

    /**
     * List of the available cardinal points
     * @param cardinal List of the available cardinal points
     */
    void setPossibilityCardinal(List<CardinalPoint> cardinal) {
        this.possibilitySelections.setCardinalPoints(cardinal);
    }

    /**
     * Sets and effect selection as required
     * @param isRequired true if it is, else false
     */
    void setPossibilityRequire(boolean isRequired) {
        this.possibilitySelections.setRequire(isRequired);
        if(!isRequired) {
            selectionEffectFinish();
            return;
        }
        handleEffectSelections();
    }

    /**
     * Sends the effect selected
     */
    void selectionEffectFinish() {
        this.client.send(new SingleSelectionMessage(SelectionMessageType.EFFECT_POSSIBILITY, this.character,
                this.possibilitySelections));
    }

    /**
     * Generates the client token
     * @return sha-256 digest of the token
     */
    String generateToken() {
        String message = UUID.randomUUID().toString();
        StringBuffer hexString = new StringBuffer();
        MessageDigest md;
        try(FileWriter writer = new FileWriter(PATH + FILE_NAME)) {
            md = MessageDigest.getInstance("SHA-256");
            md.update(message.getBytes());
            byte[] digest = md.digest();
            for (int i = 0; i < digest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            writer.write(message);
            writer.flush();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error SHA-256 algorithm");
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing data");
        }
        return hexString.toString();
    }

    /**
     * Gets the token of the client
     * @return the token of the client
     */
    private String getToken() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH + FILE_NAME))) {
            String message = reader.readLine();
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            //Passing data to the created MessageDigest Object
            md.update(message.getBytes());
            //Compute the message digest
            byte[] digest = md.digest();
            //Converting the byte array in to HexString format
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            handleConnectionError();
        }
        return null;
    }

    /**
     * Removes the token of the client
     * @throws IOException if the file could not be removed
     */
    void removeToken() throws IOException {
        Files.delete(Paths.get(PATH + FILE_NAME));
    }

}