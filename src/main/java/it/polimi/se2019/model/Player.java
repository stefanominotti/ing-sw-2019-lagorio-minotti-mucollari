package it.polimi.se2019.model;

import it.polimi.se2019.model.messages.PlayerReadyMessage;

import java.util.*;

public class Player {

    public static final int MAX_BLUE_AMMOS = 3;
    public static final int MAX_RED_AMMOS = 3;
    public static final int MAX_YELLOW_AMMOS = 3;

    private Board board;
    private String name;
    private final GameCharacter character;
    private int score;
    List<Player> damages;
    List<Integer> killshotPoints;
    Map<AmmoType, Integer> availableAmmos;
    Map<Player, Integer> revengeMarks;
    List<WeaponCard> weapons;
    List<Powerup> powerups;
    Square position;

    Player(Board board, GameCharacter character) {
        this.name = null;
        this.board = board;
        this.character = character;
        this.score = 0;
        this.damages = new ArrayList<>();
        this.killshotPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
        this.availableAmmos = new EnumMap<>(AmmoType.class);
        this.availableAmmos.put(AmmoType.BLUE, 1);
        this.availableAmmos.put(AmmoType.RED, 1);
        this.availableAmmos.put(AmmoType.YELLOW, 1);
        this.revengeMarks = new HashMap<>();
        this.weapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.position = null;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public String getNickname() {
        return this.name;
    }

    public void setNickname(String name) {
        this.name = name;
    }

    public List<Player> getDamages() {
        return new ArrayList<>();
    }

    public List<WeaponCard> getWeapons() {
        return new ArrayList<>();
    }

    public Map<Player, Integer> getRevengeMarks() {
        return new HashMap<>();
    }

    public Map<AmmoType, Integer> getAvailableAmmos() {
        return new EnumMap<>(AmmoType.class);
    }

    public List<Powerup> getPowerups() {
        return new ArrayList<>();
    }

    public Square getPosition() {
        return null;
    }

    void addDamages(Player player, int amount) {}

    void resetAfterDeath() {}

    void addWeapon(WeaponCard weapon) {}

    void removeWeapon(WeaponCard weapon) {}

    public int getScore() {
        return this.score;
    }

    void raiseScore(int amount) {}

    void addAmmos(Map<AmmoType, Integer> ammos) {}

    void removeAmmos(Map<AmmoType, Integer> ammos) {}

    void addPowerup(Powerup powerup) {}

    void removePowerup(Powerup powerup) {}

    void setPosition(Square square) {}

    void addRevengeMarks(Player player, int amount) {}

    void marksToDamages(Player player) {}
}

