package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.sizableshrimp.adventofcode.Day;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Day13 extends Day {
    @AllArgsConstructor
    @EqualsAndHashCode
    private class Coordinate {
        int x, y;

        private Coordinate direction(Direction direction) {
            if (direction == Direction.UP) {
                return new Coordinate(x, y - 1); //0-based from top left
            } else if (direction == Direction.DOWN) {
                return new Coordinate(x, y + 1); //0-based from top left
            } else if (direction == Direction.LEFT) {
                return new Coordinate(x - 1, y);
            } else if (direction == Direction.RIGHT) {
                return new Coordinate(x + 1, y);
            }
            return null;
        }
    }

    private enum Track {
        BACKSLASH, FORWARDSLASH, VERTICAL, HORIZONTAL, INTERSECTION
    }

    private class Cart {
        @Getter
        @Setter
        Coordinate coordinate;
        @Getter
        @Setter
        Direction direction;
        int turns;

        private Cart(Coordinate coordinate, Direction direction) {
            this.coordinate = coordinate;
            this.direction = direction;
        }

        private Direction getNextTurn() {
            int next = turns % 3;
            turns++;
            if (next == 0) direction = direction.leftTurn();
            if (next == 1) return direction; //go straight
            if (next == 2) direction = direction.rightTurn();
            return direction;
        }

        private Coordinate getNextCoordinate() {
            return coordinate.direction(direction);
        }

        private int getX() {
            return coordinate.x;
        }

        private int getY() {
            return coordinate.y;
        }
    }

    private enum Direction {
        UP, RIGHT, DOWN, LEFT;

        private Direction getDirectionOffset(int rightOffset) {
            Direction[] values = values();
            int index = (ordinal() + rightOffset) % values.length;
            return values[index];
        }

        private Direction rightTurn() {
            return getDirectionOffset(1);
        }

        private Direction leftTurn() {
            return getDirectionOffset(3);
        }

        private Direction backSlash() {
            if (this == Direction.UP) {
                return Direction.LEFT;
            } else if (this == Direction.DOWN) {
                return Direction.RIGHT;
            } else if (this == Direction.LEFT) {
                return Direction.UP;
            } else if (this == Direction.RIGHT) {
                return Direction.DOWN;
            }
            return null;
        }

        private Direction forwardSlash() {
            if (this == Direction.UP) {
                return Direction.RIGHT;
            } else if (this == Direction.DOWN) {
                return Direction.LEFT;
            } else if (this == Direction.LEFT) {
                return Direction.DOWN;
            } else if (this == Direction.RIGHT) {
                return Direction.UP;
            }
            return null;
        }
    }

    private Track[][] tracks;
    private List<Cart> carts = new CopyOnWriteArrayList<>();

    public Day13() {
        super();
        int maxX = lines
                .stream()
                .max(Comparator.comparing(String::length)).map(String::length).orElse(5000);
        tracks = new Track[maxX][lines.size()];
    }

    @Override
    protected Object part1() {
        parse();
        Coordinate crash = getFirstCrash();
        return crash.x + "," + crash.y;
    }

    @Override
    protected Object part2() {
        parse();
        Coordinate coord = getLastNotCrashed();
        return coord.x + "," + coord.y;
    }

    private Coordinate getFirstCrash() {
        while (true) {
            Coordinate crash = moveCarts(true);
            if (crash != null) return crash;
        }
    }

    private Coordinate getLastNotCrashed() {
        while (true) {
            moveCarts(false);
            if (carts.size() == 1) {
                return carts.get(0).coordinate;
            }
        }
    }

    private Coordinate moveCarts(boolean returnCrash) {
        carts.sort(Comparator.comparing(Cart::getX).thenComparing(Cart::getY));
        for (Cart cart : carts) {
            Track track = tracks[cart.getX()][cart.getY()];
            if (track == null) continue;
            Direction direction;
            Coordinate crash;
            if (track == Track.INTERSECTION) {
                direction = cart.getNextTurn();
                crash = moveCart(cart, direction);
            } else {
                direction = cart.getDirection();
                if (track == Track.BACKSLASH) {
                    crash = moveCart(cart, direction.backSlash());
                } else if (track == Track.FORWARDSLASH) {
                    crash = moveCart(cart, direction.forwardSlash());
                } else {
                    crash = moveCart(cart, direction);
                }
            }
            if (returnCrash && crash != null) return crash;
        }
        return null;
    }

    private Coordinate moveCart(Cart cart, Direction direction) {
        cart.setDirection(direction);
        Coordinate next = cart.getNextCoordinate();
        Optional<Cart> collision = carts
                .stream()
                .filter(c -> c.coordinate.equals(next))
                .findFirst();
        if (collision.isPresent()) {
            Cart crash = collision.get();
            carts.remove(crash);
            carts.remove(cart);
            return next;
        }
        cart.setCoordinate(next);
        return null;
    }

    private void parse() {
        carts.clear();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '\\') {
                    tracks[x][y] = Track.BACKSLASH;
                } else if (c == '/') {
                    tracks[x][y] = Track.FORWARDSLASH;
                } else if (c == '|') {
                    tracks[x][y] = Track.VERTICAL;
                } else if (c == '-') {
                    tracks[x][y] = Track.HORIZONTAL;
                } else if (c == 'v') {
                    tracks[x][y] = Track.VERTICAL;
                    carts.add(new Cart(new Coordinate(x, y), Direction.DOWN));
                } else if (c == '^') {
                    tracks[x][y] = Track.VERTICAL;
                    carts.add(new Cart(new Coordinate(x, y), Direction.UP));
                } else if (c == '<') {
                    tracks[x][y] = Track.HORIZONTAL;
                    carts.add(new Cart(new Coordinate(x, y), Direction.LEFT));
                } else if (c == '>') {
                    tracks[x][y] = Track.HORIZONTAL;
                    carts.add(new Cart(new Coordinate(x, y), Direction.RIGHT));
                } else if (c == '+') {
                    tracks[x][y] = Track.INTERSECTION;
                }
            }
        }
    }
}