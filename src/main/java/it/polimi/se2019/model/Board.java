package it.polimi.se2019.model;

import it.polimi.se2019.model.messages.*;

import java.util.*;

import static it.polimi.se2019.model.GameState.*;

public class Board extends Observable {

    private int skulls;
    private List<Room> rooms;
    private List<Player> players;
    private List<WeaponCard> weaponsDeck;
    private List<Powerup> powerupsDeck;
    private List<AmmoTile> ammosDeck;
    private List<Powerup> powerupsDiscardPile;
    private List<AmmoTile> ammosDiscardPile;
    private List<Player> killshotTrack;
    private GameState gameState;

    public Board() {
        this.gameState = ACCEPTINGPLAYERS;
        this.rooms = new ArrayList<>();
        this.players = new ArrayList<>();
        this.weaponsDeck = new ArrayList<>();
        this.powerupsDeck = new ArrayList<>();
        this.ammosDeck = new ArrayList<>();
        this.powerupsDiscardPile = new ArrayList<>();
        this.ammosDiscardPile = new ArrayList<>();
        this.killshotTrack = new ArrayList<>();
    }

    public void notifyChanges(Object object) {
        setChanged();
        notifyObservers(object);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public Player getPlayerByCharacter(GameCharacter character) {
        for (Player player : this.players) {
            if (player.getCharacter() == character) {
                return player;
            }
        }
        return null;
    }

    public int getPlayersCount() {
        int count = 0;
        for (Player player : this.players) {
            if (player.getNickname() != null) {
                count++;
            }
        }
        return count;
    }

    public void addPlayer(GameCharacter character) {
        if (this.gameState != ACCEPTINGPLAYERS) {
            notifyObservers(new GameAlreadyStartedMessage(character));
        }
        this.players.add(new Player(this, character));
        List<String> otherNames = new ArrayList<>();
        for(Player player : this.players){
            otherNames.add(player.getNickname());
        }
        notifyChanges(new PlayerCreatedMessage(character, otherNames));
    }

    public void setPlayerNickname(GameCharacter player, String name) {
        if (this.gameState != ACCEPTINGPLAYERS) {
            this.players.remove(getPlayerByCharacter(player));
            notifyChanges(new GameAlreadyStartedMessage(player));
            return;
        }
        getPlayerByCharacter(player).setNickname(name);
        Map<GameCharacter, String> others = new EnumMap<>(GameCharacter.class);
        for (Player p : this.players) {
            if (p.getNickname() != null && p.getCharacter() != player) {
                others.put(p.getCharacter(), p.getNickname());
            }
        }
        notifyChanges(new PlayerReadyMessage(player, name, others));
    }

    public void finalizePlayersCreation() {
        List<Player> toRemove = new ArrayList<>();
        List<GameCharacter> characters = new ArrayList<>();
        for (Player player : this.players) {
            if (player.getNickname() == null) {
                toRemove.add(player);
            } else {
                characters.add(player.getCharacter());
            }
        }
        for (Player player : toRemove) {
            this.players.remove(player);
        }
        this.gameState = SETTINGUP;
        notifyChanges(new PlayerListMessage(characters));
    }

    public void handleDisconnection(GameCharacter character) {
        if (this.gameState == ACCEPTINGPLAYERS) {
            for (Player p : this.players) {
                if (p.getCharacter() == character) {
                    this.players.remove(p);
                    if (p.getNickname() != null) {
                        notifyChanges(new ClientDisconnectedMessage(character));
                    }
                    break;
                }
            }
        }
    }

    public int getSkulls() {
        return skulls;
    }

    public List<Player> getKillshotTrack() {
        return new ArrayList<>();
    }

    public void initializeGame(int skulls, int arenaNumber) {
        this.skulls = skulls;
        switch (arenaNumber) {
            case (1):
                //crea arena
                break;
            case (2):
                //crea arena
                break;
            case (3):
                //crea arena
                break;
            case (4):
                //crea arena
                break;
        }
        notifyChanges(new ArenaCreatedMessage());
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
