package it.polimi.se2019.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Scanner;

public class AmmoTile implements Serializable {

    private final boolean powerup;
    private final Map<AmmoType, Integer> ammos;

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
