package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

public class Day11 extends Day {

    private static final int SERIAL_NUMBER = 1308;
    private static final int SIZE = 300;

    public Day11() {
        //don't need input file
    }

    public static void main(String[] args) {
        new Day11().run();
    }

    @Override
    protected Object part1() {
        int maxPower = -1;
        int maxX = 0;
        int maxY = 0;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int power = getPowerLevel(x, y, 3);
                if (power > maxPower) {
                    maxPower = power;
                    maxX = x;
                    maxY = y;
                }
            }
        }
        return maxX + "," + maxY;
    }

    @Override
    protected Object part2() {
        int maxSize = 0;
        int maxX = 0;
        int maxY = 0;
        int maxPower = -1;

        for (int regionSize = 1; regionSize <= 300; regionSize++) {
            System.out.println(regionSize);
            for (int x = 0; x < SIZE; x++) {
                if (x + regionSize >= SIZE) break;
                for (int y = 0; y < SIZE; y++) {
                    if (y + regionSize >= SIZE) break;
                    int totalPower = getPowerLevel(x, y, regionSize);
                    if (totalPower > maxPower) {
                        maxSize = regionSize;
                        maxX = x;
                        maxY = y;
                        maxPower = totalPower;
                    }
                    //System.out.println(x+","+y);
                }
            }
        }

        return maxX + "," + maxY + "," + maxSize;
    }

    private int getPowerLevel(int x, int y) {
        int rackId = x + 10;
        int powerLevel = rackId * y;
        powerLevel += SERIAL_NUMBER;
        powerLevel *= rackId;
        powerLevel = (powerLevel / 100) % 10; //gets the hundreds digit
        powerLevel -= 5;
        return powerLevel;
    }

    private int getPowerLevel(int x, int y, int regionSize) {
        int total = 0;
        for (int i = x; i < x + regionSize; i++) {
            for (int j = y; j < y + regionSize; j++) {
                total += getPowerLevel(i, j);
            }
        }
        return total;
    }
}
