package days;

import util.Day;

import java.util.function.Function;
import java.util.stream.Collectors;

public class Day2 extends Day {
    @Override
    protected Object part1() {
        return lines().stream().filter(str -> hasLetterCount(str, 2)).count() * lines().stream().filter(str -> hasLetterCount(str, 3)).count();
    }

    @Override
    protected Object part2() {
        for (int i = 0; i < lines().size()-1; i++) {
            String current = lines().get(i);
            for (int j = i+1; j < lines().size(); j++) {
                String other = lines().get(j);
                if (oneDifference(current, other)) {
                    return removeDifferences(current, other);
                }
            }
        }
        return null;
    }

    private static boolean hasLetterCount(String str, int count) {
        return str.chars().boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).values().contains((long) count);
    }

    private static boolean oneDifference(String a, String b) {
        if (a.equals(b) || a.length() != b.length()) return false;
        int differences = 0;
        char[] aArray = a.toCharArray();
        char[] bArray = b.toCharArray();
        for (int i = 0; i < aArray.length; i++) {
            if (aArray[i] != bArray[i]) differences++;
        }
        return differences == 1;
    }

    private static String removeDifferences(String a, String b) {
        StringBuilder builder = new StringBuilder();
        char[] aArray = a.toCharArray();
        char[] bArray = b.toCharArray();
        for (int i = 0; i < a.length(); i++) {
            char aChar = aArray[i];
            char bChar = bArray[i];
            if (aChar == bChar) builder.append(aChar);
        }
        return builder.toString();
    }
}
