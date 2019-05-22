package it.polimi.se2019.view;

import it.polimi.se2019.model.CardinalPoint;
import it.polimi.se2019.model.RoomColor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BoardViewTest {

    private BoardView board;

    @Before
    public void SetUp() {
        List<SquareView> squares = new ArrayList<>();
        Map<CardinalPoint, Boolean> nearbyAccessibility = new EnumMap<>(CardinalPoint.class);
        nearbyAccessibility.put(CardinalPoint.NORTH, false);
        nearbyAccessibility.put(CardinalPoint.EAST, true);
        nearbyAccessibility.put(CardinalPoint.WEST, false);
        nearbyAccessibility.put(CardinalPoint.SOUTH, false);
        squares.add(new SquareView(1, 0, RoomColor.YELLOW, true, nearbyAccessibility));
        nearbyAccessibility = new EnumMap<>(CardinalPoint.class);
        nearbyAccessibility.put(CardinalPoint.NORTH, false);
        nearbyAccessibility.put(CardinalPoint.EAST, true);
        nearbyAccessibility.put(CardinalPoint.WEST, true);
        nearbyAccessibility.put(CardinalPoint.SOUTH, false);
        squares.add(new SquareView(2, 0, RoomColor.YELLOW, true, nearbyAccessibility));
        this.board = new BoardView(3, squares);
    }

    @Test
    public void printTest() {
        System.out.println(this.board.arenaToString());
    }
}
