package com.utcn.models;

import javax.swing.JButton;

public class Intersection extends JButton {

	private static final long serialVersionUID = 1L;

	// semafoare din intersectie: true-verde, false-rosu
	private boolean trafficLightNorth;
	private boolean trafficLightSouth;
	private boolean trafficLightVest;
	private boolean trafficLightEast;

	/* phases */
	private int phases = 4; // 4 phases
	private int currentPhase = 1;
	private int phaseCounter;

	/* segments */
	private Segment segmentNorthIn;
	private Segment segmentNorthOut;
	private Segment segmentSouthIn;
	private Segment segmentSouthOut;
	private Segment segmentVestIn;
	private Segment segmentVestOut;
	private Segment segmentEastIn;
	private Segment segmentEastOut;

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

	public Segment getSegmentNorthIn() {
		return segmentNorthIn;
	}

	public void setSegmentNorthIn(Segment segmentNorthIn) {
		this.segmentNorthIn = segmentNorthIn;
	}

	public Segment getSegmentNorthOut() {
		return segmentNorthOut;
	}

	public void setSegmentNorthOut(Segment segmentNorthOut) {
		this.segmentNorthOut = segmentNorthOut;
	}

	public Segment getSegmentSouthIn() {
		return segmentSouthIn;
	}

	public void setSegmentSouthIn(Segment segmentSouthIn) {
		this.segmentSouthIn = segmentSouthIn;
	}

	public Segment getSegmentSouthOut() {
		return segmentSouthOut;
	}

	public void setSegmentSouthOut(Segment segmentSouthOut) {
		this.segmentSouthOut = segmentSouthOut;
	}

	public Segment getSegmentVestIn() {
		return segmentVestIn;
	}

	public void setSegmentVestIn(Segment segmentVestIn) {
		this.segmentVestIn = segmentVestIn;
	}

	public Segment getSegmentVestOut() {
		return segmentVestOut;
	}

	public void setSegmentVestOut(Segment segmentVestOut) {
		this.segmentVestOut = segmentVestOut;
	}

	public Segment getSegmentEastIn() {
		return segmentEastIn;
	}

	public void setSegmentEastIn(Segment segmentEastIn) {
		this.segmentEastIn = segmentEastIn;
	}

	public Segment getSegmentEastOut() {
		return segmentEastOut;
	}

	public void setSegmentEastOut(Segment segmentEastOut) {
		this.segmentEastOut = segmentEastOut;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
