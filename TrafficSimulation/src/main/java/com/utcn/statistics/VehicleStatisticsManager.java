package com.utcn.statistics;


import java.util.ArrayList;
import java.util.List;

public class VehicleStatisticsManager {
    private List<VehicleStatistics> vehicleStatisticsList;

    public VehicleStatisticsManager() {
        this.vehicleStatisticsList = new ArrayList<>();
    }

    /**
     * Add new vehicle for statistics.
     */
    public void addNewVehicle(int vehicleId, int startTime) {
        vehicleStatisticsList.add(new VehicleStatistics(vehicleId, startTime));
    }

    /**
     * Increment number of intersection passed for vehicle.
     */
    public void incrementIntersectionsPassed(int vehicleId) {
        vehicleStatisticsList
                .get(vehicleStatisticsList.indexOf(new VehicleStatistics(vehicleId))).incrementIntersectionsPassed();
    }

    /**
     * Add vehicle simulation end time.
     */
    public void addVehicleEndTime(int vehicleId, int endTime) {
        vehicleStatisticsList
                .get(vehicleStatisticsList.indexOf(new VehicleStatistics(vehicleId))).setEndTime(endTime);
    }

    /**
     * Get number of vehicles that finished simulation.
     */
    public int getNumberOfVehiclesExited() {
        int k = 0;
        for (VehicleStatistics vehicleStatistics : vehicleStatisticsList) {
            if (vehicleStatistics.getEndTime() > 0) {
                k++;
            }
        }
        return k;
    }

    /**
     * Increment waiting time for vehicle.
     */
    public void incrementVehicleWaitingTime(int vehicleId) {
        vehicleStatisticsList
                .get(vehicleStatisticsList.indexOf(new VehicleStatistics(vehicleId))).incrementWaitingTime();
    }

    /**
     * Get average time of simulation for vehicles.
     */
    public float getVehiclesAverageSimulationTime() {
        int sum = 0;
        for (VehicleStatistics vehicleStatistics : vehicleStatisticsList) {
            if (vehicleStatistics.getEndTime() > 0) {
                sum += vehicleStatistics.getSimulationExitTime();
            }
        }
        return sum / vehicleStatisticsList.size();
    }

    /**
     * Get average time of waiting at intersections for all vehicles.
     */
    public float getAllVehiclesAverageWaitingTime() {
        int sum = 0;
        for (VehicleStatistics vehicleStatistics : vehicleStatisticsList) {
            sum += vehicleStatistics.getWaitingTime();
        }
        return sum / vehicleStatisticsList.size();
    }

    /**
     * Get average time of waiting at intersections only for vehicles that finished the simulation.
     */
    public float getFinishedVehiclesAverageWaitingTime() {
        List<VehicleStatistics> finishedVehicles = new ArrayList<>();
        for (VehicleStatistics vehicleStatistics : vehicleStatisticsList) {
            if (vehicleStatistics.getEndTime() != 0) {
                finishedVehicles.add(vehicleStatistics);
            }
        }

        int sum = 0;
        for (VehicleStatistics vehicleStatistics : finishedVehicles) {
            sum += vehicleStatistics.getWaitingTime();
        }
        return sum / finishedVehicles.size();
    }

    public List<VehicleStatistics> getVehicleStatisticsList() {
        return vehicleStatisticsList;
    }

    public void setVehicleStatisticsList(List<VehicleStatistics> vehicleStatisticsList) {
        this.vehicleStatisticsList = vehicleStatisticsList;
    }
}
