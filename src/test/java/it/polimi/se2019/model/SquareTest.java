package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class SquareTest {
    private Square squareSpawn;
    private Square squareNotSpawn;
    private Board board;

    @Before
    public void setUp() {
        this.board = new Board();
        this.board.loadArena("1");
        Arena arena = this.board.getArena();
        this.squareNotSpawn = arena.getSquareByCoordinate(0,0);
        this.squareSpawn = arena.getSquareByCoordinate(2,0);
    }

    @Test
    public void getCoordinatesTest () {
        int x = this.squareNotSpawn.getX();
        int y = this.squareNotSpawn.getY();
        assertEquals(0, x);
        assertEquals(0, y);
    }

    @Test
    public void isSpawnTest () {
        assertTrue(this.squareSpawn.isSpawn());
        assertFalse(this.squareNotSpawn.isSpawn());
    }

    @Test
    public void nearbySquaresTest () {
        this.squareNotSpawn.setNearbySquares();
        Map<CardinalPoint, Square> nearbySquares = this.squareNotSpawn.getNearbySquares();
        Map<CardinalPoint, Boolean> nearbyAccessibility = this.squareNotSpawn.getNearbyAccessibility();
        for(CardinalPoint cardinal : CardinalPoint.values()){
            switch (cardinal) {
                case SOUTH:
                    assertNotNull(nearbySquares.get(cardinal));
                    assertTrue(nearbyAccessibility.get(cardinal));
                    break;
                case NORTH:
                    assertNull(nearbySquares.get(cardinal));
                    assertFalse(nearbyAccessibility.get(cardinal));
                    break;
                case WEST:
                    assertNull(nearbySquares.get(cardinal));
                    assertFalse(nearbyAccessibility.get(cardinal));
                    break;
                case EAST:
                    assertNotNull(nearbySquares.get(cardinal));
                    assertTrue(nearbyAccessibility.get(cardinal));
                    break;
            }
        }
    }

    @Test
    public void roomTest () {
        RoomColor color = this.squareNotSpawn.getRoom().getColor();
        assertEquals(RoomColor.BLUE, color);
    }

    @Test
    public void weaponsStoreTest() {
        WeaponCard weaponCard = new WeaponCard(Weapon.LOCK_RIFLE);
        this.squareSpawn.addWeapon(weaponCard);
        assertEquals(1, this.squareSpawn.getWeaponsStore().size());
        assertEquals(weaponCard, this.squareSpawn.getWeaponsStore().get(0));
        this.squareSpawn.removeWeapon(weaponCard);
        assertEquals(0, this.squareSpawn.getWeaponsStore().size());
    }

    @Test
    public void availableAmmoTileTest() {
        this.board.fillAmmosDeck();
        AmmoTile tile = this.board.getAmmosDeck().get(0);
        assertNull(this.squareSpawn.getAvailableAmmoTile());
        this.squareNotSpawn.addAmmoTile(tile);
        assertEquals(tile, this.squareNotSpawn.getAvailableAmmoTile());
        this.squareNotSpawn.removeAmmoTile();
        assertNull(this.squareSpawn.getAvailableAmmoTile());
    }

    @Test
    public void pathsDistanceToSquareTest() {
        assertEquals(3, this.squareNotSpawn.pathsTo(this.squareSpawn).size());
        assertEquals(2, this.squareNotSpawn.minimumDistanceFrom(this.squareSpawn));
    }

    @Test
    public void canSeeTest() {
        assertTrue(this.squareNotSpawn.canSee(this.board.getArena().getSquareByCoordinate(0, 1)));
        assertFalse(this.squareNotSpawn.canSee(this.board.getArena().getSquareByCoordinate(2, 2)));
    }

    @Test
    public void activePlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player1 = this.board.getPlayers().get(0);
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        Player player2 = this.board.getPlayers().get(1);
        this.squareNotSpawn.addPlayer(player1);
        assertEquals(1, this.squareNotSpawn.getActivePlayers().size());
        assertEquals(player1, this.squareNotSpawn.getActivePlayers().get(0));
        this.squareNotSpawn.addPlayer(player2);
        assertEquals(2, this.squareNotSpawn.getActivePlayers().size());
        assertEquals(player2, this.squareNotSpawn.getActivePlayers().get(1));
        this.squareNotSpawn.removePlayer(player1);
        assertEquals(1, this.squareNotSpawn.getActivePlayers().size());
        assertEquals(player2, this.squareNotSpawn.getActivePlayers().get(0));
    }
}