package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day1 extends Day {
    private final List<Integer> freqs = new ArrayList<>();

    public Day1() {
        super();
        for (String line : lines) {
            freqs.add(Integer.parseInt(line));
        }
    }

    @Override
    protected Object part1() {
        int freq = 0;
        for (int change : freqs) {
            freq += change;
        }
        return freq;
    }

    @Override
    protected Object part2() {
        Set<Integer> output = new HashSet<>();
        output.add(0);
        int freq = 0;
        while (true) {
            for (int change : freqs) {
                freq += change;
                if (output.contains(freq)) {
                    return freq;
                }
                output.add(freq);
            }
        }
    }
}
