package it.polimi.se2019.model;

import it.polimi.se2019.model.messages.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static it.polimi.se2019.model.GameState.*;

public class Board extends Observable {

    private static final long STARTTIME = 10L*1000L;

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
        this.players.add(new Player(this, character));
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
        return skulls;
    }

    public List<Player> getKillshotTrack() {
        return new ArrayList<>();
    }

    public void setSkulls(int skulls){
        this.skulls = skulls;
        notifyChanges(new SkullsSettedMessage(getValidPlayers().get(0).getCharacter()));
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
        notifyChanges(new ArenaCreatedMessage(arenaColor, arenaSpawn));

    }

    protected void fillWeaponsDeck() {}

    protected void fillPowerupsDeck() {}

    protected void fillAmmosDeck() {}

    public void handleEndTurn() {}

    public void removePowerup(Player player, Powerup powerup) {}

    public void drawPowerup(Player player) {}

    public void changeWeapon(Player player, WeaponCard oldCard, WeaponCard newCard) {}

    public void giveWeapon(Player player, WeaponCard weapon) {}

    public void giveAmmoTile(Player player, AmmoTile tile) {}

    public void useAmmos(Player player, Map<AmmoType, Integer> usedAmmos) {}

    public void movePlayer(Player player, Square square) {}

    public void handleDeadPlayer(Player player) {}

    public void respawnPlayer(Player player, Room room) {}
}
