package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 extends Day {
    @AllArgsConstructor
    private class Coordinate {
        int x, y;
    }

    private enum Tile {
        CLAY, SAND, REST_WATER, WATER_THROUGH_SAND, SPRING;

        @Override
        public String toString() {
            if (this == Tile.CLAY) return "#";
            if (this == Tile.SAND) return ".";
            if (this == Tile.REST_WATER) return "~";
            if (this == Tile.WATER_THROUGH_SAND) return "|";
            if (this == Tile.SPRING) return "+";
            return "";
        }
    }

    private Tile[][] grid;
    private int minX;
    private int minY;

    @Override
    protected Result doParts() {
        parse();

        String previous;
        int iteration = 0;
        while (true) {
            previous = gridString();

            for (int x = minX-1/*account for water that drips off the left side*/; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {
                    Tile tile = grid[x][y];
                    if (tile == Tile.SPRING || tile == Tile.WATER_THROUGH_SAND) {
                        addWaterBelow(x, y);
                    }
                }
            }

            String current = gridString();
            System.out.println(++iteration);
            if (current.equals(previous)) break;
        }
        System.out.println(gridString());
        return new Result(countAllWater(), countRestWater());
    }

    private String gridString() {
        StringBuilder builder = new StringBuilder();
        for (int y = minY; y < grid[0].length; y++) {
            for (int x = minX-1/*account for water that drips off the left side*/; x < grid.length; x++) {
                builder.append(grid[x][y].toString());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private void addWaterBelow(int x, int y) {
        Tile below = tileBelow(x, y);
        if (below == Tile.WATER_THROUGH_SAND && tileBelow(x, y + 1) == Tile.WATER_THROUGH_SAND) return;
        if (below == Tile.CLAY) {
            if (tileLeft(x, y) == Tile.SAND) grid[x - 1][y] = Tile.WATER_THROUGH_SAND;
            if (tileRight(x, y) == Tile.SAND) grid[x + 1][y] = Tile.WATER_THROUGH_SAND;
            return;
        }
        while (true) {
            if (++y >= grid[0].length) break;
            Tile tile = grid[x][y];
            if (y < grid[0].length - 1 && restWaterLayerFull(x, y + 1) && tile == Tile.WATER_THROUGH_SAND && tileBelow(x, y) == Tile.REST_WATER && !overflow(x, y)) {
                grid[x][y] = Tile.REST_WATER;
                addWaterHorizontal(x, y);
                return;
            }
            if (tile == Tile.REST_WATER) {
                addWaterHorizontal(x, y);
                return;
            }
            if (tile == Tile.SAND) {
                grid[x][y] = Tile.WATER_THROUGH_SAND;
                return;
            }
            if (tile == Tile.CLAY) {
                if (!overflow(x, y)) {
                    grid[x][y - 1] = Tile.REST_WATER;
                    addWaterHorizontal(x, y - 1);
                }
                return;
            }
        }
    }

    private void addWaterHorizontal(int x, int y) {
        if (overflow(x, y)) {
            return;
        }
        int startX = x;

        //going right
        while (true) {
            if (++x >= grid.length) break;
            Tile tile = grid[x][y];
            if (tile == Tile.REST_WATER) {
                continue;
            }
            if (tile == Tile.SAND || tile == Tile.WATER_THROUGH_SAND) {
                //System.out.println("Adding water horizontally " + x + "," + y);
                grid[x][y] = Tile.REST_WATER;
            }
            if (tile == Tile.CLAY) {
                break;
            }
        }

        x = startX;
        //going left
        while (true) {
            if (--x < 0) break;
            Tile tile = grid[x][y];
            if (tile == Tile.REST_WATER) {
                continue;
            }
            if (tile == Tile.SAND) {
//                System.out.println("Adding water horizontally " + x + "," + y);
                grid[x][y] = Tile.REST_WATER;
            }
            if (tile == Tile.CLAY) {
                break;
            }
        }
    }

    private boolean overflow(int x, int y) {
        Set<Coordinate> coordinates = new HashSet<>();
        boolean canOverflow = false;
        final int startX = x;
        //going right
        while (true) {
            if (++x >= grid.length) break;
            Tile tile = grid[x][y];
            if (tile == Tile.SAND || tile == Tile.WATER_THROUGH_SAND) {
                coordinates.add(new Coordinate(x, y));
                Tile bottom = tileBelow(x, y);
                if (bottom == Tile.SAND || bottom == Tile.WATER_THROUGH_SAND) {
                    canOverflow = true;
                    break;
                }
            }
            if (tile == Tile.CLAY) {
                break;
            }
        }

        x = startX;
        //going left
        while (true) {
            if (--x < 0) break;
            Tile tile = grid[x][y];
            if (tile == Tile.REST_WATER) {
                continue;
            }
            if (tile == Tile.SAND || tile == Tile.WATER_THROUGH_SAND) {
                coordinates.add(new Coordinate(x, y));
                Tile bottom = tileBelow(x, y);
                if (bottom == Tile.SAND || bottom == Tile.WATER_THROUGH_SAND) {
                    canOverflow = true;
                    break;
                }
            }
            if (tile == Tile.CLAY) {
                if (canOverflow) {
                    for (Coordinate coordinate : coordinates) {
                        grid[coordinate.x][coordinate.y] = Tile.WATER_THROUGH_SAND;
                    }
                    return true;
                }
                return false;
            }
        }
        if (canOverflow) {
            for (Coordinate coordinate : coordinates) {
                grid[coordinate.x][coordinate.y] = Tile.WATER_THROUGH_SAND;
            }
            return true;
        }
        return false;
    }

    private boolean restWaterLayerFull(int x, int y) {
        final int startX = x;
        //going right
        while (true) {
            if (++x >= grid.length) break;
            Tile tile = grid[x][y];
            if (tile == Tile.REST_WATER) {
                continue;
            }
            if (tile == Tile.SAND) {
                return false;
            }
            if (tile == Tile.CLAY) {
                break;
            }
        }

        x = startX;
        //going left
        while (true) {
            if (--x < 0) break;
            Tile tile = grid[x][y];
            if (tile == Tile.REST_WATER) {
                continue;
            }
            if (tile == Tile.SAND) {
                return false;
            }
            if (tile == Tile.CLAY) {
                return true; //both sides have walls
            }
        }
        return false;
    }

    private Tile tileBelow(int x, int y) {
        if (outsideGrid(x, y + 1)) return null;
        return grid[x][y + 1];
    }

    private Tile tileLeft(int x, int y) {
        if (outsideGrid(x - 1, y)) return null;
        return grid[x - 1][y];
    }

    private Tile tileRight(int x, int y) {
        if (outsideGrid(x + 1, y)) return null;
        return grid[x + 1][y];
    }

    private boolean outsideGrid(int x, int y) {
        return x < 0 || x >= grid.length || y < 0 || y >= grid[0].length;
    }

    private int countAllWater() {
        int amount = 0;
        for (int x = minX-1; x < grid.length; x++) {
            for (int y = minY; y < grid[0].length; y++) {
                Tile tile = grid[x][y];
                if (tile == Tile.REST_WATER || tile == Tile.WATER_THROUGH_SAND) {
                    amount++;
                }
            }
        }
        return amount;
    }

    private int countRestWater() {
        int amount = 0;
        for (int x = minX-1; x < grid.length; x++) {
            for (int y = minY; y < grid[0].length; y++) {
                Tile tile = grid[x][y];
                if (tile == Tile.REST_WATER) {
                    amount++;
                }
            }
        }
        return amount;
    }

    private void parse() {
        System.out.println("start parse");
        Pattern xy = Pattern.compile("x=(\\d*), y=(\\d*)..(\\d*)");
        Pattern yx = Pattern.compile("y=(\\d*), x=(\\d*)..(\\d*)");
        List<Integer> maxXs = new ArrayList<>();
        List<Integer> maxYs = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = xy.matcher(line);
            if (matcher.matches()) {
                maxXs.add(Integer.parseInt(matcher.group(1)));
                maxYs.add(Integer.parseInt(matcher.group(2)));
                maxYs.add(Integer.parseInt(matcher.group(3)));
            }
            matcher = yx.matcher(line);
            if (matcher.matches()) {
                maxYs.add(Integer.parseInt(matcher.group(1)));
                maxXs.add(Integer.parseInt(matcher.group(2)));
                maxXs.add(Integer.parseInt(matcher.group(3)));
            }
        }
        int maxX = maxXs
                .stream()
                .mapToInt(i -> i)
                .max().orElseThrow(IllegalStateException::new);
        int maxY = maxYs
                .stream()
                .mapToInt(i -> i)
                .max().orElseThrow(IllegalStateException::new);
        minX = maxXs
                .stream()
                .mapToInt(i -> i)
                .min().orElseThrow(IllegalStateException::new);
        minY = maxYs
                .stream()
                .mapToInt(i -> i)
                .min().orElseThrow(IllegalStateException::new);
        grid = new Tile[maxX + 1][maxY + 1];
        for (Tile[] innerArray : grid) {
            Arrays.fill(innerArray, Tile.SAND);
        }
        for (String line : lines) {
            Matcher matcher = xy.matcher(line);
            if (matcher.matches()) {
                int x = Integer.parseInt(matcher.group(1));
                for (int y = Integer.parseInt(matcher.group(2)); y <= Integer.parseInt(matcher.group(3)); y++) {
                    grid[x][y] = Tile.CLAY;
                }
            }
            matcher = yx.matcher(line);
            if (matcher.matches()) {
                int y = Integer.parseInt(matcher.group(1));
                for (int x = Integer.parseInt(matcher.group(2)); x <= Integer.parseInt(matcher.group(3)); x++) {
                    grid[x][y] = Tile.CLAY;
                }
            }
        }
        grid[500][0] = Tile.SPRING;
        System.out.println("minX: " + minX);
        System.out.println("minY: " + minY);
    }
}
