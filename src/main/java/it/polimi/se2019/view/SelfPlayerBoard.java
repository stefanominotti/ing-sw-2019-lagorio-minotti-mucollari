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

    void incrementScore(int amount) {}

    void addPowerup(Powerup powerup) {}

    void removePowerup(Powerup powerup) {}
}
