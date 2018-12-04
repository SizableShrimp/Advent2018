package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.HashMap;
import java.util.Map;

public class Day3 extends Day {
    private int[][] claims = new int[1000][1000];
    private Map<Integer, Integer> sizes = new HashMap<>(); //a map to keep track of the size for each id; for checking the one non-overlapping claim
    private int overlapAmount = 0;

    @Override
    protected Object part1() {
        for (String line : lines) {
            int id = getId(line);
            sizes.put(id, addClaim(id, getLeftEdge(line), getTopEdge(line), getWidth(line), getLength(line)));
        }
//        for (int[] innerArray : claims) {
//            if (Arrays.stream(innerArray).filter(i -> i != 0).sum() == 0) continue;
//            System.out.println(Arrays.toString(innerArray));
//        }
        return overlapAmount;
    }

    @Override
    protected Object part2() {
        Map<Integer, Integer> realSizes = new HashMap<>();
        for (int x = 0; x < claims.length; x++) {
            for (int y = 0; y < claims[x].length; y++) {
                if (claims[x][y] > 0) realSizes.put(claims[x][y], realSizes.getOrDefault(claims[x][y], 0)+1);
            }
        }
        for (Integer id : sizes.keySet()) {
            Integer size = sizes.get(id);
            Integer realSize = realSizes.getOrDefault(id, 0);
            if (realSize.equals(size)) return id;
        }
        return null;
    }

    /**
     * @param length The length of the claim (y-axis)
     * @param width The width of the claim (x-axis)
     * @param leftEdge The number of spaces away from the left edge (starting x = leftEdge+1)
     * @param topEdge The number of spaces away from the top edge (starting y = topEdge+1)
     * @return Returns the projected size of the claim
     */
    private int addClaim(int id, int leftEdge, int topEdge, int width, int length) {
        int size = 0;
        for (int x = leftEdge; x < leftEdge+width && x < claims.length; x++) {
            for (int y = topEdge; y < topEdge+length && y < claims[x].length; y++) {
                size++;
                if (claims[x][y] == -1) continue;
                if (claims[x][y] > 0) {
                    overlapAmount++;
                    //System.out.println(x+", "+y+" has overlapped. New overlap amount is "+overlapAmount);
                    claims[x][y] = -1;
                    continue;
                }
                if (claims[x][y] == 0) claims[x][y] = id;
            }
        }
        return size;
    }

    private int getId(String claim) {
        return Integer.parseInt(claim.split(" ")[0].substring(1));
    }

    private int getLeftEdge(String claim) {
        return Integer.parseInt(claim.split(" ")[2].split(",")[0]);
    }

    private int getTopEdge(String claim) {
        String s = claim.split(" ")[2].split(",")[1];
        return Integer.parseInt(s.substring(0, s.length()-1)); //exclude the ":"
    }

    private int getWidth(String claim) {
        return Integer.parseInt(claim.split(" ")[3].split("x")[0]);
    }

    private int getLength(String claim) {
        return Integer.parseInt(claim.split(" ")[3].split("x")[1]);
    }
}
