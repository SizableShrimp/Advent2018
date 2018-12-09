package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day9 extends Day {
    private List<Long> players = new ArrayList<>();
    private List<Integer> marbles = new LinkedList<>();

    @Override
    protected Result doParts() {
        final int marbleAmount = parse();
        marbles.add(0);
        int currentPlayer = 0;
        long part1 = 0;
        ListIterator<Integer> iterator = marbles.listIterator();
        for (int i = 1; i <= 100 * marbleAmount; i++) {
            if (++currentPlayer >= players.size()) currentPlayer = 0;
            //System.out.println(marbles);
            if (i % 23 == 0) {
                long score = players.get(currentPlayer);
                score += i;
                int num = 0;
                for (int j = 0; j <= 7; j++) {
                    if (!iterator.hasPrevious()) iterator = marbles.listIterator(marbles.size());
                    num = iterator.previous();
                }
                score += num;
                players.set(currentPlayer, score);
                iterator.remove();
                if (!iterator.hasNext()) iterator = marbles.listIterator();
                iterator.next();
                continue;
            }
            if (!iterator.hasNext()) iterator = marbles.listIterator();
            iterator.next();
            iterator.add(i);
            if (i == marbleAmount) {
                part1 = players
                        .stream()
                        .max(Comparator.naturalOrder()).get();
            }
        }
        long part2 = players
                .stream()
                .max(Comparator.naturalOrder()).get();
        return new Result(part1, part2);
    }

    private int parse() {
        Pattern pattern = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points");
        Matcher matcher = pattern.matcher(lines.get(0));
        matcher.matches();
        int playerAmount = Integer.parseInt(matcher.group(1));
        for (int i = 1; i <= playerAmount; i++) {
            players.add(0L);
        }
        return Integer.parseInt(matcher.group(2)) + 1;
    }
}
