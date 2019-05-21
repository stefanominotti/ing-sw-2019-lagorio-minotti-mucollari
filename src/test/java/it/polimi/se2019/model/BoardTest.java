package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BoardTest {
    Board board ;

    @Before
    public void setUp(){
        this.board = new Board();
    }

    @Test
    public void notNullTest() {
        assertNotNull(this.board);
    }

    @Test
    public void getPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        assertNotNull(this.board.getPlayers());
        assertEquals(1 , this.board.getPlayers().size());
    }

    @Test
    public void getValidPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        assertNotNull(this.board.getPlayers());
        assertEquals(1 , this.board.getPlayers().size());
    }

    @Test
    public void getValidCharactersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        assertNotNull(this.board.getValidCharacters());
        assertEquals(Arrays.asList(GameCharacter.BANSHEE), this.board.getValidCharacters());
    }

    @Test
    public void getPlayerByCharacter() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        assertNotNull(this.board.getValidCharacters());
        assertEquals(this.board.getPlayers().get(0), this.board.getPlayerByCharacter(GameCharacter.BANSHEE));
    }

    @Test
    public void skullTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        this.board.setSkulls(5);
        assertEquals(5, this.board.getSkulls());
    }

    @Test
    public void getPlayerOnCardinalDirectionTest(){
        Arena arena = new Arena("1");
        board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4");
        this.board.addPlayer(GameCharacter.VIOLETTA, "playerTest5");
        Player p1 = board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = board.getPlayerByCharacter(GameCharacter.VIOLETTA);

        p1.setPosition(arena.getSquareByCoordinate(0, 1));
        p2.setPosition(arena.getSquareByCoordinate(1, 1));
        p3.setPosition(arena.getSquareByCoordinate(2, 1));
        p4.setPosition(arena.getSquareByCoordinate(3, 1));
        p5.setPosition(arena.getSquareByCoordinate(1, 1));
        List<Player> result = new ArrayList<>();

        result.add(p5);
        assertEquals(result, board.getPlayersOnCardinalDirection(p2, CardinalPoint.NORTH));
        result = new ArrayList<>();

        result.add(p5);
        assertEquals(result,board.getPlayersOnCardinalDirection(p2, CardinalPoint.SOUTH));
        result = new ArrayList<>();

        result.add(p3);
        result.add(p4);
        result.add(p5);
        assertEquals(result,board.getPlayersOnCardinalDirection(p2, CardinalPoint.EAST));
        result = new ArrayList<>();

        result.add(p1);
        result.add(p5);
        assertEquals(result,board.getPlayersOnCardinalDirection(p2, CardinalPoint.WEST));
    }
}