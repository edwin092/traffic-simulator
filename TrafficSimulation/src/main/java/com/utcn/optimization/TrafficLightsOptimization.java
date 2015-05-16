package com.utcn.optimization;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrafficLightsOptimization {

    public static final int MIN = 10;
    public static final int MAX = 30;
    private static List<Integer> phases = new ArrayList<>();

    static {
        phases.add(1);
        phases.add(2);
        phases.add(3);
        phases.add(4);
    }

    public static int[] getRandomTimeList() {
        Random rand = new Random();
        return new int[]{rand.nextInt((MAX - MIN) + 1) + MIN, rand.nextInt((MAX - MIN) + 1) + MIN,
                rand.nextInt((MAX - MIN) + 1) + MIN, rand.nextInt((MAX - MIN) + 1) + MIN};
    }

    public static Integer[] getRandomPhaseOrderList() {

        Collections.shuffle(phases);

        return phases.toArray(new Integer[phases.size()]);
    }
}