package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day6 extends Day {
    private class Coordinate {
        int x,y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int distance(int x2, int y2) {
            return Math.abs(x - x2) + Math.abs(y - y2);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Coordinate)) return false;
            Coordinate coordinate = (Coordinate) o;
            return coordinate.x == this.x && coordinate.y == this.y;
        }
    }

    private Map<Integer, Coordinate> coordinates = new HashMap<>();
    private int[][] grid = new int[500][500];

    @Override
    protected Object part1() {
        int id = 1;
        for (String line : lines) {
            String[] split = line.split(", ");
            coordinates.put(id, new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            id++;
        }
        int area = findBiggestArea();
//        for (int[] innerArray : grid) {
//            System.out.println(Arrays.toString(innerArray));
//        }
        return area;
    }

    @Override
    protected Object part2() {
        int regionSize = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                final Coordinate coordinate = new Coordinate(j, i);
                int sum = coordinates.values()
                        .stream()
                        .mapToInt(c -> c.distance(coordinate.x, coordinate.y))
                        .sum();
                if (sum < 10000) regionSize++;
            }
        }
        return regionSize;
    }

    private int findBiggestArea() {
        calculateClosestDistances();

        Map<Integer, Integer> sizes = new HashMap<>();
        List<Integer> infiniteIds = new ArrayList<>(); //a list of the ids that touch the edge, meaning they are infinite
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int id = grid[j][i];
                if (id == 0 || id == -1) continue;
                if (i == 0 || j == 0 || i == grid.length - 1 || j == grid[i].length - 1) {
                    infiniteIds.add(id);
                    sizes.remove(id);
                }
                if (infiniteIds.contains(id)) continue;
                sizes.merge(id, 1, Integer::sum);
            }
        }
        //System.out.println(Arrays.toString(sizes.values().toArray()));
        return Collections.max(sizes.values());
    }

    private void calculateClosestDistances() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (coordinates.values().contains(new Coordinate(j, i))) {
                    grid[j][i] = getKey(new Coordinate(j, i));
                    continue;
                }
                int closestId = 0; //the id of the closest coordinate
                int closestValue = -1; //the distance between the closest coordinate
                boolean mayBeEquidistant = false;
                for (Map.Entry<Integer, Coordinate> entry : coordinates.entrySet()) {
                    int distance = entry.getValue().distance(j, i);
                    if (distance < closestValue || closestValue == -1) {
                        closestId = entry.getKey();
                        closestValue = distance;
                        mayBeEquidistant = false;
                    } else if (distance == closestValue) {
                        mayBeEquidistant = true;
                    }
                }
                if (!mayBeEquidistant) grid[j][i] = closestId;
            }
        }
    }

    private int getKey(Coordinate coordinate) {
        for (Map.Entry<Integer, Coordinate> entry : coordinates.entrySet()) {
            if (entry.getValue().equals(coordinate)) return entry.getKey();
        }
        return 0;
    }
}
