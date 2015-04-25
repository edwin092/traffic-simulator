package com.utcn.bl;

import com.utcn.models.StandardEngine;
import com.utcn.models.Vehicle;

import java.util.Random;

public class VehicleGenerator {

    public static final int MIN = 2;
    public static final int MAX = 8;

    private int currentId;
    private int counter;

    public VehicleGenerator() {
        generateNewRandomTime();
    }

    public Vehicle generateNewVehicle() {
        Vehicle vehicle = new Vehicle();

        incrementCurrentId();
        // setare
        vehicle.setId(currentId);
        vehicle.setEngine(new StandardEngine());
        vehicle.setSize(Vehicle.SIZE);

        return vehicle;
    }

    public void generateNewRandomTime() {
        Random rand = new Random();
        counter = rand.nextInt((MAX - MIN) + 1) + MIN;
    }

    public boolean isCounterZero() {
        if (counter == 0) {
            generateNewRandomTime();
            return true;
        }

        counter--;
        return false;
    }

    private void incrementCurrentId() {
        currentId++;
    }
}