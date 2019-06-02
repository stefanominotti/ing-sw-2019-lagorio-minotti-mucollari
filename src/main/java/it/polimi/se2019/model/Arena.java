package it.polimi.se2019.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Arena {

    private static final String ROOT = "arenas/data/arena_";
    private int number;
    private List<Room> rooms;

    public Arena(String filename){
        this.rooms = null;
        String path = ROOT + filename + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        String jsonString = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject)parser.parse(jsonString);
        Gson gson = new Gson();
        Type roomType = new TypeToken<List<Room>>(){}.getType();

        this.number = gson.fromJson(jsonElement.get("number"), int.class);
        this.rooms = gson.fromJson(jsonElement.get("rooms"), roomType);
        for (Room room : rooms) {
            List<Square> squares = room.getSquares();
            for(Square square : squares) {
                square.setRoom(room);
                square.setArena(this);
                square.setNearbySquares();
            }
        }
    }

    public String toJson() {
        return String.valueOf(this.number);
    }

    public List<Room> getRoomList() {
        return this.rooms;
    }

    public Square getSquareByCoordinate(int x, int y) {
        for(Room room : this.rooms) {
            for(Square square : room.getSquares()) {
                if(square.getX() == x && square.getY() == y) {
                    return square;
                }
            }
        }
        return null;
    }

    public List<Square> getAllSquares() {
        List<Square> squares  = new ArrayList<>();

        for (Room r : this.rooms) {
            squares.addAll(r.getSquares());
        }

        return squares;
    }

    public Room getRoombyColor(RoomColor color) {
        for(Room room : this.rooms) {
            if(room.getColor() == color) {
                return room;
            }
        }
        return null;
    }
}
