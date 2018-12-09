package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 extends Day {
    private class Node {
        List<Node> childNodes = new ArrayList<>();
        List<Integer> metaEntries = new ArrayList<>();

        void addChildren(Node child) {
            childNodes.add(child);
        }

        void addEntry(Integer i) {
            metaEntries.add(i);
        }

        int getValue() {
            if (childNodes.isEmpty()) return sumMetaEntries();
            int value = 0;
            for (Integer entry : metaEntries) {
                if (entry-1 >= childNodes.size()) continue;
                value += childNodes.get(entry-1).getValue();
            }
            return value;
        }

        private int sumMetaEntries() {
            return metaEntries
                    .stream()
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        Set<Node> getAllNodes() {
            Set<Node> nodes = new HashSet<>();
            nodes.add(this);
            childNodes.forEach(n -> nodes.addAll(n.getAllNodes()));
            return nodes;
        }
    }

    private Node root;

    @Override
    protected Object part1() {
        parse();
        return root.getAllNodes()
                .stream()
                .mapToInt(Node::sumMetaEntries)
                .sum();
    }

    @Override
    protected Object part2() {
        return root.getValue();
    }

    private int index = 0;

    private void parse() {
        List<Integer> split = Arrays.stream(lines.get(0).split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        root = parseNode(split.get(index), split.get(++index), split);
    }

    private Node parseNode(int childNodes, int metaEntries, List<Integer> split) {
        Node node = new Node();
        for (int currentChildNode = 0; currentChildNode < childNodes; currentChildNode++) {
            Node child = parseNode(split.get(++index), split.get(++index), split);
            node.addChildren(child);
        }
        for (int currentMetaEntry = 0; currentMetaEntry < metaEntries; currentMetaEntry++) {
            node.addEntry(split.get(++index));
        }
        return node;
    }
}