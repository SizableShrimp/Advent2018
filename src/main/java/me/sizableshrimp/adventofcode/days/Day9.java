package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day9 extends Day {
    @AllArgsConstructor
    private class Player {
        long score;
    }

    private List<Player> players = new ArrayList<>();
    private List<Integer> marbles = new LinkedList<>();

    @Override
    protected Result doParts() {
        final int marbleAmount = parse();
        marbles.add(0);
        int currentPlayer = 0;
        long part1 = 0;
        ListIterator<Integer> iterator = marbles.listIterator();
        for (int i = 1; i <= 100 * marbleAmount; i++) {
            currentPlayer++;
            if (currentPlayer >= players.size()) currentPlayer = 0;
            //System.out.println(marbles);
            if (i % 23 == 0) {
                Player player = players.get(currentPlayer);
                player.score += i;
                int num = 0;
                for (int j = 0; j <= 7; j++) {
                    if (!iterator.hasPrevious()) iterator = marbles.listIterator(marbles.size());
                    num = iterator.previous();
                }
                player.score += num;
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
                        .mapToLong(p -> p.score)
                        .max().getAsLong();
            }
        }
        return new Result(part1, players
        .stream()
        .mapToLong(p -> p.score)
        .max().getAsLong());
    }

    private int parse() {
        Pattern pattern = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points");
        Matcher matcher = pattern.matcher(lines.get(0));
        matcher.matches();
        int playerAmount = Integer.parseInt(matcher.group(1));
        for (int i = 1; i <= playerAmount; i++) {
            players.add(new Player(0));
        }
        return Integer.parseInt(matcher.group(2)) + 1;
    }
}
