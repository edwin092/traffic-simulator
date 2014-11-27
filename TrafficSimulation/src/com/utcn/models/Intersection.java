package com.utcn.models;

public class Intersection {

	// semafoare din intersectie: true-verde, false-rosu
	private boolean trafficLightNorth;
	private boolean trafficLightSouth;
	private boolean trafficLightVest;
	private boolean trafficLightEast;
	private int phases = 4; // 4 faze
	private int currentPhase = 1;
	private int phaseCounter;
	// liste tronsoane
	private Segment segmentNorth;
	private Segment segmentSouth;
	private Segment segmentVest;
	private Segment segmentEast;

	public boolean isTrafficLightNorth() {
		return trafficLightNorth;
	}

	public void setTrafficLightNorth(boolean trafficLightNorth) {
		this.trafficLightNorth = trafficLightNorth;
	}

	public boolean isTrafficLightSouth() {
		return trafficLightSouth;
	}

	public void setTrafficLightSouth(boolean trafficLightSouth) {
		this.trafficLightSouth = trafficLightSouth;
	}

	public boolean isTrafficLightVest() {
		return trafficLightVest;
	}

	public void setTrafficLightVest(boolean trafficLightVest) {
		this.trafficLightVest = trafficLightVest;
	}

	public boolean isTrafficLightEast() {
		return trafficLightEast;
	}

	public void setTrafficLightEast(boolean trafficLightEast) {
		this.trafficLightEast = trafficLightEast;
	}

	public Segment getSegmentNorth() {
		return segmentNorth;
	}

	public void setSegmentNorth(Segment segmentNorth) {
		this.segmentNorth = segmentNorth;
	}

	public Segment getSegmentSouth() {
		return segmentSouth;
	}

	public void setSegmentSouth(Segment segmentSouth) {
		this.segmentSouth = segmentSouth;
	}

	public Segment getSegmentVest() {
		return segmentVest;
	}

	public void setSegmentVest(Segment segmentVest) {
		this.segmentVest = segmentVest;
	}

	public Segment getSegmentEast() {
		return segmentEast;
	}

	public void setSegmentEast(Segment segmentEast) {
		this.segmentEast = segmentEast;
	}

	public int getPhaseCounter() {
		return phaseCounter;
	}

	public void setPhaseCounter(int phaseCounter) {
		this.phaseCounter = phaseCounter;
	}

	public int getPhases() {
		return phases;
	}

	public void setPhases(int phases) {
		this.phases = phases;
	}

	public int getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(int currentPhase) {
		this.currentPhase = currentPhase;
	}

	/**
	 * Switch to the next phase
	 */
	public void nextPhase() {
		if (currentPhase == phases) {
			currentPhase = 1;
		} else {
			currentPhase++;
		}
	}
}
