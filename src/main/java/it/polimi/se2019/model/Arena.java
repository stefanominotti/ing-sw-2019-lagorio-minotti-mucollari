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

/**
 * Class for handling the Arenas
 */
public class Arena {

    private static final String ROOT = "arenas/data/arena_";
    private int number;
    private List<Room> rooms;

    /**
     * Class constructor, it builds an arena
     * @param filename of the arena you want to build (eg. arena_1)
     */
    public Arena(String filename){
        this.rooms = null;
        String path = ROOT + filename + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        if (inputStream == null) {
            return;
        }
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

    /**
     * Gets the arena representation from JSON
     * @return String representing the arena number
     */
    public String toJson() {
        return String.valueOf(this.number);
    }

    /**
     * Gets the rooms of the arena
     * @return List of the rooms of the arena
     */
    public List<Room> getRoomList() {
        return new ArrayList<>(this.rooms);
    }

    /**
     * Gets a square of the Arena from its coordinates
     * @param x coordinate X of the square you want to get
     * @param y coordinate Y of the square you want to get
     * @return the square with coordinates X and Y
     */
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

    /**
     * Gets the squares of the arena
     * @return List of squares of the arena
     */
    public List<Square> getAllSquares() {
        List<Square> squares  = new ArrayList<>();

        for (Room r : this.rooms) {
            squares.addAll(r.getSquares());
        }

        return squares;
    }

    /**
     * Gets a room of the arena by its color
     * @param color of the room you want to get
     * @return room of that color, null if it doesn't exist
     */
    public Room getRoomByColor(RoomColor color) {
        for(Room room : this.rooms) {
            if(room.getColor() == color) {
                return room;
            }
        }
        return null;
    }
}
