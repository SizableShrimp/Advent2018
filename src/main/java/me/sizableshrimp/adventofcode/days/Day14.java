package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class Day14 extends Day {
    @AllArgsConstructor
    private class Elf {
        private int currentIndex; //the index in the list of the current score
        @Getter
        private int currentScore;

        private void pickNewScore() {
            ListIterator<Integer> iter = scoreboard.listIterator(currentIndex);
            int nextIndex = -1;
            for (int i = 0; i <= 1 + currentScore; i++) {
                if (!iter.hasNext()) iter = scoreboard.listIterator();
                nextIndex = iter.nextIndex();
                iter.next();
                //System.out.println(nextIndex);
            }
            //int nextIndex = (1 + currentScore) % scoreboard.size();
            setCurrentIndex(nextIndex);
            //System.out.println("Elf picked "+currentScore+" at index "+ currentIndex);
        }

        private void setCurrentIndex(int index) {
            currentIndex = index;
            currentScore = scoreboard.get(index);
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
        firstElf = new Elf(0, 3);
        secondElf = new Elf(1, 7);

        for (int i = 0; i < INPUT + 10; i++) {
            int sum = firstElf.getCurrentScore() + secondElf.getCurrentScore();
            List<Integer> digits = parseDigits(sum);
            scoreboard.addAll(digits);
            firstElf.pickNewScore();
            secondElf.pickNewScore();
        }
        System.out.println(scoreboard);
        int startingIndex = INPUT;
        StringBuilder result = new StringBuilder();
        for (int i = startingIndex; i < startingIndex + 10; i++) {
            result.append(scoreboard.get(i));
        }
        return result.toString();
    }

    @Override
    protected Object part2() {
        List<Integer> digits = parseDigits(INPUT);

        for (int i = 0; i < scoreboard.size(); i++) {
            int score = scoreboard.get(i);
            if (digits.get(0) != score) continue;
            boolean valid = true;
            for (int j = 1; j < digits.size(); j++) {
                if (!digits.get(j).equals(scoreboard.get(i + j))) {
                    valid = false;
                } else System.out.println(digits.get(j));
            }
            if (valid) return i+1;
        }
        return null;
    }

    private List<Integer> parseDigits(Integer number) {
        if (number < 10) return Arrays.asList(number);
        if (number < 20) return Arrays.asList(1, number - 10);
        return Arrays.stream(number.toString().split(""))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
