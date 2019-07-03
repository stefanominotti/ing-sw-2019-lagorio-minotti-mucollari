package it.polimi.se2019.server;

import com.google.gson.*;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.Room;
import it.polimi.se2019.model.arena.Square;
import it.polimi.se2019.model.playerassets.AmmoTile;
import it.polimi.se2019.model.playerassets.weapons.WeaponCard;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling game loader
 */
public class GameLoader {

    private static final Logger LOGGER = Logger.getLogger(GameLoader.class.getName());
    private static final String SERVER_SETTINGS = "/server_settings.json";
    private static final String CONFIG_PATH = System.getProperty("user.home");
    private static final String DEFAULT_SAVE_FILE = "/game_data.json";

    private Board board;
    private String filePath;
    private JsonParser parser;
    private Gson gson;

    /**
     * Class constructor, it builds a game loader
     */
    public GameLoader() {
        this.parser = new JsonParser();
        this.gson = new Gson();
        try(FileReader configReader = new FileReader(CONFIG_PATH + SERVER_SETTINGS)) {
            this.filePath = CONFIG_PATH + '/' +
                    ((JsonObject)this.parser.parse(configReader)).get("savePath").getAsString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Invalid settings file, save file set to default game_data.json");
            this.filePath = CONFIG_PATH + DEFAULT_SAVE_FILE;
        }
        this.board = new Board();
    }

    /**
     * Loads saved board from the game save
     * @return board loaded
     */
    public Board loadBoard() {
        FileReader reader;
        JsonObject jsonElement;
        List<Player> players;
        
        try {
            reader = new FileReader(this.filePath);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "No game data found, a new game will start");
            return this.board;
        }
        jsonElement = (JsonObject)this.parser.parse(reader);
        players = new ArrayList<>();
        JsonArray jsonPlayers = jsonElement.getAsJsonArray("players");
        JsonObject weaponSquares = jsonElement.getAsJsonObject("others").getAsJsonObject("weapon_square");
        JsonArray ammoSquares = jsonElement.getAsJsonObject("others").getAsJsonArray("ammos_square");


        this.board = gson.fromJson(jsonElement.get("board"), Board.class);
        this.board.loadTimers();
        this.board.loadArena(this.gson.fromJson(jsonElement.get("arena"), String.class));
        for (JsonElement jsonPlayer : jsonPlayers) {
            Player player = this.gson.fromJson(jsonPlayer.getAsJsonObject().get("player"), Player.class);
            if (player.isDead()) {
                this.board.addDeadPlayer(player);
            }
            player.disconnect();
            try {
                int posX = this.gson.fromJson(
                        jsonPlayer.getAsJsonObject().get("player_position").getAsJsonObject().get("x"), int.class);
                int posY = this.gson.fromJson(
                        jsonPlayer.getAsJsonObject().get("player_position").getAsJsonObject().get("y"), int.class);
                Square square = this.board.getArena().getSquareByCoordinate(posX, posY);
                this.board.movePlayer(player, square);
            } catch (NullPointerException e) {
                // Ignore
            }
            players.add(player);
        }
        this.board.setPlayers(players);
        for (JsonElement ammoSquare : ammoSquares) {
            int posX = gson.fromJson(ammoSquare.getAsJsonObject().getAsJsonObject().get("x"), int.class);
            int posY = gson.fromJson(ammoSquare.getAsJsonObject().getAsJsonObject().get("y"), int.class);
            this.board.getArena().getSquareByCoordinate(posX, posY).addAmmoTile(
                    this.gson.fromJson(ammoSquare.getAsJsonObject().get("tile"), AmmoTile.class));
        }
        for (Room room : this.board.getArena().getRoomList()) {
            if (room.hasSpawn()) {
                for (JsonElement weapon : weaponSquares.getAsJsonArray(room.getColor().toString())) {
                    room.getSpawn().addWeapon(this.gson.fromJson(weapon.getAsJsonObject(), WeaponCard.class));
                }
            }
        }
        return this.board;
    }

    /**
     * Saves the board to the game save
     */
    void saveBoard() {
        StringBuilder jObject = new StringBuilder("{");
        String toAppend = "\"board\":" + this.board.toJson() + ",";
        jObject.append(toAppend);
        jObject.append(("\"players\":["));
        for(Player player : this.board.getPlayers()){
            toAppend = "{\"player\":" + player.toJson() + ",";
            jObject.append(toAppend);
            jObject.append("\"player_position\":{");
            try {
                toAppend = "\"x\":" + player.getPosition().getX() + ",";
                jObject.append(toAppend);
                toAppend = "\"y\":" + player.getPosition().getY() + "}},";
                jObject.append(toAppend);
            } catch (NullPointerException e) {
                jObject.append("}},");
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("],");
        toAppend = "\"arena\":" + "\"" + this.board.getArena().toJson() + "\"" + ",";
        jObject.append(toAppend);
        jObject.append("\"others\":{");
        jObject.append("\"ammos_square\":[");
        for(Square square : this.board.getArena().getAllSquares()) {
            if(!square.isSpawn()) {
                toAppend = "{\"x\":" + square.getX() + ",";
                jObject.append(toAppend);
                toAppend = "\"y\":" + square.getY() + ",";
                jObject.append(toAppend);
                toAppend = "\"tile\":" + this.gson.toJson(square.getAvailableAmmoTile()) + "},";
                jObject.append(toAppend);
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("],");
        jObject.append("\"weapon_square\":{");
        for(Room room : this.board.getArena().getRoomList()) {
            if(room.hasSpawn()) {
                toAppend = "\"" + room.getColor() + "\":" + this.gson.toJson(room.getSpawn().getWeaponsStore()) + ",";
                jObject.append(toAppend);
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("}}}");
        try(FileWriter writer = new FileWriter(this.filePath)) {
            writer.write(jObject.toString());
            writer.flush();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Error writing game data, game won't be saved");
        }


    }

    /**
     * Deletes the game save
     */
    void deleteGame() {
        try {
            Files.delete(Paths.get(this.filePath));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No save file to delete");
        }
    }
}
