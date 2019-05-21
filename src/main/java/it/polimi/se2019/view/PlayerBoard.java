package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

import java.io.Serializable;
import java.util.*;

public class PlayerBoard implements Serializable {

    private GameCharacter character;
    private String nickname;
    private Map<AmmoType, Integer> availableAmmos;
    private Map<GameCharacter, Integer> revengeMarks;
    private List<GameCharacter> damages;
    private List<Integer> killshotPoints;
    private List<Weapon> unloadedWeapons;
    private int weaponsNumber;
    private int powerupsNumber;

    PlayerBoard(GameCharacter character, String nickname) {
        this.nickname = nickname;
        this.character = character;
        this.damages = new ArrayList<>();
        this.killshotPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
        this.availableAmmos = new EnumMap<>(AmmoType.class);
        this.availableAmmos.put(AmmoType.BLUE, 1);
        this.availableAmmos.put(AmmoType.RED, 1);
        this.availableAmmos.put(AmmoType.YELLOW, 1);
        this.revengeMarks = new EnumMap<>(GameCharacter.class);
        this.weaponsNumber = 0;
        this.powerupsNumber = 0;
        this.unloadedWeapons = new ArrayList<>();
    }

    public PlayerBoard(GameCharacter character, String nickname, Map<AmmoType, Integer> availableAmmos,
                       Map<GameCharacter, Integer> revengeMarks, List<GameCharacter> damages,
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

    List<Integer> getKillshotPoints() {
        return new ArrayList<>(this.killshotPoints);
    }

    Map<GameCharacter, Integer> getRevengeMarks(){
        return new HashMap<>(this.revengeMarks);
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

    void addDamage(GameCharacter target, int amount) {
        for (int i=0; i<amount; i++) {
            this.damages.add(target);
        }
    }

    void addMarks(GameCharacter target, int amount) {
        int newAmount = this.revengeMarks.get(target) + amount;
        this.revengeMarks.put(target, newAmount);
    }

    void removeMarks(GameCharacter target) {
        this.revengeMarks.remove(target);
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
}
