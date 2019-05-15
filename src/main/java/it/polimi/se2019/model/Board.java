package it.polimi.se2019.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.model.messages.*;

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
    private List<Player> killshotTrack;
    private GameState gameState;
    private Timer timer;
    private LocalDateTime gameTimerStartDate;
    private int currentPlayer;
    private List<Player> finalFrenzyOrder;

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

    public void addPlayer(GameCharacter character) {
        this.players.add(new Player(character));
        notifyChanges(new PlayerCreatedMessage(character));
    }

    public void setPlayerNickname(GameCharacter player, String name) {
        if (getPlayerByCharacter(player) == null) {
            notifyChanges(new GameAlreadyStartedMessage(player));
            return;
        }
        for (Player p : this.players) {
            if (p.getCharacter() == player || p.getNickname() == null) {
                continue;
            }
            if (p.getNickname().equals(name)) {
                notifyChanges(new NicknameDuplicatedMessage(player));
                return;
            }
        }
        getPlayerByCharacter(player).setNickname(name);
        Map<GameCharacter, String> others = new EnumMap<>(GameCharacter.class);
        for (Player p : this.players) {
            if (p.getNickname() != null && p.getCharacter() != player) {
                others.put(p.getCharacter(), p.getNickname());
            }
        }
        notifyChanges(new PlayerReadyMessage(player, name, others));

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

    private void finalizePlayersCreation() {
        this.gameState = SETTINGUPGAME;
        List<Player> toRemove = new ArrayList<>();
        for (Player p : this.players) {
            if (p.getNickname() == null) {
                toRemove.add(p);
            }
        }
        for (Player p : toRemove) {
            notifyChanges(new GameAlreadyStartedMessage(p.getCharacter()));
            this.players.remove(p);
        }

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
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                Coordinates coordinates = new Coordinates(square.getX(), square.getY());
                RoomColor color =  room.getColor();
                Boolean spawn = square.isSpawn();
                arenaColor.put(coordinates, color);
                arenaSpawn.put(coordinates, spawn);
            }
        }
        notifyChanges(new GameSetMessage(this.skulls, Integer.parseInt(arenaNumber), arenaColor, arenaSpawn));
        finalizeGameSetup();
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
        Map<Coordinates, AmmoTile> ammos = new HashMap<>();
        Map<Coordinates, List<Weapon>> stores = new HashMap<>();
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                Coordinates coordinates = new Coordinates(square.getX(), square.getY());
                if(square.isSpawn()) {
                    List<Weapon> weapons = new ArrayList<>();
                    for(WeaponCard card : square.getWeaponsStore()) {
                        weapons.add(card.getWeaponType());
                    }
                    stores.put(coordinates, weapons);
                } else {
                    ammos.put(coordinates, square.getAvailableAmmoTile());
                }
            }
        }
        notifyChanges(new ArenaFilledMessage(ammos, stores));
        this.gameState = FIRSTTURN;
        this.currentPlayer = 0;
        startTurn();
    }

    private void startTurn() {
        TurnType type;
        if(this.gameState == FIRSTTURN) {
            type = TurnType.FIRST_TURN;
        } else if(this.players.get(this.currentPlayer).isDead()) {
            type = TurnType.AFTER_DEATH;
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
        notifyChanges(new StartTurnMessage(type, this.players.get(this.currentPlayer).getCharacter()));
    }

    private void incrementCurrentPlayer() {
        if(this.currentPlayer == this.players.size() - 1) {
            this.currentPlayer = 0;
        } else {
            this.currentPlayer++;
        }
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
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                if(!square.isSpawn()) {
                    continue;
                }
                while(square.getWeaponsStore().size() < MAX_WEAPONS_STORE){
                    square.addWeapon(this.weaponsDeck.get(0));
                    this.weaponsDeck.remove(0);
                }
            }
        }
    }

    public void fillAmmoTiles() {
        for(Room room : this.arena.getRoomList()) {
            for(Square square : room.getSquares()) {
                if(square.isSpawn()) {
                    continue;
                }
                if(square.getAvailableAmmoTile() != null) {
                    continue;
                }
                square.addAmmoTile(this.ammosDeck.get(0));
                this.ammosDeck.remove(0);
            }
        }
    }

    public void removePowerup(Player player, Powerup powerup) {
        player.removePowerup(powerup);
        this.powerupsDiscardPile.add(powerup);
        notifyChanges(new PowerupRemoved(player.getCharacter(), powerup));
    }

    public void drawPowerup(Player player) {
        Powerup powerup = this.powerupsDeck.get(0);
        player.addPowerup(powerup);
        this.powerupsDeck.remove(powerup);
        notifyChanges(new PowerupDrawnMessage(player.getCharacter(), powerup));
        notifyChanges(new PowerupDrawnMessage(player.getCharacter(), null));
    }

    public void changeWeapon(Player player, WeaponCard oldCard, WeaponCard newCard) {
        player.removeWeapon(oldCard);
        player.getPosition().removeWeapon(newCard);
        player.getPosition().addWeapon(oldCard);
        player.addWeapon(newCard);
    }

    public void giveWeapon(Player player, WeaponCard weapon) {
        player.getPosition().removeWeapon(weapon);
        player.addWeapon(weapon);
        this.fillWeaponStores();
    }

    public void giveAmmoTile(Player player, AmmoTile tile) {
        if(tile.hasPowerup() && player.getPowerupsNumber() < 3) {
            drawPowerup(player);
        }
        player.addAmmos(tile.getAmmos());
        this.ammosDiscardPile.add(tile);
    }

    public void useAmmos(Player player, Map<AmmoType, Integer> usedAmmos) {
        player.removeAmmos(usedAmmos);
    }

    public void movePlayer(Player player, Square square) {
        player.getPosition().removePlayer(player);
        square.addPlayer(player);
        player.setPosition(square);
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

    public void giveKillshotTrack() {
        Map<Player, Integer> killshotRank = new HashMap<>();
        for(Player p : this.players) {
            killshotRank.put(p, 0);
        }
        for(Player p : this.killshotTrack) {
            int value = killshotRank.get(p) + 1;
            killshotRank.put(p, 1);
        }
        Map<Player, Integer> sorted = killshotRank
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        List<Player> rank = new ArrayList<>(sorted.keySet());
        List<Integer> points = Arrays.asList(8, 6, 4, 2, 1, 1);
        for(int i = 0; i < 5; i++) {
            rank.get(i).raiseScore(points.get(i));
        }
    }

    public void handleDeadPlayer(Player player) {}

    public void handleEndTurn() {}
}
