package pr2mapAgent;

import java.util.*;

public class Environment {

    int[][] path;
    int width;
    int height;
    Node[][] exploredArea;
    private Map map;

    public Environment(int width, int height, Map map) {
        this.width = width;
        this.height = height;
        this.exploredArea = new Node[height][width];
        this.map = map;
    }

    public void setNode(int x, int y, Node n) {
        exploredArea[y][x] = n;
    }

    public Node getNode(int x, int y) {
        return exploredArea[y][x];
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean yetDiscovered(int x, int y) {
        return exploredArea[y][x] != null;
    }

    public boolean yetDiscovered(Node current) {
        return exploredArea[current.y][current.x] != null;
    }

    public int see(Node current, int x, int y) {
        return yetDiscovered(current) ? map.getMatrix()[y][x] : 1;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void resetHeuristic() {
        for (int ny = 0; ny < getHeight(); ny++) {
            for (int nx = 0; nx < getWidth(); nx++) {
                if (yetDiscovered(nx, ny)) {
                    getNode(nx, ny).resetHeuristicValues();
                }
            }
        }
    }

    public java.util.Map<String, int[]> reindeerLocations() {
        java.util.Map<String, int[]> reindeerLocations = new HashMap<>();
        String[] reindeerNames = {"Dasher", "Dancer", "Vixen", "Prancer", "Cupid", "Comet", "Blitzen", "Donner"};
        Random random = new Random();
        Set<String> usedPositions = new HashSet<>();

        // generate unique positions
        for (String name : reindeerNames) {
            int[] position;
            do {
                position = new int[]{random.nextInt(width), random.nextInt(height)};
            } while (!usedPositions.add(Arrays.toString(position)) && map.getMatrix()[position[0]][position[1]] == -1);
            reindeerLocations.put(name, position);
        }

        return reindeerLocations;
    }
}
