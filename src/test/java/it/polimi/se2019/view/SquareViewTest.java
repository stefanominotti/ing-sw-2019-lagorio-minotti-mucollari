package it.polimi.se2019.view;

import it.polimi.se2019.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class SquareViewTest {

    private SquareView squareNotSpawn;
    private SquareView squareSpawn;

    @Before
    public void setUp() {
        Map<CardinalPoint, Boolean> visibility = new EnumMap<>(CardinalPoint.class);
        visibility.put(CardinalPoint.WEST, true);
        visibility.put(CardinalPoint.NORTH, false);
        visibility.put(CardinalPoint.EAST, false);
        visibility.put(CardinalPoint.SOUTH, false);
        this.squareNotSpawn = new SquareView(0, 0, RoomColor.YELLOW, false, visibility);
        visibility.put(CardinalPoint.EAST, true);
        visibility.put(CardinalPoint.WEST, false);
        this.squareSpawn = new SquareView(1, 0, RoomColor.YELLOW, true, visibility);
        new BoardView(3, Arrays.asList(this.squareSpawn, this.squareNotSpawn), 1);
    }

    @Test
    public void coordinatesTest() {
        assertEquals(0, this.squareNotSpawn.getX());
        assertEquals(0, this.squareNotSpawn.getY());
    }

    @Test
    public void spawnTest() {
        assertFalse(this.squareNotSpawn.isSpawn());
    }

    @Test
    public void availableAmmoTileTest() {
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.YELLOW, 1);
        ammos.put(AmmoType.RED, 2);
        ammos.put(AmmoType.BLUE, 0);
        AmmoTile tile = new AmmoTile(false, ammos);
        this.squareNotSpawn.setAvailableAmmoTile(tile);
        Assert.assertEquals(tile, this.squareNotSpawn.getAvailableAmmoTile());
        this.squareNotSpawn.removeAmmoTile();
        assertNull(this.squareNotSpawn.getAvailableAmmoTile());
    }

    @Test
    public void activePlayersTest() {
        this.squareNotSpawn.addActivePlayer(GameCharacter.D_STRUCT_OR);
        Assert.assertEquals(1, this.squareNotSpawn.getActivePlayers().size());
        Assert.assertEquals(GameCharacter.D_STRUCT_OR, this.squareNotSpawn.getActivePlayers().get(0));
        this.squareNotSpawn.addActivePlayer(GameCharacter.BANSHEE);
        Assert.assertEquals(2, this.squareNotSpawn.getActivePlayers().size());
        Assert.assertEquals(GameCharacter.BANSHEE, this.squareNotSpawn.getActivePlayers().get(1));
        this.squareNotSpawn.removeActivePlayer(GameCharacter.D_STRUCT_OR);
        Assert.assertEquals(1, this.squareNotSpawn.getActivePlayers().size());
        Assert.assertEquals(GameCharacter.BANSHEE, this.squareNotSpawn.getActivePlayers().get(0));
    }

    @Test
    public void getColorTest() {
        assertEquals(RoomColor.YELLOW, this.squareNotSpawn.getColor());
    }

    @Test
    public void weaponsStoreTest() {
        Weapon weapon = Weapon.LOCK_RIFLE;
        this.squareSpawn.addStoreWeapon(weapon);
        Assert.assertEquals(1, this.squareSpawn.getStore().size());
        Assert.assertEquals(weapon, this.squareSpawn.getStore().get(0));
        this.squareSpawn.removeStoreWeapon(weapon);
        Assert.assertEquals(0, this.squareSpawn.getStore().size());
    }

    @Test
    public void squareAtDirectionTest() {
        Map<CardinalPoint, Boolean> visibility = new EnumMap<>(CardinalPoint.class);
        List<SquareView> squares = new ArrayList<>();
        visibility.put(CardinalPoint.WEST, false);
        visibility.put(CardinalPoint.NORTH, false);
        visibility.put(CardinalPoint.EAST, false);
        visibility.put(CardinalPoint.SOUTH, false);
        SquareView square = new SquareView(1, 1, RoomColor.YELLOW, false, visibility);
        SquareView west = new SquareView(0, 1, RoomColor.YELLOW, false, visibility);
        SquareView north = new SquareView(1, 0, RoomColor.YELLOW, false, visibility);
        SquareView south = new SquareView(1, 2, RoomColor.YELLOW, false, visibility);
        SquareView east = new SquareView(2, 1, RoomColor.YELLOW, false, visibility);
        new BoardView(3, Arrays.asList(south, north, west, east, square), 1);
        assertEquals(west, square.getSquareAtDirection(CardinalPoint.WEST));
        assertEquals(east, square.getSquareAtDirection(CardinalPoint.EAST));
        assertEquals(south, square.getSquareAtDirection(CardinalPoint.SOUTH));
        assertEquals(north, square.getSquareAtDirection(CardinalPoint.NORTH));
    }

    @Test
    public void toStringTest() {
        assertEquals("│‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾│\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│        SPAWN        │\n" +
                "│       YELLOW        │\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│_____________________│\n", this.squareSpawn.toString(false));
        assertEquals("│‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾│\n" +
                "│                     │\n" +
                "│         ***         │\n" +
                "│        SPAWN        │\n" +
                "│       YELLOW        │\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│_____________________│\n", this.squareSpawn.toString(true));
        this.squareSpawn.addStoreWeapon(Weapon.MACHINE_GUN);
        this.squareSpawn.addStoreWeapon(Weapon.ZX_2);
        this.squareSpawn.addStoreWeapon(Weapon.FLAMETHROWER);
        assertEquals("│‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾│\n" +
                "│                     │\n" +
                "│                     │\n" +
                "│        SPAWN        │\n" +
                "│       YELLOW        │\n" +
                "│                     │\n" +
                "│     MACHINE_GUN     │\n" +
                "│        ZX_2         │\n" +
                "│    FLAMETHROWER     │\n" +
                "│_____________________│\n", this.squareSpawn.toString(false));
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.YELLOW, 1);
        ammos.put(AmmoType.RED, 2);
        ammos.put(AmmoType.BLUE, 0);
        AmmoTile tile = new AmmoTile(false, ammos);
        this.squareNotSpawn.setAvailableAmmoTile(tile);
        this.squareNotSpawn.addActivePlayer(GameCharacter.BANSHEE);
        assertEquals("│‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\n" +
                "│          β           \n" +
                "│                      \n" +
                "│                      \n" +
                "│       YELLOW         \n" +
                "│                      \n" +
                "│         RED          \n" +
                "│         RED          \n" +
                "│       YELLOW         \n" +
                "│______________________\n", this.squareNotSpawn.toString(false));
    }
}
