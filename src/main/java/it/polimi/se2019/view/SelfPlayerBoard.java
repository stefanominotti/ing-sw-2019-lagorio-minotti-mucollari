package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SelfPlayerBoard extends PlayerBoard {

    private List<Weapon> readyWeapons;
    private List<Powerup> powerups;
    private int score;

    SelfPlayerBoard(GameCharacter character, String name) {
        super(character, name);
        this.readyWeapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.score = 0;
    }

    public SelfPlayerBoard(PlayerBoard playerBoard,  List<Weapon> readyWeapons, List<Powerup> powerups, int score) {
        super(playerBoard.getCharacter(), playerBoard.getNickname(), playerBoard.getAvailableAmmos(),
                playerBoard.getRevengeMarks(), playerBoard.getDamages(), playerBoard.getKillshotPoints(),
                playerBoard.getUnloadedWeapons(), playerBoard.getWeaponsNumber(), playerBoard.getPowerupsNumber());
        this.readyWeapons = readyWeapons;
        this.powerups = powerups;
        this.score = score;
    }

    List<Powerup> getPowerups() {
        return new ArrayList<>(this.powerups);
    }

    void addPowerup(Powerup powerup) {
        super.addPowerup();
        this.powerups.add(powerup);
    }

    void raiseScore(int amount) {
        this.score += amount;
    }

    void removePowerup(PowerupType type, AmmoType color) {
        super.removePowerup();
        for (Powerup powerup : this.powerups) {
            if (powerup.getColor() == color && powerup.getType() == type) {
                this.powerups.remove(powerup);
                break;
            }
        }
    }

    void addWeapon(Weapon weapon) {
        super.addWeapon();
        this.readyWeapons.add(weapon);
    }

    @Override
    void removeWeapon(Weapon weapon) {
        super.removeWeapon(weapon);
        if (this.readyWeapons.contains(weapon)) {
            this.readyWeapons.remove(weapon);
        }
    }

    List<Weapon> getReadyWeapons() {
        return new ArrayList<>(this.readyWeapons);
    }

    @Override
    void reloadWeapon(Weapon weapon) {
        super.reloadWeapon(weapon);
        this.readyWeapons.add(weapon);
    }

    @Override
    void unloadWeapon(Weapon weapon) {
        super.unloadWeapon(weapon);
        this.readyWeapons.remove(weapon);
    }

    @Override
    public String toString() {
        String[] rows = super.toString().split("\n");
        String[] toKeep = Arrays.copyOf(rows, rows.length-5);

        StringBuilder builder = new StringBuilder(String.join("\n", toKeep));
        builder.append("\n\n");

        builder.append("Available ammos:    ");
        for(Map.Entry<AmmoType, Integer> ammo : super.getAvailableAmmos().entrySet()) {
            for(int i=0; i<ammo.getValue(); i++) {
                builder.append(ammo.getKey().getIdentifier() + " ");
            }
        }
        builder.append("\n");

        builder.append("Available powerups: ");
        for(Powerup p : this.powerups) {
            builder.append(p.getType() + " " + p.getColor().getIdentifier() + ", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n");

        builder.append("Ready weapons:      ");
        for(Weapon w : this.readyWeapons) {
            builder.append(w + ", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n");

        builder.append("Unloaded weapons:   ");
        for(Weapon w : super.getUnloadedWeapons()) {
            builder.append(w + ", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n");

        return builder.toString();
    }
}
