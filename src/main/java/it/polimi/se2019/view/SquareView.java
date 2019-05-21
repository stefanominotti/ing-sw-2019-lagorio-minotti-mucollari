package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.polimi.se2019.model.CardinalPoint.NORTH;
import static it.polimi.se2019.model.CardinalPoint.WEST;

public class SquareView implements Serializable {

    private int x;
    private int y;
    private boolean spawn;
    private RoomColor color;
    private List<GameCharacter> activePlayers;
    private AmmoTile availableAmmoTile;
    private List<Weapon> store;
    private Map<CardinalPoint, Boolean> nearbyAccessibility;
    private BoardView board;

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

    public void setBoard(BoardView board) {
        this.board = board;
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

    SquareView getSquareAtDirection(CardinalPoint direction) {
        switch(direction) {
            case NORTH:
                return this.board.getSquareByCoordinates(this.x, this.y - 1);
            case SOUTH:
                return this.board.getSquareByCoordinates(this.x, this.y + 1);
            case WEST:
                return this.board.getSquareByCoordinates(this.x - 1, this.y);
            case EAST:
                return this.board.getSquareByCoordinates(this.x + 1, this.y);
        }
        return null;
    }

    public String toString() {

        StringBuilder builder = new StringBuilder();
        List<String> leftVertical = new ArrayList<>();
        List<String> rightVertical = new ArrayList<>();
        int verticalIndex = 0;
        SquareView leftSquare = getSquareAtDirection(WEST);
        if (this.nearbyAccessibility.get(WEST) && leftSquare.color == this.color) {
            leftVertical.add("‾");
           for (int i=0; i<8; i++) {
               leftVertical.add(" ");
           }
           leftVertical.add("_");
        } else if (this.nearbyAccessibility.get(WEST) && leftSquare.color != this.color) {
            leftVertical.add("│");
            leftVertical.add("│");
            leftVertical.add("│");
            for (int i=0; i<4; i++) {
                leftVertical.add(" ");
            }
            leftVertical.add("│");
            leftVertical.add("│");
            leftVertical.add("│");
        } else {
            leftVertical.add("│");
            for (int i=0; i<8; i++) {
                leftVertical.add("│");
            }
            leftVertical.add("│");
        }

        SquareView rightSquare = getSquareAtDirection(CardinalPoint.EAST);
        if (this.nearbyAccessibility.get(CardinalPoint.EAST) && rightSquare.color == this.color) {
            rightVertical.add("‾");
            for (int i=0; i<8; i++) {
                rightVertical.add(" ");
            }
            rightVertical.add("_");
        } else if (this.nearbyAccessibility.get(CardinalPoint.EAST) && rightSquare.color != this.color) {
            rightVertical.add("│");
            rightVertical.add("│");
            rightVertical.add("│");
            for (int i=0; i<4; i++) {
                rightVertical.add(" ");
            }
            rightVertical.add("│");
            rightVertical.add("│");
            rightVertical.add("│");
        } else {
            rightVertical.add("│");
            for (int i=0; i<8; i++) {
                rightVertical.add("│");
            }
            rightVertical.add("│");
        }

        String left = leftVertical.get(verticalIndex);
        String right = rightVertical.get(verticalIndex);
        verticalIndex++;
        if (this.nearbyAccessibility.get(NORTH) && getSquareAtDirection(NORTH).color == this.color) {
            builder.append(left + "                     " + right + "\n");
        } else if (this.nearbyAccessibility.get(NORTH) && getSquareAtDirection(NORTH).color != this.color) {
            builder.append(left + "‾‾‾‾‾‾         ‾‾‾‾‾‾" + right + "\n");
        } else {
            builder.append(left + "‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾" + right + "\n");
        }

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        StringBuilder playerRowBuilder = new StringBuilder();
        for(GameCharacter p : this.activePlayers) {
            playerRowBuilder.append(p.getIdentifier() + "  ");
        }
        if (!this.activePlayers.isEmpty()) {
            playerRowBuilder.setLength(playerRowBuilder.length() - 2);
        }
        String playerRow = left + center(playerRowBuilder.toString(), 21) + right + "\n";
        builder.append(playerRow);

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        builder.append(left + "                     " + right + "\n");

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        String spawnRow;
        if (this.spawn) {
            spawnRow = left + center("SPAWN",21) + right + "\n";
        } else {
            spawnRow = left + "                     " + right + "\n";
        }
        builder.append(String.format(spawnRow));

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        String colorRow = left + center(String.valueOf(this.color.toString()), 21) + right + "\n";
        builder.append(colorRow);

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        builder.append(left + "                     " + right + "\n");

        if (this.spawn) {
            String weaponsRow;
            for (int i=0; i<3; i++) {
                left = leftVertical.get(verticalIndex);
                right = rightVertical.get(verticalIndex);
                verticalIndex++;
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
                left = leftVertical.get(verticalIndex);
                right = rightVertical.get(verticalIndex);
                verticalIndex++;
                builder.append(left + "                     " + right + "\n");
            }
        } else {
            String ammosRow;
            int i = 0;
            if (this.availableAmmoTile.hasPowerup()) {
                left = leftVertical.get(verticalIndex);
                right = rightVertical.get(verticalIndex);
                verticalIndex++;
                ammosRow = left + center("POWERUP", 21) + right + "\n";
                builder.append(ammosRow);
                i = 1;
            }
            for (Map.Entry<AmmoType, Integer> ammo : this.availableAmmoTile.getAmmos().entrySet()) {
                for (int j=0; j<ammo.getValue(); j++) {
                    left = leftVertical.get(verticalIndex);
                    right = rightVertical.get(verticalIndex);
                    verticalIndex++;
                    ammosRow = left + center(ammo.getKey().toString(), 21) + right + "\n";
                    builder.append(ammosRow);
                    i++;
                }
                if (i == 3) {
                    break;
                }
            }
        }

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        if (this.nearbyAccessibility.get(CardinalPoint.SOUTH) && getSquareAtDirection(CardinalPoint.SOUTH).color == this.color) {
            builder.append(left + "                     " + right + "\n");
        } else if (this.nearbyAccessibility.get(CardinalPoint.SOUTH) && getSquareAtDirection(CardinalPoint.SOUTH).color != this.color) {
            builder.append(left + "______         ______" + right + "\n");
        } else {
            builder.append(left + "_____________________" + right + "\n");
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
