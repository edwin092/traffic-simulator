package com.utcn.models;

import com.utcn.utils.TrafficSimulationUtil;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    // id of the segment
    private int id;

    // list of vehicles on segment
    private List<Vehicle> vehicles;

    // segment intersections
    private Intersection intersectionFrom;
    private Intersection intersectionTo;

    // polyline coordinates from View
    private int[] lineCoordsX;
    private int[] lineCoordsY;

    public Segment() {
        vehicles = new ArrayList<>();
    }

    /**
     * Gets the size of the intersection.
     */
    public double getSize() {
        int sizeInPixels = 0;
        for (int i = 0; i < lineCoordsX.length - 1; i++) {
            sizeInPixels += TrafficSimulationUtil.distanceBetweenPoints(lineCoordsX[i],
                    lineCoordsY[i], lineCoordsX[i + 1], lineCoordsY[i + 1]);
        }

        return TrafficSimulationUtil.convertPixelsToMeters(sizeInPixels);
    }

    /**
     * Returns traffic lights for current segment.
     */
    public boolean[] getTrafficLights() {
        if (intersectionTo != null) {
            if ((intersectionTo.getSegmentNorthIn() != null && intersectionTo.getSegmentNorthIn().getId() == this.id)
                    || (intersectionTo.getSegmentNorthOut() != null && intersectionTo.getSegmentNorthOut().getId() == this.id)) {
                // NORTH
                return intersectionTo.getTrafficLightsNorth();
            } else if ((intersectionTo.getSegmentSouthIn() != null && intersectionTo.getSegmentSouthIn().getId() == this.id)
                    || (intersectionTo.getSegmentSouthOut() != null && intersectionTo.getSegmentSouthOut().getId() == this.id)) {
                // SOUTH
                return intersectionTo.getTrafficLightsSouth();
            } else if ((intersectionTo.getSegmentEastIn() != null && intersectionTo.getSegmentEastIn().getId() == this.id)
                    || (intersectionTo.getSegmentEastOut() != null && intersectionTo.getSegmentEastOut().getId() == this.id)) {
                // EAST
                return intersectionTo.getTrafficLightsEast();
            } else {
                // VEST
                return intersectionTo.getTrafficLightsVest();
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Intersection getIntersectionFrom() {
        return intersectionFrom;
    }

    public void setIntersectionFrom(Intersection intersectionFrom) {
        this.intersectionFrom = intersectionFrom;
    }

    public Intersection getIntersectionTo() {
        return intersectionTo;
    }

    public void setIntersectionTo(Intersection intersectionTo) {
        this.intersectionTo = intersectionTo;
    }

    public int[] getLineCoordsX() {
        return lineCoordsX;
    }

    public void setLineCoordsX(int[] lineCoordsX) {
        this.lineCoordsX = lineCoordsX;
    }

    public int[] getLineCoordsY() {
        return lineCoordsY;
    }

    public void setLineCoordsY(int[] lineCoordsY) {
        this.lineCoordsY = lineCoordsY;
    }
}
