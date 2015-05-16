package com.utcn.bl;


public class FastVehicleGenerator extends VehicleGenerator {

    public FastVehicleGenerator() {
        super();
    }

    @Override
    public void generateNewTime() {
        counter = 1;
    }
}
