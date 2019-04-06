package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;

public class PowerupMoveEvent {
    private int x;
    private int y;
    private GameCharacter target;

    public PowerupMoveEvent(GameCharacter target, int x, int y) {
        this.x = x;
        this.y = y;
        this.target = target;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public GameCharacter getTarget() {
        return null;
    }
}
