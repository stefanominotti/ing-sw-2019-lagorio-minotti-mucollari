package it.polimi.se2019.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.view.BoardView;
import org.junit.Before;
import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.swing.plaf.synth.SynthEditorPaneUI;

import static org.junit.Assert.*;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


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
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        assertNotNull(this.board.getPlayers());
        assertEquals(1, this.board.getPlayers().size());
    }

    @Test
    public void getValidPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1");
        assertNotNull(this.board.getPlayers());
        assertEquals(1, this.board.getPlayers().size());
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
    public void getPlayerOnCardinalDirectionTest() {
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
        assertEquals(result, board.getPlayersOnCardinalDirection(p2, CardinalPoint.SOUTH));
        result = new ArrayList<>();

        result.add(p3);
        result.add(p4);
        result.add(p5);
        assertEquals(result, board.getPlayersOnCardinalDirection(p2, CardinalPoint.EAST));
        result = new ArrayList<>();

        result.add(p1);
        result.add(p5);
        assertEquals(result, board.getPlayersOnCardinalDirection(p2, CardinalPoint.WEST));
    }

    @Test
    public void getVisiblePlayersTest() {
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

        this.board.movePlayer(p1, board.getArena().getSquareByCoordinate(2, 2));
        this.board.movePlayer(p2, board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, board.getArena().getSquareByCoordinate(2, 2));

        List<Player> players = new ArrayList<>();
        players.add(p4);
        players.add(p1);
        players.add(p5);

        assertEquals(players, board.getPlayersByDistance(board.getArena().getSquareByCoordinate(1,1), 2));
        assertEquals(players, board.getPlayersByDistance(p2, 2));
        players.remove(p4);
        assertEquals(players, board.getPlayersByDistance(board.getArena().getSquareByCoordinate(2,2), 0));
        players.remove(p1);
        assertEquals(players, board.getPlayersByDistance(p1, 0));

/*        int dist = board.getArena().getSquareByCoordinate(2, 0)
                    .minimumDistanceFrom(board.getArena().getSquareByCoordinate(3, 2));
        System.out.println(dist);

        for(Player p : board.getPlayersByDistance(board.getArena().getSquareByCoordinate(1,1), 2)){
            System.out.println(p.getNickname());
        }

        System.out.println(" ---- ");

        for(Player p : board.getPlayersByDistance(p2, 2)){
            System.out.println(p.getNickname());
        }
*/
    }

}