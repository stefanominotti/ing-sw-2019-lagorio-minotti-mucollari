package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;

public class MoveEvent {

    private GameCharacter player;
    private int x;
    private int y;

    public MoveEvent(GameCharacter player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public GameCharacter getPlayer() {}

    public int getX() {}

    public int getY() {}
}
