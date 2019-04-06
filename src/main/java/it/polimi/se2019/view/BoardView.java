package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.*;

public class BoardView {

    private int skulls;
    private List<SquareView> squares;
    private List<PlayerBoard> enemyBoards;
    private SelfPlayerBoard selfPlayerBoard;
    private Map<RoomColor, List<Weapon>> shops;
    private List<GameCharacter> killshotTrack;
    private boolean emptyWeaponsDeck;

    BoardView(Map<GameCharacter, String> enemies, GameCharacter playerCharacter, String playerName, int skulls) {
        this.enemyBoards = new ArrayList<>();
        for(Map.Entry<GameCharacter, String> enemy : enemies.entrySet()) {
            this.enemyBoards.add(new PlayerBoard(enemy.getKey(), enemy.getValue()));
        }
        this.selfPlayerBoard = new SelfPlayerBoard(playerCharacter, playerName);
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
