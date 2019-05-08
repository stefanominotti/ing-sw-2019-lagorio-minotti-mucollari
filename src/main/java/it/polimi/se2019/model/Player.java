package it.polimi.se2019.model;

import java.util.*;

public class Player {

    public static final int MAX_BLUE_AMMOS = 3;
    public static final int MAX_RED_AMMOS = 3;
    public static final int MAX_YELLOW_AMMOS = 3;

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

    Player(GameCharacter character) {
        this.name = null;
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
        return new ArrayList<>(this.damages);
    }

    public List<WeaponCard> getWeapons() {
        return new ArrayList<>(this.weapons);
    }

    public Map<Player, Integer> getRevengeMarks() {
        return new HashMap<>(this.revengeMarks);
    }

    public Map<AmmoType, Integer> getAvailableAmmos() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.availableAmmos);
        return returnMap;
    }

    public List<Powerup> getPowerups() {
        return new ArrayList<>(this.powerups);
    }

    public Square getPosition() {
        return this.position;
    }

    void addDamages(Player player, int amount) {
        for(int i = 0; i < amount; i++) {
            this.damages.add(player);
        }
    }

    void resetAfterDeath() {}

    void addWeapon(WeaponCard weapon) {
        this.weapons.add(weapon);
    }

    void removeWeapon(WeaponCard weapon) {
        this.weapons.remove(weapon);
    }

    public int getScore() {
        return this.score;
    }

    void raiseScore(int amount) {
        this.score = this.score + amount;
    }

    void addAmmos(Map<AmmoType, Integer> ammos) {
        for(Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            int newAmmos;
            if(this.availableAmmos.containsKey(ammo.getKey())) {
                newAmmos = this.availableAmmos.get(ammo.getKey()) + ammo.getValue();
            } else {
                newAmmos = ammo.getValue();
            }
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    void removeAmmos(Map<AmmoType, Integer> ammos) {
        for(Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) - ammo.getValue();
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    void addPowerup(Powerup powerup) {
        this.powerups.add(powerup);
    }

    void removePowerup(Powerup powerup) {
        this.powerups.remove(powerup);
    }

    void setPosition(Square square) {
        this.position = square;
    }

    void addRevengeMarks(Player player, int amount) {
        int newAmount;
        if(this.revengeMarks.containsKey(player)) {
            newAmount = this.revengeMarks.get(player) + amount;
        } else {
            newAmount = amount;
        }
        this.revengeMarks.put(player, newAmount);
    }

    void marksToDamages(Player player) {
        for(int i = 0; i < this.revengeMarks.get(player); i++) {
            this.damages.add(player);
        }
        this.revengeMarks.remove(player);
    }
}

