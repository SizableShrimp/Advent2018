package me.sizableshrimp.adventofcode;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public abstract class Day {
    protected final List<String> lines = new ArrayList<>();

    public Day() {
        Class<?> clazz = getClass();
        String filename = clazz.getSimpleName().toLowerCase(Locale.ROOT) + ".txt";
        try (Scanner scan = new Scanner(clazz.getResourceAsStream("/" + filename))) {
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
        }
    }

    public Day(String filename) {
        try (Scanner scan = new Scanner(getClass().getResourceAsStream("/" + filename))) {
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
        }
    }

    /**
     * Used to run the day's code when the two parts are separate
     */
    public void run() {
        long before = System.nanoTime();
        Pair<Object, Object> pair = doParts();
        long after = System.nanoTime();
        System.out.println("Part 1: "+Objects.toString(pair.getKey(), ""));
        System.out.println("Part 2: "+Objects.toString(pair.getValue(), ""));
        System.out.printf("Completed in %.4fs%n%n", (after - before) / 1_000_000_000f);
    }

    /**
     * This method can be overridden to combine the logic of both parts if needed.
     * @return A pair of Part 1 and 2
     */
    protected Pair<Object, Object> doParts() {
        return new Pair<>(part1(), part2());
    }

    /**
     *
     * @return The result of Part 1
     */
    protected Object part1() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return The result of Part 2
     */
    protected Object part2() {
        throw new UnsupportedOperationException();
    }

    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
}
