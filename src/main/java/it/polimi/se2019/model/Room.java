package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final RoomColor color;
    private List<Square> squares;

    Room(RoomColor color , List<Square> squares) {
        this.color = color;
        this.squares = squares;
    }

    public RoomColor getColor() {
        return color;
    }

    public List<Square> getSquares() {
        return new ArrayList<>(this.squares);
    }

    boolean hasSpawn() {
        for(Square square : this.squares) {
            if (square.isSpawn()) {
                return true;
            }
        }
        return false;
    }

    Square getSpawn() {
        for(Square square : this.squares) {
            if(square.isSpawn()) {
                return square;
            }
        }
        throw new IllegalStateException("This room doesn't have a spawn point");
    }

}
