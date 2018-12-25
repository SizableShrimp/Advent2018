package me.sizableshrimp.adventofcode.days;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 extends Day {
    private enum Type {
        IMMUNE, INFECTION
    }

    @AllArgsConstructor
    @ToString
    private class Group {
        @Getter
        Type type;
        int units;
        int hitPoints;
        int attackDamage;
        String attackType;
        int initiative;
        List<String> weaknesses = new ArrayList<>();
        List<String> strengths = new ArrayList<>();

        private int effectivePower() {
            return units * attackDamage;
        }

        private void removeUnits(int amount) {
            units -= amount;
        }

        private int calculateDamageFrom(Group other) {
            if (strengths.contains(other.attackType)) return 0;
            if (weaknesses.contains(other.attackType)) return other.effectivePower() * 2;
            return other.effectivePower();
        }
    }

    private class SelectingComparator implements Comparator<Group> {
        @Override
        public int compare(Group first, Group second) {
            //want decreasing order
            if (first.effectivePower() < second.effectivePower()) return 1;
            if (first.effectivePower() > second.effectivePower()) return -1;
            return Integer.compare(first.initiative, second.initiative);
        }
    }

    private class AttackingComparator implements Comparator<Group> {
        @Override
        public int compare(Group first, Group second) {
            //want decreasing order (reversed)
            return Integer.compare(second.initiative, first.initiative);
        }
    }

    private List<Group> groups = new LinkedList<>();

    @Override
    protected Result doParts() {
        int part1 = -1;
        for (int i = 0; true; i++) {
            parse(i);
            List<Integer> oldUnits = new ArrayList<>();
            while (true) {
                if (over()) {
                    if (i == 0) {
                        part1 = getOverallUnits();
                        break;
                    }
                    if (getTotalUnits(Type.IMMUNE) != 0) return new Result(part1, getTotalUnits(Type.IMMUNE));
                    break;
                }
                List<Group> selectingOrder = groups
                        .stream()
                        .filter(g -> g.units != 0)
                        .sorted(new SelectingComparator())
                        .collect(Collectors.toList());
                Map<Group, Group> selecteds = new HashMap<>(); //attacking group, defending group
                for (Group group : selectingOrder) {
                    List<Group> enemies = getEnemy(group.type);
                    enemies.removeIf(enemy -> selecteds.values().contains(enemy));
                    if (enemies.isEmpty()) continue;
                    int maxDamage = enemies
                            .stream()
                            .mapToInt(g -> g.calculateDamageFrom(group))
                            .max().getAsInt();
                    if (maxDamage == 0) continue;
                    List<Group> select = enemies
                            .stream()
                            .filter(g -> g.calculateDamageFrom(group) == maxDamage)
                            .collect(Collectors.toList());
                    if (select.size() != 1) select.sort(new SelectingComparator()); //same sorting
                    selecteds.put(group, select.get(0));
                }
                List<Group> attacking = groups
                        .stream()
                        .filter(g -> g.units != 0)
                        .sorted(new AttackingComparator())
                        .collect(Collectors.toList());
                for (Group group : attacking) {
                    Group target = selecteds.get(group);
                    if (target == null) continue; //no target so just go on
                    int wholeUnitDamage = target.calculateDamageFrom(group);
                    int unitsToRemove = (wholeUnitDamage - (wholeUnitDamage % target.hitPoints)) / target.hitPoints;
                    if (unitsToRemove >= target.units) {
                        target.units = 0;
                    } else {
                        target.removeUnits(unitsToRemove);
                    }
                }
                List<Integer> newUnits = groups
                        .stream()
                        .map(g -> g.units)
                        .filter(u -> u != 0)
                        .collect(Collectors.toList());
                if (newUnits.equals(oldUnits)) break;
                oldUnits = newUnits;
            }
        }
    }

    private boolean over() {
        boolean immunesGone = getTotalUnits(Type.IMMUNE) == 0;
        boolean infectionsGone = getTotalUnits(Type.INFECTION) == 0;
        return immunesGone || infectionsGone;
    }

    //gets the opposing enemy of the type
    private List<Group> getEnemy(Type type) {
        return groups
                .stream()
                .filter(g -> g.units != 0)
                .filter(g -> g.type != type)
                .collect(Collectors.toList());
    }

    //gets the type itself
    private List<Group> getType(Type type) {
        return groups
                .stream()
                .filter(g -> g.units != 0)
                .filter(g -> g.type == type)
                .collect(Collectors.toList());
    }

    private int getTotalUnits(Type type) {
        return getType(type).stream()
                .mapToInt(g -> g.units)
                .sum();
    }

    private int getOverallUnits() {
        return groups
                .stream()
                .mapToInt(g -> g.units)
                .filter(u -> u != 0)
                .sum();
    }

    private void parse(int boost) {
        groups.clear();
        Pattern pattern = Pattern.compile("(\\d+) units each with (\\d+) hit points *(.*) with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");
        Type type = Type.IMMUNE;
        for (String s : lines) {
            if (s.equals("Infection:")) {
                type = Type.INFECTION;
                continue;
            }
            Matcher matcher = pattern.matcher(s);
            if (!matcher.matches()) continue;
            int units = Integer.parseInt(matcher.group(1));
            int hitPoints = Integer.parseInt(matcher.group(2));
            List<String> weaknesses = parseWeaknesses(matcher.group(3));
            List<String> strengths = parseStrengths(matcher.group(3));
            int attackDamage = Integer.parseInt(matcher.group(4));
            if (type == Type.IMMUNE) attackDamage += boost;
            String attackType = matcher.group(5);
            int initiative = Integer.parseInt(matcher.group(6));
            Group group = new Group(type, units, hitPoints, attackDamage, attackType, initiative, weaknesses, strengths);
            groups.add(group);
        }
    }

    private List<String> parseWeaknesses(String input) {
        return parse(input, "weak to");
    }

    private List<String> parse(String input, String type) {
        int index = input.indexOf(type);
        if (index == -1) return new LinkedList<>();
        List<String> list = new LinkedList<>();
        for (String s : input.substring(index).split(" ")) {
            if (Arrays.asList(type.split(" ")).contains(s)) continue;
            s = s.replace(",", "").replace(")", "");
            boolean end = s.contains(";");
            s = s.replace(";", "");
            list.add(s);
            if (end) break;
        }
        return list;
    }

    private List<String> parseStrengths(String input) {
        return parse(input, "immune to");
    }
}
