package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import me.sizableshrimp.adventofcode.Day;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day6 extends Day {
    @AllArgsConstructor @EqualsAndHashCode
    private class Coordinate {
        int x,y;

        int distance(int x2, int y2) {
            return Math.abs(x - x2) + Math.abs(y - y2);
        }
    }

    private Map<Integer, Coordinate> coordinates = new HashMap<>();
    private int[][] grid;

    public Day6() {
        super();
        int id = 1;
        for (String line : lines) {
            String[] split = line.split(", ");
            coordinates.put(id, new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            id++;
        }
        int maxX = coordinates.values()
                .stream()
                .mapToInt(c -> c.x)
                .max().orElse(500);
        int maxY = coordinates.values()
                .stream()
                .mapToInt(c -> c.y)
                .max().orElse(500);
        grid = new int[maxX+1][maxY+1];
    }

    @Override
    protected Object part1() {
        calculateClosestDistances();

        Map<Integer, Integer> sizes = new HashMap<>();
        Set<Integer> infiniteIds = new HashSet<>(); //a list of the ids that touch the edge, meaning they are infinite
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int id = grid[i][j];
                if (id == 0 || id == -1 || infiniteIds.contains(id)) continue;
                if (i == 0 || j == 0 || i == grid.length - 1 || j == grid[i].length - 1) {
                    infiniteIds.add(id);
                    sizes.remove(id);
                }
                sizes.merge(id, 1, Integer::sum);
            }
        }
        return Collections.max(sizes.values());
    }

    @Override
    protected Object part2() {
        int regionSize = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                final Coordinate coordinate = new Coordinate(i, j);
                int sum = coordinates.values()
                        .stream()
                        .mapToInt(c -> c.distance(coordinate.x, coordinate.y))
                        .sum();
                if (sum < 10000) regionSize++;
            }
        }
        return regionSize;
    }

    private void calculateClosestDistances() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (isCoordinate(new Coordinate(i, j))) continue;
                int closestId = 0; //the id of the closest coordinate
                int closestValue = -1; //the distance between the closest coordinate
                boolean mayBeEquidistant = false;
                for (Map.Entry<Integer, Coordinate> entry : coordinates.entrySet()) {
                    int distance = entry.getValue().distance(i, j);
                    if (distance < closestValue || closestValue == -1) {
                        closestId = entry.getKey();
                        closestValue = distance;
                        mayBeEquidistant = false;
                    } else if (distance == closestValue) {
                        mayBeEquidistant = true;
                    }
                }
                if (!mayBeEquidistant) grid[i][j] = closestId;
            }
        }
    }

    private boolean isCoordinate(Coordinate coordinate) {
        if (coordinates.containsValue(coordinate)) {
            int id = -1;
            for (Map.Entry<Integer, Coordinate> entry : coordinates.entrySet()) {
                if (entry.getValue().equals(coordinate)) id = entry.getKey();
            }
            grid[coordinate.x][coordinate.y] = id;
            return true;
        }
        return false;
    }
}
