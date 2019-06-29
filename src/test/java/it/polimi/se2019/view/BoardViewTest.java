package it.polimi.se2019.view;

import it.polimi.se2019.model.CardinalPoint;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.RoomColor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class BoardViewTest {

    private BoardView board;

    @Before
    public void setUp() {
        Map<CardinalPoint, Boolean> visibility = new EnumMap<>(CardinalPoint.class);
        visibility.put(CardinalPoint.WEST, true);
        visibility.put(CardinalPoint.NORTH, false);
        visibility.put(CardinalPoint.EAST, false);
        visibility.put(CardinalPoint.SOUTH, false);
        SquareView square1 = new SquareView(0, 0, RoomColor.YELLOW, true, visibility);
        visibility.put(CardinalPoint.EAST, true);
        visibility.put(CardinalPoint.WEST, false);
        SquareView square2 = new SquareView(1, 0, RoomColor.YELLOW, true, visibility);
        this.board = new BoardView(3, Arrays.asList(square1, square2), 1);
    }

    @Test
    public void frenzyTest() {
        assertFalse(this.board.isFrenzy());
        this.board.startFrenzy();
        assertTrue(this.board.isFrenzy());
        this.board.setBeforeFirstPlayer(true);
        assertTrue(this.board.isBeforeFirstPlayer());
    }

    @Test
    public void skullsTest() {
        this.board.setSkulls(3);
        assertEquals(3, this.board.getSkulls());
    }

    @Test
    public void getSquaresTest() {
        assertEquals(2, this.board.getSquares().size());
        SquareView square = this.board.getSquareByCoordinates(0, 0);
        assertEquals(square.getX(), 0);
        assertEquals(square.getY(), 0);
    }

    @Test
    public void positionsTest() {
        assertNull(this.board.getPlayerPosition(GameCharacter.D_STRUCT_OR));
        SquareView square1 = this.board.getSquares().get(0);
        SquareView square2 = this.board.getSquares().get(1);
        this.board.setPlayerPosition(GameCharacter.D_STRUCT_OR, square1);
        assertEquals(square1, this.board.getPlayerPosition(GameCharacter.D_STRUCT_OR));
        assertEquals(1, square1.getActivePlayers().size());
        assertEquals(GameCharacter.D_STRUCT_OR, square1.getActivePlayers().get(0));
        this.board.setPlayerPosition(GameCharacter.D_STRUCT_OR, square2);
        assertEquals(square2, this.board.getPlayerPosition(GameCharacter.D_STRUCT_OR));
        assertEquals(0, square1.getActivePlayers().size());
        assertEquals(1, square2.getActivePlayers().size());
        assertEquals(GameCharacter.D_STRUCT_OR, square2.getActivePlayers().get(0));
    }

    @Test
    public void killshotTrackTest() {
        List<GameCharacter> players = Arrays.asList(GameCharacter.D_STRUCT_OR, GameCharacter.BANSHEE);
        this.board.addKillshotPoints(players, 3);
        assertEquals(players, this.board.getKillshotTrack().get(3));
    }

    @Test
    public void alternativeConstructorTest() {
        List<SquareView> squares = this.board.getSquares();
        killshotTrackTest();
        Map<Integer, List<GameCharacter>> killshotTrack = this.board.getKillshotTrack();
        BoardView alternativeBoard = new BoardView(3, squares, killshotTrack, true, true);
        assertTrue(alternativeBoard.isFrenzy());
        assertTrue(alternativeBoard.isBeforeFirstPlayer());
        assertEquals(squares, alternativeBoard.getSquares());
        assertEquals(killshotTrack, alternativeBoard.getKillshotTrack());
    }
}
