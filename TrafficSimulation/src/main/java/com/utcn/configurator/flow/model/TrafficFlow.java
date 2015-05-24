package com.utcn.configurator.flow.model;

import com.utcn.bl.FastVehicleGenerator;
import com.utcn.bl.MediumVehicleGenerator;
import com.utcn.bl.SlowVehicleGenerator;
import com.utcn.bl.VehicleGenerator;
import org.codehaus.jackson.annotate.JsonIgnore;

public class TrafficFlow {

    @JsonIgnore
    private VehicleGenerator vehicleGenerator;
    private String type;
    private int startingPoint;
    private int startTime;
    private int endTime;

    public TrafficFlow() {
    }

    public TrafficFlow(String type, int startingPoint, int startTime,
                       int endTime) {
        this.type = type;
        this.startingPoint = startingPoint;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

        switch (type) {
            case "Slow":
                vehicleGenerator = new SlowVehicleGenerator();
                break;
            case "Medium":
                vehicleGenerator = new MediumVehicleGenerator();
                break;
            case "Fast":
                vehicleGenerator = new FastVehicleGenerator();
                break;
        }
    }

    public int getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(int startingPoint) {
        this.startingPoint = startingPoint;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    @JsonIgnore
    public VehicleGenerator getVehicleGenerator() {
        return vehicleGenerator;
    }

    @JsonIgnore
    public void setVehicleGenerator(VehicleGenerator vehicleGenerator) {
        this.vehicleGenerator = vehicleGenerator;
    }
}
