package me.sizableshrimp.adventofcode.days;

import lombok.EqualsAndHashCode;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 extends Day {
    @EqualsAndHashCode
    private class Instruction {
        OPCode code;
        int a;
        int b;
        int c;

        private Instruction(String[] split) {
            this.code = OPCode.valueOf(split[0]);
            this.a = Integer.valueOf(split[1]);
            this.b = Integer.valueOf(split[2]);
            this.c = Integer.valueOf(split[3]);
        }
    }

    private Integer ipRegister;
    private List<Instruction> input = new ArrayList<>();

    @Override
    protected Result doParts() {
        parse();

        int[] registers = new int[6];

        final int part1 = calculateUntil(registers, -1);

        registers = new int[6];
        registers[0] = 1;

        return new Result(part1, calculateUntil(registers, 33));
    }

    private int calculateUntil(int[] registers, int endPointer) {
        while (true) {
            int pointer = registers[ipRegister];
            if (pointer + 1 >= input.size()) break;
            Instruction instruction = input.get(pointer);
            OPCode opcode = instruction.code;
            opcode.calculate(registers, instruction.a, instruction.b, instruction.c);
            if (pointer == endPointer) break;
            registers[ipRegister]++;
        }
        return sumFactors(registers[4]);
    }

    private int sumFactors(int a) {
        List<Integer> factors = new ArrayList<>();
        for (int i = 1; i <= a; i += 1) {
            if (a % i == 0) {
                factors.add(i);
                if (i != a / i) {
                    factors.add(a / i);
                }
            }
        }
        return factors
                .stream()
                .sorted()
                .distinct()
                .mapToInt(i -> i)
                .sum();
    }

    private void parse() {
        Pattern ip = Pattern.compile("#ip (\\d)");
        Matcher matcher = ip.matcher(lines.get(0));
        matcher.matches();
        ipRegister = Integer.valueOf(matcher.group(1));

        for (int i = 1; i < lines.size(); i++) {
            Instruction instruction = new Instruction(lines.get(i).split(" "));
            input.add(instruction);
        }
    }

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

        public void calculate(int[] registers, int a, int b, int c) {
            code.calculate(registers, a, b, c);
        }

        public static List<OPCode> list() {
            return new ArrayList<>(Arrays.asList(OPCode.values()));
        }
    }
}
