package it.polimi.se2019.model;

import com.google.gson.Gson;

import java.util.*;

public class Player {

    public static final int MAX_AMMOS = 3;
    public static final int MAX_DAMAGES = 12;

    private String name;
    private final GameCharacter character;
    private int score;
    private List<GameCharacter> damages;
    private List<Integer> killshotPoints;
    private Map<AmmoType, Integer> availableAmmos;
    private Map<GameCharacter, Integer> revengeMarks;
    private List<WeaponCard> weapons;
    private List<Powerup> powerups;
    private Square position;
    private boolean dead;

    Player(GameCharacter character, String name) {
        this.name = name;
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

    public String toJson(){
        Gson gson = new Gson();
        StringBuilder jObject = new StringBuilder("{");
        jObject.append("\"name\": " + "\"" + this.name + "\"" + ",");
        jObject.append("\"character\": " + "\"" + this.character + "\"" + ",");
        jObject.append("\"score\": " + this.score + ",");
        jObject.append("\"dead\": " + this.dead + ",");
        jObject.append("\"damages\": " + gson.toJson(this.damages) + ",");
        jObject.append("\"killshotPoints\": " + gson.toJson(this.killshotPoints) + ",");
        jObject.append("\"availableAmmos\": " + gson.toJson(this.availableAmmos) + ",");
        jObject.append("\"revengeMarks\": " + gson.toJson(this.revengeMarks) + ",");
        jObject.append("\"weapons\": " + gson.toJson(this.weapons) + ",");
        jObject.append("\"powerups\": " + gson.toJson(this.powerups) + "}");
        return jObject.toString();
    }

    public List<Integer> getKillshotPoints() {
        return this.killshotPoints;
    }

    public void reducekillshotPoints() {
        this.killshotPoints.remove(0);
    }

    public boolean isDead() {
        return this.dead;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public String getNickname() {
        return this.name;
    }

    public List<GameCharacter> getDamages() {
        return new ArrayList<>(this.damages);
    }

    public List<WeaponCard> getWeapons() {
        return new ArrayList<>(this.weapons);
    }

    public Map<GameCharacter, Integer> getRevengeMarks() {
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

    void addDamages(GameCharacter player, int amount) {
        while (amount > 0 && this.damages.size() <= MAX_DAMAGES) {
            this.damages.add(player);
            amount--;
        }
    }

    void addWeapon(WeaponCard weapon) {
        this.weapons.add(weapon);
    }

    void removeWeapon(WeaponCard weapon) {
        this.weapons.remove(weapon);
    }

    public int getScore() {
        return this.score;
    }

    public void raiseScore(int amount) {
        this.score = this.score + amount;

    }

    Map<AmmoType, Integer> addAmmos(Map<AmmoType, Integer> ammos) {
        Map<AmmoType, Integer> addedAmmos = new EnumMap<>(AmmoType.class);
        for(Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) + ammo.getValue();
            if(newAmmos > MAX_AMMOS) {
                newAmmos = MAX_AMMOS;
            }
            addedAmmos.put(ammo.getKey(), newAmmos - this.availableAmmos.get(ammo.getKey()));
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
        return addedAmmos;
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

    Powerup getPowerupByType(PowerupType type, AmmoType color) {
        for (Powerup powerup : this.powerups) {
            if (powerup.getColor() == color && powerup.getType() == type) {
                return powerup;
            }
        }
        return null;
    }

    void removePowerup(Powerup powerup) {
        this.powerups.remove(powerup);
    }

    void setPosition(Square square) {
        this.position = square;
    }

    void addRevengeMarks(GameCharacter player, int amount) {
        int newAmount;
        if(this.revengeMarks.containsKey(player)) {
            newAmount = this.revengeMarks.get(player) + amount;
        } else {
            newAmount = amount;
        }
        this.revengeMarks.put(player, newAmount);
    }

    void marksToDamages(GameCharacter player) {
        for(int i = 0; i < this.revengeMarks.get(player); i++) {
            if(this.damages.size() <= MAX_DAMAGES) {
                this.damages.add(player);
            }
        }
        this.revengeMarks.remove(player);
    }

    int getPowerupsNumber() {
        return this.powerups.size();
    }

    int getAmmosNumber(AmmoType color) {
        return  this.availableAmmos.get(color);
    }

    void resetAfterDeath() {}
}

