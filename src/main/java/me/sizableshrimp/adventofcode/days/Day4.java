package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Day4 extends Day {
    private Map<Integer, int[]> guards = new HashMap<>();
    @Override
    protected Object part1() {
        lines().sort(Comparator.comparingLong(Day4::convertFromString));
        int guardOnDuty = 0;
        int fallAsleepMinute = 0;
        for (String line : lines()) {
            if (line.contains("begins shift")) {
                guardOnDuty = Integer.parseInt(line.split(" ")[3].substring(1));
                System.out.println("Guard #"+guardOnDuty+" begin shift");
            } else if (line.contains("falls asleep")) {
                fallAsleepMinute = parseMinutes(line);
                System.out.println("Guard #"+guardOnDuty+" fell asleep at minute "+(fallAsleepMinute+1));
            } else if (line.contains("wakes up")) {
                int[] sleepMinutes = guards.getOrDefault(guardOnDuty, new int[60]);
                int endMinute = parseMinutes(line);
                for (int i = 0; i < sleepMinutes.length; i++) {
                    if (i >= fallAsleepMinute && i <= endMinute) {
                        sleepMinutes[i] = sleepMinutes[i]+1;
                    }
                }
                guards.put(guardOnDuty, sleepMinutes);
                System.out.println("Guard #"+guardOnDuty+" woke up at minute "+(endMinute+1)+" array ("+Arrays.toString(sleepMinutes)+")");
            }
        }
        int highestGuardId = 0;
        int highestGuardMinute = 0;
        int highestGuardValue = 0;
        for (Map.Entry<Integer, int[]> entry : guards.entrySet()) {
            int max = 0;
            int maxIndex = 0;
            int[] array = entry.getValue();
            for (int i = 0; i < array.length; i++) {
                if (max < array[i]) {
                    max = array[i];
                    maxIndex = i;
                }
            }
            System.out.println("Guard #"+entry.getKey()+" achieved a PERSONAL max of "+max+" on minute "+(maxIndex+1));
            if (highestGuardValue < max) {
                highestGuardId = entry.getKey();
                highestGuardMinute = maxIndex;
                highestGuardValue = max;
                System.out.println("Guard #"+entry.getKey()+" got a GLOBAL max of "+max+" on minute "+(maxIndex+1));
            }
        }
        return highestGuardId * (highestGuardMinute+1);
    }

    @Override
    protected Object part2() {
        return null;
    }

    private static long convertFromString(String s) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            s = s.substring(1, 17);
            Date date = format.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private static int parseMinutes(String s) {
        return Integer.parseInt(s.substring(15, 17));
    }
}
