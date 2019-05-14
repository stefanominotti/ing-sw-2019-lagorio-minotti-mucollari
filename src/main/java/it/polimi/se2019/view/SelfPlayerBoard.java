package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.Weapon;

import java.util.ArrayList;
import java.util.List;

public class SelfPlayerBoard extends PlayerBoard {

    private List<Weapon> readyWeapons;
    private List<Powerup> powerups;
    int score;

    SelfPlayerBoard(GameCharacter character, String name) {
        super(character, name);
        this.readyWeapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.score = 0;
    }

    List<Powerup> getPowerups() {
        return new ArrayList<>(this.powerups);
    }

    void addPowerup(Powerup powerup) {
        super.addPowerup();
        this.powerups.add(powerup);
    }

    void incrementScore(int amount) {}

    void removePowerup(Powerup powerup) {}
}
