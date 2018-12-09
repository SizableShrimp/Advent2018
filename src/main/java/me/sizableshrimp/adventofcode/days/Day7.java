package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day7 extends Day {
    private class Node {
        Character id;
        boolean used = false;
        List<Node> required = new ArrayList<>();
        List<Node> availables = new ArrayList<>(); //a list of nodes that are made getAvailableNodes when this one is used

        private Node(Character id) {
            this.id = id;
        }

        private void addAvailable(Node node) {
            availables.add(node);
            availables.sort(Comparator.comparing(n -> n.id));
        }

        private void addRequired(Node node) {
            required.add(node);
        }

        private boolean isAvailable() {
            return !used && required.isEmpty();
        }

        private void use() {
            used = true;
            builder.append(id);
            for (Node node : availables) {
                node.required.remove(this);
            }
        }
    }

    private Map<Character, Node> nodes = new HashMap<>();
    private StringBuilder builder = new StringBuilder();

    @Override
    protected Object part1() {
        parse();
        while (!available().isEmpty()) {
            available().forEach(Node::use);
        }
        //System.out.println(nodes.toString());
        return builder.toString();
    }

    @Override
    protected Object part2() {
        parse();
        final String order = builder.toString();
        StringBuilder result = new StringBuilder();
        int secondsPassed = 0;
        int usedWorkers = 0;
        Map<Character, Integer> workTimes = new HashMap<>(); //character, seconds when expire
        while (result.length() != order.length()) {
            while (workTimes.containsValue(secondsPassed)) {
                Character c = null;
                for (Map.Entry<Character, Integer> entry : workTimes.entrySet()) {
                    if (entry.getValue() == secondsPassed) c = entry.getKey();
                }
                nodes.get(c).use();
                workTimes.remove(c);
                usedWorkers--;
                result.append(c);
            }
            if (usedWorkers < 5) {
                List<Node> available = available();
                for (Node n : available) {
                    if (usedWorkers >= 5 || workTimes.containsKey(n.id)) continue;
                    usedWorkers++;
                    workTimes.put(n.id, secondsPassed+getSeconds(n.id));
                }
            }
            if (result.length() == order.length()) return secondsPassed;
            //System.out.println("Seconds passed: " + secondsPassed + " " + workTimes);
            secondsPassed++;
        }
        return null;
    }

    private int getSeconds(char c) {
        return (c - 'A' + 1) + 60;
    }

    private List<Node> available() {
        return getAllNodes()
                .stream()
                .filter(Node::isAvailable)
                .sorted(Comparator.comparing(n -> n.id))
                .collect(Collectors.toList());
    }

    private void add(Character mainChar, Character availableChar) {
        Set<Node> allNodes = getAllNodes();
        Node main = allNodes
                .stream()
                .filter(n -> n.id == mainChar)
                .findFirst().orElse(new Node(mainChar));
        Node available = allNodes
                .stream()
                .filter(n -> n.id == availableChar)
                .findFirst().orElse(new Node(availableChar));
        main.addAvailable(available);
        available.addRequired(main);
        nodes.put(mainChar, main);
        nodes.put(availableChar, available);
    }

    private Set<Node> getAllNodes() {
        return new HashSet<>(nodes.values());
    }

    private void parse() {
        nodes.clear();
        Pattern pattern = Pattern.compile("Step (\\w) must be finished before step (\\w) can begin.");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            char mainChar = matcher.group(1).charAt(0);
            char availableChar = matcher.group(2).charAt(0);
            add(mainChar, availableChar);
        }
    }
}
