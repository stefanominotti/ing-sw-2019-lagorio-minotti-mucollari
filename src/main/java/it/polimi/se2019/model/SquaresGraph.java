package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling squares graph
 */
public class SquaresGraph {

    private List<Square> vertices;
    private List<Square>[] nearbyAccessibilityList;
    private List<List<Square>> currentPaths;

    /**
     * Class constructor, it builds a square graph
     * @param arena of which the squares graph needs to be created
     */
    SquaresGraph(Arena arena) {

        this.vertices = new ArrayList<>(arena.getAllSquares());
        this.currentPaths = new ArrayList<>();
        this.nearbyAccessibilityList = new ArrayList[this.vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            this.nearbyAccessibilityList[i] = new ArrayList<>();
        }

        for (Square s : this.vertices) {
            for (CardinalPoint point : CardinalPoint.values()) {
                if (s.getNearbyAccessibility().get(point)) {
                    this.nearbyAccessibilityList[this.vertices.indexOf(s)].add(s.getNearbySquares().get(point));
                }
            }
        }
    }

    /**
     * Finds paths between two squares
     * @param square1 the departure square
     * @param square2 the arrival square
     * @return List of List of square with the paths between the two squares
     */
    List<List<Square>> findPaths(Square square1, Square square2) {
        this.currentPaths = new ArrayList<>();

        boolean[] visited = new boolean[this.vertices.size()];
        ArrayList<Square> path = new ArrayList<>();
        path.add(square1);

        findPathsRecursive(square1, square2, visited, path);
        return this.currentPaths;
    }

    /**
     * Finds recursive paths between two squares
     * @param square1 the starting square
     * @param square2 the arrival square
     * @param visited true if the path is already visited, else false
     * @param path recursive path
     */
    private void findPathsRecursive(Square square1, Square square2,
                                   boolean[] visited,
                                   List<Square> path) {

        visited[this.vertices.indexOf(square1)] = true;

        if (square1 == square2) {
            this.currentPaths.add(new ArrayList<>(path));
            visited[this.vertices.indexOf(square1)] = false;
            return;
        }

        for (Square s : this.nearbyAccessibilityList[this.vertices.indexOf(square1)]) {
            if (!visited[this.vertices.indexOf(s)]) {
                path.add(s);
                findPathsRecursive(s, square2, visited, path);
                path.remove(s);
            }
        }

        visited[this.vertices.indexOf(square1)] = false;
    }

    /**
     * Finds minimum distance between two squares
     * @param square1 the starting square
     * @param square2 the arrival square
     * @return the value of the minimum distance
     */
    int findMinimumDistance(Square square1, Square square2) {
        List<List<Square>> paths = findPaths(square1, square2);
        int minimumDistance = Integer.MAX_VALUE;

        for (List<Square> path : paths) {
            int distance = path.size() - 1;
            if (distance < minimumDistance) {
                minimumDistance = distance;
            }
        }

        return minimumDistance;
    }

}