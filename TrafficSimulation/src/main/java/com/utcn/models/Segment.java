package com.utcn.models;

import com.utcn.utils.TrafficSimulationUtil;

import java.util.ArrayList;
import java.util.List;

public class Segment {

    // private static final long serialVersionUID = 1L;

    private int id;

    private List<Vehicle> vehicles;
    private int length = 20;
    // legatura intersectii
    private Intersection intersectionIn;
    private Intersection intersectionOut;

    // If way = true then [Intersection ->] else [-> Intersection]
    private boolean way;

    // polyline coordinates
    private int[] lineCoordsX;
    private int[] lineCoordsY;

    public Segment() {
        vehicles = new ArrayList<>();
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

    /**
     * @deprecated
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length
     * @deprecated
     */
    public void setLength(int length) {
        this.length = length;
    }

    public Intersection getIntersectionIn() {
        return intersectionIn;
    }

    public void setIntersectionIn(Intersection intersectionIn) {
        this.intersectionIn = intersectionIn;
    }

    public Intersection getIntersectionOut() {
        return intersectionOut;
    }

    public void setIntersectionOut(Intersection intersectionOut) {
        this.intersectionOut = intersectionOut;
    }

    public boolean isWay() {
        return way;
    }

    public void setWay(boolean way) {
        this.way = way;
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

    /**
     * @return
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
     *
     * @return
     */
    public boolean[] getTrafficLights() {

        if (intersectionOut != null) {
            if ((intersectionOut.getSegmentNorthIn() != null && intersectionOut.getSegmentNorthIn().getId() == this.id)
                    || (intersectionOut.getSegmentNorthOut() != null && intersectionOut.getSegmentNorthOut().getId() == this.id)) {
                // NORTH
                return intersectionOut.getTrafficLightsNorth();
            } else if ((intersectionOut.getSegmentSouthIn() != null && intersectionOut.getSegmentSouthIn().getId() == this.id)
                    || (intersectionOut.getSegmentSouthOut() != null && intersectionOut.getSegmentSouthOut().getId() == this.id)) {
                // SOUTH
                return intersectionOut.getTrafficLightsSouth();
            } else if ((intersectionOut.getSegmentEastIn() != null && intersectionOut.getSegmentEastIn().getId() == this.id)
                    || (intersectionOut.getSegmentEastOut() != null && intersectionOut.getSegmentEastOut().getId() == this.id)) {
                // EAST
                return intersectionOut.getTrafficLightsEast();
            } else {
                // VEST
                return intersectionOut.getTrafficLightsVest();
            }
        }
        return null;
    }
}
