package com.utcn.configurator.flow.model;

import com.utcn.bl.VehicleGenerator;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

public class TrafficFlow {

    @JsonIgnore
    private VehicleGenerator vehicleGenerator;
    private int startingPoint;
    private List<Integer> routeList;
    private int duration;

    public TrafficFlow() {
    }

    public TrafficFlow(int startingPoint, int duration) {
        this.startingPoint = startingPoint;
        this.duration = duration;
    }

    public TrafficFlow(int startingPoint, List<Integer> routeList, int duration) {
        this.startingPoint = startingPoint;
        this.routeList = routeList;
        this.duration = duration;
    }

    public int getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(int startingPoint) {
        this.startingPoint = startingPoint;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        vehicleGenerator = new VehicleGenerator(duration);
        this.duration = duration;
    }

    public List<Integer> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<Integer> routeList) {
        this.routeList = routeList;
    }

    public VehicleGenerator getVehicleGenerator() {
        return vehicleGenerator;
    }

    public void setVehicleGenerator(VehicleGenerator vehicleGenerator) {
        this.vehicleGenerator = vehicleGenerator;
    }
}
