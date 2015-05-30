package com.utcn.bl;


import com.utcn.models.StandardEngine;
import com.utcn.models.Vehicle;

public class VehicleGenerator {

    public static int currentId;
    protected int counter;
    private int duration;

    public VehicleGenerator(int duration) {
        this.counter = duration;
        this.duration = duration;
    }

    public Vehicle initNewVehicle() {
        Vehicle vehicle = new Vehicle();

        incrementCurrentId();

        vehicle.setId(currentId);
        vehicle.setEngine(new StandardEngine());
        vehicle.setSize(Vehicle.SIZE);

        return vehicle;
    }

    private void incrementCurrentId() {
        currentId++;
    }

    public void generateNewTime() {
        // TODO add variation
        counter = duration;
    }

    public boolean isCounterZero() {
        counter--;
        if (counter == 0) {
            generateNewTime();
            return true;
        }

        return false;
    }
}
