package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RoomTest {

    private Board board;
    private Room roomSpawn;
    private Room roomNotSpawn;

    @Before
    public void setUp() {
        this.board = new Board();
        this.board.loadArena("1");
        Arena arena = this.board.getArena();
        this.roomSpawn = arena.getRoomList().get(0);
        this.roomNotSpawn = arena.getRoomList().get(3);
    }

    @Test
    public void constructorTest() {
        List<Square> squares = this.board.getArena().getRoomByColor(RoomColor.RED).getSquares();
        Room room = new Room(RoomColor.RED, squares);
        assertEquals(RoomColor.RED, room.getColor());
        assertEquals(this.board.getArena().getRoomByColor(RoomColor.RED).getSquares(), room.getSquares());
    }

    @Test
    public void spawnTest() {
        assertTrue(this.roomSpawn.hasSpawn());
        assertFalse(this.roomNotSpawn.hasSpawn());
        assertNotNull(this.roomSpawn.getSpawn());
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

    @Test
    public void getPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player1 = this.board.getPlayers().get(0);
        this.board.addPlayer(GameCharacter.DOZER, "playerTest2", "token");
        Player player2 = this.board.getPlayers().get(1);
        this.board.movePlayer(player1, this.roomNotSpawn.getSquares().get(0));
        this.board.movePlayer(player2, this.roomNotSpawn.getSquares().get(1));
        assertEquals(2, this.roomNotSpawn.getPlayers().size());
        assertEquals(player1, this.roomNotSpawn.getPlayers().get(0));
        assertEquals(player2, this.roomNotSpawn.getPlayers().get(1));
    }
}