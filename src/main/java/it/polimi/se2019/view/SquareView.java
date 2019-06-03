package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.polimi.se2019.model.CardinalPoint.*;
import static it.polimi.se2019.view.SquareStringUtils.center;

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

    private static final String lightVertical = "\u2502";
    private static final String overLine = "\u203E";
    private static final String lowLine = "\u005F";
    private static final String middleDot = "\u00B7";
    private static final String space = "\u0020";
    private static final String markedSquare = "\u002A" + "\u002A" + "\u002A";

    private static final String squareSeparator = space + middleDot + space + space + middleDot + space + space + middleDot + space + space + middleDot + space + space + middleDot + space + space + middleDot + space + space + middleDot + space;
    private static final String upperDoor = overLine + overLine + overLine + overLine + overLine + overLine + space + space + space + space + space + space + space + space + space + overLine + overLine + overLine + overLine + overLine + overLine;
    private static final String lowerDoor = lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + space + space + space + space + space + space + space + space + space + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine;
    private static final String upperWall = overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine + overLine;
    private static final String lowerWall = lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine + lowLine;
    private static final String freeWall = space + space + space + space + space + space + space + space + space + space + space + space + space + space + space + space + space + space + space + space + space;

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

    private SquareView getSquareAtDirection(CardinalPoint direction) {
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

    public String toString(boolean marked) {

        StringBuilder builder = new StringBuilder();
        List<String> leftVertical = new ArrayList<>();
        List<String> rightVertical = new ArrayList<>();
        int verticalIndex = 0;
        SquareView leftSquare = getSquareAtDirection(WEST);
        if (this.nearbyAccessibility.get(WEST) && leftSquare.color == this.color) {
            leftVertical.add(overLine);
           for (int i=0; i<8; i++) {
               leftVertical.add(middleDot);
           }
           leftVertical.add(lowLine);
        } else if (this.nearbyAccessibility.get(WEST) && leftSquare.color != this.color) {
            leftVertical.add(lightVertical);
            leftVertical.add(lightVertical);
            leftVertical.add(lightVertical);
            for (int i=0; i<4; i++) {
                leftVertical.add(" ");
            }
            leftVertical.add(lightVertical);
            leftVertical.add(lightVertical);
            leftVertical.add(lightVertical);
        } else {
            for (int i=0; i<10; i++) {
                leftVertical.add(lightVertical);
            }
        }

        SquareView rightSquare = getSquareAtDirection(CardinalPoint.EAST);
        if (rightSquare != null) {
            rightVertical.add(overLine);
            for (int i=0; i<8; i++) {
                rightVertical.add(" ");
            }
            if (getSquareAtDirection(SOUTH) != null) {
                rightVertical.add(" ");
            } else {
                rightVertical.add(lowLine);
            }
        } else {
            for (int i=0; i<10; i++) {
                rightVertical.add(lightVertical);
            }
        }

        String left = leftVertical.get(verticalIndex);
        String right = rightVertical.get(verticalIndex);
        verticalIndex++;
        if (this.nearbyAccessibility.get(NORTH) && getSquareAtDirection(NORTH).color == this.color) {
            if (getSquareAtDirection(EAST) != null) {
                right = " ";
            }
            builder.append(left + squareSeparator + right + "\n");
        } else if (this.nearbyAccessibility.get(NORTH) && getSquareAtDirection(NORTH).color != this.color) {
            if (getSquareAtDirection(EAST) != null) {
                right = overLine;
            }
            builder.append(left + upperDoor + right + "\n");
        } else {
            if (getSquareAtDirection(EAST) != null) {
                right = overLine;
            }
            builder.append(left + upperWall + right + "\n");
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
        if (marked) {
            builder.append(left + center(markedSquare, 21) + right + "\n");
        } else {
            builder.append(left + freeWall + right + "\n");
        }

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        String spawnRow;
        if (this.spawn) {
            spawnRow = left + center("SPAWN",21) + right + "\n";
        } else {
            spawnRow = left + freeWall + right + "\n";
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
        builder.append(left + freeWall + right + "\n");

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
                    weaponsRow = left + freeWall + right + "\n";
                }
                builder.append(weaponsRow);
            }
        } else if (this.availableAmmoTile == null) {
            for (int i=0; i<3; i++) {
                left = leftVertical.get(verticalIndex);
                right = rightVertical.get(verticalIndex);
                verticalIndex++;
                builder.append(left + freeWall + right + "\n");
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
        if (getSquareAtDirection(SOUTH) != null) {
            if(this.nearbyAccessibility.get(WEST) && getSquareAtDirection(WEST).color == this.color) {
                left = " ";
            }
            builder.append(left + freeWall + right + "\n");
        } else if (this.nearbyAccessibility.get(SOUTH) && getSquareAtDirection(SOUTH).color == this.color) {
            builder.append(left + squareSeparator + right + "\n");
        } else if (this.nearbyAccessibility.get(SOUTH) && getSquareAtDirection(SOUTH).color != this.color) {
            builder.append(left + lowerDoor + right + "\n");
        } else {
            builder.append(left + lowerWall + right + "\n");
        }

        return builder.toString();
    }
}
