package it.polimi.se2019.server;

import com.google.gson.*;
import it.polimi.se2019.model.*;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class GameLoader {

    private static final String PATH = "documents/";
    private FileReader reader;
    private FileWriter writer;
    private Board board;
    private List<Player> players;

    private JsonParser parser;
    private JsonObject jsonElement;
    private Gson gson;

    public GameLoader() {

        this.parser = new JsonParser();
        this.gson = new Gson();
    }

    public Board loadBoard() {

        try {
            this.reader = new FileReader(PATH + "game_state_data.json");
        } catch (IOException E) {
            return new Board();
        }
        this.jsonElement = (JsonObject)parser.parse(reader);
        this.players = new ArrayList<>();
        JsonArray frenezyOrder = jsonElement.getAsJsonObject("board_other").getAsJsonArray("finalFrenezyOrder");
        JsonArray jsonPlayers = jsonElement.getAsJsonArray("players");
        JsonObject weapon_square = jsonElement.getAsJsonObject("others").getAsJsonObject("weapon_square");
        JsonArray ammos_square = jsonElement.getAsJsonObject("others").getAsJsonArray("ammos_square");


        this.board = gson.fromJson(jsonElement.get("board"), Board.class);
        this.board.loadArena(gson.fromJson(jsonElement.get("arena"), String.class));
        for (JsonElement jsonPlayer : jsonPlayers) {
            Player player = gson.fromJson(jsonPlayer.getAsJsonObject().get("player"), Player.class);
            int posX = gson.fromJson(
                    jsonPlayer.getAsJsonObject().get("player_position").getAsJsonObject().get("x"), int.class);
            int posY = gson.fromJson(
                    jsonPlayer.getAsJsonObject().get("player_position").getAsJsonObject().get("y"), int.class);
            Square square = this.board.getArena().getSquareByCoordinate(posX, posY);
            this.board.movePlayer(player, square);
            for (WeaponCard weapon : player.getWeapons()) {
                weapon.setOwner(player);
            }
            if (player.isDead()) {
                this.board.addDeadPlayer(player);
            }
            this.players.add(player);
        }
        this.board.setPlayers(this.players);
        if (frenezyOrder != null) {
            for (JsonElement frenezyElement : frenezyOrder) {
                this.board.addFrenezyOrderPlayer(this.board.getPlayerByCharacter(
                        gson.fromJson(frenezyElement.getAsJsonObject(), GameCharacter.class)));
            }
        }
        for (JsonElement ammoSquare : ammos_square) {
            int posX = gson.fromJson(ammoSquare.getAsJsonObject().getAsJsonObject().get("x"), int.class);
            int posY = gson.fromJson(ammoSquare.getAsJsonObject().getAsJsonObject().get("y"), int.class);
            this.board.getArena().getSquareByCoordinate(posX, posY).addAmmoTile(
                    gson.fromJson(ammoSquare.getAsJsonObject().get("tile"), AmmoTile.class));
        }
        for (Room room : this.board.getArena().getRoomList()) {
            if (room.hasSpawn()) {
                for (JsonElement weapon : weapon_square.getAsJsonArray(room.getColor().toString())) {
                    room.getSpawn().addWeapon(gson.fromJson(weapon.getAsJsonObject(), WeaponCard.class));
                }
            }
        }
        return this.board;
    }

    public void saveBoard() {
        StringBuilder jObject = new StringBuilder("{");
        jObject.append("\"board\":" + this.board.toJson() + ",");
        jObject.append(("\"players\":["));
        for(Player player : this.board.getPlayers()){
            jObject.append("{\"player\":" + player.toJson() + ",");
            jObject.append("\"player_position\":{");
            jObject.append("\"x\":" + player.getPosition().getX() + ",");
            jObject.append("\"y\":" + player.getPosition().getX() + "}},");
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("],");
        jObject.append("\"arena\":" + "\"" + this.board.getArena().toJson()+ "\"" + ",");
        jObject.append("\"others\":{");
        jObject.append("\"ammos_square\":[");
        for(Square square : this.board.getArena().getAllSquares()) {
            if(!square.isSpawn()) {
                jObject.append("{\"x\":" + square.getX() + ",");
                jObject.append("\"y\":" + square.getX() + ",");
                jObject.append("\"tile\":" + gson.toJson(square.getAvailableAmmoTile()) + "},");
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("],");
        jObject.append("\"weapon_square\":{");
        for(Room room : this.board.getArena().getRoomList()) {
            if(room.hasSpawn()) {
                jObject.append("\"" + room.getColor() + "\":" + gson.toJson(room.getSpawn().getWeaponsStore()) + ",");
            }
        }
        jObject.deleteCharAt(jObject.length() - 1);
        jObject.append("}}}");
        try {
            this.writer = new FileWriter(PATH + "game_state_data.json");
            this.writer.write(jObject.toString());
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
