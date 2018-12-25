package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day15 extends Day {
    public enum Square {
        CAVERN, WALL, GOBLIN, ELF;

        @Override
        public String toString() {
            if (this == Square.CAVERN) return ".";
            if (this == Square.WALL) return "#";
            if (this == Square.GOBLIN) return "G";
            if (this == Square.ELF) return "E";
            return "";
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private class Coordinate {
        @Getter
        int x, y;

        private List<Coordinate> adjacents() {
            List<Coordinate> adjacents = new ArrayList<>();
            adjacents.add(new Coordinate(x, y - 1));
            adjacents.add(new Coordinate(x - 1, y));
            adjacents.add(new Coordinate(x + 1, y));
            adjacents.add(new Coordinate(x, y + 1));
            return adjacents
                    .stream()
                    .filter(c -> c.insideGrid() && grid[c.y][c.x] == Square.CAVERN)
                    .collect(Collectors.toList());
        }

        private boolean insideGrid() {
            return x >= 0 && x < maxX && y >= 0 && y < maxY;
        }
    }

    private class Fighter {
        final Square type;
        @Getter
        int x, y;
        @Getter
        @Setter
        int hp = 200;
        int attackPower;

        private Fighter(Square type, int x, int y) {
            this(type, x, y, 3);
        }

        private Fighter(Square type, int x, int y, int attackPower) {
            if (type != Square.GOBLIN && type != Square.ELF) throw new UnsupportedOperationException();
            this.type = type;
            this.x = x;
            this.y = y;
            this.attackPower = attackPower;
        }

        private void doTurn() {
            if (this.isDead()) return;
            if (isOver()) return;
            Fighter target = inRange();
            if (target == null) {
                move();
                target = inRange(); //need to recalculate because moved
            }
            if (target != null && target.isAlive()) {
                target = inRangeLowestHP();
                if (target == null) throw new IllegalStateException();
                attack(target);
            }
        }

        private void move(int x, int y) {
            grid[this.y][this.x] = Square.CAVERN; //moving out
            grid[y][x] = type; //moving in
            this.x = x;
            this.y = y;
        }

        private void attack(Fighter target) {
            target.setHp(target.getHp() - attackPower);
            if (target.getHp() <= 0) {
                grid[target.y][target.x] = Square.CAVERN;
                if (isOver() && fighters.get(fighters.size() - 1).equals(this)) {
                    endedOnLastFighter = true;
                }
            }
        }

        private Fighter inRange() {
            List<Fighter> inRange = adjacentFighters();
            return inRange
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(f -> f.isTarget(type))
                    .filter(Fighter::isAlive)
                    .findFirst()
                    .orElse(null);
        }

        private List<Fighter> adjacentFighters() {
            List<Fighter> inRange = new ArrayList<>();
            inRange.add(getFighter(x, y - 1));
            inRange.add(getFighter(x - 1, y));
            inRange.add(getFighter(x + 1, y));
            inRange.add(getFighter(x, y + 1));
            return inRange;
        }

        private Fighter inRangeLowestHP() {
            List<Fighter> inRange = adjacentFighters();
            return inRange
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(f -> f.isTarget(type))
                    .filter(Fighter::isAlive)
                    .sorted(Comparator.comparing(Fighter::getY).thenComparing(Fighter::getX))
                    .min(Comparator.comparing(Fighter::getHp))
                    .orElse(null);
        }

        private void move() {
            List<Coordinate> adjacents = fighters
                    .stream()
                    .filter(f -> f.isTarget(type))
                    .filter(Fighter::isAlive)
                    .map(Fighter::adjacents)
                    .flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList());
            breadthFirstSearch(this, adjacents);
        }

        private boolean isTarget(Square t) {
            return type != t;
        }

        private List<Coordinate> adjacents() {
            return new Coordinate(x, y).adjacents();
        }

        private Coordinate toCoordinate() {
            return new Coordinate(x, y);
        }

        private boolean isAlive() {
            return !isDead();
        }

        private boolean isDead() {
            return hp <= 0;
        }
    }

    private Square[][] grid;
    private int maxY;
    private int maxX;
    private List<Fighter> fighters = new ArrayList<>();
    private boolean endedOnLastFighter = false; //if the last fighter in reading order ended it (add one to the round)

    public Day15() {
        super();
        maxY = lines
                .stream()
                .max(Comparator.comparing(String::length)).map(String::length).orElse(50);
        maxX = lines.size();
        grid = new Square[maxY][maxX];
    }

    @Override
    protected Object part1() {
        parse();
        int round = 0;
        fighters.sort(Comparator.comparing(Fighter::getY).thenComparing(Fighter::getX));
        while (true) {
            ListIterator<Fighter> iter = fighters.listIterator();
            while (iter.hasNext()) {
                Fighter f = iter.next();
                if (f.isDead()) {
                    iter.remove();
                    continue;
                }
                f.doTurn();
                if (isOver()) {
                    if (endedOnLastFighter) round++;
                    break;
                }
            }
            if (isOver()) {
                return round * getAllHp();
            }
            fighters.sort(Comparator.comparing(Fighter::getY).thenComparing(Fighter::getX));
            round++;
        }
    }

    @Override
    protected Object part2() {
        for (int elfPower = 1; true; elfPower++) {
            parse(elfPower);
            int elfDeaths = 0;

            int round = 0;
            fighters.sort(Comparator.comparing(Fighter::getY).thenComparing(Fighter::getX));
            while (true) {
                ListIterator<Fighter> iter = fighters.listIterator();
                while (iter.hasNext()) {
                    Fighter f = iter.next();
                    if (f.isDead()) {
                        if (f.type == Square.ELF) elfDeaths++;
                        iter.remove();
                        continue;
                    }
                    f.doTurn();
                    if (isOver()) {
                        if (endedOnLastFighter) round++;
                        break;
                    }
                }
                if (isOver()) {
                    break;
                }
                fighters.sort(Comparator.comparing(Fighter::getY).thenComparing(Fighter::getX));
                round++;
            }
            if (elfDeaths == 0) {
                return round * getAllHp();
            }
        }
    }

    private void parse() {
        parse(3);
    }

    private void parse(int elfPower) {
        grid = new Square[maxY][maxX];
        fighters.clear();
        endedOnLastFighter = false;
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '#') {
                    grid[y][x] = Square.WALL;
                } else if (c == '.') {
                    grid[y][x] = Square.CAVERN;
                } else if (c == 'E') {
                    fighters.add(new Fighter(Square.ELF, x, y, elfPower));
                    grid[y][x] = Square.ELF;
                } else if (c == 'G') {
                    fighters.add(new Fighter(Square.GOBLIN, x, y));
                    grid[y][x] = Square.GOBLIN;
                }
            }
        }
    }

    private int getAllHp() {
        return fighters
                .stream()
                .filter(Fighter::isAlive)
                .mapToInt(f -> f.hp)
                .sum();
    }

    private Fighter getFighter(int x, int y) {
        return fighters
                .stream()
                .filter(Fighter::isAlive)
                .filter(f -> f.x == x)
                .filter(f -> f.y == y)
                .findFirst().orElse(null);
    }

    private List<Fighter> getTargets(Square type) {
        if (type == Square.CAVERN || type == Square.WALL) throw new UnsupportedOperationException();
        return fighters
                .stream()
                .filter(f -> f.isTarget(type))
                .filter(Fighter::isAlive)
                .collect(Collectors.toList());
    }

    private boolean isOver() {
        return getTargets(Square.ELF).isEmpty() || getTargets(Square.GOBLIN).isEmpty();
    }

    @AllArgsConstructor
    private class Node {
        @Getter
        int x, y;
        @Getter
        Node node;
    }

    private void breadthFirstSearch(Fighter start, List<Coordinate> adjacents) {
        Node[][] visited = new Node[maxY][maxX];

        for (int y = 0; y < visited.length; y++) {
            for (int x = 0; x < visited[0].length; x++) {
                if (grid[y][x] == Square.CAVERN) visited[y][x] = new Node(x, y, null);
            }
        }

        ArrayDeque<Coordinate> locations = new ArrayDeque<>();
        ArrayList<Coordinate> destinationsFound = new ArrayList<>();

        locations.offer(start.toCoordinate());

        while (!locations.isEmpty()) {
            Coordinate location = locations.poll();

            if (adjacents.contains(location)) {
                // We have found one of our destinations.
                destinationsFound.add(location);
                continue;
            }

            List<Coordinate> neighbors = location.adjacents();

            neighbors.stream()
                    .filter(neighbor -> {
                        Node node = visited[neighbor.y][neighbor.x];
                        return node != null && node.node == null;
                    })
                    .forEach(neighbor -> {
                        visited[neighbor.y][neighbor.x].node = new Node(neighbor.x, neighbor.y, visited[location.y][location.x]);
                        locations.offer(neighbor);
                    });
        }

        destinationsFound.stream()
                .map(destination -> visited[destination.y][destination.x])
                .min(Comparator.comparingInt(this::getPathLength)
                        .thenComparingInt(Node::getY)
                        .thenComparingInt(Node::getX))
                .ifPresent(node -> {
                    while (node.node != null) {
                        node = node.node;
                    }

                    start.move(node.x, node.y);
                });
    }

    private int getPathLength(Node node) {
        int length = 1;
        while (node.node != null) {
            length++;
            node = node.node;
        }

        return length;
    }
}
