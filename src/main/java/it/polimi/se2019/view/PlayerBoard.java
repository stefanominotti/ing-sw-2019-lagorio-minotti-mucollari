package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.Weapon;

import java.io.Serializable;
import java.util.*;

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
    boolean frenzyBoard;

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

    public PlayerBoard(GameCharacter character, String nickname, Map<AmmoType, Integer> availableAmmos,
                       List<GameCharacter> revengeMarks, List<GameCharacter> damages,
                       List<Integer> killshotPoints, List<Weapon> unloadedWeapons, int weaponsNumber,
                       int powerupsNumber) {
        this.nickname = nickname;
        this.character = character;
        this.damages = damages;
        this.killshotPoints = killshotPoints;
        this.availableAmmos = availableAmmos;
        this.revengeMarks = revengeMarks;
        this.weaponsNumber = weaponsNumber;
        this.powerupsNumber = powerupsNumber;
        this.unloadedWeapons = new ArrayList<>(unloadedWeapons);
    }

    void flipBoard() {
        this.frenzyBoard = true;
        this.killshotPoints = new ArrayList<>(Arrays.asList(2, 1, 1, 1));
    }

    List<Integer> getKillshotPoints() {
        return new ArrayList<>(this.killshotPoints);
    }

    List<GameCharacter> getRevengeMarks(){
        return new ArrayList<>(this.revengeMarks);
    }

    Map<AmmoType, Integer> getAvailableAmmos() {
        return new HashMap<>(this.availableAmmos);
    }

    int getWeaponsNumber() {
        return this.weaponsNumber;
    }

    int getPowerupsNumber() {
        return this.powerupsNumber;
    }

    void addPowerup() {
        this.powerupsNumber++;
    }

    GameCharacter getCharacter() {
        return this.character;
    }

    String getNickname() {
        return this.nickname;
    }

    List<GameCharacter> getDamages() {
        return new ArrayList<>(this.damages);
    }

    void addDamages(GameCharacter target, int amount) {
        for (int i=0; i<amount; i++) {
            this.damages.add(target);
        }
    }

    void addMarks(GameCharacter target, int amount) {
        for (int i=0; i<amount; i++) {
            this.revengeMarks.add(target);
        }
    }

    void resetDamages() {
        this.damages = new ArrayList<>();
    }

    void addAmmos(Map<AmmoType, Integer> ammos) {
        for (Map.Entry<AmmoType, Integer> ammo : ammos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) + ammo.getValue();
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    void useAmmos(Map<AmmoType, Integer> usedAmmos) {
        for (Map.Entry<AmmoType, Integer> ammo : usedAmmos.entrySet()) {
            int newAmmos = this.availableAmmos.get(ammo.getKey()) - ammo.getValue();
            this.availableAmmos.put(ammo.getKey(), newAmmos);
        }
    }

    void addWeapon() {
        this.weaponsNumber++;
    }

    void removeWeapon(Weapon weapon) {
        this.weaponsNumber--;
        if (this.unloadedWeapons.contains(weapon)) {
            this.unloadedWeapons.remove(weapon);
        }
    }

    void removePowerup() {
        this.powerupsNumber--;
    }

    List<Weapon> getUnloadedWeapons() {
        return new ArrayList<>(this.unloadedWeapons);
    }

    void reloadWeapon(Weapon weapon) {
        this.unloadedWeapons.remove(weapon);
    }

    void unloadWeapon(Weapon weapon) {
        this.unloadedWeapons.add(weapon);
    }

    void resetMarks(GameCharacter player) {
        while (this.revengeMarks.contains(player)) {
            this.revengeMarks.remove(player);
        }
    }

    public void reduceKillshotPoints() {
        this.killshotPoints.remove(0);
    }

    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Nickname:  " + this.nickname + "\n");

        builder.append("Character: " + this.character + " (" + this.character.getIdentifier() + ")\n\n");

        builder.append("Revenge marks: ");
        for (GameCharacter c : this.revengeMarks) {
            builder.append(c.getIdentifier() + " ");
        }
        if(!this.revengeMarks.isEmpty()) {
            builder.setLength(builder.length() - 1);
        }
        builder.append("\n\n");

        builder.append("Damages:  ");
        int i = 0;
        for (GameCharacter c : this.damages) {
            if (i == 2 || i == 5 || i == 10) {
                builder.append("| " + c.getIdentifier() + " ");
            } else {
                builder.append(c.getIdentifier() + " ");
            }
            i++;
        }
        while (i<12-this.damages.size()) {
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
            builder.append(p + " ");
        }
        builder.append("\n\n");

        builder.append("Available ammos:    ");
        for(Map.Entry<AmmoType, Integer> ammo : this.availableAmmos.entrySet()) {
            for(i=0; i<ammo.getValue(); i++) {
                builder.append(ammo.getKey().getIdentifier() + " ");
            }
        }
        builder.append("\n");

        builder.append("Available powerups: " + this.powerupsNumber + "\n");
        builder.append("Ready weapons:      " + (this.weaponsNumber - this.unloadedWeapons.size()) + "\n");

        builder.append("Unloaded weapons:   ");
        for(Weapon w : this.unloadedWeapons) {
            builder.append(w + ", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n");

        return builder.toString();
    }
}
