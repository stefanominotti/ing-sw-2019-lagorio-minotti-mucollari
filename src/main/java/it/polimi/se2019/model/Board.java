package it.polimi.se2019.model;

import java.util.*;

public class Board extends Observable {

    private int skulls;
    private List<Room> rooms;
    private List<Player> players;
    private List<WeaponCard> weaponsDeck;
    private List<Powerup> powerupsDeck;
    private List<AmmoTile> ammosDeck;
    private List<WeaponCard> weaponsDiscardPile;
    private List<Powerup> powerupsDiscardPile;
    private List<AmmoTile> ammosDiscardPile;

    public Board() {
        this.rooms = new ArrayList<>();
        this.players = new ArrayList<>();
        this.weaponsDeck = new ArrayList<>();
        this.powerupsDeck = new ArrayList<>();
        this.ammosDeck = new ArrayList<>();
        this.weaponsDiscardPile = new ArrayList<>();
        this.powerupsDiscardPile = new ArrayList<>();
        this.ammosDiscardPile = new ArrayList<>();
    }

    public List<Player> getPlayers() {}

    public int getSkulls() {
        return skulls;
    }

    public void initializeGame(int skulls, List<GameCharacter> chosenCharacters) {}

    private void fillWeaponsDeck() {}

    private void fillPowerupsDeck() {}

    private void fillAmmosDeck() {}

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
