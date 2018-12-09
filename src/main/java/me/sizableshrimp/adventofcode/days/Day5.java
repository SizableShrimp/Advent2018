package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.HashSet;
import java.util.Set;

public class Day5 extends Day {
    private String chemical = lines.get(0);

    @Override
    protected Object part1() {
        return react(chemical).length();
    }

    @Override
    protected Object part2() {
        int shortestLength = -1; //the shortest length of a reacted chemical with a certain letter removed
        Set<Character> used = new HashSet<>();
        char[] chars = chemical.toCharArray();
        for (char c : chars) {
            if (used.contains(c)) continue;
            used.add(c);
            String removed = react(chemical.replaceAll(String.valueOf(c), "").replaceAll(String.valueOf(c).toUpperCase(), ""));
            if (removed.length() < shortestLength || shortestLength == -1) shortestLength = removed.length();
        }
        return shortestLength;
    }

    private static String react(String s) {
        return react(new StringBuilder(s), 0);
    }

    private static String react(StringBuilder result, int startingIndex) {
        int index = -1;
        int i = startingIndex;
        while (i < result.length() - 1) {
            char current = result.charAt(i);
            char future = result.charAt(i + 1);
            if (future != current && (Character.toUpperCase(future) == current || Character.toLowerCase(future) == current)) {
                result.deleteCharAt(i).deleteCharAt(i); //by removing a character, future is now in the same spot as current
                i--;
                if (i < 0) {
                    i = 0;
                }
                if (index == -1) {
                    index = i;
                }
            } else i++;
        }
        if (index == -1) return result.toString();
        return react(result, index);
    }
}
