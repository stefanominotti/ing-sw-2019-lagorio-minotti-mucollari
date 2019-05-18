package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.List;

public class SquaresGraph {

    private List<Square> vertices;
    private List<Square>[] nearbyAccessibilityList;
    private List<List<Square>> currentPaths;

    public SquaresGraph(Arena arena) {

        this.vertices = new ArrayList<>();
        this.currentPaths = new ArrayList<>();

        for (Room r : arena.getRoomList()) {
            this.vertices.addAll(r.getSquares());
        }

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

    List<List<Square>> findPaths(Square square1, Square square2) {
        this.currentPaths = new ArrayList<>();

        boolean[] visited = new boolean[this.vertices.size()];
        ArrayList<Square> path = new ArrayList<>();
        path.add(square1);

        findPathsRecursive(square1, square2, visited, path);
        return this.currentPaths;
    }

    private void findPathsRecursive(Square square1, Square square2,
                                   boolean[] visited,
                                   List<Square> path) {

        visited[this.vertices.indexOf(square1)] = true;

        if (square1 == square2) {
            this.currentPaths.add(new ArrayList<>(path));
            visited[this.vertices.indexOf(square1)]= false;
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