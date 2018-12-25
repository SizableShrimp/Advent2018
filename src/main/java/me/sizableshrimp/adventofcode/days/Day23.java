package me.sizableshrimp.adventofcode.days;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import me.sizableshrimp.adventofcode.Day;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day23 extends Day {
    @AllArgsConstructor
    private class Nanobot {
        Coordinate coord;
        int radius;

        private boolean inRangeOf(Nanobot other) {
            int distance = coord.distance(other.coord);
            return distance <= other.radius;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private class Coordinate {
        int x, y, z;

        private int distance(Coordinate other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y) + Math.abs(other.z - z);
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }
    }

    private List<Nanobot> bots = new LinkedList<>();

    @Override
    protected Object part1() {
        parse();
        Nanobot strongest = bots
                .stream()
                .max(Comparator.comparingInt(b -> b.radius))
                .get();
        int total = 0;
        for (Nanobot bot : bots) {
            if (bot.inRangeOf(strongest)) total++;
        }
        return total;
    }

    private ArithExpr abs(Context context, ArithExpr expr) {
        return (ArithExpr) context.mkITE(context.mkGe(expr, context.mkInt(0)), expr, context.mkSub(context.mkInt(0), expr));
    }

    @Override
    protected Object part2() {
        try (Context context = new Context()) {
            IntExpr x = context.mkIntConst("x");
            IntExpr y = context.mkIntConst("y");
            IntExpr z = context.mkIntConst("z");
            List<IntExpr> inRanges = new LinkedList<>();
            for (int i = 0; i < bots.size(); i++) {
                inRanges.add(context.mkIntConst("in_range_" + i));
            }
            IntExpr rangeCount = context.mkIntConst("sum");
            Optimize optimize = context.mkOptimize();
            for (int i = 0; i < bots.size(); i++) {
                Nanobot bot = bots.get(i);
                IntExpr bx = context.mkInt(bot.coord.x);
                IntExpr by = context.mkInt(bot.coord.y);
                IntExpr bz = context.mkInt(bot.coord.z);
                ArithExpr distance = context.mkAdd(abs(context, context.mkSub(x, bx)), abs(context, context.mkSub(y, by)), abs(context, context.mkSub(z, bz)));
                BoolExpr inRange = context.mkLe(distance, context.mkInt(bot.radius));
                Expr expr = context.mkITE(inRange, context.mkInt(1), context.mkInt(0));
                optimize.Add(context.mkEq(inRanges.get(i), expr));
            }
            optimize.Add(context.mkEq(rangeCount, context.mkAdd(inRanges.toArray(new ArithExpr[0]))));
            IntExpr distanceFromZero = context.mkIntConst("dist");
            optimize.Add(context.mkEq(distanceFromZero, context.mkAdd(abs(context, x), abs(context, y), abs(context, y))));
            optimize.MkMaximize(rangeCount);
            optimize.MkMinimize(distanceFromZero);
            if (optimize.Check() == Status.SATISFIABLE) {
                Model m = optimize.getModel();
                Coordinate coord = new Coordinate(Integer.parseInt(m.getConstInterp(x).toString()), Integer.parseInt(m.getConstInterp(y).toString()), Integer.parseInt(m.getConstInterp(z).toString()));
                return coord.distance(new Coordinate(0, 0, 0));
            }
        }
        return null;
    }

    private void parse() {
        Pattern pattern = Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int z = Integer.parseInt(matcher.group(3));
            int radius = Integer.parseInt(matcher.group(4));
            Nanobot bot = new Nanobot(new Coordinate(x, y, z), radius);
            bots.add(bot);
        }
    }
}
