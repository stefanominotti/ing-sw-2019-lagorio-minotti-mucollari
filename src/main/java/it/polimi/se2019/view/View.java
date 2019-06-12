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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    private List<ActionType> actionsSelection;
    private List<Coordinates> coordinatesSelection;
    private List<Weapon> weaponsSelection;
    private List<GameCharacter> charactersSelection;
    private List<Powerup> powerupsSelection;
    private Map<AmmoType, Integer> ammosSelection;
    private List<WeaponEffectOrderType> effectsSelection;

    private Weapon currentWeapon;
    private boolean weaponActivated;
    private PaymentType currentPayment;
    private Map<AmmoType, Integer> requiredPayment;
    private Map<AmmoType, Integer> paidAmmos;
    private List<Powerup> paidPowerups;

    private EffectPossibilityPack effectPossibility;
    private EffectPossibilityPack possibilitySelections;

    private PowerupType activePowerup;

    View() {
        this.enemyBoards = new ArrayList<>();
        this.paidAmmos = new EnumMap<>(AmmoType.class);
        for (AmmoType type : AmmoType.values()) {
            this.paidAmmos.put(type, 0);
        }
        this.paidPowerups = new ArrayList<>();
        this.charactersSelection = new ArrayList<>();
    }

    void connect(int connectionType) {
        if (connectionType == 0) {
            Runnable r = new SocketClient(this);
            (new Thread(r)).start();
            this.client = (SocketClient)r;
        } else {
            try {
                this.client = new RMIProtocolClient(this);
            } catch (IllegalStateException e) {
                handleConnectionError();
            }
        }
    }

    public AbstractClient getClient() {
        return this.client;
    }

    ClientState getState() {
        return this.state;
    }

    GameCharacter getCharacter() {
        return this.character;
    }

    List<PlayerBoard> getEnemyBoards() {
        return new ArrayList<>(this.enemyBoards);
    }

    SelfPlayerBoard getSelfPlayerBoard() {
        return this.selfPlayerBoard;
    }

    BoardView getBoard() {
        return this.board;
    }

    List<GameCharacter> getCharactersSelection() {
        return new ArrayList<>(this.charactersSelection);
    }

    List<ActionType> getActionsSelection() {
        return new ArrayList<>(this.actionsSelection);
    }

    Map<AmmoType, Integer> getAmmosSelection() {
        return new EnumMap<>(this.ammosSelection);
    }

    List<Powerup> getPowerupsSelection() {
        return new ArrayList<>(this.powerupsSelection);
    }

    List<WeaponEffectOrderType> getEffectsSelection() {
        return new ArrayList<>(this.effectsSelection);
    }

    List<Coordinates> getCoordinatesSelection() {
        return this.coordinatesSelection;
    }

    List<Weapon> getWeaponsSelection() {
        return new ArrayList<>(this.weaponsSelection);
    }

    Map<AmmoType, Integer> getRequiredPayment() {
        return new EnumMap<>(this.requiredPayment);
    }

    PowerupType getActivePowerup() {
        return this.activePowerup;
    }

    Weapon getCurrentWeapon() {
        return this.currentWeapon;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public boolean isWeaponActivated() {
        return this.weaponActivated;
    }

    public void setWeaponActivated(boolean weaponActivated) {
        this.weaponActivated = weaponActivated;
    }

    PaymentType getCurrentPayment() {
        return this.currentPayment;
    }

    Map<AmmoType, Integer> getPaidAmmos() {
        return new EnumMap<>(this.paidAmmos);
    }

    List<Powerup> getPaidPowerups() {
        return new ArrayList<>(this.paidPowerups);
    }

    public EffectPossibilityPack getEffectPossibility() {
        return this.effectPossibility;
    }

    void setState(ClientState state) {
        this.state = state;
    }

    void setActivePowerup(PowerupType powerup) {
        this.activePowerup = powerup;
    }

    void setCharactersSelection(List<GameCharacter> characters) {
        this.charactersSelection = new ArrayList<>(characters);
    }

    void putPaidAmmos(AmmoType type, int amount) {
        this.paidAmmos.put(type, amount);
    }

    void addPaidPowerup(Powerup powerup) {
        this.paidPowerups.add(powerup);
    }

    void putAmmosSelection(AmmoType type, int amount) {
        this.ammosSelection.put(type, amount);
    }

    void removePowerupSelection(Powerup powerup) {
        this.powerupsSelection.remove(powerup);
    }

    void putRequiredPayment(AmmoType type, int amount) {
        this.requiredPayment.put(type, amount);
    }

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
    }

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

    public void handleConnectionError() {
        System.exit(0);
    }

    private PlayerBoard getBoardByCharacter(GameCharacter character) {
        for (PlayerBoard board : this.enemyBoards) {
            if (board.getCharacter() == character) {
                return board;
            }
        }
        return null;
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

    void handleNicknameRequest() {
        this.state = TYPINGNICKNAME;
    }

    void handleNicknameDuplicated() {
        this.state = TYPINGNICKNAME;
    }

    private void update(TimerMessage message) {
        switch (message.getTimerType()) {
            case SETUP:
                handleGameSetupTimer(message.getType(), message.getTime());
                break;
            case POWERUP:
                handlePowerupTimer(message.getType());
                break;
        }
    }

    abstract void handleGameSetupTimer(TimerMessageType action, long duration);

    void handlePowerupTimer(TimerMessageType action) {
        if (this.state == SELECTPOWERUPPOSITION) {
            this.state = OTHERTURN;
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
            case ATTACK:
                handleAttack(message.getCharacter(), ((AttackMessage) message).getAttacker(),
                        ((AttackMessage) message).getAmount(), ((AttackMessage) message).getAttackType());
                break;
            case MARKS_TO_DAMAGES:
                handleMarksToDamages(message.getCharacter(), ((MarksToDamagesMessage) message).getAttacker());
                break;
        }
    }

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
                playerBoard.addDamages(player, 1);
            }
        }
        playerBoard.resetMarks(attacker);
    }

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

    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter,
            String> otherPlayers) {
        this.state = WAITINGSTART;
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

    void handleReadyPlayer(GameCharacter character, String nickname) {
        if (this.state == WAITINGSTART) {
            this.enemyBoards.add(new PlayerBoard(character, nickname));
        }
    }

    void handleSpawnedPlayer(GameCharacter character, Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        this.board.setPlayerPosition(character, this.board.getSquareByCoordinates(x, y));
    }

    void handleSkullsSet() {
        this.state = SETTINGARENA;
    }

    void handleMasterChanged(GameCharacter character) {
        if (character == this.character) {
            this.state = SETTINGSKULLS;
        }
    }

    void handleStartSetup(GameCharacter character) {
        if (character == this.character) {
            this.state = SETTINGSKULLS;
        } else {
            this.state = WAITINGSETUP;
        }
    }

    void handleMovement(GameCharacter character, Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        this.board.setPlayerPosition(character, this.board.getSquareByCoordinates(x, y));
    }

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
                        ((LoadViewMessage) message).getPayerBoards(), ((LoadViewMessage) message).getReadyWeapons(),
                        ((LoadViewMessage) message).getPowerups(), ((LoadViewMessage) message).getScore(),
                        ((LoadViewMessage) message).getOtherPlayers());
                break;
        }
    }

    void handleInvalidToken() {
        System.exit(0);
    }

    void handleFullLobby() {
        System.exit(0);
    }

    void handleReconnectionRequest() {
        this.state = RECONNECTING;
        this.client.send(new ReconnectionMessage(getToken()));
    }

    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        this.state = CHOOSINGCHARACTER;
    }

    void handleClientDisconnected(GameCharacter character) {
        if (this.state == TYPINGNICKNAME || this.state == WAITINGSTART || this.state == WAITINGSETUP ||
                this.state == SETTINGSKULLS || this.state == SETTINGARENA) {
            for (PlayerBoard playerBoard : this.enemyBoards) {
                if (playerBoard.getCharacter() == character) {
                    this.enemyBoards.remove(playerBoard);
                    break;
                }
            }
        }
    }

    void handleGameAlreadyStarted() {
        System.exit(0);
    }

    void loadView(GameCharacter character, int skulls, List<SquareView> squares,
                  Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                  List<Weapon> weapons, List<Powerup> powerups, int score, Map<GameCharacter, String> others) {
        this.character = character;
        this.state = WAITINGSETUP;
        this.enemyBoards = new ArrayList<>();
        this.board = new BoardView(skulls, squares, killshotTrack);
        for (PlayerBoard playerBoard : playerBoards) {
            if (playerBoard.getCharacter() == character) {
                this.selfPlayerBoard = new SelfPlayerBoard(playerBoard, weapons, powerups, score);
            } else {
                this.enemyBoards.add(playerBoard);
            }
        }
    }

    private void update(PowerupMessage message) {
        switch (message.getType()) {
            case ADD:
                handlePowerupAdded(message.getCharacter(), message.getPowerup());
                break;
            default:
                handlePowerupRemoved(message.getCharacter(), message.getPowerup(), message.getType());
        }
    }

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

    private void update(AmmosMessage message) {
        switch (message.getType()) {
            case ADD:
                handleAddAmmos(message.getCharacter(), message.getAmmos());
                break;
            case REMOVE:
                handleRemoveAmmos(message.getCharacter(), message.getAmmos());
        }
    }

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
        }
    }

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

    private void update(PaymentMessage message) {
        handlePayment(message.getPaymentType(), message.getAmmos());
    }

    private void handlePayment(PaymentType type, Map<AmmoType, Integer> request) {
        this.state = PAYMENT;
        this.requiredPayment = request;
        this.currentPayment = type;
        this.ammosSelection = new EnumMap<>(this.selfPlayerBoard.getAvailableAmmos());
        this.powerupsSelection = new ArrayList<>(this.selfPlayerBoard.getPowerups());
        requirePayment();
    }

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

    void handleStartTurn(TurnMessage message, GameCharacter character) {
        if (character != this.character) {
            this.state = OTHERTURN;
        } else {
            this.state = YOURTURN;
            this.client.send(message);
        }
    }

    abstract void handleEndTurn(GameCharacter character);

    void handleTurnContinuation(GameCharacter player) {
        this.state = OTHERTURN;
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

    void handleSetupInterrupted() {
        this.state = WAITINGSTART;
    }

    void handleGameSet(Map<Coordinates, RoomColor> colors, Map<Coordinates, Boolean> spawns,
                       Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility, int skulls, int arena) {
        List<SquareView> squares = new ArrayList<>();
        for (Map.Entry<Coordinates, RoomColor> square : colors.entrySet()) {
            RoomColor color = square.getValue();
            Boolean spawn = spawns.get(square.getKey());
            Map<CardinalPoint, Boolean> accessibilityMap = nearbyAccessibility.get(square.getKey());
            squares.add(new SquareView(square.getKey().getX(), square.getKey().getY(), color, spawn, accessibilityMap));
        }
        this.board = new BoardView(skulls, squares);
    }

    void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        for (Map.Entry<Coordinates, Weapon> weapon : weapons.entrySet()) {
            int x = weapon.getKey().getX();
            int y = weapon.getKey().getY();
            this.board.getSquareByCoordinates(x, y).addStoreWeapon(weapon.getValue());
        }
    }

    void handleTilesRefilled(Map<Coordinates, AmmoTile> tiles) {
        for (Map.Entry<Coordinates, AmmoTile> tile : tiles.entrySet()) {
            int x = tile.getKey().getX();
            int y = tile.getKey().getY();
            this.board.getSquareByCoordinates(x, y).setAvailableAmmoTile(tile.getValue());
        }
    }

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
        }
    }

    void handleWeaponSwitchRequest(List<Weapon> weapons) {
        this.weaponsSelection = weapons;
        this.state = SWITCHWEAPON;
    }

    void handlePickupActionRequest(List<Coordinates> coordinates) {
        this.coordinatesSelection = coordinates;
        this.state = SELECTPICKUP;
    }

    void handleMovementActionRequest(List<Coordinates> coordinates) {
        this.coordinatesSelection = coordinates;
        this.state = SELECTMOVEMENT;
    }

    void handlePowerupTargetRequest(List<GameCharacter> targets) {
        this.charactersSelection = targets;
        this.state = SELECTPOWERUPTARGET;
    }

    void handlePowerupPositionRequest(List<Coordinates> coordinates) {
        this.coordinatesSelection = coordinates;
        this.state = SELECTPOWERUPPOSITION;
    }

    void handleReloadRequest(List<Weapon> weapons) {
        this.weaponsSelection = new ArrayList<>(weapons);
        this.state = RECHARGEWEAPON;
    }

    void handleDiscardPowerupRequest(List<Powerup> powerups) {
        this.powerupsSelection = powerups;
        this.state = DISCARDSPAWN;
    }

    void handleUsePowerupRequest(List<Powerup> powerups) {
        this.powerupsSelection = powerups;
        if (powerups.get(0).getType() == PowerupType.TAGBACK_GRENADE ||
                powerups.get(0).getType() == PowerupType.TARGETING_SCOPE) {
            this.state = USEMULTIPLEPOWERUPS;
        } else {
            this.state = USEPOWERUP;
        }
    }

    void handleWeaponPickupRequest(List<Weapon> weapons) {
        this.weaponsSelection = new ArrayList<>(weapons);
        this.state = SELECTWEAPON;
    }

    void handleActionSelectionRequest(List<ActionType> actions) {
        this.actionsSelection = new ArrayList<>(actions);
        this.actionsSelection.add(ActionType.ENDTURN);
        this.state = SELECTACTION;
    }

    void handleWeaponUseRequest(List<Weapon> weapons) {
        this.weaponsSelection = new ArrayList<>(weapons);
        this.state = USEWEAPON;
    }

    void handleEffectRequest(List<WeaponEffectOrderType> effects) {
        this.effectsSelection = new ArrayList<>(effects);
        this.state = USEEFFECT;
    }

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
        }
    }

    void handleEffectRequireRequest() {
        this.state = EFFECTREQUIRE;
    }

    void handleEffectSelections() {
        if (this.effectPossibility.getType() == EffectType.SELECT) {
            handleEffectSelectRequest();
        } else if (!this.effectPossibility.getMultipleSquares().isEmpty()) {
            handleMultipleSquareRequest();
        } else {
            handleEffectTargetRequest();
        }
    }

    void handleEffectComboRequest(WeaponEffectOrderType effect) {
        this.state = EFFECTCOMBO;
    }

    void handleEffectSelectRequest() {
        if(!this.effectPossibility.getSquares().isEmpty()) {
            this.state = EFFECTSELECT_SQUARE;
        } else if(!this.effectPossibility.getRooms().isEmpty()) {
            this.state = EFFECTSELECT_ROOM;
        } else if(!this.effectPossibility.getCardinalPoints().isEmpty()) {
            this.state = EFFECTSELECT_CARDINAL;
        }
    }

    void handleEffectMoveRequest() {
        this.state = EFFECTMOVE;
    }

    void handleEffectTargetRequest() {
        this.state = EFFECTTARGET;
    }

    void handleMultipleSquareRequest() {
        this.state = MULTIPLESQUARE;
    }

    abstract void requirePayment();

    void setPossibilityCharacters(List<GameCharacter> characters) {
        this.possibilitySelections.setCharacters(characters);
    }

    void setPossibilitySquares(List<Coordinates> squares) {
        this.possibilitySelections.setSquares(squares);
    }

    void setPossibilityRooms(List<RoomColor> rooms) {
        this.possibilitySelections.setRooms(rooms);
    }

    void setPossibilityCardinal(List<CardinalPoint> cardinal) {
        this.possibilitySelections.setCardinalPoints(cardinal);
    }

    void setPossibilityRequire(boolean isRequire) {
        this.possibilitySelections.setRequire(isRequire);
        if(!isRequire) {
            selectionEffectFinish();
            return;
        }
        handleEffectSelections();
    }

    void setPossibilityMutipleSquares(Map<Coordinates, List<GameCharacter>> mutipleSquares) {
        this.possibilitySelections.setMultipleSquares(mutipleSquares);
    }

    void selectionEffectFinish() {
        this.client.send(new SingleSelectionMessage(SelectionMessageType.EFFECT_POSSIBILITY, this.character,
                this.possibilitySelections));
    }

    String generateToken() {
        String path = System.getProperty("user.home");
        String message = UUID.randomUUID().toString();
        StringBuffer hexString = new StringBuffer();
        MessageDigest md;
        try(FileWriter writer = new FileWriter(path + "/" + "AdrenalinaClient.token")) {
            md = MessageDigest.getInstance("SHA-256");
            md.update(message.getBytes());
            byte[] digest = md.digest();
            for (int i = 0; i < digest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
            writer.write(message);
            writer.flush();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error SHA-256 algorithm", e);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing data", e);
        }
        return hexString.toString();
    }

    private String getToken() {
        String path = System.getProperty("user.home");
        try (BufferedReader reader = new BufferedReader(new FileReader(path + "/" + "AdrenalinaClient.token"))) {
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
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error SHA-256 algorithm", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error corrupt data", e);
        }
        return null;
    }

}