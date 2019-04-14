package it.polimi.se2019.model;

import it.polimi.se2019.model.messages.PlayerCreatedMessage;
import it.polimi.se2019.model.messages.PlayerReadyMessage;

import java.util.*;

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

    public Board() {
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

    public void addPlayer(GameCharacter character) {
        this.players.add(new Player(this, character));
        notifyChanges(new PlayerCreatedMessage(character));
    }

    public void setPlayerNickname(GameCharacter player, String name) {
        getPlayerByCharacter(player).setNickname(name);
        Map<GameCharacter, String> others = new EnumMap<>(GameCharacter.class);
        for (Player p : this.players) {
            if (p.getNickname() != null && p.getCharacter() != player) {
                others.put(p.getCharacter(), p.getNickname());
            }
        }
        notifyChanges(new PlayerReadyMessage(player, name, others));
    }

    public int getSkulls() {
        return skulls;
    }

    public List<Player> getKillshotTrack() {
        return new ArrayList<>();
    }

    public void initializeGame(int skulls, List<GameCharacter> chosenCharacters) {}

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
