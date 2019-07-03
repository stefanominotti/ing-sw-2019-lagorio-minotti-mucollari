package it.polimi.se2019.model.arena;

import it.polimi.se2019.model.arena.Arena;
import it.polimi.se2019.model.arena.Square;
import it.polimi.se2019.model.arena.SquaresGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class SquaresGraphTest {

    private SquaresGraph graph;
    private Arena arena;

    @Before
    public void setUp() {
        this.arena = new Arena("3");
        this.graph = new SquaresGraph(this.arena);
    }

    @Test
    public void pathTest() {
        List<List<Square>> paths = this.graph.findPaths(this.arena.getSquareByCoordinate(0, 0), this.arena.getSquareByCoordinate(3, 2));

        /* for (List<Square> path : paths) {
            StringBuilder string = new StringBuilder();
            for (Square s : path) {
                string.append("[" + s.getX() + ", " + s.getY() + "]");
            }
            System.out.println(string.toString());
        } */

        assertNotNull(paths);
        assertEquals(14, paths.size());
    }

    @Test
    public void minimumDistanceTest() {
        int distance = this.graph.findMinimumDistance(this.arena.getSquareByCoordinate(0, 0), this.arena.getSquareByCoordinate(3, 2));
        assertEquals(5, distance);
    }

}
