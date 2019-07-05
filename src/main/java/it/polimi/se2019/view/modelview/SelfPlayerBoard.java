package it.polimi.se2019.view.modelview;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.PowerupType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class for handling self player board
 *  * @author stefanominotti
 */
public class SelfPlayerBoard extends PlayerBoard {

    private List<Weapon> readyWeapons;
    private List<Powerup> powerups;
    private int score;

    /**
     * Class constructor, it builds a self player board
     * @param character of which the self player board has to be built
     * @param name of which the self player board has to be built
     */
    public SelfPlayerBoard(GameCharacter character, String name) {
        super(character, name);
        this.readyWeapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.score = 0;
    }

    /**
     * Class constructor, it builds a self player board when the game is resumed after a save
     * @param playerBoard corresponding the player of which the self player board has to be built
     * @param readyWeapons List of the ready weapons
     * @param powerups List of the powerups held
     * @param score amount of points raised
     */
    public SelfPlayerBoard(PlayerBoard playerBoard, List<Weapon> readyWeapons, List<Powerup> powerups, int score) {
        super(playerBoard.getCharacter(), playerBoard.getNickname(), playerBoard.getAvailableAmmos(),
                playerBoard.getRevengeMarks(), playerBoard.getDamages(), playerBoard.getKillshotPoints(),
                playerBoard.getUnloadedWeapons(), playerBoard.getWeaponsNumber(), playerBoard.getPowerupsNumber(),
                playerBoard.isDead());
        this.readyWeapons = readyWeapons;
        this.powerups = powerups;
        this.score = score;
    }

    /**
     * Gets the powerups from the self player board
     * @return List of the powerups
     */
    public List<Powerup> getPowerups() {
        return new ArrayList<>(this.powerups);
    }

    /**
     * Adds a powerup to the self player board
     * @param powerup to be added
     */
    public void addPowerup(Powerup powerup) {
        super.addPowerup();
        this.powerups.add(powerup);
    }

    /**
     * Raises the score on the self player board
     * @param amount to be raised
     */
    public void raiseScore(int amount) {
        this.score += amount;
    }

    public int getScore() {
        return this.score;
    }

    public void removePowerup(PowerupType type, AmmoType color) {
        super.removePowerup();
        for (Powerup powerup : this.powerups) {
            if (powerup.getColor() == color && powerup.getType() == type) {
                this.powerups.remove(powerup);
                break;
            }
        }
    }

    /**
     * Adds a weapon to the self player board
     * @param weapon to be added
     */
    public void addWeapon(Weapon weapon) {
        super.addWeapon();
        this.readyWeapons.add(weapon);
    }

    /**
     * Removes a weapon from the self player board
     * @param weapon to be removed
     */
    @Override
    public void removeWeapon(Weapon weapon) {
        super.removeWeapon(weapon);
        this.readyWeapons.remove(weapon);
    }

    /**
     * Gets the ready weapons from the self player board
     * @return List of the ready weapons
     */
    public List<Weapon> getReadyWeapons() {
        return new ArrayList<>(this.readyWeapons);
    }

    /**
     * Realoads a weapon on the self player board
     * @param weapon to be reloaded
     */
    @Override
    public void reloadWeapon(Weapon weapon) {
        super.reloadWeapon(weapon);
        this.readyWeapons.add(weapon);
    }

    /**
     * Unloads a weapon on the self player board
     * @param weapon to be unloaded
     */
    @Override
    public void unloadWeapon(Weapon weapon) {
        super.unloadWeapon(weapon);
        this.readyWeapons.remove(weapon);
    }

    /**
     * Writes the self player board to string
     * @return self player board as string
     */
    @Override
    public String toString() {
        String[] rows = super.toString().split("\n");
        String[] toKeep = Arrays.copyOf(rows, rows.length-5);

        StringBuilder builder = new StringBuilder(String.join("\n", toKeep));
        builder.append("\n\n");

        builder.append("Available ammos:    ");
        String toAppend;
        for(Map.Entry<AmmoType, Integer> ammo : super.getAvailableAmmos().entrySet()) {
            for(int i=0; i<ammo.getValue(); i++) {
                toAppend = ammo.getKey().getIdentifier() + " ";
                builder.append(toAppend);
            }
        }
        builder.append("\n");

        builder.append("Available powerups: ");
        for(Powerup p : this.powerups) {
            toAppend = p.getType() + " " + p.getColor().getIdentifier() + ", ";
            builder.append(toAppend);
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n");

        builder.append("Ready weapons:      ");
        for(Weapon w : this.readyWeapons) {
            toAppend = w + ", ";
            builder.append(toAppend);
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n");

        builder.append("Unloaded weapons:   ");
        for(Weapon w : super.getUnloadedWeapons()) {
            toAppend = w + ", ";
            builder.append(toAppend);
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n\n");

        toAppend = "Score: " + this.score;
        builder.append(toAppend);

        return builder.toString();
    }
}
