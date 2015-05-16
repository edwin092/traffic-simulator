package com.utcn.bl;

import java.util.Random;

public class MediumVehicleGenerator extends VehicleGenerator {

    public static final int MIN = 2;
    public static final int MAX = 8;

    public MediumVehicleGenerator() {
        super();
    }

    @Override
    public void generateNewTime() {
        Random rand = new Random();
        counter = rand.nextInt((MAX - MIN) + 1) + MIN;
    }
}
