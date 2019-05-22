package it.polimi.se2019.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.view.PlayerBoard;
import it.polimi.se2019.view.SquareView;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static it.polimi.se2019.model.GameState.*;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Board extends Observable {

    private static final long STARTTIME = 10L*1000L;
    private static final int MAX_WEAPONS_STORE = 3;

    private int skulls;
    private Arena arena;
    private List<Player> players;
    private List<WeaponCard> weaponsDeck;
    private List<Powerup> powerupsDeck;
    private List<AmmoTile> ammosDeck;
    private List<Powerup> powerupsDiscardPile;
    private List<AmmoTile> ammosDiscardPile;
    private List<GameCharacter> killshotTrack;
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
        this.killshotTrack = new ArrayList<>();
        this.timer = new Timer();
        this.deathPlayers = new ArrayList<>();
    }

    public String toJson() {
        Gson gson = new Gson();
        StringBuilder jObject = new StringBuilder("{");
        jObject.append("\"skulls\": " + this.skulls + ",");
        jObject.append("\"gameState\": " + "\"" + this.gameState + "\"" + ",");
        jObject.append("\"currentPlayer\": " + this.currentPlayer + ",");
        jObject.append("\"weaponsDeck\": " + gson.toJson(weaponsDeck) + ',');
        jObject.append("\"powerupsDeck\": " + gson.toJson(powerupsDeck) + ',');
        jObject.append("\"ammosDeck\": " + gson.toJson(ammosDeck) + ',');
        jObject.append("\"powerupsDiscardPile\": " + gson.toJson(powerupsDiscardPile) + ',');
        jObject.append("\"ammosDiscardPile\": " + gson.toJson(ammosDiscardPile) + ',');
        jObject.append("\"killshotTrack\": " + gson.toJson(killshotTrack));
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
            playerBoards.add(new PlayerBoard(character.getCharacter(), character.getNickname(),
                    character.getAvailableAmmos(), character.getRevengeMarks(), character.getDamages(),
                    character.getKillshotPoints(), weapons, character.getWeapons().size(),
                    character.getPowerups().size()));
        }
        notifyChanges(new LoadViewMessage(player.getCharacter(), player.getNickname(), this.skulls, squareViews,
                this.killshotTrack, playerBoards, playerWeapons, player.getPowerups(), player.getScore()));
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

    public void addPlayer(GameCharacter character, String nickname) {
        this.players.add(new Player(character, nickname));
        Map<GameCharacter, String> others = new EnumMap<>(GameCharacter.class);
        for (Player p : this.players) {
            if (p.getNickname() != null && p.getCharacter() != character) {
                others.put(p.getCharacter(), p.getNickname());
            }
        }
        notifyChanges(new PlayerCreatedMessage(character, nickname, others));

        if (getValidPlayers().size() > 3) {
            long remainingTime = STARTTIME/1000L -
                    Duration.between(this.gameTimerStartDate, LocalDateTime.now()).getSeconds();
            notifyChanges(new GameSetupTimerStartedMessage(remainingTime));
        }

        if (getValidPlayers().size() == 3) {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finalizePlayersCreation();
                }
            }, STARTTIME);
            notifyChanges(new GameSetupTimerStartedMessage(STARTTIME/1000L));
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
        notifyChanges(new GameAlreadyStartedMessage());
        notifyChanges(new StartGameSetupMessage(getValidPlayers().get(0).getCharacter()));
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
                    notifyChanges(new GameSetupTimerResetMessage());
                }
                break;
            case SETTINGUPGAME:
                boolean isMaster = false;
                if (getPlayerByCharacter(player) == getValidPlayers().get(0)) {
                    isMaster = true;
                }
                this.players.remove(getPlayerByCharacter(player));
                notifyChanges(new ClientDisconnectedMessage(player, true));
                if (getValidPlayers().size() == 2) {
                    this.gameState = ACCEPTINGPLAYERS;
                    this.gameTimerStartDate = null;
                    this.timer = new Timer();
                    notifyChanges(new GameSetupInterruptedMessage());
                    break;
                }
                if (isMaster) {
                    notifyChanges(new MasterChangedMessage(getValidPlayers().get(0).getCharacter()));
                }
                break;
        }

    }

    public int getSkulls() {
        return this.skulls;
    }

    public void setSkulls(int skulls){
        this.skulls = skulls;
        notifyChanges(new SkullsSetMessage(getValidPlayers().get(0).getCharacter()));
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

        notifyChanges(new ArenaFilledMessage());
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
        if(!this.deathPlayers.isEmpty()) {
            nextPlayer = this.deathPlayers.get(0);
        } else {
            incrementCurrentPlayer();
            nextPlayer = this.players.get(this.currentPlayer);
        }
        notifyChanges(new EndTurnMessage(player.getCharacter()));
        fillAmmoTiles();
        fillWeaponStores();
        startTurn(nextPlayer);
    }

    public void startTurn(Player player) {
        TurnType type;
        if(player.isDead()) {
            type = TurnType.AFTER_DEATH;
        } else if(this.gameState == FIRSTTURN) {
            type = TurnType.FIRST_TURN;
        } else if(this.finalFrenzyOrder != null) {
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
        notifyChanges(new StartTurnMessage(type, player.getCharacter()));
    }

    private void incrementCurrentPlayer() {
        if(this.currentPlayer == this.players.size() - 1) {
            this.currentPlayer = 0;
        } else {
            this.currentPlayer++;
        }
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void addFrenzyOrderPlayer(Player player) {
        this.finalFrenzyOrder.add(player);
    }

    protected void fillWeaponsDeck() {
        for(Weapon weapon : Weapon.values()) {
            this.weaponsDeck.add(new WeaponCard(weapon));
        }
        Collections.shuffle(this.weaponsDeck);
    }

    protected void fillPowerupsDeck() {
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

    protected void fillAmmosDeck() {
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

    public void fillWeaponStores() {
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

    public void fillAmmoTiles() {
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
        notifyChanges(new PowerupRemoved(player.getCharacter(), powerup));
    }

    public void drawPowerup(Player player) {
        if (this.powerupsDeck.isEmpty()) {
            fillPowerupsDeck();
        }
        Powerup powerup = this.powerupsDeck.get(0);
        player.addPowerup(powerup);
        this.powerupsDeck.remove(powerup);
        notifyChanges(new PowerupDrawnMessage(player.getCharacter(), powerup));
        notifyChanges(new PowerupDrawnMessage(player.getCharacter(), null));
    }

    public void switchWeapon(Player player, WeaponCard oldCard, WeaponCard newCard) {
        player.removeWeapon(oldCard);
        player.getPosition().removeWeapon(newCard);
        oldCard.setReady(true);
        player.getPosition().addWeapon(oldCard);
        player.addWeapon(newCard);
        notifyChanges(new WeaponsSwitchedMessage(player.getCharacter(), oldCard.getWeaponType(), newCard.getWeaponType()));
    }

    public void loadWeapon(Player player, WeaponCard weapon) {
        for (WeaponCard w : player.getWeapons()) {
            if (w == weapon) {
                w.setReady(true);
                notifyChanges(new RechargeWeaponMessage(w.getWeaponType(), player.getCharacter()));
                return;
            }
        }
    }

    public void giveWeapon(Player player, WeaponCard weapon) {
        player.getPosition().removeWeapon(weapon);
        player.addWeapon(weapon);
        notifyChanges(new WeaponGivenMessage(player.getCharacter(), weapon.getWeaponType()));
    }

    public void giveAmmoTile(Player player, AmmoTile tile) {
        if(tile.hasPowerup() && player.getPowerups().size() < 3) {
            drawPowerup(player);
        }
        Map<AmmoType, Integer> addedAmmos = player.addAmmos(tile.getAmmos());
        this.ammosDiscardPile.add(tile);
        player.getPosition().removeAmmoTile();
        notifyChanges(new AmmosGivenMessage(player.getCharacter(), addedAmmos));
    }

    public void useAmmos(Player player, Map<AmmoType, Integer> usedAmmos) {
        player.removeAmmos(usedAmmos);
        notifyChanges(new AmmosUsedMessage(player.getCharacter(), usedAmmos));
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
        notifyChanges(new PlayerSpawnedMessage(player.getCharacter(),
                new Coordinates(square.getX(), square.getY())));
    }

    public boolean verifyGameFinished() {
        return this.skulls == 0;
    }

    public void raisePlayerScore(Player p, int score) {
        p.raiseScore(score);
        notifyChanges(new ScoreMessage(p.getCharacter(), p.getScore()));
    }

    public List<Player> getPlayersOnCardinalDirection(Player activePlayer, CardinalPoint cardinalPoint) {
        List<Player> result = new ArrayList<>();
        switch (cardinalPoint) {
            case NORTH:
                for (Player p : this.players) {
                    if (p.getPosition().getX() == activePlayer.getPosition().getX()
                            && p.getPosition().getY() <= activePlayer.getPosition().getY()
                            && p != activePlayer) {
                        result.add(p);
                    }
                }
                return result;

            case SOUTH:
                for (Player p : this.players) {
                    if (p.getPosition().getX() == activePlayer.getPosition().getX()
                            && p.getPosition().getY() >= activePlayer.getPosition().getY()
                            && p != activePlayer) {
                        result.add(p);
                    }
                }
                return result;

            case EAST:
                for (Player p : this.players) {
                    if (p.getPosition().getY() == activePlayer.getPosition().getY()
                            && p.getPosition().getX() >= activePlayer.getPosition().getX()
                            && p != activePlayer) {
                        result.add(p);
                    }
                }
                return result;

            case WEST:
                for (Player p : this.players) {
                    if (p.getPosition().getY() == activePlayer.getPosition().getY()
                            && p.getPosition().getX() <= activePlayer.getPosition().getX()
                            && p != activePlayer) {
                        result.add(p);
                    }
                }
                return result;
        }
        return result;
    }
}
