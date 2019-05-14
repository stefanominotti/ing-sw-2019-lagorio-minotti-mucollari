package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.*;

public class BoardView {

    private int skulls;
    private List<SquareView> squares;
    private List<GameCharacter> killshotTrack;
    private Map<GameCharacter, SquareView> positions;
    private boolean emptyWeaponsDeck;

    BoardView(int skulls, List<SquareView> squares) {
        this.killshotTrack = new ArrayList<>();
        this.skulls = skulls;
        this.squares = squares;
        this.emptyWeaponsDeck = false;
        this.positions = new EnumMap<>(GameCharacter.class);
    }

    public int getSkulls() {
        return this.skulls;
    }

    public List<SquareView> getSquares() {
        return new ArrayList<>(this.squares);
    }

    public SquareView getSquareByCoordinates(int x, int y) {
        for(SquareView square : this.squares) {
            if(square.getX() == x && square.getY() == y) {
                return square;
            }
        }
        throw new IllegalStateException("No square with given coordinates");
    }

    public void setPlayerPosition(GameCharacter player, SquareView square) {
        this.positions.put(player, square);
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
