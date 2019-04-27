package it.polimi.se2019.model;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Room {

    private final RoomColor color;
    private List<Square> squares;

    Room(RoomColor color , List<Square> squares) {
        this.color = color;
        this.squares = squares;
    }

    public RoomColor getColor() { return color; }

    public List<Square> getSquares() {
        return new ArrayList<>(squares);
    }

    public boolean hasSpawn() {
        for(Square square : squares) {
            if (square.isSpawn()) {
                return true;
            }
        }
        return false;
    }

}
