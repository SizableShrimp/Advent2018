package days;

import util.Day;

import java.util.ArrayList;
import java.util.List;
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
                int index = getDifference(current, other);
                if (index != -1) return deleteCharAt(current, index);
            }
        }
        return null;
    }

    private static boolean hasLetterCount(String str, int count) {
        return str.chars().boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).values().contains((long) count);
    }

    private static int getDifference(String a, String b) {
        if (a.equals(b) || a.length() != b.length()) return -1;
        List<Integer> indices = new ArrayList<>();
        char[] aArray = a.toCharArray();
        char[] bArray = b.toCharArray();
        for (int i = 0; i < aArray.length; i++) {
            if (aArray[i] != bArray[i]) indices.add(i);
        }
        return indices.size() == 1 ? indices.get(0) : -1;
    }

    private static String deleteCharAt(String a, int index) {
        StringBuilder builder = new StringBuilder(a);
        builder.deleteCharAt(index);
        return builder.toString();
    }
}
