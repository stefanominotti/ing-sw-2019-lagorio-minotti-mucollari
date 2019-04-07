package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;

public class MovePlayerMessage {

    private GameCharacter player;
    private int x;
    private int y;

    public MovePlayerMessage(GameCharacter player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public GameCharacter getPlayer() {
        return null;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
