package com.utcn.bl;


import com.utcn.models.StandardEngine;
import com.utcn.models.Vehicle;

public abstract class VehicleGenerator {

    public static int currentId;
    protected int counter;

    public VehicleGenerator() {
        generateNewTime();
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

    public abstract void generateNewTime();

    public boolean isCounterZero() {
        counter--;
        if (counter == 0) {
            generateNewTime();
            return true;
        }

        return false;
    }
}
