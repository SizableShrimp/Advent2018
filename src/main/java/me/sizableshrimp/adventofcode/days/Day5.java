package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day5 extends Day {
    private Map<Character, Integer> lengths = new HashMap<>(); //the length of the string after removing the specified char and reacting it
    private StringBuilder chemical = new StringBuilder(lines.get(0));
    //private Pattern pattern = Pattern.compile("(.*[a-z][A-Z].*)|(.*[A-Z].*)");

    @Override
    protected Object part1() {
        return react(chemical).length();
    }

    @Override
    protected Object part2() {
        String alphabet = "abcdefghikjklmnopqrstuvwxyz";
        for (Character c : alphabet.toCharArray()) {
            String removed = chemical.toString().replaceAll(String.valueOf(c), "").replaceAll(String.valueOf(c).toUpperCase(), "");
            lengths.put(c, react(removed).length());
        }
        List<Map.Entry<Character, Integer>> sorted = lengths.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toList());
        return sorted.get(0).getValue();
    }

    private static String react(StringBuilder builder) {
        return react(builder.toString());
    }

    private static String react(String s) {
        boolean again = true;
        StringBuilder result = new StringBuilder(s);
        while (again) {
            again = false;
            for (int i = 0; i < result.length() - 1; i++) {
                char current = result.charAt(i);
                char future = result.charAt(i+1);
                //System.out.println("Current: " + current + " Future: " + future);
                if (future != current && (Character.toUpperCase(future) == current || Character.toLowerCase(future) == current)) {
                    result.deleteCharAt(i).deleteCharAt(i); //by removing a character, the future is now in the spot as current
                    //System.out.println("Removed " + current + " and " + future + " (current value \""+chemical.toString()+"\")");
                    again = true;
                }
            }
        }
        return result.toString();
    }
}
