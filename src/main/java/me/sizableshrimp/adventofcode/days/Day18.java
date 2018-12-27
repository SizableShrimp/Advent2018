package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day18 extends Day {
    private enum Acre {
        OPEN, TREES, LUMBERYARD;

        public static Acre parse(char c) {
            if (c == '|') return Acre.TREES;
            if (c == '#') return Acre.LUMBERYARD;
            if (c == '.') return Acre.OPEN;
            return null;
        }
    }

    private Acre[][] grid;

    @Override
    protected Result doParts() {
        parse();
        List<Acre[][]> previous = new ArrayList<>();
        int part1 = -1;
        for (int i = 1; true; i++) {
            next();
            if (i == 10) part1 = resourceValue();
            int start = findGrid(previous, grid);
            if (start != -1) {
                int end = previous.size();
                int modulo = end - start;
                int index = start + ((1_000_000_000 - start) % modulo) - 1;
                return new Result(part1, resourceValue(previous.get(index)));
            }
            previous.add(cloneGrid());
        }
    }

    private void next() {
        Acre[][] next = cloneGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                Acre acre = grid[y][x];
                List<Acre> adjacents = adjacents(x, y);
                if (acre == Acre.OPEN) {
                    if (getAcreAmount(adjacents, Acre.TREES) >= 3) next[y][x] = Acre.TREES;
                } else if (acre == Acre.TREES) {
                    if (getAcreAmount(adjacents, Acre.LUMBERYARD) >= 3) next[y][x] = Acre.LUMBERYARD;
                } else if (acre == Acre.LUMBERYARD) {
                    if (getAcreAmount(adjacents, Acre.LUMBERYARD) >= 1 && getAcreAmount(adjacents, Acre.TREES) >= 1) {
                        next[y][x] = Acre.LUMBERYARD;
                    } else {
                        next[y][x] = Acre.OPEN;
                    }
                }
            }
        }
        grid = next;
    }

    private Acre[][] cloneGrid() {
        Acre[][] clone = new Acre[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            clone[i] = grid[i].clone();
        }
        return clone;
    }

    private int resourceValue() {
        return resourceValue(grid);
    }

    private int resourceValue(Acre[][] grid) {
        int woodedAcres = 0;
        int lumberyardAcres = 0;
        for (Acre[] acres : grid) {
            for (Acre acre : acres) {
                if (acre == Acre.TREES) woodedAcres++;
                if (acre == Acre.LUMBERYARD) lumberyardAcres++;
            }
        }
        return woodedAcres * lumberyardAcres;
    }

    private int findGrid(List<Acre[][]> list, Acre[][] grid) {
        for(int i = 0; i < list.size(); i++){
            Acre[][] array = list.get(i);
            if(Arrays.deepEquals(array, grid)){
                return i;
            }
        }
        return -1;
    }

    private List<Acre> adjacents(int startX, int startY) {
        List<Acre> adjacents = new ArrayList<>();

        for (int y = startY - 1; y <= startY + 1; y++) {
            for (int x = startX - 1; x <= startX + 1; x++) {
                if (x == startX && y == startY) continue; //don't want to add own acre
                if (!insideGrid(x, y)) continue;
                Acre acre = grid[y][x];
                adjacents.add(acre);
            }
        }
        return adjacents;
    }

    private int getAcreAmount(List<Acre> acres, Acre selected) {
        return (int) acres.stream().filter(a -> a == selected).count();
    }

    private void parse() {
        grid = new Acre[50][50];
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                grid[y][x] = Acre.parse(c);
            }
        }
    }

    private boolean insideGrid(int x, int y) {
        return x >= 0 && x < grid[0].length && y >= 0 && y < grid.length;
    }
}
