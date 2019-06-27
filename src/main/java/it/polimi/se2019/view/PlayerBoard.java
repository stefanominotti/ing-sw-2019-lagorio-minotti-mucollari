package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
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
    private boolean frenzyBoard;

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
        return new EnumMap<>(this.availableAmmos);
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
        this.unloadedWeapons.remove(weapon);
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

    void reduceKillshotPoints() {
        this.killshotPoints.remove(0);
    }

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
        builder.append("\n");

        return builder.toString();
    }
}
