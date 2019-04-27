package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.*;

public class BoardView {

    private int skulls;
    private List<SquareView> squares;
    private Map<RoomColor, List<Weapon>> shops;
    private List<GameCharacter> killshotTrack;
    private boolean emptyWeaponsDeck;

    BoardView(int skulls) {
        this.killshotTrack = new ArrayList<>();
        this.skulls = skulls;
        this.emptyWeaponsDeck = false;
        this.shops = new EnumMap<>(RoomColor.class);
    }

    public int getSkulls() {
        return this.skulls;
    }

    public List<SquareView> getSquares() {
        return new ArrayList<>();
    }

    public List<PlayerBoard> getEnemyBoards() {
        return new ArrayList<>();
    }

    public SelfPlayerBoard getSelfPlayerBoard() {
        return null;
    }

    public Map<RoomColor, List<Weapon>> getShops() {
        return new EnumMap<>(RoomColor.class);
    }

    public List<GameCharacter> getKillshotTrack() {
        return new ArrayList<>();
    }

    public boolean isEmptyWeaponsDeck() {
        return this.emptyWeaponsDeck;
    }

    void addKillshotPoints(GameCharacter attacker, int amount) {}

    void decrementSkulls(int amount) {}

    void removePowerup(Powerup powerup) {}

    void givePowerup(Powerup powerup) {}

    void removeShopWeapon(Weapon weapon) {}

    void addShopWeapon(Weapon weapon) {}

    void removePlayerWeapon(GameCharacter player, Weapon weapon) {}

    void addPlayerWeapon(GameCharacter player, Weapon weapon) {}

    void movePlayer(GameCharacter player, int x, int y) {}
}
