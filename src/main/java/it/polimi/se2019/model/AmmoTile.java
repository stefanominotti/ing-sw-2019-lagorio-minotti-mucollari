package it.polimi.se2019.model;

import java.util.EnumMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Scanner;

public class AmmoTile {

    private static final String ROOT = "arenas/data/arena_";
    private final boolean powerup;
    private final Map<AmmoType, Integer> ammos;

    AmmoTile(String filename) {
        String path = ROOT + filename + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        String jsonString = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject)parser.parse(jsonString);
        Gson gson = new Gson();
        Type ammosColor = new TypeToken<Map<AmmoType, Integer>>(){}.getType();

        this.powerup = gson.fromJson(jsonElement.get("powerup"), Boolean.class);
        this.ammos = gson.fromJson(jsonElement.get("ammos"), ammosColor);
    }

    AmmoTile(boolean powerup, Map<AmmoType, Integer> ammos) {
        this.powerup = powerup;
        this.ammos = new EnumMap<>(AmmoType.class);
        this.ammos.putAll(ammos);
    }

    public Map<AmmoType, Integer> getAmmos() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.ammos);
        return returnMap;
    }

    public boolean hasPowerup() {
        return this.powerup;
    }
}
