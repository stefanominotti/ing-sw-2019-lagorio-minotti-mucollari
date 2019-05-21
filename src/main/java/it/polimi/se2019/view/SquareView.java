package it.polimi.se2019.view;

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

    public String toString() {

        String left = "|";
        String right = "|";
        StringBuilder builder = new StringBuilder();
        if (this.nearbyAccessibility.get(CardinalPoint.EAST)) {
            right = " ";
        }
        if (this.nearbyAccessibility.get(CardinalPoint.WEST)) {
            left = " ";
        }
        if (this.nearbyAccessibility.get(CardinalPoint.NORTH)) {
            builder.append("+--                 --+\n");
        } else {
            builder.append("+---------------------+\n");
        }
        StringBuilder playerRowBuilder = new StringBuilder();
        for(GameCharacter p : this.activePlayers) {
            playerRowBuilder.append(p.getIdentifier() + "  ");
        }
        if (!this.activePlayers.isEmpty()) {
            playerRowBuilder.setLength(playerRowBuilder.length() - 2);
        }
        String playerRow = "|" + center(playerRowBuilder.toString(), 21) + "|\n";
        builder.append(playerRow);
        builder.append(left + "                     " + right + "\n");
        String spawnRow;
        if (this.spawn) {
            spawnRow = left + center("SPAWN",21) + right + "\n";
        } else {
            spawnRow = left + "                     " + right + "\n";
        }
        builder.append(String.format(spawnRow));
        String colorRow = left + center(String.valueOf(this.color.toString()), 21) + right + "\n";
        builder.append(colorRow);
        builder.append(left + "                     " + right + "\n");
        if (this.spawn) {
            String weaponsRow;
            for (int i=0; i<3; i++) {
                if (i == 2) {
                    left = "|";
                    right = "|";
                }
                try {
                    Weapon w = this.store.get(i);
                    weaponsRow = left + center(w.toString(), 21) + right + "\n";
                } catch (IndexOutOfBoundsException e) {
                    weaponsRow = left + "                     " + right + "\n";
                }
                builder.append(weaponsRow);
            }
        } else if (this.availableAmmoTile == null) {
            for (int i=0; i<3; i++) {
                if (i != 2) {
                    builder.append(left + "                     " + right + "\n");
                } else {
                    builder.append("|                     |\n");
                }
            }
        } else {
            String ammosRow;
            int i = 0;
            if (this.availableAmmoTile.hasPowerup()) {
                ammosRow = left + center("POWERUP", 21) + right + "\n";
                builder.append(ammosRow);
                i = 1;
            }
            for (Map.Entry<AmmoType, Integer> ammo : this.availableAmmoTile.getAmmos().entrySet()) {
                for (int j=0; j<ammo.getValue(); j++) {
                    if (i == 2) {
                        left = "|";
                        right = "|";
                    }
                    ammosRow = left + center(ammo.getKey().toString(), 21) + right + "\n";
                    builder.append(ammosRow);
                    i++;
                }
                if (i == 3) {
                    break;
                }
            }
        }
        if (this.nearbyAccessibility.get(CardinalPoint.SOUTH)) {
            builder.append("+--                 --+\n");
        } else {
            builder.append("+---------------------+\n");
        }
        return builder.toString();
    }

    static String center(String text, int len){
        if (len <= text.length())
            return text.substring(0, len);
        int before = (len - text.length())/2;
        if (before == 0)
            return String.format("%-" + len + "s", text);
        int rest = len - before;
        return String.format("%" + before + "s%-" + rest + "s", "", text);
    }
}
