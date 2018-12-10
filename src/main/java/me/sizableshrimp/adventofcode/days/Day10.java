package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day10 extends Day {
    @AllArgsConstructor
    @EqualsAndHashCode
    private class Coordinate {
        int x, y, xVel, yVel;

        private void addVel() {
            this.x += this.xVel;
            this.y += this.yVel;
        }
    }

    private Map<Integer, Coordinate> positions = new HashMap<>();

    private int index = 0;

    @Override
    protected Result doParts() {
        parse();

        List<Map.Entry<Integer, Coordinate>> entries = positions.entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getValue().y))
                .collect(Collectors.toList());
        while (distance(entries.get(0).getValue().y, entries.get(entries.size() - 1).getValue().y) > 10) {
            positions.forEach((key, value) -> value.addVel());
            index++;
        }

        int maxX = positions.entrySet()
                .stream()
                .mapToInt(entry -> entry.getValue().x)
                .max().getAsInt();
        int maxY = positions.entrySet()
                .stream()
                .mapToInt(entry -> entry.getValue().y)
                .max().getAsInt();
        char[][] grid = new char[2 * maxX + 1][2 * maxY + 1];
        for (int j = 0; j < grid.length; j++) {
            for (int k = 0; k < grid[j].length; k++) {
                grid[j][k] = '.';
            }
        }
        for (Coordinate c : positions.values()) {
            setCoordinate(grid, c);
        }
        int topLeft = -1;
        int topRight = findLastLightIndex(grid);
        List<Integer> noLights = new ArrayList<>(); //the rows without any lights
        for (int y = 0; y < grid.length; y++) {
            boolean lights = false;
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == '#') {
                    lights = true;
                    if (topLeft == -1) {
                        topLeft = x;
                        break;
                    }
                }
            }
            if (!lights) noLights.add(y);
        }
        for (int i = 0; i < grid.length; i++) {
            if (noLights.contains(i)) continue;
            char[] arr = Arrays.copyOfRange(grid[i], topLeft, topRight+1);
            String s = Arrays.toString(arr);
            if (s.equalsIgnoreCase("null")) continue;
            for (char c : arr) {
                System.out.print(c+""+c);
            }
            System.out.println();
        }
        return new Result(null, index);
    }

    private void setCoordinate(char[][] grid, Coordinate coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;
        grid[y][x] = '#';
    }

    private int distance(int y1, int y2) {
        return Math.abs(y1 - y2);
    }

    private int findLastLightIndex(char[][] grid) {
        int lastIndex = -1;
        for (char[] chars : grid) {
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == '#' && j > lastIndex) lastIndex = j;
            }
        }
        return lastIndex;
    }

    private void parse() {
        int id = 0;
        Pattern pattern = Pattern.compile("position=<(?:\\s*)(\\d+|-\\d+),(?:\\s*)(\\d+|-\\d+)> velocity=<(?:\\s*)(\\d+|-\\d+),(?:\\s*)(\\d+|-\\d+)>");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int xVel = Integer.parseInt(matcher.group(3));
            int yVel = Integer.parseInt(matcher.group(4));
            positions.put(++id, new Coordinate(x, y, xVel, yVel));
        }
    }
}
