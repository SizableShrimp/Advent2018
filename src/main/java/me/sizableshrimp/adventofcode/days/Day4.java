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
                System.out.println("Guard #"+guardOnDuty+" fell asleep at minute "+fallAsleepMinute);
            } else if (line.contains("wakes up")) {
                int[] sleepMinutes = guards.getOrDefault(guardOnDuty, new int[60]);
                int endMinute = parseMinutes(line);
                for (int i = 0; i < sleepMinutes.length; i++) {
                    if (i >= fallAsleepMinute && i < endMinute) {
                        sleepMinutes[i] = sleepMinutes[i]+1;
                    }
                }
                guards.put(guardOnDuty, sleepMinutes);
                System.out.println("Guard #"+guardOnDuty+" woke up at minute "+endMinute+" array ("+Arrays.toString(sleepMinutes)+")");
            }
        }
        int globalGuardId = 0;
        int globalMostMinutesSlept = 0;
        int globalCommonMinute = 0;
        for (Map.Entry<Integer, int[]> entry : guards.entrySet()) {
            int max = 0;
            int maxMinute = 0;
            int totalValue = 0;
            int[] array = entry.getValue();
            for (int i = 0; i < array.length; i++) {
                totalValue += array[i];
                if (max < array[i]) {
                    max = array[i];
                    maxMinute = i+1; //compensate for 0-based arrays
                }
            }
            System.out.println("Guard #"+entry.getKey()+" achieved a PERSONAL most minutes slept at "+totalValue+" with common minute "+maxMinute+" because they slept for "+max+" days during that minute");
            if (globalMostMinutesSlept < totalValue) {
                globalGuardId = entry.getKey();
                globalMostMinutesSlept = totalValue;
                globalCommonMinute = maxMinute;
                System.out.println("Guard #"+entry.getKey()+" got GLOBAL most minutes slept at "+totalValue+" with common minute "+maxMinute+" because they slept for "+max+" days during that minute");
            }
        }
        return globalGuardId * globalCommonMinute;
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
