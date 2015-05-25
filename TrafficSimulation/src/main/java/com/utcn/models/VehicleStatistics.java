package com.utcn.models;


public class VehicleStatistics {

    // time to go through an intersection
    public static final int INTERSECTION_PASS_TIME = 3;

    private int vehicleId;
    private int numberOfIntersectionsPassed;
    private int startTime;
    private int endTime;

    public VehicleStatistics(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public VehicleStatistics(int vehicleId, int startTime) {
        this.vehicleId = vehicleId;
        this.startTime = startTime;
    }

    public void incrementIntersectionsPassed(){
        this.numberOfIntersectionsPassed++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleStatistics that = (VehicleStatistics) o;

        return vehicleId == that.vehicleId;

    }

    @Override
    public int hashCode() {
        return vehicleId;
    }

    public int getSimulationExitTime() {
        return endTime - startTime + (numberOfIntersectionsPassed * INTERSECTION_PASS_TIME);
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
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

    public int getNumberOfIntersectionsPassed() {
        return numberOfIntersectionsPassed;
    }

    public void setNumberOfIntersectionsPassed(int numberOfIntersectionsPassed) {
        this.numberOfIntersectionsPassed = numberOfIntersectionsPassed;
    }
}
