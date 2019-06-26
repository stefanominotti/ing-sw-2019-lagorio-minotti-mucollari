package it.polimi.se2019.model;

import com.google.gson.Gson;

import java.util.*;

/**
 * Class for handling players
 */
public class Player {

    private static final int MAX_AMMOS = 3;
    public static final int MAX_DAMAGES = 12;

    private final String token;
    private final String name;
    private final GameCharacter character;
    private int score;
    private List<GameCharacter> damages;
    private List<Integer> killshotPoints;
    private Map<AmmoType, Integer> availableAmmos;
    private List<GameCharacter> revengeMarks;
    private List<WeaponCard> weapons;
    private List<Powerup> powerups;
    private Square position;
    private boolean dead;
    private boolean connected;

    /**
     * Class constructor, it builds a player
     * @param character character assigned to the player
     * @param name nickname of the player
     * @param token token of the client associated to the player
     */
    Player(GameCharacter character, String name, String token) {
        this.token = token;
        this.name = name;
        this.character = character;
        this.score = 0;
        this.damages = new ArrayList<>();
        this.killshotPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
        this.availableAmmos = new EnumMap<>(AmmoType.class);
        this.availableAmmos.put(AmmoType.BLUE, 1);
        this.availableAmmos.put(AmmoType.RED, 1);
        this.availableAmmos.put(AmmoType.YELLOW, 1);
        this.revengeMarks = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.position = null;
        this.connected = true;
    }

    /**
     * Writes player data to JSON
     */
    public String toJson(){
        Gson gson = new Gson();
        StringBuilder jObject = new StringBuilder("{");
        String toAppend = "\"token\": " + "\"" + this.token + "\"" + ",";
        jObject.append(toAppend);
        toAppend = "\"name\": " + "\"" + this.name + "\"" + ",";
        jObject.append(toAppend);
        toAppend = "\"character\": " + "\"" + this.character + "\"" + ",";
        jObject.append(toAppend);
        toAppend = "\"score\": " + this.score + ",";
        jObject.append(toAppend);
        toAppend = "\"dead\": " + this.dead + ",";
        jObject.append(toAppend);
        toAppend = "\"connected\": " + false + ",";
        jObject.append(toAppend);
        toAppend = "\"damages\": " + gson.toJson(this.damages) + ",";
        jObject.append(toAppend);
        toAppend = "\"killshotPoints\": " + gson.toJson(this.killshotPoints) + ",";
        jObject.append(toAppend);
        toAppend = "\"availableAmmos\": " + gson.toJson(this.availableAmmos) + ",";
        jObject.append(toAppend);
        toAppend = "\"revengeMarks\": " + gson.toJson(this.revengeMarks) + ",";
        jObject.append(toAppend);
        toAppend = "\"weapons\": " + gson.toJson(this.weapons) + ",";
        jObject.append(toAppend);
        toAppend = "\"powerups\": " + gson.toJson(this.powerups) + "}";
        jObject.append(toAppend);
        return jObject.toString();
    }

    /**
     * Verifies if the token is associated to any character
     * @param token you want to verify
     * @return the game character associated to that token, null if it doesn't exist
     */
    public GameCharacter verifyPlayer(String token) {
        if(this.token.equals(token)) {
            return this.character;
        }
        return null;
    }

    /**
     * Connects the player
     */
    public void connect() {
        this.connected = true;
    }

    /**
     * Disconnects the player
     */
    void disconnect() {
        this.connected = false;
    }

    /**
     * Knows if the player is connected
     * @return true if he is, else false
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Gets the kill shot points of the player
     * @return List of kill shot points of the player
     */
    List<Integer> getKillshotPoints() {
        return new ArrayList<>(this.killshotPoints);
    }

    /**
     * Removes the first element of the kill shot points list, reducing it
     */
    void reduceKillshotPoints() {
        if (this.killshotPoints.isEmpty()) {
            return;
        }
        this.killshotPoints.remove(0);
    }

    /**
     * Knows if player is dead
     * @return true if he is, else false
     */
    public boolean isDead() {
        return this.dead;
    }

    /**
     * Gets the associated character to the player
     * @return the game character associated
     */
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets player nickname
     * @return the nickname of the player
     */
    public String getNickname() {
        return this.name;
    }

    /**
     * Gets damages dealt to the player
     * @return List of the game characters that dealt the player
     */
    public List<GameCharacter> getDamages() {
        return new ArrayList<>(this.damages);
    }

    /**
     * Gets the weapons of the player
     * @return List of weapon of the player
     */
    public List<WeaponCard> getWeapons() {
        return new ArrayList<>(this.weapons);
    }

    /**
     * Gets the weapon card corresponding the weapon
     * @param weapon the weapon of which you want to get the related weapon card
     * @return the weapon card if the player got the weapon, else null
     */
    public WeaponCard getWeaponCardByWeapon(Weapon weapon) {
        for(WeaponCard weaponCard : this.weapons) {
            if(weaponCard.getWeaponType() == weapon) {
                return weaponCard;
            }
        }
        return null;
    }

    /**
     * Gets revenge marks given to the player
     * @return List of game characters that marked the player
     */
    List<GameCharacter> getRevengeMarks() {
        return new ArrayList<>(this.revengeMarks);
    }

    /**
     * Gets available ammos of the player
     * @return Map with ammo type and its available quantity
     */
    public Map<AmmoType, Integer> getAvailableAmmos() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.availableAmmos);
        return returnMap;
    }

    /**
     * Gets powerups of the player
     * @return List of the powerups of the player
     */
    public List<Powerup> getPowerups() {
        return new ArrayList<>(this.powerups);
    }

    /**
     * Gets player position
     * @return the square corresponding to the player position
     */
    public Square getPosition() {
        return this.position;
    }

    /**
     * Adds damages to the player
     * @param player that is dealing damages to the player
     * @param amount of damages to deal
     */
    void addDamages(GameCharacter player, int amount) {
        while (amount > 0 && this.damages.size() <= MAX_DAMAGES) {
            this.damages.add(player);
            amount--;
        }
    }

    /**
     * Adds a weapon card to the player
     * @param weapon card to give
     */
    void addWeapon(WeaponCard weapon) {
        this.weapons.add(weapon);
    }

    /**
     * Removes a weapon card from the player
     * @param weapon card to remove
     */
    void removeWeapon(WeaponCard weapon) {
        this.weapons.remove(weapon);
    }

    /**
     * Gets player score
     * @return the player score
     */
    int getScore() {
        return this.score;
    }

    /**
     * Raises player score
     * @param amount to raise
     */
    void raiseScore(int amount) {
        this.score = this.score + amount;

    }

    /**
     * Add ammo to the player
     * @param ammos Map with ammo type and its quantity to add
     * @return Map with the added ammo and its quantity
     */
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

    /**
     * Removes ammos from the player
     * @param ammos Map with ammo type and its quantity to remove
     */
    void removeAmmos(Map<AmmoType, Integer> ammos) {
        for(Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) - ammo.getValue();
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    /**
     * Adds a powerup to the player
     * @param powerup you want to add
     */
    void addPowerup(Powerup powerup) {
        this.powerups.add(powerup);
    }

    /**
     * Gets a powerup of the player by its type and color
     * @param type of the powerup you want to get
     * @param color of the powerup you want to get
     * @return the powerup of that type and color, null if he doesn't have it
     */
    Powerup getPowerupByType(PowerupType type, AmmoType color) {
        for (Powerup powerup : this.powerups) {
            if (powerup.getColor() == color && powerup.getType() == type) {
                return powerup;
            }
        }
        return null;
    }

    /**
     * Gets the powerups of the player by their type
     * @param type of the powerups you want to get
     * @return List of powerups of that type, empty if he has none
     */
    public List<Powerup> getPowerupsByType(PowerupType type) {
        List<Powerup> validPowerups = new ArrayList<>();
        for (Powerup powerup : this.powerups) {
            if (powerup.getType() == type) {
                validPowerups.add(powerup);
            }
        }
        return validPowerups;
    }

    /**
     * Removes a powerup from the player
     * @param powerup you want to remove
     */
    void removePowerup(Powerup powerup) {
        this.powerups.remove(powerup);
    }

    /**
     * Sets position for the player
     * @param square where you want to place the player
     */
    public void setPosition(Square square) {
        this.position = square;
    }

    /**
     * Adds revenge marks to the player
     * @param target the game character which is marking the player
     * @param amount of marks you want to give
     */
    void addRevengeMarks(GameCharacter target, int amount) {
        for (int i=0; i<amount; i++) {
            this.revengeMarks.add(target);
        }
    }

    /**
     * Resets marks given by a player
     * @param player of which marks need to be removed
     */
    void resetMarks(GameCharacter player) {
        while (this.revengeMarks.contains(player)) {
            this.revengeMarks.remove(player);
        }
    }

    /**
     * Flips the player board into Final Frenzy mode
     */
    void flipBoard() {
        this.killshotPoints = new ArrayList<>(Arrays.asList(2, 1, 1, 1));
    }

    /**
     * Gets number of marks given by a player to the player
     * @param character game character of which you want to know how many marks has given
     * @return the numbers of marks given by that character
     */
    int getMarksNumber(GameCharacter character) {
        int number = 0;
        for (GameCharacter p : this.revengeMarks) {
            if (p == character) {
                number++;
            }
        }
        return number;
    }

    /**
     * Reset damages of the player
     */
    void resetDamages() {
        this.damages = new ArrayList<>();
    }

    /**
     * Sets the player dead or alive
     * @param dead true if you want to make him dead, false for alive
     */
    void setDead(boolean dead) {
        this.dead = dead;
    }
}

