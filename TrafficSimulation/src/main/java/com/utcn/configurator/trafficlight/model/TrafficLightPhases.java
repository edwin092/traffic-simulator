package com.utcn.configurator.trafficlight.model;


public class TrafficLightPhases {

    private int intersectionId;
    private int phase1Time;
    private int phase2Time;
    private int phase3Time;
    private int phase4Time;

    public TrafficLightPhases() {
    }

    public TrafficLightPhases(int intersectionId, int phase1Time, int phase2Time, int phase3Time, int phase4Time) {
        this.intersectionId = intersectionId;
        this.phase1Time = phase1Time;
        this.phase2Time = phase2Time;
        this.phase3Time = phase3Time;
        this.phase4Time = phase4Time;
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(int intersectionId) {
        this.intersectionId = intersectionId;
    }

    public int getPhase1Time() {
        return phase1Time;
    }

    public void setPhase1Time(int phase1Time) {
        this.phase1Time = phase1Time;
    }

    public int getPhase2Time() {
        return phase2Time;
    }

    public void setPhase2Time(int phase2Time) {
        this.phase2Time = phase2Time;
    }

    public int getPhase3Time() {
        return phase3Time;
    }

    public void setPhase3Time(int phase3Time) {
        this.phase3Time = phase3Time;
    }

    public int getPhase4Time() {
        return phase4Time;
    }

    public void setPhase4Time(int phase4Time) {
        this.phase4Time = phase4Time;
    }
}
