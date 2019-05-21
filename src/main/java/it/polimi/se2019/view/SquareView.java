package it.polimi.se2019.view;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.polimi.se2019.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SquareView implements Serializable {

    private int x;
    private int y;
    private boolean spawn;
    private RoomColor color;
    private List<GameCharacter> activePlayers;
    private AmmoTile availableAmmoTile;
    private List<Weapon> store;
    private Map<CardinalPoint, Boolean> nearbyAccessibility;
    SquareView(int x, int y, RoomColor color, boolean spawn, Map<CardinalPoint, Boolean> map) {
        this.x = x;
        this.y = y;
        this.spawn = spawn;
        this.color = color;
        this.nearbyAccessibility = map;
        this.activePlayers = new ArrayList<>();
        if(spawn) {
            this.store = new ArrayList<>();
        } else {
            this.store = null;
        }
    }

    public SquareView(int x, int y, RoomColor color, boolean spawn, List<GameCharacter> activePlayers,
                      AmmoTile availableAmmoTile, List<Weapon> store, Map<CardinalPoint, Boolean> map) {
        this.x = x;
        this.y = y;
        this.spawn = spawn;
        this.color = color;
        this.activePlayers = activePlayers;
        this.availableAmmoTile= availableAmmoTile;
        this.store = store;
        this.nearbyAccessibility = map;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    boolean isSpawn() {
        return this.spawn;
    }

    RoomColor color() {
        return this.color;
    }

    AmmoTile getAvailableAmmoTile() {
        return this.availableAmmoTile;
    }

    List<GameCharacter> getActivePlayers() {
        return new ArrayList<>(this.activePlayers);
    }

    void addStoreWeapon(Weapon weapon) {
        this.store.add(weapon);
    }

    void removeStoreWeapon(Weapon weapon) {
        this.store.remove(weapon);
    }

    List<Weapon> getStore() {
        return new ArrayList<>(this.store);
    }

    RoomColor getColor() {
        return this.color;
    }

    void addActivePlayer(GameCharacter player) {
        this.activePlayers.add(player);
    }

    void removeActivePlayer(GameCharacter player) {
        this.activePlayers.remove(player);
    }

    void setAvailableAmmoTile(AmmoTile tile) {
        this.availableAmmoTile = tile;
    }

    void removeAmmoTile() {
        this.availableAmmoTile = null;
    }

    public void print() {

        String format = "|%21|%n";

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("+---------------------+%n"));
        StringBuilder playerRowBuilder = new StringBuilder();
        for(GameCharacter p : this.activePlayers) {
            playerRowBuilder.append(p.getIdentifier() + "  ");
        }
        playerRowBuilder.setLength(playerRowBuilder.length()-2);
        String playerRow = "|" + center(playerRowBuilder.toString(), 21) + "|%n";
        builder.append(String.format(playerRow));
        builder.append(String.format("|                     |%n"));
        String spawnRow;
        if (this.spawn) {
            spawnRow = "|" + center("SPAWN",21) + "|%n";
        } else {
            spawnRow = "";
        }
        String colorRow = "|" + center(String.valueOf(this.color.toString()), 21) + "|%n";
        builder.append(String.format(colorRow));
        builder.append(String.format(spawnRow));
        builder.append(String.format("|                     |%n"));
        if (this.spawn) {
            String weaponsRow;
            for (int i=0; i<3; i++) {
                try {
                    Weapon w = this.store.get(i);
                    weaponsRow = "|" + center(w.toString(), 21) + "|%n";
                } catch (IndexOutOfBoundsException e) {
                    weaponsRow = "|                     |%n";
                }
                builder.append(String.format(weaponsRow));
            }
        } else if (this.availableAmmoTile == null) {
            for (int i=0; i<3; i++) {
                builder.append(String.format("|                     |%n"));
            }
        } else {
            String ammosRow;
            int i = 0;
            if (this.availableAmmoTile.hasPowerup()) {
                ammosRow = "|" + center("POWERUP", 21) + "|%n";
                builder.append(String.format(ammosRow));
                i = 1;
            }
            for (Map.Entry<AmmoType, Integer> ammo : this.availableAmmoTile.getAmmos().entrySet()) {
                for (int j=0; j<ammo.getValue(); j++) {
                    ammosRow = "|" + center(ammo.getKey().toString(), 21) + "|%n";
                    builder.append(String.format(ammosRow));
                }
                i += ammo.getValue();
                if (i == 3) {
                    break;
                }
            }
        }
        builder.append(String.format("+---------------------+%n"));
        System.out.format(builder.toString());
    }

    public static String center(String text, int len){
        if (len <= text.length())
            return text.substring(0, len);
        int before = (len - text.length())/2;
        if (before == 0)
            return String.format("%-" + len + "s", text);
        int rest = len - before;
        return String.format("%" + before + "s%-" + rest + "s", "", text);
    }
}
