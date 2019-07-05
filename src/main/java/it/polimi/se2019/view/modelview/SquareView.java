package it.polimi.se2019.view.modelview;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.RoomColor;
import it.polimi.se2019.model.playerassets.AmmoTile;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.se2019.model.arena.CardinalPoint.*;
import static it.polimi.se2019.view.SquareStringUtils.center;

/**
 * Class for handling square view
 *  @author stefanominotti
 */
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

    private static final String LIGHT_VERTICAL = "\u2502";
    private static final String OVER_LINE = "\u203E";
    private static final String LOW_LINE = "\u005F";
    private static final String MIDDLE_DOT = "\u00B7";
    private static final String SPACE = "\u0020";
    private static final String MARKED_SQUARE = "\u002A" + "\u002A" + "\u002A";

    private static final String SQUARE_SEPARATOR = SPACE + MIDDLE_DOT + SPACE + SPACE + MIDDLE_DOT + SPACE + SPACE +
            MIDDLE_DOT + SPACE + SPACE + MIDDLE_DOT + SPACE + SPACE + MIDDLE_DOT + SPACE + SPACE + MIDDLE_DOT + SPACE +
            SPACE + MIDDLE_DOT + SPACE;

    private static final String UPPER_DOOR = OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE +
            SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + OVER_LINE + OVER_LINE + OVER_LINE +
            OVER_LINE + OVER_LINE + OVER_LINE;

    private static final String LOWER_DOOR = LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + SPACE +
            SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE +
            LOW_LINE + LOW_LINE;

    private static final String UPPER_WALL = OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE +
            OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE +
            OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE + OVER_LINE;

    private static final String LOWER_WALL = LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE
            + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE +
            LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE + LOW_LINE;

    private static final String FREE_WALL = SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE +
            SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE;

    /**
     * Class constructor, it builds a square view to show the arena
     * @param x coordinate of the square
     * @param y coordinate of the square
     * @param color of the room where the square is placed
     * @param spawn true if the square is a spawn point, else false
     * @param map with cardinal points and true if the direction is accessible from the square, else false
     */
    public SquareView(int x, int y, RoomColor color, boolean spawn, Map<CardinalPoint, Boolean> map) {
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

    /**
     * Class constructor, it builds a square view when the game is resumed after a save
     * @param x coordinate of the square
     * @param y coordinate of the square
     * @param color of the room where the square is placed
     * @param spawn true if the square is a spawn point, else false
     * @param activePlayers List of the active game characters on the square
     * @param availableAmmoTile Ammo tile on the square
     * @param map with cardinal points and true if the direction is accessible from the square, else false
     */
    public SquareView(int x, int y, RoomColor color, boolean spawn, List<GameCharacter> activePlayers,
                      AmmoTile availableAmmoTile, List<Weapon> store, Map<CardinalPoint, Boolean> map) {
        this.x = x;
        this.y = y;
        this.spawn = spawn;
        this.color = color;
        this.activePlayers = activePlayers;
        this.availableAmmoTile = availableAmmoTile;
        this.store = store;
        this.nearbyAccessibility = map;
    }

    /**
     * Sets the board view for the square view
     * @param board view to be set
     */
    public void setBoard(BoardView board) {
        this.board = board;
    }

    /**
     * Gets the x coordinate of the square view
     * @return x coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y coordinate of the square view
     * @return y coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Knows if the square is a spawn point
     * @return true if it is, else false
     */
    public boolean isSpawn() {
        return this.spawn;
    }

    /**
     * Gets the ammo tile of the square
     * @return the ammo tile of the square
     */
    public AmmoTile getAvailableAmmoTile() {
        return this.availableAmmoTile;
    }

    /**
     * Gets players in the square
     * @return List of players in the square
     */
    public List<GameCharacter> getActivePlayers() {
        return new ArrayList<>(this.activePlayers);
    }

    /**
     * Adds a weapon to the weapons store of the square
     * @param weapon to add to the square store
     */
    public void addStoreWeapon(Weapon weapon) {
        this.store.add(weapon);
    }

    /**
     * Removes a weapon from the weapons store of the square
     * @param weapon to remove from the square store
     */
    public void removeStoreWeapon(Weapon weapon) {
        this.store.remove(weapon);
    }

    /**
     * Gets the weapons store of the square
     * @return List of weapons of the square
     */
    public List<Weapon> getStore() {
        return new ArrayList<>(this.store);
    }

    /**
     *
     * @return
     */
    public RoomColor getColor() {
        return this.color;
    }

    public void addActivePlayer(GameCharacter player) {
        this.activePlayers.add(player);
    }

    public void removeActivePlayer(GameCharacter player) {
        this.activePlayers.remove(player);
    }

    public void setAvailableAmmoTile(AmmoTile tile) {
        this.availableAmmoTile = tile;
    }

    public void removeAmmoTile() {
        this.availableAmmoTile = null;
    }

    public SquareView getSquareAtDirection(CardinalPoint direction) {
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
        String toAppend;
        int verticalIndex = 0;
        SquareView leftSquare = getSquareAtDirection(WEST);
        if (this.nearbyAccessibility.get(WEST) && leftSquare != null && leftSquare.getColor() == this.color) {
            leftVertical.add(OVER_LINE);
           for (int i=0; i<8; i++) {
               leftVertical.add(MIDDLE_DOT);
           }
           leftVertical.add(LOW_LINE);
        } else if (this.nearbyAccessibility.get(WEST) && leftSquare != null && leftSquare.getColor() != this.color) {
            leftVertical.add(LIGHT_VERTICAL);
            leftVertical.add(LIGHT_VERTICAL);
            leftVertical.add(LIGHT_VERTICAL);
            for (int i=0; i<4; i++) {
                leftVertical.add(" ");
            }
            leftVertical.add(LIGHT_VERTICAL);
            leftVertical.add(LIGHT_VERTICAL);
            leftVertical.add(LIGHT_VERTICAL);
        } else {
            for (int i=0; i<10; i++) {
                leftVertical.add(LIGHT_VERTICAL);
            }
        }

        SquareView rightSquare = getSquareAtDirection(CardinalPoint.EAST);
        if (rightSquare != null) {
            rightVertical.add(OVER_LINE);
            for (int i=0; i<8; i++) {
                rightVertical.add(" ");
            }
            if (getSquareAtDirection(SOUTH) != null) {
                rightVertical.add(" ");
            } else {
                rightVertical.add(LOW_LINE);
            }
        } else {
            for (int i=0; i<10; i++) {
                rightVertical.add(LIGHT_VERTICAL);
            }
        }

        String left = leftVertical.get(verticalIndex);
        String right = rightVertical.get(verticalIndex);
        verticalIndex++;
        SquareView northSquare = getSquareAtDirection(NORTH);
        if (this.nearbyAccessibility.get(NORTH) && northSquare != null && northSquare.getColor() == this.color) {
            if (getSquareAtDirection(EAST) != null) {
                right = " ";
            }
            toAppend = left + SQUARE_SEPARATOR + right + "\n";
            builder.append(toAppend);
        } else if (this.nearbyAccessibility.get(NORTH) && northSquare != null && northSquare.getColor() != this.color) {
            if (getSquareAtDirection(EAST) != null) {
                right = OVER_LINE;
            }
            toAppend = left + UPPER_DOOR + right + "\n";
            builder.append(toAppend);
        } else {
            if (getSquareAtDirection(EAST) != null) {
                right = OVER_LINE;
            }
            toAppend = left + UPPER_WALL + right + "\n";
            builder.append(toAppend);
        }

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        StringBuilder playerRowBuilder = new StringBuilder();
        for(GameCharacter p : this.activePlayers) {
            toAppend = p.getIdentifier() + "  ";
            playerRowBuilder.append(toAppend);
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
            toAppend = left + center(MARKED_SQUARE, 21) + right + "\n";
            builder.append(toAppend);
        } else {
            toAppend = left + FREE_WALL + right + "\n";
            builder.append(toAppend);
        }

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        String spawnRow;
        if (this.spawn) {
            spawnRow = left + center("SPAWN",21) + right + "\n";
        } else {
            spawnRow = left + FREE_WALL + right + "\n";
        }
        builder.append(spawnRow);

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        String colorRow = left + center(String.valueOf(this.color.toString()), 21) + right + "\n";
        builder.append(colorRow);

        left = leftVertical.get(verticalIndex);
        right = rightVertical.get(verticalIndex);
        verticalIndex++;
        toAppend = left + FREE_WALL + right + "\n";
        builder.append(toAppend);

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
                    weaponsRow = left + FREE_WALL + right + "\n";
                }
                builder.append(weaponsRow);
            }
        } else if (this.availableAmmoTile == null) {
            for (int i=0; i<3; i++) {
                left = leftVertical.get(verticalIndex);
                right = rightVertical.get(verticalIndex);
                verticalIndex++;
                toAppend = left + FREE_WALL + right + "\n";
                builder.append(toAppend);
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
        SquareView southSquare = getSquareAtDirection(SOUTH);
        SquareView westSquare = getSquareAtDirection(WEST);
        if (getSquareAtDirection(SOUTH) != null) {
            if(this.nearbyAccessibility.get(WEST) && westSquare != null && westSquare.getColor() == this.color) {
                left = " ";
            }
            toAppend = left + FREE_WALL + right + "\n";
            builder.append(toAppend);
        } else if (this.nearbyAccessibility.get(SOUTH) && southSquare != null && southSquare.getColor() == this.color) {
            toAppend = left + SQUARE_SEPARATOR + right + "\n";
            builder.append(toAppend);
        } else if (this.nearbyAccessibility.get(SOUTH) && southSquare != null && southSquare.getColor() != this.color) {
            toAppend = left + LOWER_DOOR + right + "\n";
            builder.append(toAppend);
        } else {
            toAppend = left + LOWER_WALL + right + "\n";
            builder.append(toAppend);
        }

        return builder.toString();
    }
}
