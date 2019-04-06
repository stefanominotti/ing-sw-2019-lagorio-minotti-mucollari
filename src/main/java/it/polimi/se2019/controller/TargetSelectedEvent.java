package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;

import java.util.ArrayList;
import java.util.List;

public class TargetSelectedEvent {

    private GameCharacter player;
    private List<GameCharacter> targets;
    private int squareX;
    private int squareY;

    public TargetSelectedEvent(GameCharacter player, List<GameCharacter> targets, int squareX, int squareY) {
        this.player = player;
        this.targets = targets;
        this.squareX = squareX;
        this.squareY = squareY;
    }

    public GameCharacter getPlayer() {
        return null;
    }

    public List<GameCharacter> getTargets() {
        return new ArrayList<>();
    }

    public int getSquareX() {
        return this.squareX;
    }

    public int getSquareY() {
        return this.squareY;
    }
}
