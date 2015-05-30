package com.utcn.models;

import javax.swing.*;

public class Intersection extends JButton {

    private static final long serialVersionUID = 1L;

    private int id;

    // semafoare din intersectie: true-verde, false-rosu
    private boolean trafficLightNorth;
    private boolean trafficLightSouth;
    private boolean trafficLightVest;
    private boolean trafficLightEast;

    // semafoare din intersectie: true-verde, false-rosu
    // 0 - left, 1 - straight, 2 - right
    private boolean[] trafficLightsNorth = new boolean[]{false, true, true};
    private boolean[] trafficLightsSouth = new boolean[]{false, true, true};
    private boolean[] trafficLightsVest = new boolean[]{false, false, false};
    private boolean[] trafficLightsEast = new boolean[]{false, false, false};

    /* phases */
    private int phases = 4; // 4 phases
    private int currentPhase = 1;
    private int phaseCounter;

    private int[] phaseTimes;
    private Integer[] phaseOrder;

    /* segments */
    private Segment segmentNorthIn;
    private Segment segmentNorthOut;
    private Segment segmentSouthIn;
    private Segment segmentSouthOut;
    private Segment segmentVestIn;
    private Segment segmentVestOut;
    private Segment segmentEastIn;
    private Segment segmentEastOut;

    /* needed in view */
    private boolean segmentNorthSelected;
    private boolean segmentSouthSelected;
    private boolean segmentEastSelected;
    private boolean segmentVestSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public boolean isSegmentNorthSelected() {
        return segmentNorthSelected;
    }

    public void setSegmentNorthSelected(boolean segmentNorthSelected) {
        this.segmentNorthSelected = segmentNorthSelected;
    }

    public boolean isSegmentSouthSelected() {
        return segmentSouthSelected;
    }

    public void setSegmentSouthSelected(boolean segmentSouthSelected) {
        this.segmentSouthSelected = segmentSouthSelected;
    }

    public boolean isSegmentEastSelected() {
        return segmentEastSelected;
    }

    public void setSegmentEastSelected(boolean segmentEastSelected) {
        this.segmentEastSelected = segmentEastSelected;
    }

    public boolean isSegmentVestSelected() {
        return segmentVestSelected;
    }

    public void setSegmentVestSelected(boolean segmentVestSelected) {
        this.segmentVestSelected = segmentVestSelected;
    }

    public int getCurrentPhase() {
        return phaseOrder[currentPhase - 1];
    }

    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
    }

    public boolean[] getTrafficLightsNorth() {
        return trafficLightsNorth;
    }

    public void setTrafficLightsNorth(boolean[] trafficLightsNorth) {
        this.trafficLightsNorth = trafficLightsNorth;
    }

    public boolean[] getTrafficLightsSouth() {
        return trafficLightsSouth;
    }

    public void setTrafficLightsSouth(boolean[] trafficLightsSouth) {
        this.trafficLightsSouth = trafficLightsSouth;
    }

    public boolean[] getTrafficLightsVest() {
        return trafficLightsVest;
    }

    public void setTrafficLightsVest(boolean[] trafficLightsVest) {
        this.trafficLightsVest = trafficLightsVest;
    }

    public boolean[] getTrafficLightsEast() {
        return trafficLightsEast;
    }

    public void setTrafficLightsEast(boolean[] trafficLightsEast) {
        this.trafficLightsEast = trafficLightsEast;
    }

    public int[] getPhaseTimes() {
        return phaseTimes;
    }

    public void setPhaseTimes(int[] phaseTimes) {
        this.phaseTimes = phaseTimes;
    }

    public Integer[] getPhaseOrder() {
        return phaseOrder;
    }

    public void setPhaseOrder(Integer[] phaseOrder) {
        this.phaseOrder = phaseOrder;
    }

    /**
     * Switch to the next phase from list.
     */
    public void nextPhase() {
        if (currentPhase == phases) {
            currentPhase = 1;
        } else {
            currentPhase++;
        }
    }

    /**
     * Check if any of the 4 segments (north, vest, east, south) is selected
     *
     * @return true if one of he segments is selected
     */
    public boolean isAnySegmentSelected() {
        return segmentVestSelected || segmentEastSelected
                || segmentNorthSelected || segmentSouthSelected;
    }

    /**
     * Set all segments selected false
     */
    public void setAllSegmentsFalse() {
        segmentEastSelected = false;
        segmentVestSelected = false;
        segmentNorthSelected = false;
        segmentSouthSelected = false;
    }

    /**
     * @param currentSeg
     * @param nextSeg
     * @return
     */
    public int getDirection(Segment currentSeg, Segment nextSeg) {

        if (segmentEastIn != null && segmentEastIn.getId() == currentSeg.getId()) {
            // EAST
            if (segmentNorthOut != null && segmentNorthOut.getId() == nextSeg.getId()) {
                // right
                return 2;
            }
            if (segmentVestOut != null && segmentVestOut.getId() == nextSeg.getId()) {
                // straight
                return 1;
            }
            if (segmentSouthOut != null && segmentSouthOut.getId() == nextSeg.getId()) {
                // left
                return 0;
            }
        } else if (segmentVestIn != null && segmentVestIn.getId() == currentSeg.getId()) {
            // VEST
            if (segmentNorthOut != null && segmentNorthOut.getId() == nextSeg.getId()) {
                return 0;
            }
            if (segmentEastOut != null && segmentEastOut.getId() == nextSeg.getId()) {
                return 1;
            }
            if (segmentSouthOut != null && segmentSouthOut.getId() == nextSeg.getId()) {
                return 2;
            }
        } else if (segmentNorthIn != null && segmentNorthIn.getId() == currentSeg.getId()) {
            // NORTH
            if (segmentEastOut != null && segmentEastOut.getId() == nextSeg.getId()) {
                return 2;
            }
            if (segmentVestOut != null && segmentVestOut.getId() == nextSeg.getId()) {
                return 0;
            }
            if (segmentSouthOut != null && segmentSouthOut.getId() == nextSeg.getId()) {
                return 1;
            }
        } else {
            // SOUTH
            if (segmentNorthOut != null && segmentNorthOut.getId() == nextSeg.getId()) {
                return 1;
            }
            if (segmentVestOut != null && segmentVestOut.getId() == nextSeg.getId()) {
                return 0;
            }
            if (segmentEastOut != null && segmentEastOut.getId() == nextSeg.getId()) {
                return 2;
            }
        }

        throw new NullPointerException("Segments missing!");
    }

    /**
     * Checks if intersection has 4 phases.
     */
    public boolean isFourPhased() {
        int nOfSegments = 0;

        if (segmentEastIn != null) {
            nOfSegments++;
        }
        if (segmentEastOut != null) {
            nOfSegments++;
        }
        if (segmentVestIn != null) {
            nOfSegments++;
        }
        if (segmentVestOut != null) {
            nOfSegments++;
        }
        if (segmentNorthIn != null) {
            nOfSegments++;
        }
        if (segmentNorthOut != null) {
            nOfSegments++;
        }
        if (segmentSouthIn != null) {
            nOfSegments++;
        }
        if (segmentSouthOut != null) {
            nOfSegments++;
        }

        return nOfSegments >= 6 ? true : false;
    }

    /**
     *
     */
    public void setAllLightsTrue() {
        trafficLightsNorth = new boolean[]{true, true, true};
        trafficLightsSouth = new boolean[]{true, true, true};
        trafficLightsVest = new boolean[]{true, true, true};
        trafficLightsEast = new boolean[]{true, true, true};
    }

    /**
     * maybe use isFourPhased
     */
    public int getSegmentsNumber() {
        int k = 0;
        if (segmentEastIn != null) {
            k++;
        }
        if (segmentEastOut != null) {
            k++;
        }
        if (segmentVestIn != null) {
            k++;
        }
        if (segmentVestOut != null) {
            k++;
        }
        if (segmentNorthIn != null) {
            k++;
        }
        if (segmentNorthOut != null) {
            k++;
        }
        if (segmentSouthIn != null) {
            k++;
        }
        if (segmentSouthOut != null) {
            k++;
        }
        return k;
    }
}
