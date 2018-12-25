package me.sizableshrimp.adventofcode.days;

import lombok.EqualsAndHashCode;
import lombok.Value;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 extends Day {
    private enum OPCode {
        addr((registers, a, b, c) -> registers[c] = registers[a] + registers[b]),
        addi((registers, a, b, c) -> registers[c] = registers[a] + b),
        mulr((registers, a, b, c) -> registers[c] = registers[a] * registers[b]),
        muli((registers, a, b, c) -> registers[c] = registers[a] * b),
        banr((registers, a, b, c) -> registers[c] = registers[a] & registers[b]),
        bani((registers, a, b, c) -> registers[c] = registers[a] & b),
        borr((registers, a, b, c) -> registers[c] = registers[a] | registers[b]),
        bori((registers, a, b, c) -> registers[c] = registers[a] | b),
        setr((registers, a, b, c) -> registers[c] = registers[a]),
        seti((registers, a, b, c) -> registers[c] = a),
        gtir((registers, a, b, c) -> registers[c] = a > registers[b] ? 1 : 0),
        gtri((registers, a, b, c) -> registers[c] = registers[a] > b ? 1 : 0),
        gtrr((registers, a, b, c) -> registers[c] = registers[a] > registers[b] ? 1 : 0),
        eqir((registers, a, b, c) -> registers[c] = a == registers[b] ? 1 : 0),
        eqri((registers, a, b, c) -> registers[c] = registers[a] == b ? 1 : 0),
        eqrr((registers, a, b, c) -> registers[c] = registers[a] == registers[b] ? 1 : 0);

        Code code;

        @FunctionalInterface
        private interface Code {
            void calculate(int[] input, int a, int b, int c);
        }

        OPCode(Code code) {
            this.code = code;
        }

        private void calculate(int[] registers, int a, int b, int c) {
            code.calculate(registers, a, b, c);
        }

        private static List<OPCode> list() {
            return new ArrayList<>(Arrays.asList(OPCode.values()));
        }
    }

    @Value
    private class Sample {
        int[] before;
        Instruction instruction;
        int[] after;
    }

    @EqualsAndHashCode
    private class Instruction {
        int opcodeNumber;
        int a;
        int b;
        int c;

        private Instruction(String[] split) {
            this.opcodeNumber = Integer.valueOf(split[0]);
            this.a = Integer.valueOf(split[1]);
            this.b = Integer.valueOf(split[2]);
            this.c = Integer.valueOf(split[3]);
        }
    }

    private List<Sample> samples = new ArrayList<>();
    private List<Instruction> testInput = new ArrayList<>();

    @Override
    protected Result doParts() {
        parse();
        int threeOrMore = 0;
        Map<Integer, OPCode> codes = new HashMap<>(); //opcode number, opcode
        Map<Integer, Set<OPCode>> possibles = new HashMap<>();
        Map<Integer, Set<OPCode>> impossibles = new HashMap<>();

        for (Sample sample : samples) {
            Set<OPCode> possibleForSample = new HashSet<>();
            OPCode.list().forEach(opcode -> {
                int[] output = sample.before.clone();
                opcode.calculate(output, sample.instruction.a, sample.instruction.b, sample.instruction.c);
                if (!Arrays.equals(sample.after, output)) return;
                possibleForSample.add(opcode);
            });
            if (possibleForSample.size() >= 3) threeOrMore++;


            OPCode.list().forEach(opcode -> {
                Set<OPCode> impossibleCodes = impossibles.getOrDefault(sample.instruction.opcodeNumber, new HashSet<>());
                if (!possibleForSample.isEmpty() && !possibleForSample.contains(opcode) && !impossibleCodes.contains(opcode) /*make sure it doesn't already have it*/) {
                    impossibleCodes.add(opcode);
                    impossibles.put(sample.instruction.opcodeNumber, impossibleCodes);
                }
                if (!impossibleCodes.contains(opcode)) {
                    Set<OPCode> possibleCodes = possibles.getOrDefault(sample.instruction.opcodeNumber, new HashSet<>());
                    possibleCodes.add(opcode);
                    possibles.put(sample.instruction.opcodeNumber, possibleCodes);
                }
            });
        }

        List<Map.Entry<Integer, Set<OPCode>>> toRemove = possibles.entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 1)
                .collect(Collectors.toList());
        Set<Integer> used = new HashSet<>();
        while (used.size() < 16) {
            for (Map.Entry<Integer, Set<OPCode>> entry : toRemove) {
                if (used.contains(entry.getKey())) continue;
                used.add(entry.getKey());
                removePossibilities(possibles, entry.getValue());
            }
            toRemove = possibles.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() == 1)
                    .collect(Collectors.toList());
        }

        for (Map.Entry<Integer, Set<OPCode>> entry : possibles.entrySet()) {
            codes.put(entry.getKey(), new ArrayList<>(entry.getValue()).get(0));
        }

//        for (Map.Entry<Integer, OPCode> entry : codes.entrySet()) {
//            System.out.println(entry.getKey()+"="+opcodes.get(entry.getValue()));
//        }
//        System.out.println();

        int[] registers = new int[4];
        for (Instruction instruction : testInput) {
            OPCode opcode = codes.get(instruction.opcodeNumber);
            opcode.calculate(registers, instruction.a, instruction.b, instruction.c);
            //System.out.printf("%s %d %d %d with registers %s%n", opcodes.get(opcode), instruction.a, instruction.b, instruction.c, Arrays.toString(registers));
        }

        return new Result(threeOrMore, registers[0]);
    }

    private void removePossibilities(Map<Integer, Set<OPCode>> possibles, Set<OPCode> codes) {
        possibles.values().forEach(set -> {
            if (set.size() > 1 /*don't want to remove from the own set*/) set.removeAll(codes);
        });
    }

    private void parse() {
        Pattern beforePattern = Pattern.compile("Before: \\[(.*)]");
        Pattern afterPattern = Pattern.compile("After: {2}\\[(.*)]");
        int testInputStart = -1;
        for (int i = 0; i < lines.size(); i += 4) {
            Matcher beforeMatcher = beforePattern.matcher(lines.get(i));
            if (!beforeMatcher.matches()) {
                testInputStart = i;
                break; //on to the test program
            }
            Matcher afterMatcher = afterPattern.matcher(lines.get(i + 2));
            afterMatcher.matches();
            String beforeString = beforeMatcher.group(1);
            String opcodesString = lines.get(i + 1);
            String afterString = afterMatcher.group(1);
            int[] before = Arrays.stream(beforeString.split(", "))
                    .mapToInt(Integer::valueOf)
                    .toArray();
            Instruction instruction = new Instruction(opcodesString.split(" "));
            int[] after = Arrays.stream(afterString.split(", "))
                    .mapToInt(Integer::valueOf)
                    .toArray();
            samples.add(new Sample(before, instruction, after));
        }
        for (int i = testInputStart; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;
            Instruction instruction = new Instruction(line.split(" "));
            testInput.add(instruction);
        }
    }
}
