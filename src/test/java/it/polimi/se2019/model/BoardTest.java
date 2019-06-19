package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardTest {
    Board board;

    @Before
    public void setUp() {
        this.board = new Board();
    }

    @Test
    public void notNullTest() {
        assertNotNull(this.board);
    }

    @Test
    public void getPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        assertNotNull(this.board.getPlayers());
        assertEquals(1, this.board.getPlayers().size());
    }

    @Test
    public void getValidPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        assertNotNull(this.board.getPlayers());
        assertEquals(1, this.board.getPlayers().size());
    }

    /*@Test
    public void getValidCharactersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        assertNotNull(this.board.getValidCharacters());
        assertEquals(Arrays.asList(GameCharacter.BANSHEE), this.board.getValidCharacters());
    }

    @Test
    public void getPlayerByCharacter() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        assertNotNull(this.board.getValidCharacters());
        assertEquals(this.board.getPlayers().get(0), this.board.getPlayerByCharacter(GameCharacter.BANSHEE));
    }*/

    @Test
    public void skullTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.setSkulls(5);
        assertEquals(5, this.board.getSkulls());
    }
    
    @Test
    public void getVisiblePlayersTest() {
        board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4", "token");
        this.board.addPlayer(GameCharacter.VIOLETTA, "playerTest5", "token");
        Player p1 = board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = board.getPlayerByCharacter(GameCharacter.VIOLETTA);

        this.board.movePlayer(p1, board.getArena().getSquareByCoordinate(0, 1));
        this.board.movePlayer(p2, board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, board.getArena().getSquareByCoordinate(2, 2));

        List<Player> visiblePlayers = new ArrayList<>();
        visiblePlayers.add(p1);
        visiblePlayers.add(p2);
        visiblePlayers.add(p4);

        assertEquals(visiblePlayers, board.getVisiblePlayers(p3));


/*        //DEBUG PRINTER
        for (Player p : board.getPlayers()){
            System.out.println(p.getCharacter() + " - " + p.getNickname() + " ("+ p.getPosition().getX() +", "+p.getPosition().getY()+")");
        }
        System.out.println("----");

        for (Player p0 : board.getPlayers()){
            System.out.println("\n"+p0.getNickname() + " vede:");
            for (Player p9 : board.getVisiblePlayers(p0)){
                System.out.println(p9.getNickname());
            }
        }*/
    }

    @Test
    public void getPlayersByDistanceTest() {
        board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4", "token");
        this.board.addPlayer(GameCharacter.VIOLETTA, "playerTest5", "token");
        Player p1 = board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = board.getPlayerByCharacter(GameCharacter.VIOLETTA);

        this.board.movePlayer(p1, board.getArena().getSquareByCoordinate(2, 2));
        this.board.movePlayer(p2, board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, board.getArena().getSquareByCoordinate(2, 2));

        List<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p3);
        players.add(p4);

        List<String> distances = new ArrayList<>();
        distances.add("2");
        distances.add("3");

        assertEquals(players, board.getPlayersByDistance(p1, distances));

        /* //DEBUG
        for (Player p : board.getPlayersByDistance(p1, distances)) {
            System.out.println(p.getNickname());
        }
         */
    }

    @Test
    public void getSquaresByDistanceTest() {
        board.loadArena("1");

        List<String> distances = new ArrayList<>();
        distances.add("2");
        distances.add("3");

        List<Square> squares = new ArrayList<>();
        squares.add(board.getArena().getSquareByCoordinate(2, 0));
        squares.add(board.getArena().getSquareByCoordinate(1, 1));
        squares.add(board.getArena().getSquareByCoordinate(2, 1));
        squares.add(board.getArena().getSquareByCoordinate(1, 2));

        assertEquals(squares, board.getSquaresByDistance(board.getArena().getSquareByCoordinate(0, 0), distances));

        /* //DEBUG
        for (Square s : board.getSquaresByDistance(board.getArena().getSquareByCoordinate(0, 0), distances)){
            System.out.println(s.getX() + ", " + s.getY());
        }
         */
    }

    @Test
    public void getSquaresOnCardinalDirectionTest() {
        board.loadArena("1");

        List<Square> squares = new ArrayList<>();
        squares.add(board.getArena().getSquareByCoordinate(1, 1));
        squares.add(board.getArena().getSquareByCoordinate(2, 1));
        squares.add(board.getArena().getSquareByCoordinate(3, 1));

        assertEquals(squares, board.getSquaresOnCardinalDirection(board.getArena().getSquareByCoordinate(0, 1), CardinalPoint.EAST));
        /* //DEBUG
        for(Square s : board.getSquaresOnCardinalDirection(board.getArena().getSquareByCoordinate(0, 1), CardinalPoint.EAST)) {
            System.out.println(s.getX() + ", " + s.getY());
            for(Player p : s.getActivePlayers()) {
                p.getNickname();
            }
        }
         */

    }

    @Test
    public void getPlayersOnCardinalDirectionTest() {
        board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4", "token");
        this.board.addPlayer(GameCharacter.VIOLETTA, "playerTest5", "token");
        Player p1 = board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = board.getPlayerByCharacter(GameCharacter.VIOLETTA);

        this.board.movePlayer(p1, board.getArena().getSquareByCoordinate(0, 1));
        this.board.movePlayer(p2, board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, board.getArena().getSquareByCoordinate(2, 2));

        List<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p3);
        players.add(p4);

        assertEquals(players, board.getPlayersOnCardinalDirection(p1.getPosition(), CardinalPoint.EAST));
    }

}