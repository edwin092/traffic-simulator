package com.utcn.models;


import java.util.ArrayList;
import java.util.List;

public class IntersectionStatisticsManager {

    private List<IntersectionStatistics> intersectionStatisticsList;

    public IntersectionStatisticsManager() {
        this.intersectionStatisticsList = new ArrayList<>();
    }

    /**
     *
     * @param intersectionId
     */
    public void addNewVehPassedIntersection(int intersectionId) {
        if (intersectionStatisticsList.contains(new IntersectionStatistics(intersectionId))) {
            intersectionStatisticsList
                    .get(intersectionStatisticsList.indexOf(new IntersectionStatistics(intersectionId))).incrementNumberOfVehPassed();
        } else {
            IntersectionStatistics intersectionStatistics = new IntersectionStatistics(intersectionId);
            intersectionStatistics.incrementNumberOfVehPassed();
            intersectionStatisticsList.add(intersectionStatistics);
        }
    }

    public List<IntersectionStatistics> getIntersectionStatisticsList() {
        return intersectionStatisticsList;
    }

    public void setIntersectionStatisticsList(List<IntersectionStatistics> intersectionStatisticsList) {
        this.intersectionStatisticsList = intersectionStatisticsList;
    }
}
