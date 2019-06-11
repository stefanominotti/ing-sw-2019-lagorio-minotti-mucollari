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
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Board extends Observable {

    private static final int MAX_WEAPONS_STORE = 3;

    private long startTimer;
    private long turnTimer;
    private long respawnTimer;
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
    private List<Player> finalFrenzyOrder;
    private List<Player> deathPlayers;

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
                track, playerBoards, playerWeapons, player.getPowerups(), player.getScore(), otherPlayers));
    }

    public GameState getGameState() {
        return this.gameState;
    }

    private void notifyChanges(Object object) {
        setChanged();
        notifyObservers(object);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public List<Player> getValidPlayers() {
        List<Player> validPlayers = new ArrayList<>();
        for (Player p : this.players) {
            if (p.getNickname() != null) {
                validPlayers.add(p);
            }
        }
        return validPlayers;
    }

    public List<Player> getAvilablePlayers() {
        List<Player> players = new ArrayList<>();
        for (Player p : this.players) {
            if(p.getPosition() != null) {
                players.add(p);
            }
        }
        return players;
    }

    public List<GameCharacter> getValidCharacters() {
        List<GameCharacter> validCharacters = new ArrayList<>();
        for (Player p : getValidPlayers()) {
            validCharacters.add(p.getCharacter());
        }
        return validCharacters;
    }

    public Player getPlayerByCharacter(GameCharacter character) {
        for (Player player : this.players) {
            if (player.getCharacter() == character) {
                return player;
            }
        }
        return null;
    }

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

        if (getValidPlayers().size() > 3) {
            long remainingTime = this.startTimer /1000L -
                    Duration.between(this.gameTimerStartDate, LocalDateTime.now()).getSeconds();
            notifyChanges(new TimerMessage(TimerMessageType.UPDATE, TimerType.SETUP, remainingTime));
        }

        if (getValidPlayers().size() == 3) {
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

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

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
                if (this.gameTimerStartDate != null && getValidPlayers().size() == 2) {
                    this.timer.cancel();
                    this.gameTimerStartDate = null;
                    this.timer = new Timer();
                    notifyChanges(new TimerMessage(TimerMessageType.STOP, TimerType.SETUP));
                }
                break;
            case SETTINGUPGAME:
                boolean isMaster = false;
                if (getPlayerByCharacter(player) == getValidPlayers().get(0)) {
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
                if (validPlayers < 3) {
                    // termina partita TODO
                }
        }

    }

    public int getSkulls() {
        return this.skulls;
    }

    public void setSkulls(int skulls){
        this.skulls = skulls;
        for (int i=0; i<this.skulls; i++) {
            this.killshotTrack.put(i+1, new ArrayList<>());
        }
        notifyChanges(new PlayerMessage(PlayerMessageType.SKULLS_SET, this.players.get(0).getCharacter()));
    }

    public void loadTimers() {
        String path = System.getProperty("user.home");
        FileReader reader;
        try {
            reader = new FileReader(path + "/" + "server_settings.json");
        } catch (IOException E) {
            this.startTimer = 10L*1000L;
            this.turnTimer = 100L*1000L;
            this.respawnTimer = 30L*1000L;
            return;
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject)parser.parse(reader);

        this.startTimer = gson.fromJson(jsonElement.get("startTimer"), Long.class);
        this.turnTimer = gson.fromJson(jsonElement.get("turnTimer"), Long.class);
        this.respawnTimer = gson.fromJson(jsonElement.get("respawnTimer"), Long.class);
    }

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

    public Arena getArena() {
        return this.arena;
    }

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

    public void addDeadPlayer(Player player) {
        this.deathPlayers.add(player);
    }

    public void endTurn(Player player) {
        if(this.gameState == FIRSTTURN && this.currentPlayer == this.players.size() - 1) {
            this.gameState = INGAME;
        }
        Player nextPlayer;
        if(availableDeathPlayers()) {
            int index = 0;
            do {
                nextPlayer = this.deathPlayers.get(index);
                index++;
            } while (!nextPlayer.isConnected());
        } else {
            incrementCurrentPlayer();
            nextPlayer = this.players.get(this.currentPlayer);
        }
        fillAmmoTiles();
        fillWeaponStores();
        notifyChanges(new TurnMessage(TurnMessageType.END, player.getCharacter()));
        startTurn(nextPlayer);
    }

    public boolean availableDeathPlayers() {
        if (this.deathPlayers.isEmpty()) {
            return false;
        }
        for (Player p : this.deathPlayers) {
            if (p.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void startTurn(Player player) {
        TurnType type;
        if(player.isDead()) {
            type = TurnType.AFTER_DEATH;
        } else if(this.gameState == FIRSTTURN || player.getPosition() == null) {
            type = TurnType.FIRST_TURN;
        } else if(!this.finalFrenzyOrder.isEmpty()) {
            int currentPlayerOrder = 0;
            int firstPlayerOrder = 0;
            for(Player p : this.finalFrenzyOrder) {
                if(p == this.players.get(this.currentPlayer)) {
                    currentPlayerOrder = this.players.indexOf(p);
                }
                if(p == this.players.get(0)) {
                    firstPlayerOrder = this.players.indexOf(p);
                }
            }
            if(currentPlayerOrder < firstPlayerOrder) {
                type = TurnType.FINAL_FRENZY_FIRST;
            } else {
                type = TurnType.FINAL_FRENZY_AFTER;
            }
        } else {
            type = TurnType.NORMAL;
        }
        notifyChanges(new TurnMessage(TurnMessageType.START, type, player.getCharacter()));
    }

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

    public void startTurnTimer(Player player) {
        if (player == this.players.get(this.currentPlayer)) {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    endTurn(player);
                }
            }, this.turnTimer);
        } else {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    endTurn(player);
                }
            }, this.respawnTimer);
        }
        this.gameTimerStartDate = LocalDateTime.now();
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void addFrenzyOrderPlayer(Player player) {
        this.finalFrenzyOrder.add(player);
    }

    private void fillWeaponsDeck() {
        for(Weapon weapon : Weapon.values()) {
            this.weaponsDeck.add(new WeaponCard(weapon));
        }
        Collections.shuffle(this.weaponsDeck);
    }

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

    public void removePowerup(Player player, Powerup powerup) {
        player.removePowerup(player.getPowerupByType(powerup.getType(), powerup.getColor()));
        this.powerupsDiscardPile.add(powerup);
        notifyChanges(new PowerupMessage(PowerupMessageType.DISCARD, player.getCharacter(), powerup));
    }

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

    public void switchWeapon(Player player, WeaponCard oldCard, WeaponCard newCard) {
        player.removeWeapon(oldCard);
        player.getPosition().removeWeapon(newCard);
        oldCard.setReady(true);
        player.getPosition().addWeapon(oldCard);
        player.addWeapon(newCard);
        notifyChanges(new WeaponSwitchMessage(newCard.getWeaponType(), oldCard.getWeaponType(), player.getCharacter()));
    }

    public void loadWeapon(Player player, WeaponCard weapon) {
        for (WeaponCard w : player.getWeapons()) {
            if (w == weapon) {
                w.setReady(true);
                notifyChanges(new WeaponMessage(WeaponMessageType.RELOAD, w.getWeaponType(), player.getCharacter()));
                return;
            }
        }
    }

    public void giveWeapon(Player player, WeaponCard weapon) {
        player.getPosition().removeWeapon(weapon);
        player.addWeapon(weapon);
        notifyChanges(new WeaponMessage(WeaponMessageType.PICKUP, weapon.getWeaponType(), player.getCharacter()));
    }

    public void giveAmmoTile(Player player, AmmoTile tile) {
        if(tile.hasPowerup() && player.getPowerups().size() < 3) {
            drawPowerup(player);
        }
        Map<AmmoType, Integer> addedAmmos = player.addAmmos(tile.getAmmos());
        this.ammosDiscardPile.add(tile);
        player.getPosition().removeAmmoTile();
        notifyChanges(new AmmosMessage(AmmosMessageType.ADD, player.getCharacter(), addedAmmos));
    }

    public void useAmmos(Player player, Map<AmmoType, Integer> usedAmmos) {
        player.removeAmmos(usedAmmos);
        notifyChanges(new AmmosMessage(AmmosMessageType.REMOVE, player.getCharacter(), usedAmmos));
    }

    public void movePlayer(Player player, Square square) {
        if(player.getPosition() != null) {
            player.getPosition().removePlayer(player);
        }
        square.addPlayer(player);
        player.setPosition(square);
        notifyChanges(new MovementMessage(player.getCharacter(), new Coordinates(square.getX(), square.getY())));
    }

    public void respawnPlayer(Player player, Room room) {
        Square square = room.getSpawn();
        player.setPosition(square);
        square.addPlayer(player);
        notifyChanges(new SpawnMessage(player.getCharacter(),
                new Coordinates(square.getX(), square.getY())));
    }

    public boolean verifyGameFinished() {
        return this.skulls == 0;
    }

    void startFinalFrenzy(Player player) {
        int index = this.players.indexOf(player);
        for (int i=index+1; i<this.players.size(); i++) {
            this.finalFrenzyOrder.add(this.players.get(i));
        }
        for (int i=0; i<=index; i++) {
            this.finalFrenzyOrder.add(this.players.get(i));
        }

        for (Player p : this.players) {
            if (p.getDamages().isEmpty()) {
                p.flipBoard();

                // notify TODO
            }
        }

        // notify TODO
    }

    public void raisePlayerScore(Player p, int score) {
        p.raiseScore(score);
        notifyChanges(new ScoreMessage(p.getCharacter(), p.getScore()));
    }

    public List<Player> getVisiblePlayers (Player player) {

        List<Player> visiblePlayers = new ArrayList<>();
        for(Square s : this.arena.getAllSquares()){
            for (Player p : this.players){
                if(p.getPosition() == s && p != player) {
                    if (player.getPosition().canSee(p.getPosition())){
                        visiblePlayers.add(p);
                    }
                }
            }
        }
        return visiblePlayers;
    }

    public List<Room> getVisibleRooms (Player player) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        List<Room> visibleRooms = new ArrayList<>();
        Square playerSquare = player.getPosition();
        visibleRooms.add(playerSquare.getRoom());

        for(CardinalPoint point : CardinalPoint.values()) {
            if(playerSquare.getNearbyAccessibility().get(point)) {
                if(playerSquare.getNearbySquares().get(point).getRoom().getColor() != playerSquare.getRoom().getColor()
                        && !visibleRooms.contains(playerSquare.getNearbySquares().get(point).getRoom())){
                    visibleRooms.add(playerSquare.getNearbySquares().get(point).getRoom());
                }
            }
        }
        return visibleRooms;
    }

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

    //Usare se il target e' uno Square
    public List<Player> getPlayersByDistance (Square square, List<String> amount) {
        List<Player> validPlayers = new ArrayList<>();
        for(Square s : getSquaresByDistance(square, amount)) {
            validPlayers.addAll(s.getActivePlayers());
        }
        return validPlayers;
    }

    //Usare se il target e' un Player
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

    public void attackPlayer(GameCharacter player, GameCharacter target, int damage, EffectType type) {
        switch (type) {
            case DAMAGE:
                getPlayerByCharacter(target).addDamages(player, damage);
                break;
            case MARK:
                getPlayerByCharacter(target).addRevengeMarks(player, damage);
                break;
        }
        notifyChanges(new AttackMessage(target, player, damage, type));
    }

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

    public List<Square> getSquaresByDistance (Player player, List<String> amount) {
        if (player.getPosition() == null) {
            return new ArrayList<>();
        }
        return getSquaresByDistance(player.getPosition(), amount);
    }

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

    public List<Player> getPlayersOnCardinalDirection(Square square, CardinalPoint cardinalPoint) {
        List<Player> validPlayers = new ArrayList<>();

        for(Square s : getSquaresOnCardinalDirection(square, cardinalPoint)){
            validPlayers.addAll(s.getActivePlayers());
        }
        return validPlayers;
    }

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

    public List<Player> getNotVisiblePlayers(Player player) {
        List<Player> validPlayers = new ArrayList<>(this.players);
        validPlayers.removeAll(getVisiblePlayers(player));
        return validPlayers;
    }

    public CardinalPoint getCardinalFromSquares(Square square1, Square square2) {
        for(CardinalPoint cardinal : CardinalPoint.values()){
            if(square1.getNearbySquares().get(cardinal) != null && square1.getNearbySquares().get(cardinal).equals(square2)) {
                return cardinal;
            }
        }
        return null;
    }
}
