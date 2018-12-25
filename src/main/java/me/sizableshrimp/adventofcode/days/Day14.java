package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.Setter;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day14 extends Day {
    @AllArgsConstructor
    private class Elf {
        @Setter
        private int currentIndex; //the index in the list of the current score

        private void pickNewScore() {
            currentIndex = (currentIndex + 1 + getCurrentScore()) % scoreboard.size();
            //System.out.println("Elf picked "+getCurrentScore()+" at index "+ currentIndex);
        }

        private int getCurrentScore() {
            return scoreboard.get(currentIndex);
        }
    }

    private static final Integer INPUT = 320851;
    private List<Integer> scoreboard = new ArrayList<>();
    private Elf firstElf;
    private Elf secondElf;

    @Override
    protected Object part1() {
        scoreboard.add(3);
        scoreboard.add(7);
        firstElf = new Elf(0);
        secondElf = new Elf(1);

        makeRecipe(INPUT + 10);

        StringBuilder result = new StringBuilder();
        for (int i = INPUT; i < INPUT + 10; i++) {
            result.append(scoreboard.get(i));
        }
        return result.toString();
    }

    @Override
    protected Object part2() {
        List<Integer> digits = parseDigits(INPUT);

        return findFirstIndex(digits);
    }

    private void makeRecipe(int amount) {
        for (int i = 0; i < amount; i++) {
            makeRecipe();
        }
    }

    private void makeRecipe() {
        int sum = firstElf.getCurrentScore() + secondElf.getCurrentScore();
        List<Integer> digits = parseDigits(sum);
        scoreboard.addAll(digits);
        firstElf.pickNewScore();
        secondElf.pickNewScore();
    }

    private int findFirstIndex(List<Integer> digits) {
        Integer index = null;
        int startIndex = 0;

        while (index == null) {
            for (int i = startIndex; i < scoreboard.size(); i++) {
                if (index != null) break; //don't want to reassign value after it appears once
                int score = scoreboard.get(i);
                if (digits.get(0) != score) continue;
                boolean valid = true;
                for (int j = 1; j < digits.size(); j++) {
                    if (i + j >= scoreboard.size()) {
                        valid = false;
                        break;
                    }
                    if (!digits.get(j).equals(scoreboard.get(i + j))) {
                        valid = false;
                    }
                }
                if (valid) index = i;
            }
            if (index == null) {
                //start where list ended and account for rare case where the list of digits may have been cut off
                startIndex = scoreboard.size() - digits.size();
                makeRecipe(1_000_000);
            }
        }
        return index;
    }

    private List<Integer> parseDigits(Integer number) {
        if (number < 10) return Arrays.asList(number);
        if (number < 20) return Arrays.asList(1, number - 10);
        return Arrays.stream(number.toString().split(""))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
