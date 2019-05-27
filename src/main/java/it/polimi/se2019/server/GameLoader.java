package it.polimi.se2019.server;

import com.google.gson.*;
import it.polimi.se2019.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameLoader {

    private static final Logger LOGGER = Logger.getLogger(GameLoader.class.getName());

    private final String path;
    private Board board;

    private JsonParser parser;
    private Gson gson;

    public GameLoader() {

        this.path = System.getProperty("user.home");
        this.parser = new JsonParser();
        this.gson = new Gson();
        this.board = new Board();
    }

    public Board loadBoard() {
        FileReader reader;
        JsonObject jsonElement;
        List<Player> players;
        
        try {
            reader = new FileReader(this.path + "/" + "game_state_data.json");
        } catch (IOException E) {
            return this.board;
        }
        jsonElement = (JsonObject)this.parser.parse(reader);
        players = new ArrayList<>();
        JsonArray frenzyOrder = jsonElement.getAsJsonArray("finalFrenzyOrder");
        JsonArray jsonPlayers = jsonElement.getAsJsonArray("players");
        JsonObject weaponSquares = jsonElement.getAsJsonObject("others").getAsJsonObject("weapon_square");
        JsonArray ammoSquares = jsonElement.getAsJsonObject("others").getAsJsonArray("ammos_square");


        this.board = gson.fromJson(jsonElement.get("board"), Board.class);
        this.board.loadTimers();
        this.board.loadArena(this.gson.fromJson(jsonElement.get("arena"), String.class));
        for (JsonElement jsonPlayer : jsonPlayers) {
            Player player = this.gson.fromJson(jsonPlayer.getAsJsonObject().get("player"), Player.class);
            for (WeaponCard weapon : player.getWeapons()) {
                weapon.setOwner(player);
            }
            if (player.isDead()) {
                this.board.addDeadPlayer(player);
            }
            try {
                int posX = this.gson.fromJson(
                        jsonPlayer.getAsJsonObject().get("player_position").getAsJsonObject().get("x"), int.class);
                int posY = this.gson.fromJson(
                        jsonPlayer.getAsJsonObject().get("player_position").getAsJsonObject().get("y"), int.class);
                Square square = this.board.getArena().getSquareByCoordinate(posX, posY);
                this.board.movePlayer(player, square);
            } catch (NullPointerException e) {

            }
            players.add(player);
        }
        this.board.setPlayers(players);
        if (frenzyOrder != null) {
            for (JsonElement frenzyElement : frenzyOrder) {
                this.board.addFrenzyOrderPlayer(this.board.getPlayerByCharacter(
                        this.gson.fromJson(frenzyElement.getAsJsonObject(), GameCharacter.class)));
            }
        }
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

    public void saveBoard() {
        FileWriter writer;
        
        StringBuilder jObject = new StringBuilder("{");
        jObject.append("\"board\":" + this.board.toJson() + ",");
        jObject.append(("\"players\":["));
        for(Player player : this.board.getPlayers()){
            jObject.append("{\"player\":" + player.toJson() + ",");
            jObject.append("\"player_position\":{");
            try {
                jObject.append("\"x\":" + player.getPosition().getX() + ",");
                jObject.append("\"y\":" + player.getPosition().getY() + "}},");
            } catch (NullPointerException e) {
                jObject.append("}},");
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("],");
        jObject.append("\"arena\":" + "\"" + this.board.getArena().toJson() + "\"" + ",");
        jObject.append("\"others\":{");
        jObject.append("\"ammos_square\":[");
        for(Square square : this.board.getArena().getAllSquares()) {
            if(!square.isSpawn()) {
                jObject.append("{\"x\":" + square.getX() + ",");
                jObject.append("\"y\":" + square.getY() + ",");
                jObject.append("\"tile\":" + this.gson.toJson(square.getAvailableAmmoTile()) + "},");
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("],");
        jObject.append("\"weapon_square\":{");
        for(Room room : this.board.getArena().getRoomList()) {
            if(room.hasSpawn()) {
                jObject.append("\"" + room.getColor() + "\":" + this.gson.toJson(room.getSpawn().getWeaponsStore()) + ",");
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("}}}");
        try {
            writer = new FileWriter(this.path + "/" + "game_state_data.json");
            writer.write(jObject.toString());
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Error writing data", e);
        }


    }
}
