package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day25 extends Day {
    @AllArgsConstructor
    @EqualsAndHashCode
    private class Coordinate {
        int x, y, z, fourth;

        int distance(Coordinate other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y) + Math.abs(other.z - z) + Math.abs(other.fourth - fourth);
        }
    }

    List<Coordinate> coordinates = new LinkedList<>();
    Set<Set<Coordinate>> constellations = new HashSet<>();

    @Override
    protected Object part1() {
        parse();
        for (Coordinate coord : coordinates) {
            Set<Coordinate> set = new HashSet<>();
            set.add(coord);
            constellations.add(set);
        }
        for (Coordinate coord : coordinates) {
            for (Coordinate other : coordinates) {
                if (other.equals(coord)) continue;
                int distance = coord.distance(other);
                if (distance > 3) continue;
                Set<Set<Coordinate>> find = constellations
                        .stream()
                        .filter(l -> l.contains(coord) || l.contains(other))
                        .collect(Collectors.toSet());
                if (find.isEmpty()) {
                    Set<Coordinate> constellation = new HashSet<>();
                    constellation.add(other);
                    constellation.add(coord);
                    constellations.add(constellation);
                    continue;
                }
                if (find.size() > 1) {
                    for (Set<Coordinate> constellation : find) {
                        constellations.remove(constellation);
                    }
                    Set<Coordinate> constellation = find
                            .stream()
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet());
                    constellation.add(other);
                    constellation.add(coord);
                    constellations.add(constellation);
                    continue;
                }
                Set<Coordinate> constellation = new ArrayList<>(find).get(0);
                constellation.add(other);
                constellation.add(coord);
            }
        }
        return constellations.size();
    }

    //there is no part 2 :)

    private void parse() {
        Pattern pattern = Pattern.compile("(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int z = Integer.parseInt(matcher.group(3));
            int fourth = Integer.parseInt(matcher.group(4));
            Coordinate coord = new Coordinate(x, y, z, fourth);
            coordinates.add(coord);
        }
    }
}
