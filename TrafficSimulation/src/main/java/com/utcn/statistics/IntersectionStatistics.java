package com.utcn.statistics;

public class IntersectionStatistics {
    // id of intersection
    private int intersectionId;
    // the number of vehicles that passed through intersection
    private int numberOfVehPassed;

    public IntersectionStatistics() {
    }

    public IntersectionStatistics(int intersectionId) {
        this.intersectionId = intersectionId;
    }

    public void incrementNumberOfVehPassed() {
        this.numberOfVehPassed++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntersectionStatistics that = (IntersectionStatistics) o;

        return intersectionId == that.intersectionId;

    }

    @Override
    public int hashCode() {
        return intersectionId;
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(int intersectionId) {
        this.intersectionId = intersectionId;
    }

    public int getNumberOfVehPassed() {
        return numberOfVehPassed;
    }

    public void setNumberOfVehPassed(int numberOfVehPassed) {
        this.numberOfVehPassed = numberOfVehPassed;
    }
}
