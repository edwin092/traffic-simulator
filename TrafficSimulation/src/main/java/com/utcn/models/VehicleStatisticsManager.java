package com.utcn.models;


import java.util.ArrayList;
import java.util.List;

public class VehicleStatisticsManager {
    private List<VehicleStatistics> vehicleStatisticsList;

    public VehicleStatisticsManager() {
        this.vehicleStatisticsList = new ArrayList<>();
    }


    public void addNewVehicle(int vehicleId, int startTime) {
        vehicleStatisticsList.add(new VehicleStatistics(vehicleId, startTime));
    }

    public void incrementIntersectionsPassed(int vehicleId) {
        vehicleStatisticsList
                .get(vehicleStatisticsList.indexOf(new VehicleStatistics(vehicleId))).incrementIntersectionsPassed();
    }

    public void addVehicleEndTime(int vehicleId, int endTime) {
        vehicleStatisticsList
                .get(vehicleStatisticsList.indexOf(new VehicleStatistics(vehicleId))).setEndTime(endTime);
    }

    public int getNumberOfVehiclesExited() {
        int k = 0;
        for (VehicleStatistics vehicleStatistics : vehicleStatisticsList) {
            if (vehicleStatistics.getEndTime() > 0) {
                k++;
            }
        }
        return k;
    }

    public int getVehiclesAverageSimulationTime() {
        int sum = 0;
        for (VehicleStatistics vehicleStatistics : vehicleStatisticsList) {
            if (vehicleStatistics.getEndTime() > 0) {
                sum += vehicleStatistics.getSimulationExitTime();
            }
        }
        return sum / vehicleStatisticsList.size();
    }

    public List<VehicleStatistics> getVehicleStatisticsList() {
        return vehicleStatisticsList;
    }

    public void setVehicleStatisticsList(List<VehicleStatistics> vehicleStatisticsList) {
        this.vehicleStatisticsList = vehicleStatisticsList;
    }
}
