package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 extends Day {
    private Map<Pattern, Character> notes = new HashMap<>();
    private static final String FILLER = ".....";
    private String beginningState;

    @Override
    protected Object part1() {
        parse();

        return getPlantValues(20);
    }

    @Override
    protected Object part2() {
        return getPlantValues(5000, 200);
    }

    private long getPlantValues(long maxGeneration) {
        return getPlantValues(maxGeneration, 0);
    }

    private long getPlantValues(long maxGeneration, long cutoff) {
        StringBuilder state = new StringBuilder(beginningState);
        state.insert(0, FILLER);
        state.append(FILLER);
        int startingPoint = FILLER.length();
        //System.out.println("0: " + state.toString());

        StringBuilder emptyState = new StringBuilder(state);
        for (int i = 0; i < emptyState.length(); i++) {
            emptyState.setCharAt(i, '.');
        }
        for (long generation = 1; generation <= maxGeneration; generation++) {
            StringBuilder newState = new StringBuilder(emptyState);
            for (int j = 2; j < newState.length() - 2; j++) {
                String section = getPlantSection(state, j);
                if (j > newState.length() - 4 && section.contains("#")) {
                    state.append(FILLER);
                    newState.append(FILLER);
                    emptyState.append(FILLER);
                    j += FILLER.length();
                }
                if (j < 3 && section.contains("#")) {
                    state.insert(0, FILLER);
                    newState.insert(0, FILLER);
                    emptyState.append(FILLER);
                    startingPoint += FILLER.length();
                    j += FILLER.length();
                }
                for (Map.Entry<Pattern, Character> entry : notes.entrySet()) {
                    Matcher matcher = entry.getKey().matcher(section);
                    if (matcher.matches()) {
                        newState.setCharAt(j, entry.getValue());
                        break;
                    }
                }
            }
            if (generation == cutoff) {
                long difference = 50_000_000_000L - generation;
                return getGenerationValue(newState.toString(), startingPoint, difference);
            }
            state = newState;
            //System.out.println(generation + ": " + state.toString());
        }
        return getGenerationValue(state.toString(), startingPoint);
    }

    private void parse() {
        beginningState = lines.get(0).split(" ")[2];

        for (int i = 2; i < lines.size(); i++) {
            String[] split = lines.get(i).split(" ");
            String note = split[0].replace(".", "\\.");
            Pattern pattern = Pattern.compile(note);
            notes.put(pattern, split[2].charAt(0));
        }
    }

    private String getPlantSection(StringBuilder builder, int middleIndex) {
        return builder.substring(middleIndex - 2, middleIndex + 3);
    }

    private long getGenerationValue(String generation, long startingPoint) {
        return getGenerationValue(generation, startingPoint, 0);
    }

    private long getGenerationValue(String generation, long startingPoint, long difference) {
        long plantValue = 0;
        char[] array = generation.toCharArray();
        for (int k = 0; k < array.length; k++) {
            char c = array[k];
            if (c == '#') {
                plantValue += k - startingPoint + difference;
            }
        }
        return plantValue;
    }
}
