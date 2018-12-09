package me.sizableshrimp.adventofcode.days;

import me.sizableshrimp.adventofcode.Day;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 extends Day {
    private Map<Integer, int[]> guards = new HashMap<>();
    private static final Pattern BEGINS_SHIFT = Pattern.compile("\\[.*] Guard #(\\d+) begins shift");
    private static final Pattern FALLS_ASLEEP = Pattern.compile("\\[.* (?:\\d+):(\\d+)] falls asleep");
    private static final Pattern WAKES_UP = Pattern.compile("\\[.* (?:\\d+):(\\d+)] wakes up");

    @Override
    protected Result doParts() {
        parse();

        int highestSleeper = 0; //the guard ID of the highest sleeper (most minutes slept in total)
        int mostMinutesSlept = 0; //the most minutes slept
        int commonMinute = 0; //the minute in which the guard was most commonly asleep

        int biggestMinuteSleeper = 0; //the guard ID who slept the most days in a given minute
        int mostDaysSlept = 0; //the most days slept in a given minute
        int mostDaysSleptMinute = 0; //the given minute

        for (Map.Entry<Integer, int[]> entry : guards.entrySet()) {
            int max = 0; //the maximum days slept in a given minute
            int maxMinute = 0; //the given minute
            int totalValue = 0; //the total amount of minutes slept
            int[] array = entry.getValue();
            for (int i = 0; i < array.length; i++) {
                totalValue += array[i];
                if (max < array[i]) {
                    max = array[i];
                    maxMinute = i;
                }
            }
            if (mostMinutesSlept < totalValue) {
                highestSleeper = entry.getKey();
                mostMinutesSlept = totalValue;
                commonMinute = maxMinute;
                //System.out.println("Guard #"+entry.getKey()+" got GLOBAL most minutes slept at "+totalValue+" with common minute "+maxMinute);
            }
            if (mostDaysSlept < max) {
                biggestMinuteSleeper = entry.getKey();
                mostDaysSlept = max;
                mostDaysSleptMinute = maxMinute;
                System.out.println("Guard #"+entry.getKey()+" got GLOBAL most common minute slept for sleeping "+max+" days on minute "+maxMinute);
            }
        }
        int part1 = highestSleeper * commonMinute;
        int part2 = biggestMinuteSleeper * mostDaysSleptMinute;
        return new Result(part1, part2);
    }

//    @Override
//    protected Object part1() {
//        int highestSleeper = 0; //the guard ID of the highest sleeper (most minutes slept in total)
//        int mostMinutesSlept = 0; //the most minutes slept
//        int commonMinute = 0; //the minute in which the guard was most commonly asleep
//
//        for (Map.Entry<Integer, int[]> entry : guards.entrySet()) {
//            int max = 0; //the maximum days slept in a given minute
//            int maxMinute = 0; //the given minute
//            int totalValue = 0; //the total amount of minutes slept
//            int[] array = entry.getValue();
//            for (int i = 0; i < array.length; i++) {
//                totalValue += array[i];
//                if (max < array[i]) {
//                    max = array[i];
//                    maxMinute = i;
//                }
//            }
//            if (mostMinutesSlept < totalValue) {
//                highestSleeper = entry.getKey();
//                mostMinutesSlept = totalValue;
//                commonMinute = maxMinute;
//                //System.out.println("Guard #"+entry.getKey()+" got GLOBAL most minutes slept at "+totalValue+" with common minute "+maxMinute);
//            }
//        }
//        return highestSleeper * commonMinute;
//    }
//
//    @Override
//    protected Object part2() {
//        int biggestMinuteSleeper = 0; //the guard ID who slept the most days in a given minute
//        int mostDaysSlept = 0; //the most days slept in a given minute
//        int mostDaysSleptMinute = 0; //the given minute
//        for (Map.Entry<Integer, int[]> entry : guards.entrySet()) {
//            int max = 0; //the maximum days a guard slept in a given minute
//            int maxMinute = 0; //the given minute
//            int[] array = entry.getValue();
//            for (int i = 0; i < array.length; i++) {
//                if (max < array[i]) {
//                    max = array[i];
//                    maxMinute = i;
//                }
//            }
//            if (mostDaysSlept < max) {
//                biggestMinuteSleeper = entry.getKey();
//                mostDaysSlept = max;
//                mostDaysSleptMinute = maxMinute;
//                System.out.println("Guard #"+entry.getKey()+" got GLOBAL most common minute slept for sleeping "+max+" days on minute "+maxMinute);
//            }
//        }
//        return biggestMinuteSleeper * mostDaysSleptMinute;
//    }

    /**
     * Parses the date in an input line
     * @param line A line in the input file
     * @return The epoch millis of the date
     */
    private static long parseMillis(String line) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("[yyyy-MM-dd HH:mm]");
            Date date = format.parse(line.substring(0, 18));
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * Parses the input into the map of guards
     */
    private void parse() {
        lines.sort(Comparator.comparingLong(Day4::parseMillis));
        int guardOnDuty = 0;
        int fallAsleepMinute = 0;
        for (String line : lines) {
            Matcher beginShift = BEGINS_SHIFT.matcher(line);
            Matcher fallsAsleep = FALLS_ASLEEP.matcher(line);
            Matcher wakesUp = WAKES_UP.matcher(line);
            if (beginShift.matches()) {
                guardOnDuty = Integer.parseInt(beginShift.group(1));
                //System.out.println("Guard #"+guardOnDuty+" begin shift");
            } else if (fallsAsleep.matches()) {
                fallAsleepMinute = Integer.parseInt(fallsAsleep.group(1));
                //System.out.println("Guard #"+guardOnDuty+" fell asleep at minute "+fallAsleepMinute);
            } else if (wakesUp.matches()) {
                int[] sleepMinutes = guards.getOrDefault(guardOnDuty, new int[60]);
                int endMinute = Integer.parseInt(wakesUp.group(1));
                for (int i = 0; i < sleepMinutes.length; i++) {
                    if (i >= fallAsleepMinute && i < endMinute) {
                        sleepMinutes[i]++;
                    }
                }
                guards.put(guardOnDuty, sleepMinutes);
                //System.out.println("Guard #"+guardOnDuty+" woke up at minute "+endMinute+" array ("+Arrays.toString(sleepMinutes)+")");
            }
        }
    }
}
