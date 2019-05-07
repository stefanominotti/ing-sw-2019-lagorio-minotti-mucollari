package it.polimi.se2019.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public enum Weapon {
    LOCK_RIFLE("lock_rifle.json"),
    ELECTROSCYTHE("electroscythe.json"),
    MACHINE_GUN("machine_gun.json"),
    TRACTOR_BEAM ("tractor_beam.json"),
    THOR("thor.json"),
    VORTEX_CANNON("vortex_cannon.json"),
    FURNACE("furnace.json"),
    PLASMA_GUN ("plasma_gun.json"),
    HEATSEEKER ("heatseeker.json"),
    WHISPER("whisper.json"),
    HELLION ("hellion.json"),
    FLAMETHROWER ("flamethrower.json"),
    ZX_2("zx-2.json"),
    GRENADE_LAUNCHER ("grenade_launcher.json"),
    SHOTGUN("shotgun.json"),
    ROCKET_LAUNCHER("rocket_launcher.json"),
    POWER_GLOVE("power_glove.json"),
    RAILGUN("railgun.json"),
    SHOCKWAVE("shockwave.json"),
    CYBERBLADE("cyberblade.json"),
    SLEDGEHAMMER("sledgehammer.json");

    private static final String ROOT = "/weapons/data/";
    private String name;
    private AmmoType color;
    private Map<AmmoType, Integer> buyCost;
    private List<WeaponEffect> primaryEffect;
    private List<WeaponEffect> alternativeMode;
    private List<WeaponEffect> secondaryEffectOne;
    private List<WeaponEffect> secondaryEffectTwo;

    Weapon(String filename) {
        this.primaryEffect = null;
        this.alternativeMode = null;
        this.secondaryEffectOne = null;
        this.secondaryEffectTwo = null;

        String path = ROOT + filename;
        String jsonString = new Scanner(Weapon.class.getResourceAsStream(path), "UTF-8").useDelimiter("\\A").next();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject)parser.parse(jsonString);
        Gson gson = new Gson();
        Type ammoCostHashMapType = new TypeToken<HashMap<AmmoType, Integer>>(){}.getType();
        Type weaponEffectListType = new TypeToken<List<WeaponEffect>>(){}.getType();

        this.name = gson.fromJson(jsonElement.get("name"), String.class);

        this.color = gson.fromJson(jsonElement.get("color"), AmmoType.class);

        this.buyCost = gson.fromJson(jsonElement.get("buyCost"), ammoCostHashMapType);

        if (jsonElement.has("primaryEffect")) {
            this.primaryEffect = gson.fromJson(jsonElement.get("primaryEffect"), weaponEffectListType);
        }

        if (jsonElement.has("alternativeMode")) {
            this.alternativeMode = gson.fromJson(jsonElement.get("alternativeMode"), weaponEffectListType);
        }

        if (jsonElement.has("secondaryEffectOne")) {
            this.secondaryEffectOne = gson.fromJson(jsonElement.get("secondaryEffectOne"), weaponEffectListType);
        }

        if (jsonElement.has("secondaryEffectTwo")) {
            this.secondaryEffectTwo = gson.fromJson(jsonElement.get("secondaryEffectTwo"), weaponEffectListType);
        }
    }

    public String getName() {
        return this.name;
    }

    public AmmoType getColor() {
        return this.color;
    }

    public Map<AmmoType, Integer> getBuyCost() {
        return new EnumMap<>(this.buyCost);
    }

    public List<WeaponEffect> getPrimaryEffect() {
        return new ArrayList<>(this.primaryEffect);
    }

    public List<WeaponEffect> getAlternativeMode() {
        if (this.alternativeMode == null) {
            throw new IllegalStateException("No alternative mode for this weapon");
        }
        return new ArrayList<>(this.alternativeMode);
    }

    public List<WeaponEffect> getSecondaryEffectOne() {
        if (this.secondaryEffectOne == null) {
            throw new IllegalStateException("No secondary effects for this weapon");
        }
        return new ArrayList<>(this.secondaryEffectOne);
    }

    public List<WeaponEffect> getSecondaryEffectTwo() {
        if (this.secondaryEffectTwo == null) {
            throw new IllegalStateException("No alternative secondary effect for this weapon");
        }
        return new ArrayList<>(this.secondaryEffectTwo);
    }
}
