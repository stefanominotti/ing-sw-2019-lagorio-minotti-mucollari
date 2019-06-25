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
import it.polimi.se2019.model.messages.timer.TimerMessage;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import it.polimi.se2019.model.messages.timer.TimerType;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import it.polimi.se2019.model.messages.weapon.WeaponMessage;
import it.polimi.se2019.model.messages.weapon.WeaponMessageType;
import it.polimi.se2019.model.messages.weapon.WeaponSwitchMessage;
import it.polimi.se2019.view.PlayerBoard;
import it.polimi.se2019.view.SquareView;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static it.polimi.se2019.model.GameState.*;
import static it.polimi.se2019.model.PowerupType.TAGBACK_GRENADE;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Board extends Observable {

    private static final int MAX_WEAPONS_STORE = 3;
    private static final int MAX_POWERUPS = 3;
    private static final long DEFAULT_START_TIMER = 10L*1000L;
    private static final long DEFAULT_TURN_TIMER = 100L*1000L;
    private static final long DEFAULT_RESPAWN_TIMER = 30L*1000L;
    private static final long DEFAULT_POWERUPS_TIMER = 10L*1000L;
    private static final int MIN_PLAYERS = 3;

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
    private Timer timer;
    private LocalDateTime gameTimerStartDate;
    private int currentPlayer;
    private List<GameCharacter> finalFrenzyOrder;
    private List<GameCharacter> deathPlayers;
    private long timerRemainingTime;

    /**
     * Class constructor, it builds the board
     */
    public Board() {
        this.gameState = ACCEPTINGPLAYERS;
        this.players = new ArrayList<>();
        this.weaponsDeck = new ArrayList<>();
        this.powerupsDeck = new ArrayList<>();
        this.ammosDeck = new ArrayList<>();
        this.powerupsDiscardPile = new ArrayList<>();
        this.ammosDiscardPile = new ArrayList<>();
        this.killshotTrack = new HashMap<>();
        this.timer = new Timer();
        this.deathPlayers = new ArrayList<>();
        this.finalFrenzyOrder = new ArrayList<>();
        loadTimers();
    }

    /**
     * Writes board data to JSON
     */
    public String toJson() {
        Gson gson = new Gson();
        StringBuilder jObject = new StringBuilder("{");
        jObject.append("\"skulls\": " + this.skulls + ",");
        jObject.append("\"gameState\": " + "\"" + this.gameState + "\"" + ",");
        jObject.append("\"currentPlayer\": " + this.currentPlayer + ",");
        jObject.append("\"weaponsDeck\": " + gson.toJson(this.weaponsDeck) + ',');
        jObject.append("\"powerupsDeck\": " + gson.toJson(this.powerupsDeck) + ',');
        jObject.append("\"ammosDeck\": " + gson.toJson(this.ammosDeck) + ',');
        jObject.append("\"powerupsDiscardPile\": " + gson.toJson(this.powerupsDiscardPile) + ',');
        jObject.append("\"ammosDiscardPile\": " + gson.toJson(this.ammosDiscardPile) + ',');
        jObject.append("\"killshotTrack\": " + gson.toJson(this.killshotTrack));
        jObject.append("},");
        jObject.append("\"finalFrenzyOrder\":" + gson.toJson(this.finalFrenzyOrder));
        return jObject.toString();
    }

    /**
     * Creates model and view for a player
     * @param player of which you want to create model and view
     */
    public void  createModelView(Player player) {
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
        Map<GameCharacter, String> otherPlayers = new HashMap<>();
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


        notifyChanges(new LoadViewMessage(player.getCharacter(), player.getNickname(), this.skulls, squareViews,
                track, playerBoards, playerWeapons, player.getPowerups(), player.getScore(), otherPlayers,
                !this.finalFrenzyOrder.isEmpty(), isPlayerBeforeFirst(player)));
    }




    /**
     * Gets status of the game
     * @return game state
     */
    public GameState getGameState() {
        return this.gameState;
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
     * Gets powerups timer duration
     * @return Powerups timer duration
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
            if(p.getPosition() != null) {
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
            if (p.getNickname() != null && p.getCharacter() != character) {
                others.put(p.getCharacter(), p.getNickname());
            }
        }
        notifyChanges(new PlayerCreatedMessage(character, nickname, others));

        if (this.players.size() > 3) {
            long remainingTime = this.startTimer /1000L -
                    Duration.between(this.gameTimerStartDate, LocalDateTime.now()).getSeconds();
            notifyChanges(new TimerMessage(TimerMessageType.UPDATE, TimerType.SETUP, remainingTime));
        }

        if (this.players.size() == 3) {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finalizePlayersCreation();
                }
            }, this.startTimer);
            notifyChanges(new TimerMessage(TimerMessageType.START, TimerType.SETUP,this.startTimer /1000L));
            this.gameTimerStartDate = LocalDateTime.now();
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
    private void finalizePlayersCreation() {
        this.gameState = SETTINGUPGAME;
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
     * @param player you want to handle disconnection
     */
    public void handleDisconnection(GameCharacter player) {
        if (getPlayerByCharacter(player).getNickname() == null) {
            this.players.remove(getPlayerByCharacter(player));
            notifyChanges(new ClientDisconnectedMessage(player, true));
            return;
        }
        switch(this.gameState) {
            case ACCEPTINGPLAYERS:
                if (getPlayerByCharacter(player).getNickname() != null) {
                    notifyChanges(new ClientDisconnectedMessage(player, true));
                }
                this.players.remove(getPlayerByCharacter(player));
                if (this.gameTimerStartDate != null && this.players.size() == 2) {
                    this.timer.cancel();
                    this.gameTimerStartDate = null;
                    this.timer = new Timer();
                    notifyChanges(new TimerMessage(TimerMessageType.STOP, TimerType.SETUP));
                }
                break;
            case SETTINGUPGAME:
                boolean isMaster = false;
                if (getPlayerByCharacter(player) == this.players.get(0)) {
                    isMaster = true;
                }
                this.players.remove(getPlayerByCharacter(player));
                notifyChanges(new ClientDisconnectedMessage(player, true));
                if (this.players.size() == 2) {
                    this.gameState = ACCEPTINGPLAYERS;
                    this.gameTimerStartDate = null;
                    this.timer = new Timer();
                    notifyChanges(new BoardMessage(BoardMessageType.SETUP_INTERRUPTED));
                    break;
                }
                if (isMaster) {
                    notifyChanges(new PlayerMessage(PlayerMessageType.MASTER_CHANGED,
                            this.players.get(0).getCharacter()));
                }
                break;
            default:
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
                    endGame();
                    // termina partita TODO
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
        // this.skulls = skulls; TODO
        this.skulls = 1;
        for (int i=0; i<this.skulls; i++) {
            this.killshotTrack.put(i + 1, new ArrayList<>());
        }
        notifyChanges(new PlayerMessage(PlayerMessageType.SKULLS_SET, this.players.get(0).getCharacter()));
    }

    /**
     * Loads timers from server settings file or default if it's not available
     */
    public void loadTimers() {
        String path = System.getProperty("user.home");
        FileReader reader;
        try {
            reader = new FileReader(path + "/" + "server_settings.json");
        } catch (IOException E) {
            this.startTimer = DEFAULT_START_TIMER;
            this.turnTimer = DEFAULT_TURN_TIMER;
            this.respawnTimer = DEFAULT_RESPAWN_TIMER;
            this.powerupsTimer = DEFAULT_POWERUPS_TIMER;
            return;
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject)parser.parse(reader);

        try {
            this.startTimer = gson.fromJson(jsonElement.get("startTimer"), Long.class);
            this.turnTimer = gson.fromJson(jsonElement.get("turnTimer"), Long.class);
            this.respawnTimer = gson.fromJson(jsonElement.get("respawnTimer"), Long.class);
            this.powerupsTimer = gson.fromJson(jsonElement.get("powerupsTimer"), Long.class);
        } catch (ClassCastException | NullPointerException e) {
            this.startTimer = DEFAULT_START_TIMER;
            this.turnTimer = DEFAULT_TURN_TIMER;
            this.respawnTimer = DEFAULT_RESPAWN_TIMER;
            this.powerupsTimer = DEFAULT_POWERUPS_TIMER;
        }
    }

    /**
     * Creates an arena by its ID
     * @param arenaNumber number of the arena you want to build
     */
    public void createArena(String arenaNumber) {
        this.arena = new Arena(arenaNumber);
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
        notifyChanges(new GameSetMessage(this.skulls, Integer.parseInt(arenaNumber), arenaColor, arenaSpawn, nearbyAccessibility));
        finalizeGameSetup();
    }

    /**
     * Loads the arena by its ID
     * @param arenaNumber number of the Arena you want to load
     */
    public void loadArena(String arenaNumber) {
        this.arena = new Arena(arenaNumber);
        Map<Coordinates, RoomColor> arenaColor = new HashMap<>();
        Map<Coordinates, Boolean> arenaSpawn = new HashMap<>();
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                Coordinates coordinates = new Coordinates(square.getX(), square.getY());
                RoomColor color =  room.getColor();
                Boolean spawn = square.isSpawn();
                arenaColor.put(coordinates, color);
                arenaSpawn.put(coordinates, spawn);
            }
        }
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
    private void finalizeGameSetup() {
        fillAmmosDeck();
        fillWeaponsDeck();
        fillPowerupsDeck();
        fillWeaponStores();
        fillAmmoTiles();

        this.gameState = FIRSTTURN;
        this.currentPlayer = 0;
        startTurn(this.players.get(this.currentPlayer));
    }

    /**
     * Adds a dead player
     * @param player you want to make dead
     */
    public void addDeadPlayer(Player player) {
        this.deathPlayers.add(player.getCharacter());
    }

    /**
     * Ends a player turn and reset the board
     * @param character you want him to end turn
     */
    public void endTurn(GameCharacter character) {
        Player player = getPlayerByCharacter(character);
        this.timer.cancel();
        if(this.gameState == FIRSTTURN && this.currentPlayer == this.players.size() - 1) {
            this.gameState = INGAME;
        }

        if (player.isDead()) {
            Room room =
                    getArena().getRoomByColor(RoomColor.valueOf(player.getPowerups().get(0).getColor().toString()));

            removePowerup(player, player.getPowerups().get(0));
            respawnPlayer(player, room);
        }

        Player nextPlayer = null;
        for (GameCharacter c : this.deathPlayers) {
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
     * Knows if there are any dead players
     * @return true if are there, else false
     */
    public boolean availableDeathPlayers() {
        if (this.deathPlayers.isEmpty()) {
            return false;
        }
        for (GameCharacter c : this.deathPlayers) {
            Player p = getPlayerByCharacter(c);
            if (p.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Starts a player turn
     * @param player you want to start turn
     */
    public void startTurn(Player player) {
        TurnType type;
        if(player.isDead()) {
            type = TurnType.AFTER_DEATH;
        } else if(this.gameState == FIRSTTURN || player.getPosition() == null) {
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
    private void incrementCurrentPlayer() {
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
     * Starts a player turn timer
     * @param character you want to start turn timer
     */
    public void startTurnTimer(GameCharacter character) {
        this.timer = new Timer();
        if (character == this.players.get(this.currentPlayer).getCharacter()) {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    endTurn(character);
                }
            }, this.turnTimer);
            this.gameTimerStartDate = LocalDateTime.now();
        } else {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    endTurn(character);
                }
            }, this.respawnTimer);
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
     * Adds a player to Final Frenzy players list
     * @param player you want to add to the list
     */
    public void addFrenzyOrderPlayer(Player player) {
        this.finalFrenzyOrder.add(player.getCharacter());
    }

    /**
     * Fills the weapons deck with a random weapon
     */
    private void fillWeaponsDeck() {
        for(Weapon weapon : Weapon.values()) {
            this.weaponsDeck.add(new WeaponCard(weapon));
        }
        Collections.shuffle(this.weaponsDeck);
    }

    /**
     * Fills the powerups deck with a random powerup
     */
    private void fillPowerupsDeck() {
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
     * Fills the ammos deck loading data from JSON
     */
    private void fillAmmosDeck() {
        if(this.ammosDiscardPile.isEmpty()) {
            String path = "ammotiles/data/ammotile_";
            int ammosNumber;
            ClassLoader classLoader = getClass().getClassLoader();
            Gson gson = new Gson();
            for(ammosNumber = 1; ammosNumber < 13; ammosNumber++) {
                InputStream inputStream = classLoader.getResourceAsStream(path + ammosNumber + ".json");
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
     * Fills the weapons store
     */
    private void fillWeaponStores() {
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
                    // square.addWeapon(this.weaponsDeck.get(0)); TODO
                    square.addWeapon(new WeaponCard(Weapon.LOCK_RIFLE));
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
    private void fillAmmoTiles() {
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
     * Draws a powerup from powerups deck
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
        this.deathPlayers.remove(player.getCharacter());
        notifyChanges(new SpawnMessage(player.getCharacter(),
                new Coordinates(square.getX(), square.getY())));
    }

    /**
     * Handles Game Finale
     */
    public void endGame() {
        this.gameState = ENDED;
        List<Integer> points = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1));

        List<GameCharacter> trackReworked = new ArrayList<>();
        for (Map.Entry<Integer, List<GameCharacter>> i : this.killshotTrack.entrySet()) {
            for (GameCharacter p : i.getValue()) {
                trackReworked.add(p);
            }
        }

        Map<GameCharacter, Integer> killsByPlayer = new HashMap<>();
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
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        int index = 0;
        for (GameCharacter p : sorted.keySet()) {
            raisePlayerScore(getPlayerByCharacter(p), points.get(index));
            index++;
        }

        notifyChanges(new BoardMessage(BoardMessageType.GAME_FINISHED));
    }

    /**
     * Handles Final Frenzy mode for a player setting up the his board
     * @param character you want to start Final Frenzy mode
     */
    private void startFinalFrenzy(GameCharacter character) {
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
    public void raisePlayerScore(Player player, int score) {
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
            if(playerSquare.getNearbyAccessibility().get(point) && playerSquare.getNearbySquares().get(point).getRoom().getColor() != playerSquare.getRoom().getColor()
                    && !visibleRooms.contains(playerSquare.getNearbySquares().get(point).getRoom())) {
                visibleRooms.add(playerSquare.getNearbySquares().get(point).getRoom());
            }
        }
        return visibleRooms;
    }

    /**
     * Gets visible squares from a player
     * @param player of which you want to get visible squares
     * @return List of visible squares for that player
     */
    public List<Square> getVisibleSquares (Player player) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Square> availableSquares = new ArrayList<>();
        for (Square s : this.arena.getAllSquares()) {
            if (player.getPosition().canSee(s) && player.getPosition() != null) {
                availableSquares.add(s);
            }
        }
        return availableSquares;
    }

    /**
     * Gets players with a distance from a player position
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
     * Gets players with a distance from the reference player. The reference player is removed from resulting list
     * @param player from which you want to get other players by distance
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
     * Gets squares with a distance from a square
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
                // Ignore
        }
        if (notify) {
            notifyChanges(new AttackMessage(target, player, damage, type));
        }
    }

    /**
     * Converts marks given to a player into damages
     * @param player of which you want to convert marks
     * @param attacker player of which marks need to be converted
     */
    void marksToDamages(GameCharacter player, GameCharacter attacker) {
        Player p = getPlayerByCharacter(player);
        for(GameCharacter c : p.getRevengeMarks()) {
            if(p.getDamages().size() < Player.MAX_DAMAGES && c == attacker) {
                p.addDamages(player, 1);
            }
        }
        p.resetMarks(attacker);
        notifyChanges(new MarksToDamagesMessage(player, attacker));
    }

    /**
     * Handles a player death
     * @param character of which you want to handle the death
     */
    public void handleDeadPlayer(GameCharacter character) {
        Player player = getPlayerByCharacter(character);

        player.setDead(true);
        this.deathPlayers.add(player.getCharacter());

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

        Map<GameCharacter, Integer> damagesByPlayer = new HashMap<>();
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
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
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
     * Gets squares with a distance from a player position
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
     * Gets players on a cardinal direction by the reference player
     * @param activePlayer of which you want to get players on a cardinal direction
     * @param cardinalPoint of you want to get players
     * @return List of player on that direction from the reference player
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
     * Gets players on a cardinal direction by the reference square
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
     * Gets squares on a cardinal direction by the reference square
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
     * Gets visible squares on a cardinal direction by the reference player
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
     * Gets players not visible from the reference player
     * @param player of which you want to get not visible players
     * @return List of not visible players from the player
     */
    public List<Player> getNotVisiblePlayers(Player player) {
        List<Player> validPlayers = new ArrayList<>(this.players);
        validPlayers.removeAll(getVisiblePlayers(player));
        return validPlayers;
    }

    /**
     * Gets the cardinal direction of a square from the reference square
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
     * Sets in pause the turn timer
     */
    public void pauseTurnTimer() {
        this.timerRemainingTime = this.turnTimer/1000L -
                Duration.between(this.gameTimerStartDate, LocalDateTime.now()).getSeconds();
        this.timer.cancel();
    }

    /**
     * Resumes the turn timer
     */
    public void resumeTurnTimer() {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endTurn(Board.this.players.get(Board.this.currentPlayer).getCharacter());
            }
        }, this.timerRemainingTime*1000L);
    }
}
