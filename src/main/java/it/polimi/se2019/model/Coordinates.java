package it.polimi.se2019.model;

import java.io.Serializable;

/**
 * Class for handling coordinates
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
     * Gets x coordinate
     * @return x coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets y coordinate
     * @return y coordinate
     */
    public int getY() {
        return this.y;
    }
}