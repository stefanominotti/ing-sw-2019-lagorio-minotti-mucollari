package it.polimi.se2019.view;

import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.playerassets.weapon.Weapon;

import java.io.Serializable;
import java.util.*;

/**
 * Class for handling player board
 */
public class PlayerBoard implements Serializable {

    private static final int KILLSHOT_POINTS_SIZE = 6;
    private static final int KILLSHOT_POINTS_SIZE_FRENZY = 4;

    private GameCharacter character;
    private String nickname;
    private Map<AmmoType, Integer> availableAmmos;
    private List<GameCharacter> revengeMarks;
    private List<GameCharacter> damages;
    private List<Integer> killshotPoints;
    private List<Weapon> unloadedWeapons;
    private int weaponsNumber;
    private int powerupsNumber;
    private boolean frenzyBoard;
    private boolean dead;

    /**
     * Class constructor, it builds a player board
     * @param character of which the player board has to be built
     * @param nickname of which the player board has to be built
     */
    PlayerBoard(GameCharacter character, String nickname) {
        this.nickname = nickname;
        this.character = character;
        this.damages = new ArrayList<>();
        this.killshotPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
        this.availableAmmos = new EnumMap<>(AmmoType.class);
        this.availableAmmos.put(AmmoType.BLUE, 1);
        this.availableAmmos.put(AmmoType.RED, 1);
        this.availableAmmos.put(AmmoType.YELLOW, 1);
        this.revengeMarks = new ArrayList<>();
        this.weaponsNumber = 0;
        this.powerupsNumber = 0;
        this.unloadedWeapons = new ArrayList<>();
    }

    /**
     * Class constructor, it builds a player board when the game is resumed after a save
     * @param character of which the player board has to be built
     * @param nickname of which the player board has to be built
     * @param availableAmmos Map with ammo type and its quantity
     * @param revengeMarks List of the game characters which has marked the player
     * @param damages List of the game characters which has damaged the player
     * @param killshotPoints List of the kill shot points
     * @param unloadedWeapons List of the unloaded weapons
     * @param weaponsNumber amount of weapons
     * @param powerupsNumber amount of powerups
     * @param dead true if player is dead, else false
     */
    public PlayerBoard(GameCharacter character, String nickname, Map<AmmoType, Integer> availableAmmos,
                       List<GameCharacter> revengeMarks, List<GameCharacter> damages,
                       List<Integer> killshotPoints, List<Weapon> unloadedWeapons, int weaponsNumber,
                       int powerupsNumber, boolean dead) {
        this.nickname = nickname;
        this.character = character;
        this.damages = damages;
        this.killshotPoints = killshotPoints;
        this.availableAmmos = availableAmmos;
        this.revengeMarks = revengeMarks;
        this.weaponsNumber = weaponsNumber;
        this.powerupsNumber = powerupsNumber;
        this.unloadedWeapons = new ArrayList<>(unloadedWeapons);
        this.dead = dead;
    }

    /**
     * Flips the player board into Final Frenzy mode
     */
    void flipBoard() {
        this.frenzyBoard = true;
        this.killshotPoints = new ArrayList<>(Arrays.asList(2, 1, 1, 1));
    }

    /**
     * Knows if board is in frenzy mode
     * @return true if it is, else false
     */
    boolean isFrenzyBoard() {
        return this.frenzyBoard;
    }

    /**
     * Knows if player is dead
     * @return true if it is, else false
     */
    public boolean isDead() {
        return this.dead;
    }

    /**
     * Sets if player is dead or not
     * @param dead true if player is dead, else false
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Gets the kill shot points of the player
     * @return List of kill shot points of the player
     */
    List<Integer> getKillshotPoints() {
        return new ArrayList<>(this.killshotPoints);
    }

    /**
     * Gets revenge marks given to the player
     * @return List of game characters that marked the player
     */
    List<GameCharacter> getRevengeMarks(){
        return new ArrayList<>(this.revengeMarks);
    }

    /**
     * Gets available ammos of the player
     * @return Map with ammo type and its available quantity
     */
    Map<AmmoType, Integer> getAvailableAmmos() {
        return new EnumMap<>(this.availableAmmos);
    }

    /**
     * Gets the weapon number
     * @return the weapon amount held by the player
     */
    int getWeaponsNumber() {
        return this.weaponsNumber;
    }

    /**
     * Gets the the powerups number
     * @return the powerups amount held by the player
     */
    int getPowerupsNumber() {
        return this.powerupsNumber;
    }

    /**
     * Raises powerup amount
     */
    void addPowerup() {
        this.powerupsNumber++;
    }

    /**
     * Gets the player character
     * @return the game character of the player
     */
    GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets the player nickname
     * @return the nickname of the player
     */
    String getNickname() {
        return this.nickname;
    }

    /**
     * Gets the player damages
     * @return List of the the characters which has dealt the player
     */
    List<GameCharacter> getDamages() {
        return new ArrayList<>(this.damages);
    }

    /**
     * Adds a damages to a player
     * @param target that has to be dealt
     * @param amount of damages that has to be dealt
     */
    void addDamages(GameCharacter target, int amount) {
        for (int i=0; i<amount; i++) {
            this.damages.add(target);
        }
    }

    /**
     * Adds a marks to a player
     * @param target that has to be dealt
     * @param amount of marks that has to be dealt
     */
    void addMarks(GameCharacter target, int amount) {
        for (int i = 0; i < amount; i++) {
            this.revengeMarks.add(target);
        }
    }

    /**
     * Reset damages of the player
     */
    void resetDamages() {
        this.damages = new ArrayList<>();
    }
    /**
     * Add ammo to the player
     * @param ammos Map with ammo type and its quantity to add
     */
    void addAmmos(Map<AmmoType, Integer> ammos) {
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) + ammo.getValue();
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    /**
     * Uses ammo
     * @param usedAmmos Map with ammo and quantity to be used
     */
    void useAmmos(Map<AmmoType, Integer> usedAmmos) {
        for (Map.Entry<AmmoType, Integer> ammo : usedAmmos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) - ammo.getValue();
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    /**
     * Raises weapons amount
     */
    void addWeapon() {
        this.weaponsNumber++;
    }

    /**
     * Removes a weapon from the player
     * @param weapon to remove
     */
    void removeWeapon(Weapon weapon) {
        this.weaponsNumber--;
        this.unloadedWeapons.remove(weapon);
    }

    /**
     * Reduces powerups amount
     */
    void removePowerup() {
        this.powerupsNumber--;
    }

    /**
     * Gets the unloaded weapons
     * @return List of the unloaded weapons of the player
     */
    List<Weapon> getUnloadedWeapons() {
        return new ArrayList<>(this.unloadedWeapons);
    }

    /**
     * Reloads a weapon
     * @param weapon to be reloaded
     */
    void reloadWeapon(Weapon weapon) {
        this.unloadedWeapons.remove(weapon);
    }

    /**
     * Unloads a weapon
     * @param weapon to be unloaded
     */
    void unloadWeapon(Weapon weapon) {
        this.unloadedWeapons.add(weapon);
    }

    /**
     * Reset marks given by a player
     * @param player of which the marks has to be removed
     */
    void resetMarks(GameCharacter player) {
        while (this.revengeMarks.contains(player)) {
            this.revengeMarks.remove(player);
        }
    }

    /**
     * Reduces the killshot points
     */
    void reduceKillshotPoints() {
        this.killshotPoints.remove(0);
    }

    /**
     * Writes the player board as a string
     * @return the player board as string
     */
    public String toString() {

        StringBuilder builder = new StringBuilder();

        String toAppend = "Nickname:  " + this.nickname + "\n";
        builder.append(toAppend);

        toAppend = "Character: " + this.character + " (" + this.character.getIdentifier() + ")\n\n";
        builder.append(toAppend);

        builder.append("Revenge marks: ");
        for (GameCharacter c : this.revengeMarks) {
            toAppend = c.getIdentifier() + " ";
            builder.append(toAppend);
        }
        if(!this.revengeMarks.isEmpty()) {
            builder.setLength(builder.length() - 1);
        }
        builder.append("\n\n");

        builder.append("Damages:  ");
        int i = 0;
        for (GameCharacter c : this.damages) {
            if (i == 2 || i == 5 || i == 10) {
                toAppend = "| " + c.getIdentifier() + " ";
                builder.append(toAppend);
            } else {
                toAppend = c.getIdentifier() + " ";
                builder.append(toAppend);
            }
            i++;
        }
        while (i < 12) {
            if (i == 2 || i == 5 || i == 10) {
                builder.append("| _ ");
            } else {
                builder.append("_ ");
            }
            i++;
        }
        builder.append("\n");

        builder.append("Killshot: ");
        int size = KILLSHOT_POINTS_SIZE;
        if (this.frenzyBoard) {
            size = KILLSHOT_POINTS_SIZE_FRENZY;
        }
        for(i=0; i<size-this.killshotPoints.size(); i++) {
            builder.append("x ");
        }
        for (Integer p : this.killshotPoints) {
            toAppend = p + " ";
            builder.append(toAppend);
        }
        builder.append("\n\n");

        builder.append("Available ammos:    ");
        for(Map.Entry<AmmoType, Integer> ammo : this.availableAmmos.entrySet()) {
            for(i=0; i<ammo.getValue(); i++) {
                toAppend = ammo.getKey().getIdentifier() + " ";
                builder.append(toAppend);
            }
        }
        builder.append("\n");

        toAppend = "Available powerups: " + this.powerupsNumber + "\n";
        builder.append(toAppend);
        toAppend = "Ready weapons:      " + (this.weaponsNumber - this.unloadedWeapons.size()) + "\n";
        builder.append(toAppend);

        builder.append("Unloaded weapons:   ");
        for(Weapon w : this.unloadedWeapons) {
            toAppend = w + ", ";
            builder.append(toAppend);
        }
        builder.setLength(builder.length() - 2);

        return builder.toString();
    }
}
