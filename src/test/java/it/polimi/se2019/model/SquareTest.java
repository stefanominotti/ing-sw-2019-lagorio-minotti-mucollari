package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class SquareTest {
    Square squareSpawn;
    Square squareNotSpawn;

    @Before
    public void seUp() {
        Arena arena = new Arena("1");
        this.squareNotSpawn = arena.getSquareByCoordinate(0,0);
        this.squareSpawn = arena.getSquareByCoordinate(2,0);
        squareNotSpawn.setArena(arena);
        squareNotSpawn.setRoom(arena.getRoomList().get(0));
    }

    @Test
    public void getCoordinatesTest () {
        int x;
        int y;
        x = this.squareNotSpawn.getX();
        y = this.squareNotSpawn.getY();
        assertEquals(0, x);
        assertEquals(0, y);
    }

    @Test
    public void isSpawnTest () {
        Boolean spawn;
        Boolean notSpawn;
        spawn = this.squareSpawn.isSpawn();
        notSpawn = this.squareNotSpawn.isSpawn();
        assertTrue(spawn);
        assertFalse(notSpawn);
    }

    @Test
    public void nearbySquaresTest () {
        this.squareNotSpawn.setNearbySquares();
        Map<CardinalPoint, Square> nearbySquares;
        nearbySquares = this.squareNotSpawn.getNearbySquares();
        for(CardinalPoint cardinal : CardinalPoint.values()){
            switch (cardinal){
                case SOUTH:
                    assertNotNull(nearbySquares.get(cardinal));
                    break;
                case NORTH:
                    assertNull(nearbySquares.get(cardinal));
                    break;
                case WEST:
                    assertNull(nearbySquares.get(cardinal));
                    break;
                case EAST:
                    assertNotNull(nearbySquares.get(cardinal));
                    break;
            }
        }

    }

    @Test
    public void roomTest () {
        RoomColor color = this.squareNotSpawn.getRoom().getColor();
        assertEquals(RoomColor.BLUE, color);
    }
}