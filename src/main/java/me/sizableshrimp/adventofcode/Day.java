package me.sizableshrimp.adventofcode;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public abstract class Day {
    @NoArgsConstructor
    protected static class Result {
        @Getter
        String part1, part2;

        public Result(Object part1, Object part2) {
            this.part1 = Objects.toString(part1, "");
            this.part2 = Objects.toString(part2, "");
        }
    }

    protected final List<String> lines = new ArrayList<>();

    public Day() {
        Class<?> clazz = getClass();
        String filename = clazz.getSimpleName().toLowerCase(Locale.ROOT) + ".txt";
        InputStream stream = clazz.getResourceAsStream("/" + filename);
        if (stream == null) return;
        try (Scanner scan = new Scanner(stream)) {
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
        }
    }

    public Day(String filename) {
        InputStream stream = getClass().getResourceAsStream("/" + filename);
        if (stream == null) return;
        try (Scanner scan = new Scanner(stream)) {
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
        }
    }

    /**
     * Execute both parts of a given day's code
     */
    public void run() {
        long before = System.nanoTime();
        Result result = doParts();
        long after = System.nanoTime();
        System.out.println("Part 1: "+result.getPart1());
        System.out.println("Part 2: "+result.getPart2());
        System.out.printf("Completed in %.4fs%n%n", (after - before) / 1_000_000_000f);
    }

    /**
     * This method can be overridden to combine the logic of both parts if needed.
     * @return A result of both part 1 and 2
     */
    protected Result doParts() {
        return new Result(part1(), part2());
    }

    /**
     *
     * @return The result of part 1
     */
    protected Object part1() {
        return null;
    }

    /**
     *
     * @return The result of part 2
     */
    protected Object part2() {
        return null;
    }
}
