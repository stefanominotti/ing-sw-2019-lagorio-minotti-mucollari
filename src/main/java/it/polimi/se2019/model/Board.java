package it.polimi.se2019.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.model.messages.player.MarksToDamagesMessage;
import it.polimi.se2019.model.messages.ammos.AmmosMessage;
import it.polimi.se2019.model.messages.ammos.AmmosMessageType;
import it.polimi.se2019.model.messages.board.*;
import it.polimi.se2019.model.messages.client.ClientDisconnectedMessage;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;
import it.polimi.se2019.model.messages.client.LoadViewMessage;
import it.polimi.se2019.model.messages.player.*;
import it.polimi.se2019.model.messages.powerups.PowerupMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessageType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.timer.TimerMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.timer.TimerType;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import it.polimi.se2019.model.messages.weapon.WeaponMessage;
import it.polimi.se2019.model.messages.weapon.WeaponMessageType;
import it.polimi.se2019.model.messages.weapon.WeaponSwitchMessage;
import it.polimi.se2019.server.SocketVirtualClient;
import it.polimi.se2019.view.PlayerBoard;
import it.polimi.se2019.view.SquareView;

import java.io.FileReader;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.se2019.model.GameState.*;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toMap;

public class Board extends Observable {

    private static final Logger LOGGER = Logger.getLogger(SocketVirtualClient.class.getName());
    private static final int MAX_WEAPONS_STORE = 3;
    private static final int MAX_POWERUPS = 3;
    private static final long DEFAULT_START_TIMER = 10L*1000L;
    private static final long DEFAULT_TURN_TIMER = 100L*1000L;
    private static final long DEFAULT_RESPAWN_TIMER = 30L*1000L;
    private static final long DEFAULT_POWERUPS_TIMER = 10L*1000L;
    private static final int MIN_PLAYERS = 3;
    private static final String PATH = System.getProperty("user.home");
    private static final String SERVER_SETTINGS = "/server_settings.json";

    private long startTimer;
    private long turnTimer;
    private long respawnTimer;
    private long powerupsTimer;
    private int skulls;
    private Arena arena;
    private List<Player> players;
    private List<WeaponCard> weaponsDeck;
    private List<Powerup> powerupsDeck;
    private List<AmmoTile> ammosDeck;
    private List<Powerup> powerupsDiscardPile;
    private List<AmmoTile> ammosDiscardPile;
    private Map<Integer, List<GameCharacter>> killshotTrack;
    private GameState gameState;
    private GameTimer gameTimer;
    private Timer utilsTimer;
    private long utilsTimerStartTime;
    private int currentPlayer;
    private List<GameCharacter> finalFrenzyOrder;
    private List<GameCharacter> deadPlayers;
    private Map<GameCharacter, Integer> pointsFromKillshotTrack;

    /**
     * Class constructor, it builds a board
     */
    public Board() {
        this.gameState = ACCEPTING_PLAYERS;
        this.players = new ArrayList<>();
        this.weaponsDeck = new ArrayList<>();
        this.powerupsDeck = new ArrayList<>();
        this.ammosDeck = new ArrayList<>();
        this.powerupsDiscardPile = new ArrayList<>();
        this.ammosDiscardPile = new ArrayList<>();
        this.killshotTrack = new HashMap<>();
        this.utilsTimer = new Timer();
        this.deadPlayers = new ArrayList<>();
        this.finalFrenzyOrder = new ArrayList<>();
        this.pointsFromKillshotTrack = new EnumMap<>(GameCharacter.class);
        for (GameCharacter c : GameCharacter.values()) {
            this.pointsFromKillshotTrack.put(c, 0);
        }
        loadTimers();
    }

    /**
     * Writes board data to JSON
     */
    public String toJson() {
        Gson gson = new Gson();
        StringBuilder jObject = new StringBuilder("{");
        String toAppend;
        toAppend = "\"skulls\": " + this.skulls + ",";
        jObject.append(toAppend);
        toAppend = "\"gameState\": " + "\"" + this.gameState + "\"" + ",";
        jObject.append(toAppend);
        toAppend = "\"currentPlayer\": " + this.currentPlayer + ",";
        jObject.append(toAppend);
        toAppend = "\"weaponsDeck\": " + gson.toJson(this.weaponsDeck) + ',';
        jObject.append(toAppend);
        toAppend = "\"powerupsDeck\": " + gson.toJson(this.powerupsDeck) + ',';
        jObject.append(toAppend);
        toAppend = "\"ammosDeck\": " + gson.toJson(this.ammosDeck) + ',';
        jObject.append(toAppend);
        toAppend = "\"powerupsDiscardPile\": " + gson.toJson(this.powerupsDiscardPile) + ',';
        jObject.append(toAppend);
        toAppend = "\"ammosDiscardPile\": " + gson.toJson(this.ammosDiscardPile) + ',';
        jObject.append(toAppend);
        toAppend = "\"killshotTrack\": " + gson.toJson(this.killshotTrack) + ',';
        jObject.append(toAppend);
        toAppend = "\"finalFrenzyOrder\":" + gson.toJson(this.finalFrenzyOrder) + ',';
        jObject.append(toAppend);
        toAppend = "\"deadPlayers\":" + gson.toJson(this.deadPlayers);
        jObject.append(toAppend);
        jObject.append("}");
        return jObject.toString();
    }

    /**
     * Creates model and view for a player
     * @param player of which you want to create model and view
     */
    LoadViewMessage createModelView(Player player) {
        List<SquareView> squareViews = new ArrayList<>();
        for(Square square : this.arena.getAllSquares()) {
            List<GameCharacter> activePlayers = new ArrayList<>();
            for(Player character : square.getActivePlayers()) {
                activePlayers.add(character.getCharacter());
            }
            List<Weapon> weapons = null;
            if(square.isSpawn()) {
                weapons = new ArrayList<>();
                for (WeaponCard weaponCard : square.getWeaponsStore()) {
                    weapons.add(weaponCard.getWeaponType());
                }
            }
            squareViews.add(new SquareView(
                    square.getX(), square.getY(), square.getRoom().getColor(), square.isSpawn(),
                    activePlayers, square.getAvailableAmmoTile(), weapons, square.getNearbyAccessibility()));
        }

        List<PlayerBoard> playerBoards = new ArrayList<>();
        List<Weapon> playerWeapons = new ArrayList<>();
        Map<GameCharacter, String> otherPlayers = new EnumMap<>(GameCharacter.class);
        for(Player character : this.players) {
            List<Weapon> weapons = new ArrayList<>();
            for(WeaponCard weaponCard : player.getWeapons()) {
                if(!weaponCard.isReady()) {
                    weapons.add(weaponCard.getWeaponType());
                }
                if(weaponCard.isReady() && character == player){
                    playerWeapons.add(weaponCard.getWeaponType());
                }
            }
            if(character.isConnected()) {
                otherPlayers.put(character.getCharacter(), character.getNickname());
            }
            playerBoards.add(new PlayerBoard(character.getCharacter(), character.getNickname(),
                    character.getAvailableAmmos(), character.getRevengeMarks(), character.getDamages(),
                    character.getKillshotPoints(), weapons, character.getWeapons().size(),
                    character.getPowerups().size()));
        }
        Map<Integer, List<GameCharacter>> track = new HashMap<>();
        for (Map.Entry<Integer, List<GameCharacter>> kill : this.killshotTrack.entrySet()) {
            track.put(kill.getKey(), new ArrayList<>(kill.getValue()));
        }

        return new LoadViewMessage(player.getCharacter(), player.getNickname(), this.skulls, squareViews,
                track, playerBoards, playerWeapons, player.getPowerups(), player.getScore(), otherPlayers,
                !this.finalFrenzyOrder.isEmpty(), isPlayerBeforeFirst(player));

    }


    /** Sends model and view to a player
     * @param player to send the message to
     */
    public void sendModelView(Player player) {
        notifyChanges(createModelView(player));
    }

    /**
     * Notifies changes to the Observers
     * @param object to notify
     */
    private void notifyChanges(Object object) {
        setChanged();
        notifyObservers(object);
    }

    /**
     * Gets player list of the board
     * @return List of players of the board
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    /**
     * Set the state of the game
     * @param state you want to be set
     */
    void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Gets the state of the game
     * @return game state of the game
     */
    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * Gets powerups utilsTimer duration
     * @return Powerups utilsTimer duration
     */
    public long getPowerupsTimerDuration() {
        return this.powerupsTimer;
    }

    /**
     * Gets available players, which have a defined position on the board
     * @return List of valid players
     */
    public List<Player> getAvailablePlayers() {
        List<Player> availablePlayers = new ArrayList<>();
        for (Player p : this.players) {
            if(p.getPosition() != null && !p.isDead()) {
                availablePlayers.add(p);
            }
        }
        return availablePlayers;
    }

    /**
     * Gets valid characters of the board
     * @return List of GameCharacter of the board
     */
    List<GameCharacter> getValidCharacters() {
        List<GameCharacter> validCharacters = new ArrayList<>();
        for (Player p : this.players) {
            validCharacters.add(p.getCharacter());
        }
        return validCharacters;
    }

    /**
     * Gets the player by its character
     * @param character of player you want to get
     * @return player of that character
     */
    public Player getPlayerByCharacter(GameCharacter character) {
        for (Player player : this.players) {
            if (player.getCharacter() == character) {
                return player;
            }
        }
        return null;
    }

    /**
     * Adds a new player to the board
     * @param character of player you want to add
     * @param nickname of player you want to add
     * @param token of player you want to add
     */
    public void addPlayer(GameCharacter character, String nickname, String token) {
        if (getPlayerByCharacter(character) != null) {
            getPlayerByCharacter(character).connect();
            return;
        }
        this.players.add(new Player(character, nickname, token));
        Map<GameCharacter, String> others = new LinkedHashMap<>();
        for (Player p : this.players) {
            if (p.getCharacter() != character) {
                others.put(p.getCharacter(), p.getNickname());
            }
        }
        notifyChanges(new PlayerCreatedMessage(character, nickname, others));

        if (this.players.size() > 3) {
            long remainingTime = this.startTimer/1000L - (this.utilsTimerStartTime - System.currentTimeMillis());
            notifyChanges(new TimerMessage(TimerMessageType.UPDATE, TimerType.SETUP, remainingTime*1000L));
        }

        if (this.players.size() == 3) {
            this.utilsTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finalizePlayersCreation();
                }
            }, this.startTimer);
            notifyChanges(new TimerMessage(TimerMessageType.START, TimerType.SETUP,this.startTimer/1000L));
            this.utilsTimerStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Sets the players to the board
     * @param players List of players you want to set
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Finalizes players creation sending messages to notify game status
     */
    void finalizePlayersCreation() {
        this.gameState = SETTING_UP_GAME;
        List<Player> toRemove = new ArrayList<>();
        for (Player p : this.players) {
            if (p.getNickname() == null) {
                toRemove.add(p);
            }
        }
        for (Player p : toRemove) {
            this.players.remove(p);
        }
        notifyChanges(new ClientMessage(ClientMessageType.GAME_ALREADY_STARTED, null));
        notifyChanges(new PlayerMessage(PlayerMessageType.START_SETUP, this.players.get(0).getCharacter()));
    }

    /**
     * Handles a player disconnection removing him from the game
     * @param player of which you want to handle disconnection
     */
    public void handleDisconnection(GameCharacter player) {
        switch(this.gameState) {
            case ACCEPTING_PLAYERS:
                handleAcceptingPlayersDisconnection(player);
                break;
            case SETTING_UP_GAME:
                handleSettingUpGameDisconnection(player);
                break;
            case ENDED:
                break;
            default:
                handleInGameDisconnection(player);
        }

    }

    /**
     * Handles a player disconnection during accepting players phase
     * @param player of which you want to handle disconnection
     */
    private void handleAcceptingPlayersDisconnection(GameCharacter player) {
        notifyChanges(new ClientDisconnectedMessage(player, true));
        this.players.remove(getPlayerByCharacter(player));
        if (this.players.size() == 2) {
            this.utilsTimer.cancel();
            this.utilsTimer = new Timer();
            notifyChanges(new TimerMessage(TimerMessageType.STOP, TimerType.SETUP));
        }
    }

    /**
     * Handles a player disconnection during game set up
     * @param player of which you want to handle disconnection
     */
    private void handleSettingUpGameDisconnection(GameCharacter player) {
        boolean isMaster = false;
        if (getPlayerByCharacter(player) == this.players.get(0)) {
            isMaster = true;
        }
        this.players.remove(getPlayerByCharacter(player));
        notifyChanges(new ClientDisconnectedMessage(player, true));
        if (this.players.size() == 2) {
            this.gameState = ACCEPTING_PLAYERS;
            this.utilsTimer = new Timer();
            notifyChanges(new BoardMessage(BoardMessageType.SETUP_INTERRUPTED));
            return;
        }
        if (isMaster) {
            notifyChanges(new PlayerMessage(PlayerMessageType.MASTER_CHANGED,
                    this.players.get(0).getCharacter()));
        }
    }

    /**
     * Handles a player disconnection when game is started
     * @param player of which you want to handle disconnection
     */
    private void handleInGameDisconnection(GameCharacter player) {
        int validPlayers = 0;
        Player disconnected = getPlayerByCharacter(player);
        disconnected.disconnect();
        notifyChanges(new ClientDisconnectedMessage(player, true));
        for (Player p : this.players) {
            if (p.isConnected()) {
                validPlayers++;
            }
        }
        if (validPlayers < MIN_PLAYERS) {
            this.gameTimer.cancel();
            for (Player p : this.players) {
                if (p.isConnected()) {
                    notifyChanges(new SingleSelectionMessage(SelectionMessageType.PERSISTENCE, p.getCharacter(),
                            null));
                    break;
                }
            }
        }
    }

    /**
     * Gets skulls number
     * @return number of skulls of the board
     */
    public int getSkulls() {
        return this.skulls;
    }

    /**
     * Sets skulls number
     * @param skulls number to set
     */
    public void setSkulls(int skulls){
        this.skulls = skulls;
        for (int i=0; i<this.skulls; i++) {
            this.killshotTrack.put(i + 1, new ArrayList<>());
        }
        notifyChanges(new PlayerMessage(PlayerMessageType.SKULLS_SET, this.players.get(0).getCharacter()));
    }

    /**
     * Loads timers from server settings file or default if it's not available
     */
    public void loadTimers() {
        FileReader reader;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement;
        try {
            reader = new FileReader(PATH + SERVER_SETTINGS);
            jsonElement = (JsonObject)parser.parse(reader);
            this.startTimer = gson.fromJson(jsonElement.get("startTimer"), Long.class);
            this.turnTimer = gson.fromJson(jsonElement.get("turnTimer"), Long.class);
            this.respawnTimer = gson.fromJson(jsonElement.get("respawnTimer"), Long.class);
            this.powerupsTimer = gson.fromJson(jsonElement.get("powerupsTimer"), Long.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Invalid settings file, timers set to default");
            setDefaultTimers();
        }
    }

    /**
     * Sets default values for timers
     */
    void setDefaultTimers() {
        this.startTimer = DEFAULT_START_TIMER;
        this.turnTimer = DEFAULT_TURN_TIMER;
        this.respawnTimer = DEFAULT_RESPAWN_TIMER;
        this.powerupsTimer = DEFAULT_POWERUPS_TIMER;
    }

    /**
     * Creates an arena by its ID and notify the view
     * @param arenaNumber number of the arena you want to build
     */
    public void createArena(String arenaNumber) {
        loadArena(arenaNumber);
        notifyChanges(createArenaMessage());
    }

    /**
     * Creates an arena message to be notified
     */
    GameSetMessage createArenaMessage() {
        Map<Coordinates, RoomColor> arenaColor = new HashMap<>();
        Map<Coordinates, Boolean> arenaSpawn = new HashMap<>();
        Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility = new HashMap<>();
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                Coordinates coordinates = new Coordinates(square.getX(), square.getY());
                RoomColor color =  room.getColor();
                Boolean spawn = square.isSpawn();
                arenaColor.put(coordinates, color);
                arenaSpawn.put(coordinates, spawn);
                Map <CardinalPoint, Boolean> localAccessibility = new EnumMap<>(square.getNearbyAccessibility());
                nearbyAccessibility.put(coordinates, localAccessibility);
            }
        }
        return new GameSetMessage(this.skulls, Integer.parseInt(this.arena.toJson()), arenaColor, arenaSpawn,
                nearbyAccessibility);
    }

    /**
     * Loads the arena by its ID
     * @param arenaNumber number of the Arena you want to load
     */
    public void loadArena(String arenaNumber) {
        this.arena = new Arena(arenaNumber);
    }

    /**
     * Gets the arena
     * @return the arena
     */
    public Arena getArena() {
        return this.arena;
    }

    /**
     * Finalizes game setup filling ammo tiles, weapons and powerups decks on the board
     */
    public void finalizeGameSetup() {
        fillAmmosDeck();
        fillWeaponsDeck();
        fillPowerupsDeck();
        fillWeaponStores();
        fillAmmoTiles();

        this.gameState = FIRST_TURN;
        this.currentPlayer = 0;
        startTurn(this.players.get(this.currentPlayer));
    }

    /**
     * Adds a dead player
     * @param player you want to make dead
     */
    public void addDeadPlayer(Player player) {
        this.deadPlayers.add(player.getCharacter());
    }

    /**
     * Knows if there are any dead players
     * @return true if are there, else false
     */
    public List<GameCharacter> getDeadPlayers() {
        return this.deadPlayers;
    }

    /**
     * Ends a player turn and reset the board
     * @param character you want him to end turn
     */
    public void endTurn(GameCharacter character) {
        Player player = getPlayerByCharacter(character);
        this.gameTimer.cancel();
        if(this.gameState == FIRST_TURN && this.currentPlayer == this.players.size() - 1) {
            this.gameState = IN_GAME;
        }

        if (player.isDead() || player.getPosition() == null) {
            Room room =
                    getArena().getRoomByColor(RoomColor.valueOf(player.getPowerups().get(0).getColor().toString()));

            removePowerup(player, player.getPowerups().get(0));
            respawnPlayer(player, room);
        }

        Player nextPlayer = null;
        for (GameCharacter c : this.deadPlayers) {
            Player p = getPlayerByCharacter(c);
            if (!player.isConnected()) {
                drawPowerup(p);

                Room room =
                        this.arena.getRoomByColor(RoomColor.valueOf(p.getPowerups().get(0).getColor().toString()));

                removePowerup(p, p.getPowerups().get(0));
                respawnPlayer(p, room);
            } else {
                nextPlayer = p;
                break;
            }
        }

        fillAmmoTiles();
        fillWeaponStores();
        notifyChanges(new TurnMessage(TurnMessageType.END, player.getCharacter()));
        if (nextPlayer == null) {
            if (!this.finalFrenzyOrder.isEmpty() && player.getCharacter() == this.finalFrenzyOrder.get(this.finalFrenzyOrder.size() - 1)) {
                endGame();
                return;
            }
            if (this.skulls == 0) {
                startFinalFrenzy(this.players.get(this.currentPlayer).getCharacter());
            }
            incrementCurrentPlayer();
            nextPlayer = this.players.get(this.currentPlayer);
        }
        startTurn(nextPlayer);
    }

    /**
     * Starts a player turn
     * @param player you want to start turn
     */
    public void startTurn(Player player) {
        TurnType type;
        if(player.isDead()) {
            type = TurnType.AFTER_DEATH;
        } else if(this.gameState == FIRST_TURN || player.getPosition() == null) {
            type = TurnType.FIRST_TURN;
        } else if(!this.finalFrenzyOrder.isEmpty()) {
            this.skulls = -1;
            if(isPlayerBeforeFirst(player)) {
                type = TurnType.FINAL_FRENZY_FIRST;
            } else {
                type = TurnType.FINAL_FRENZY_AFTER;
            }
        } else {
            type = TurnType.NORMAL;
        }
        notifyChanges(new TurnMessage(TurnMessageType.START, type, player.getCharacter()));
    }

    /**
     * Increments the current player index
     */
    void incrementCurrentPlayer() {
        if(this.currentPlayer == this.players.size() - 1) {
            this.currentPlayer = 0;
        } else {
            this.currentPlayer++;
        }
        if (!this.players.get(this.currentPlayer).isConnected()) {
            incrementCurrentPlayer();
        }
    }

    /**
     * Gets the current player
     * @return the current player
     */
    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Starts a player turn utilsTimer
     * @param character you want to start turn utilsTimer
     */
    public void startTurnTimer(GameCharacter character) {
        if (character == this.players.get(this.currentPlayer).getCharacter()) {
            this.gameTimer = new GameTimer(1000, this.turnTimer, this, character);
        } else {
            this.gameTimer = new GameTimer(1000, this.respawnTimer, this, character);
        }
        this.gameTimer.start();
    }

    /**
     * Adds a player to Final Frenzy players list
     * @param player you want to add to the list
     */
    public void addFrenzyOrderPlayer(Player player) {
        this.finalFrenzyOrder.add(player.getCharacter());
    }

    /**
     * Gets Final Frenzy players order
     * @return Ordered list of characters for the Final Frenzy
     */
    List<GameCharacter> getFinalFrenzyOrder() {
        return new ArrayList<>(this.finalFrenzyOrder);
    }

    /**
     * Fills the weapons deck with a random weapon
     */
    void fillWeaponsDeck() {
        for(Weapon weapon : Weapon.values()) {
            this.weaponsDeck.add(new WeaponCard(weapon));
        }
        Collections.shuffle(this.weaponsDeck);
    }

    List<WeaponCard> getWeaponsDeck() {
        return new ArrayList<>(this.weaponsDeck);
    }

    /**
     * Fills the powerups deck with a random powerup
     */
    void fillPowerupsDeck() {
        if(this.powerupsDiscardPile.isEmpty()) {
            for(PowerupType type : PowerupType.values()) {
                for (AmmoType color : AmmoType.values()) {
                    for(int i = 0; i < 2; i++) {
                        this.powerupsDeck.add(new Powerup(type, color));
                    }
                }
            }
            Collections.shuffle(this.powerupsDeck);
            return;
        }
        this.powerupsDeck = this.powerupsDiscardPile;
        this.powerupsDiscardPile = new ArrayList<>();
        Collections.shuffle(powerupsDeck);
    }

    /**
     * Gets the powerups deck
     * @return List of powerups of the deck
     */
    List<Powerup> getPowerupsDeck() {
        return new ArrayList<>(this.powerupsDeck);
    }

    /**
     * Gets the powerups discard pile
     * @return List of powerups of the pile
     */
    List<Powerup> getPowerupsDiscardPile() {
        return new ArrayList<>(this.powerupsDiscardPile);
    }

    /**
     * Fills the ammos deck loading data from JSON
     */
    void fillAmmosDeck() {
        if(this.ammosDiscardPile.isEmpty()) {
            String path = "ammotiles/data/ammotile_";
            int ammosNumber;
            ClassLoader classLoader = getClass().getClassLoader();
            Gson gson = new Gson();
            for(ammosNumber = 1; ammosNumber < 13; ammosNumber++) {
                InputStream inputStream = classLoader.getResourceAsStream(path + ammosNumber + ".json");
                if (inputStream == null) {
                    return;
                }
                String jsonString = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
                JsonParser parser = new JsonParser();
                JsonObject jsonElement = (JsonObject) parser.parse(jsonString);
                for(int j = gson.fromJson(jsonElement.get("qty"), Integer.class); j > 0; j--){
                    this.ammosDeck.add(gson.fromJson(jsonElement.get("ammo"), AmmoTile.class));
                }
            }
            Collections.shuffle(this.ammosDeck);
            return;
        }
        this.ammosDeck = this.ammosDiscardPile;
        this.ammosDiscardPile = new ArrayList<>();
        Collections.shuffle(this.ammosDeck);
    }

    /**
     * Gets the ammo deck
     * @return List of ammo of the deck
     */
    List<AmmoTile> getAmmosDeck() {
        return new ArrayList<>(this.ammosDeck);
    }

    /**
     * Gets the ammo discard pile
     * @return List of ammo of the pile
     */
    List<AmmoTile> getAmmosDiscardPile() {
        return new ArrayList<>(this.ammosDiscardPile);
    }

    /**
     * Fills the weapons store
     */
    void fillWeaponStores() {
        Map<Coordinates, Weapon> added = new HashMap<>();
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                if(!square.isSpawn()) {
                    continue;
                }
                while(square.getWeaponsStore().size() < MAX_WEAPONS_STORE) {
                    if (this.weaponsDeck.isEmpty()) {
                        if (!added.isEmpty()) {
                            notifyChanges(new WeaponStoresRefilledMessage(added));
                        }
                        return;
                    }
                    Weapon toAdd = this.weaponsDeck.get(0).getWeaponType();
                    square.addWeapon(this.weaponsDeck.get(0));
                    added.put(new Coordinates(square.getX(), square.getY()), toAdd);
                    this.weaponsDeck.remove(0);
                }
            }
        }

        if (!added.isEmpty()) {
            notifyChanges(new WeaponStoresRefilledMessage(added));
        }
    }

    /**
     * Fills the ammo tiles on the arena
     */
    void fillAmmoTiles() {
        Map<Coordinates, AmmoTile> added = new HashMap<>();
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                if(square.isSpawn()) {
                    continue;
                }
                if(square.getAvailableAmmoTile() == null) {
                    if (this.ammosDeck.isEmpty()) {
                        fillAmmosDeck();
                    }
                    square.addAmmoTile(this.ammosDeck.get(0));
                    this.ammosDeck.remove(0);
                    added.put(new Coordinates(square.getX(), square.getY()), square.getAvailableAmmoTile());
                }
            }
        }
        if (!added.isEmpty()) {
            notifyChanges(new AmmoTilesRefilledMessage(added));
        }
    }

    /**
     * Removes a powerup from a player and adds it to the discard pile
     * @param player you want to remove a powerup to
     * @param powerup you want to remove
     */
    public void removePowerup(Player player, Powerup powerup) {
        player.removePowerup(player.getPowerupByType(powerup.getType(), powerup.getColor()));
        this.powerupsDiscardPile.add(powerup);
        notifyChanges(new PowerupMessage(PowerupMessageType.DISCARD, player.getCharacter(), powerup));
    }

    /**
     * Draws a powerup from the powerups deck
     * @param player you want him to draw a powerup
     */
    public void drawPowerup(Player player) {
        if (this.powerupsDeck.isEmpty()) {
            fillPowerupsDeck();
        }
        Powerup powerup = this.powerupsDeck.get(0);
        player.addPowerup(powerup);
        this.powerupsDeck.remove(powerup);
        notifyChanges(new PowerupMessage(PowerupMessageType.ADD, player.getCharacter(), powerup));
        notifyChanges(new PowerupMessage(PowerupMessageType.ADD, player.getCharacter(), null));
    }

    /**
     * Gives a weapon to a player
     * @param player you want to give a weapon to
     * @param weapon you want to give
     */
    public void giveWeapon(Player player, WeaponCard weapon) {
        player.getPosition().removeWeapon(weapon);
        player.addWeapon(weapon);
        notifyChanges(new WeaponMessage(WeaponMessageType.PICKUP, weapon.getWeaponType(), player.getCharacter()));
    }

    /**
     * Switchs two weapons of a player
     * @param player you want him to switch weapons
     * @param oldCard weapon to switch
     * @param newCard weapon switched
     */
    public void switchWeapon(Player player, WeaponCard oldCard, WeaponCard newCard) {
        player.removeWeapon(oldCard);
        player.getPosition().removeWeapon(newCard);
        oldCard.setReady(true);
        player.getPosition().addWeapon(oldCard);
        player.addWeapon(newCard);
        notifyChanges(new WeaponSwitchMessage(newCard.getWeaponType(), oldCard.getWeaponType(), player.getCharacter()));
    }

    /**
     * Loads a weapon and make it ready to shoot
     * @param player you want him to load weapon
     * @param weapon needed to be loaded
     */
    public void loadWeapon(Player player, WeaponCard weapon) {
        weapon.setReady(true);
        notifyChanges(new WeaponMessage(WeaponMessageType.RELOAD, weapon.getWeaponType(), player.getCharacter()));
    }

    /**
     * Unloads a weapon
     * @param player you want him to unload weapon
     * @param weapon needed to be unloaded
     */
    public void unloadWeapon(Player player, WeaponCard weapon) {
        weapon.setReady(false);
        notifyChanges(new WeaponMessage(WeaponMessageType.UNLOAD, weapon.getWeaponType(), player.getCharacter()));
    }

    /**
     * Gives an ammo tile to a player
     * @param player you want to give an ammo tile to
     * @param tile you want to give
     */
    public void giveAmmoTile(Player player, AmmoTile tile) {
        if(tile.hasPowerup() && player.getPowerups().size() < MAX_POWERUPS) {
            drawPowerup(player);
        }
        Map<AmmoType, Integer> addedAmmos = player.addAmmos(tile.getAmmos());
        this.ammosDiscardPile.add(tile);
        player.getPosition().removeAmmoTile();
        notifyChanges(new AmmosMessage(AmmosMessageType.ADD, player.getCharacter(), addedAmmos));
    }

    /**
     * Uses ammo
     * @param player who uses ammo
     * @param usedAmmos Map with ammo and quantity to be used
     */
    public void useAmmos(Player player, Map<AmmoType, Integer> usedAmmos) {
        player.removeAmmos(usedAmmos);
        notifyChanges(new AmmosMessage(AmmosMessageType.REMOVE, player.getCharacter(), usedAmmos));
    }

    /**
     * Moves a player in a square
     * @param player you want to move
     * @param square where you want to move the player
     */
    public void movePlayer(Player player, Square square) {
        if(player.getPosition() != null) {
            player.getPosition().removePlayer(player);
        }
        square.addPlayer(player);
        player.setPosition(square);
        notifyChanges(new MovementMessage(player.getCharacter(), new Coordinates(square.getX(), square.getY())));
    }

    /**
     * Respawns a player in a room
     * @param player you want to respawn
     * @param room where you want to respawn the player
     */
    public void respawnPlayer(Player player, Room room) {
        Square square = room.getSpawn();
        player.setPosition(square);
        square.addPlayer(player);
        player.setDead(false);
        this.deadPlayers.remove(player.getCharacter());
        notifyChanges(new SpawnMessage(player.getCharacter(),
                new Coordinates(square.getX(), square.getY())));
    }

    /**
     * Handles Game Finale
     */
    public void endGame() {
        this.gameState = ENDED;
        for (Player player : this.players) {
            if(player.getDamages().size() > 0) {
                calculateBoardScore(player);
            }
        }
        List<Integer> points = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1));

        List<GameCharacter> trackReworked = new ArrayList<>();
        for (Map.Entry<Integer, List<GameCharacter>> i : this.killshotTrack.entrySet()) {
            trackReworked.addAll(i.getValue());
        }

        Map<GameCharacter, Integer> killsByPlayer = new LinkedHashMap<>();
        for (GameCharacter p : trackReworked) {
            if (killsByPlayer.containsKey(p)) {
                continue;
            }
            int damages = 0;
            for (GameCharacter c : trackReworked) {
                if (c == p) {
                    damages++;
                }
            }
            killsByPlayer.put(p, damages);
        }

        Map<GameCharacter, Integer> sorted = killsByPlayer
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        if (o1 > o2) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                })))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        int index = 0;
        for (GameCharacter p : sorted.keySet()) {
            this.pointsFromKillshotTrack.put(p, points.get(index));
            raisePlayerScore(getPlayerByCharacter(p), points.get(index));
            index++;
        }

        notifyChanges(new EndGameMessage(calculateRanking()));
    }

    void calculateBoardScore(Player player) {
        Map<GameCharacter, Integer> damagesByPlayer = new LinkedHashMap<>();
        for (GameCharacter p : player.getDamages()) {
            if (damagesByPlayer.containsKey(p)) {
                continue;
            }
            int damages = 0;
            for (GameCharacter c : player.getDamages()) {
                if (c == p) {
                    damages++;
                }
            }
            damagesByPlayer.put(p, damages);
        }

        Map<GameCharacter, Integer> sorted = damagesByPlayer
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        if (o1 >= o2) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                })))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        int index = 0;
        for (GameCharacter p : sorted.keySet()) {
            int amount;
            try {
                amount = player.getKillshotPoints().get(index);
            } catch (IndexOutOfBoundsException e) {
                amount = 1;
            }
            raisePlayerScore(getPlayerByCharacter(p), amount);
            index++;
        }
    }

    /**
     * Calculates the final ranking of the match
     * @return Ordered map with characters and corresponding scores
     */
    Map<GameCharacter, Integer> calculateRanking() {
        Map<GameCharacter, Integer> points = new LinkedHashMap<>();
        for (Player p : this.players) {
            points.put(p.getCharacter(), p.getScore());
        }

        return points
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey(new Comparator<GameCharacter>() {
                    @Override
                    public int compare(GameCharacter o1, GameCharacter o2) {
                        int s1 = Board.this.getPlayerByCharacter(o1).getScore();
                        int s2 = Board.this.getPlayerByCharacter(o2).getScore();
                        if (s1 > s2) {
                            return 1;
                        }
                        if (s1 < s2) {
                            return -1;
                        }
                        int p1 = Board.this.pointsFromKillshotTrack.get(o1);
                        int p2 = Board.this.pointsFromKillshotTrack.get(o2);
                        if (p1 > p2) {
                            return 1;
                        }
                        if (p1 < p2) {
                            return -1;
                        }
                        return 0;
                    }
                })))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    /**
     * Handles Final Frenzy mode start
     * @param character you want to start Final Frenzy mode
     */
    void startFinalFrenzy(GameCharacter character) {
        Player player = getPlayerByCharacter(character);
        int index = this.players.indexOf(player);
        for (int i = index + 1; i < this.players.size(); i++) {
            this.finalFrenzyOrder.add(this.players.get(i).getCharacter());
        }
        for (int i = 0; i <= index; i++) {
            this.finalFrenzyOrder.add(this.players.get(i).getCharacter());
        }

        for (Player p : this.players) {
            if (p.getDamages().isEmpty()) {
                p.flipBoard();

                notifyChanges(new PlayerMessage(PlayerMessageType.BOARD_FLIP, p.getCharacter()));
            }
        }

        for (Player toNotify : this.players) {
            notifyChanges(new FinalFrenzyMessage(toNotify.getCharacter(), isPlayerBeforeFirst(toNotify)));

        }

    }

    /**
     * Knows if a player is playing before the first player in this turn
     * @param player of which you want to know if is playing before the first player in this turn
     * @return true if it is, else false
     */
    private boolean isPlayerBeforeFirst(Player player) {
        if(this.finalFrenzyOrder.isEmpty()) {
            return false;
        }
        int playerOrder = this.finalFrenzyOrder.indexOf(player.getCharacter());
        int firstPlayerOrder = this.finalFrenzyOrder.indexOf(this.players.get(0).getCharacter());

        return playerOrder < firstPlayerOrder;
    }

    /**
     * Raises a player score
     * @param player you want to increase his score
     * @param score amount to increase
     */
    void raisePlayerScore(Player player, int score) {
        player.raiseScore(score);
        notifyChanges(new ScoreMessage(player.getCharacter(), score));
    }

    /**
     * Gets visible players from a player
     * @param player of which you want to get visible players
     * @return List of visible players for that player
     */
    public List<Player> getVisiblePlayers (Player player) {

        List<Player> visiblePlayers = new ArrayList<>();
        for(Square s : this.arena.getAllSquares()){
            for (Player p : this.players){
                if(p.getPosition() == s && p != player && player.getPosition().canSee(p.getPosition())) {
                    visiblePlayers.add(p);
                }
            }
        }
        return visiblePlayers;
    }

    /**
     * Gets visible rooms from a player
     * @param player of which you want to get visible rooms
     * @return List of visible rooms for that player
     */
    public List<Room> getVisibleRooms (Player player) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Room> visibleRooms = new ArrayList<>();
        Square playerSquare = player.getPosition();
        visibleRooms.add(playerSquare.getRoom());

        for(CardinalPoint point : CardinalPoint.values()) {
            if(playerSquare.getNearbyAccessibility().get(point) &&
                    playerSquare.getNearbySquares().get(point).getRoom().getColor() != playerSquare.getRoom().getColor()
                    && !visibleRooms.contains(playerSquare.getNearbySquares().get(point).getRoom())) {
                visibleRooms.add(playerSquare.getNearbySquares().get(point).getRoom());
            }
        }
        return visibleRooms;
    }

    /**
     * Gets visible squares from a player
     * @param player of which you want to get the visible squares
     * @return List of visible squares for that player
     */
    public List<Square> getVisibleSquares (Player player) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Square> availableSquares = new ArrayList<>();
        for (Square s : this.arena.getAllSquares()) {
            if (player.getPowerups() != null && player.getPosition().canSee(s) && player.getPosition() != null) {
                availableSquares.add(s);
            }
        }
        return availableSquares;
    }

    /**
     * Gets players with a certain distance from a player position
     * @param square of the reference player
     * @param amount List of distances constraints
     * @return List of players far from the player that distance
     */
    public List<Player> getPlayersByDistance (Square square, List<String> amount) {
        List<Player> validPlayers = new ArrayList<>();
        for(Square s : getSquaresByDistance(square, amount)) {
            validPlayers.addAll(s.getActivePlayers());
        }
        return validPlayers;
    }

    /**
     * Gets players with a certain distance from a reference player. The reference player is removed from the resulting list
     * @param player of which you want to get other players by distance
     * @param amount List of distances constraints
     * @return List of players far from the player that distance
     */
    public List<Player> getPlayersByDistance (Player player, List<String> amount) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Player> validPlayers = new ArrayList<>();
        if (amount.size() == 1 && amount.get(0).equals("0")) {
            validPlayers.addAll(player.getPosition().getActivePlayers());
        }
        else {
            for(Square s : getSquaresByDistance(player.getPosition(), amount)) {
                validPlayers.addAll(s.getActivePlayers());
            }
        }
        return validPlayers;
    }

    /**
     * Gets squares with a certain distance from the square
     * @param square from which you want to get other squares
     * @param amount List of distances constraints
     * @return List of squares far from the reference square that distance
     */
    public List<Square> getSquaresByDistance (Square square, List<String> amount) {
        List<Square> availableSquares = new ArrayList<>();
        if (amount.size() == 1) {
            for (Square s : this.arena.getAllSquares()) {
                if (square.minimumDistanceFrom(s) == Integer.parseInt(amount.get(0))) {
                    availableSquares.add(s);
                }
            }
        }
        if (amount.size() == 2) {
            if (amount.contains("MAX")) {
                for(Square s: this.arena.getAllSquares()) {
                    if(square.minimumDistanceFrom(s) >= Integer.parseInt(amount.get(0))) {
                        availableSquares.add(s);
                    }
                }
            }
            else {
                for(Square s: this.arena.getAllSquares()) {
                    if(square.minimumDistanceFrom(s) >= Integer.parseInt(amount.get(0))
                            && square.minimumDistanceFrom(s) <= Integer.parseInt(amount.get(1))) {
                        availableSquares.add(s);
                    }
                }
            }
        }
        return availableSquares;
    }

    Map<Integer, List<GameCharacter>> getKillshotTrack() {
        return new HashMap<>(this.killshotTrack);
    }

    /**
     * Performs an attack to a player
     * @param player GameCharacter of the attacker
     * @param target GameCharacter of the player to be attacked
     * @param damage amount of damage to deal
     * @param type of the effect used during attack
     */
    public void attackPlayer(GameCharacter player, GameCharacter target, int damage, EffectType type) {
        int marks;
        boolean notify = true;
        switch (type) {
            case DAMAGE:
                getPlayerByCharacter(target).addDamages(player, damage);
                marksToDamages(target, player);
                break;
            case MARK:
                if (getPlayerByCharacter(target).getMarksNumber(player) == 3) {
                    marks = 0;
                    notify = false;
                } else if (getPlayerByCharacter(target).getMarksNumber(player) + damage > 3) {
                    marks = 3 - getPlayerByCharacter(target).getMarksNumber(player);
                } else {
                    marks = damage;
                }
                if (marks != 0) {
                    getPlayerByCharacter(target).addRevengeMarks(player, marks);
                }
                break;
            default:
                break;
        }
        if (notify) {
            notifyChanges(new AttackMessage(target, player, damage, type));
        }
        if (getPlayerByCharacter(target).getDamages().size() >= 11) {
            handleDeadPlayer(target);
        }
    }

    /**
     * Converts marks given to a player into damages
     * @param player of which you want to convert marks
     * @param attacker player of which marks need to be converted
     */
    void marksToDamages(GameCharacter player, GameCharacter attacker) {
        Player p = getPlayerByCharacter(player);
        boolean available = false;
        for (GameCharacter mark : p.getRevengeMarks()) {
            if (mark == attacker) {
                available = true;
                break;
            }
        }
        if (!available) {
            return;
        }
        for (GameCharacter c : p.getRevengeMarks()) {
            if(p.getDamages().size() < Player.MAX_DAMAGES && c == attacker) {
                p.addDamages(attacker, 1);
            }
        }
        p.resetMarks(attacker);
        notifyChanges(new MarksToDamagesMessage(player, attacker));
    }

    /**
     * Handles a player death
     * @param character of which you want to handle the death
     */
    void handleDeadPlayer(GameCharacter character) {
        Player player = getPlayerByCharacter(character);

        player.setDead(true);
        this.deadPlayers.add(player.getCharacter());

        notifyChanges(new PlayerMessage(PlayerMessageType.DEATH, character));
        if (this.skulls != -1) {
            this.skulls--;
        }

        if (this.finalFrenzyOrder.isEmpty()) {
            GameCharacter kill = player.getDamages().get(10);
            GameCharacter overkill = null;
            try {
                overkill = player.getDamages().get(11);
            } catch (IndexOutOfBoundsException e) {
                // Ignore
            }
            List<GameCharacter> killsToAdd = new ArrayList<>(this.killshotTrack.get(this.skulls + 1));
            killsToAdd.add(kill);
            if (overkill != null) {
                killsToAdd.add(overkill);
            }
            this.killshotTrack.put(this.skulls + 1, killsToAdd);

            notifyChanges(new KillshotTrackMessage(this.skulls + 1, killsToAdd));
        }

        notifyChanges(new PlayerMessage(PlayerMessageType.FIRST_BLOOD, player.getDamages().get(0)));

        getPlayerByCharacter(player.getDamages().get(0)).raiseScore(1);

        calculateBoardScore(player);

        player.resetDamages();

        if (this.finalFrenzyOrder.isEmpty()) {
            player.reduceKillshotPoints();
            notifyChanges(new PlayerMessage(PlayerMessageType.KILLSHOT_POINTS, player.getCharacter()));
        } else {
            player.flipBoard();
            notifyChanges(new PlayerMessage(PlayerMessageType.BOARD_FLIP, player.getCharacter()));
        }
    }

    /**
     * Gets squares with a certain distance from a player position
     * @param player of which you want to get squares by distance
     * @param amount List of distances constraints
     * @return List of squares far from the player that distance
     */
    public List<Square> getSquaresByDistance (Player player, List<String> amount) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        return getSquaresByDistance(player.getPosition(), amount);
    }

    /**
     * Gets players on a cardinal direction from a reference player
     * @param activePlayer of which you want to get players on a cardinal direction
     * @param cardinalPoint direction of where you want to get players
     * @return List of players on that direction from the reference player position
     */
    public List<Player> getPlayersOnCardinalDirection(Player activePlayer, CardinalPoint cardinalPoint) {
        if (activePlayer.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Player> result = new ArrayList<>();
        switch (cardinalPoint) {
            case NORTH:
                for (Player p : this.players) {
                    if (p.getPosition() != null && p.getPosition().getX() == activePlayer.getPosition().getX()
                            && p.getPosition().getY() < activePlayer.getPosition().getY()) {
                        result.add(p);
                    }
                }
                return result;

            case SOUTH:
                for (Player p : this.players) {
                    if (p.getPosition() != null && p.getPosition().getX() == activePlayer.getPosition().getX()
                            && p.getPosition().getY() > activePlayer.getPosition().getY()) {
                        result.add(p);
                    }
                }
                return result;

            case EAST:
                for (Player p : this.players) {
                    if (p.getPosition() != null && p.getPosition().getY() == activePlayer.getPosition().getY()
                            && p.getPosition().getX() > activePlayer.getPosition().getX()) {
                        result.add(p);
                    }
                }
                return result;

            case WEST:
                for (Player p : this.players) {
                    if (p.getPosition() != null && p.getPosition().getY() == activePlayer.getPosition().getY()
                            && p.getPosition().getX() < activePlayer.getPosition().getX()) {
                        result.add(p);
                    }
                }
                return result;
        }
        return result;
    }

    /**
     * Gets players on a cardinal direction from a reference square
     * @param square of which you want to get players on a cardinal direction
     * @param cardinalPoint of you want to get players
     * @return List of player on that direction from the reference square
     */
    public List<Player> getPlayersOnCardinalDirection(Square square, CardinalPoint cardinalPoint) {
        List<Player> validPlayers = new ArrayList<>();

        for(Square s : getSquaresOnCardinalDirection(square, cardinalPoint)){
            validPlayers.addAll(s.getActivePlayers());
        }
        return validPlayers;
    }

    /**
     * Gets squares on a cardinal direction from a reference square
     * @param square of which you want to get squares on a cardinal direction
     * @param cardinalPoint of you want to get squares
     * @return List of squares on that direction from the reference square
     */
    public List<Square> getSquaresOnCardinalDirection(Square square, CardinalPoint cardinalPoint) {
        List<Square> squares = new ArrayList<>();

        switch(cardinalPoint) {
            case NORTH:
                for(Square s : this.getArena().getAllSquares()) {
                    if(s.getX() == square.getX()
                            && s.getY() < square.getY()) {
                        squares.add(s);
                    }
                }
                return squares;

            case SOUTH:
                for(Square s : this.getArena().getAllSquares()) {
                    if(s.getX() == square.getX()
                            && s.getY() > square.getY()) {
                        squares.add(s);
                    }

                }
                return squares;

            case EAST:
                for(Square s : this.getArena().getAllSquares()) {
                    if(s.getY() == square.getY()
                            && s.getX() > square.getX()) {
                        squares.add(s);
                    }
                }
                return squares;


            case WEST:
                for(Square s : this.getArena().getAllSquares()) {
                    if (s.getY() == square.getY()
                            && s.getX() < square.getX()) {
                        squares.add(s);
                    }
                }
                return squares;
        }
        return squares;
    }

    /**
     * Gets visible squares on a cardinal direction from a reference player
     * @param player of which you want to get visible squares on a cardinal direction
     * @param cardinalPoint of you want to get squares
     * @return List of visible squares on that direction from the reference player
     */
    public List<Square> getVisibleSquaresOnCardinalDirection(Player player, CardinalPoint cardinalPoint) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Square> squares = new ArrayList<>();
        int x = player.getPosition().getX();
        int y = player.getPosition().getY();
        switch (cardinalPoint) {
            case EAST:
                for(int i = x + 1; i <= 3; i++) {
                    Square square = this.arena.getSquareByCoordinate(i, y);
                    if(square != null && player.getPosition().canSee(square)) {
                        squares.add(square);
                    }
                }
                break;
            case WEST:
                for(int i = x - 1; i >= 0; i--) {
                    Square square = this.arena.getSquareByCoordinate(i, y);
                    if(square != null && player.getPosition().canSee(square)) {
                        squares.add(square);
                    }
                }
                break;
            case NORTH:
                for(int i = y - 1; i >= 0; i--) {
                    Square square = this.arena.getSquareByCoordinate(x, i);
                    if(square != null && player.getPosition().canSee(square)) {
                        squares.add(square);
                    }
                }
                break;
            case SOUTH:
                for(int i = y + 1; i <= 3; i++) {
                    Square square = this.arena.getSquareByCoordinate(x, i);
                    if(square != null && player.getPosition().canSee(square)) {
                        squares.add(square);
                    }
                }
                break;
        }
        return squares;
    }

    /**
     * Gets not visible players from a reference player
     * @param player of which you want to get not visible players
     * @return List of not visible players from the player
     */
    public List<Player> getNotVisiblePlayers(Player player) {
        List<Player> validPlayers = new ArrayList<>(this.players);
        validPlayers.removeAll(getVisiblePlayers(player));
        return validPlayers;
    }

    /**
     * Gets the cardinal direction of a square from a reference square
     * @param square1 the reference square
     * @param square2 the other square
     * @return the cardinal direction of the other square from the reference square
     */
    public CardinalPoint getCardinalFromSquares(Square square1, Square square2) {
        for(CardinalPoint cardinal : CardinalPoint.values()){
            if(square1.getNearbySquares().get(cardinal) != null && square1.getNearbySquares().get(cardinal).equals(square2)) {
                return cardinal;
            }
        }
        return null;
    }

    /**
     * Sets on pause the turn utilsTimer
     */
    public void pauseTurnTimer() {
        this.gameTimer.pause();
    }

    /**
     * Resumes the turn utilsTimer
     */
    public void resumeTurnTimer() {
        this.gameTimer.resume();
    }
}
