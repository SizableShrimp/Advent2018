package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 extends Day {
    private int[][] claims = new int[1000][1000];
    private Map<Integer, Integer> sizes = new HashMap<>(); //claim id, claim size
    private int overlapAmount = 0;

    @Override
    protected Object part1() {
        lines.forEach(this::addClaim);
        return overlapAmount;
    }

    @Override
    protected Object part2() {
        Map<Integer, Integer> realSizes = new HashMap<>();
        for (int[] claim : claims) {
            for (int i : claim) {
                if (i > 0) realSizes.merge(i, 1, Integer::sum);
            }
        }
        for (Map.Entry<Integer, Integer> entry : sizes.entrySet()) {
            Integer size = entry.getValue();
            Integer realSize = realSizes.getOrDefault(entry.getKey(), 0);
            if (realSize.equals(size)) return entry.getKey();
        }
        return null;
    }

    private void addClaim(String line) {
        Pattern pattern = Pattern.compile("#(\\d+)\\s@\\s(\\d+),(\\d+):\\s(\\d+)x(\\d+)");
        Matcher match = pattern.matcher(line);
        if (!match.matches()) return;
        int id = Integer.parseInt(match.group(1));
        int leftEdge = Integer.parseInt(match.group(2));
        int topEdge = Integer.parseInt(match.group(3));
        int width = Integer.parseInt(match.group(4));
        int length = Integer.parseInt(match.group(5));
        int size = 0;
        for (int x = leftEdge; x < leftEdge+width && x < claims.length; x++) {
            for (int y = topEdge; y < topEdge+length && y < claims[x].length; y++) {
                size++;
                if (claims[x][y] == -1) continue;
                if (claims[x][y] > 0) {
                    overlapAmount++;
                    claims[x][y] = -1;
                    continue;
                }
                claims[x][y] = id;
            }
        }
        sizes.put(id, size);
    }
}
