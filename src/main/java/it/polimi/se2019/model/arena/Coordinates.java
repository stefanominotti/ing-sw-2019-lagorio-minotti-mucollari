package it.polimi.se2019.model.arena;

import java.io.Serializable;

/**
 * Class for handling coordinates
 * @author antoniolagorio
 */
public class Coordinates implements Serializable {

    private final int x;
    private final int y;

    /**
     * Class constructor, it buils a coordinate
     * @param x coordinate
     * @param y coordinate
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets x of the coordinate
     * @return x coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets y of the coordinate
     * @return y coordinate
     */
    public int getY() {
        return this.y;
    }
}