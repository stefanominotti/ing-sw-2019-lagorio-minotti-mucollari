package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RoomTest {

    Room roomSpawn;
    Room roomNotSpawn;

    @Before
    public void seUp() {
        Arena arena = new Arena("1");
        this.roomSpawn = arena.getRoomList().get(0);
        this.roomNotSpawn = arena.getRoomList().get(3);
    }

    @Test
    public void hasSpawnTest() {
        Boolean spawn;
        Boolean notSpawn;
        spawn = this.roomSpawn.hasSpawn();
        notSpawn = this.roomNotSpawn.hasSpawn();
        assertTrue(spawn);
        assertFalse(notSpawn);
    }

    @Test
    public void colorTest() {
        RoomColor color;
        color = this.roomSpawn.getColor();
        assertEquals(RoomColor.BLUE, color);
    }

    @Test
    public void squareTest() {
        List<Square> squares;
        squares = this.roomSpawn.getSquares();
        assertNotNull(squares);
    }
}

