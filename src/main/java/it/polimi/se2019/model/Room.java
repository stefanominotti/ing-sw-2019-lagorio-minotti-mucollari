package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String color;
    private final List<Square> squares;

    Room(String color, List<Square> squares) {
        this.color = color;
        this.squares = new ArrayList<>(squares);
    }

    public String getColor() {
        return color;
    }

    public List<Square> getSquares() {}

    public boolean hasSpawn() {}
}
