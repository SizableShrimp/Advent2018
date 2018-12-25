package me.sizableshrimp.adventofcode.days;

import lombok.EqualsAndHashCode;
import me.sizableshrimp.adventofcode.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day21 extends Day {
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

        @Override
        public String toString() {
            return String.format("%s %d %d %d", code.toString(), a, b, c);
        }
    }

    private Integer ipRegister;
    private List<Instruction> input = new ArrayList<>();

    @Override
    protected Result doParts() {
        parse();

        int[] registers = new int[6];

        List<Integer> fours = new ArrayList<>();
        while (true) {
            int pointer = registers[ipRegister];
            if (pointer == 28) {
                if (fours.contains(registers[4])) return new Result(fours.get(0), fours.get(fours.size()-1));
                fours.add(registers[4]);
                //System.out.println(fours.size());
            }
            if (pointer == 18) {
                int num = registers[1] + (256 - (registers[1] % 256));
                registers[2] = (num / 256) - 1;
                registers[3] = 1;
                registers[ipRegister] = 23;
                continue;
            }
            Instruction instruction = input.get(pointer);
            //int[] before = registers.clone();
            OPCode opcode = instruction.code;
            opcode.calculate(registers, instruction.a, instruction.b, instruction.c);
            //System.out.printf("ip=%d %s %s %s%n", pointer, Arrays.toString(before), instruction.toString(), Arrays.toString(registers));
            registers[ipRegister]++;
        }
    }

    private void parse() {
        Matcher matcher = Pattern.compile("#ip (\\d)").matcher(lines.get(0));
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
