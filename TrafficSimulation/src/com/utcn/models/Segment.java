package com.utcn.models;

import java.util.ArrayList;
import java.util.List;

import com.utcn.utils.TrafficSimulationUtil;

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

	private final static int SIZE_MODIFIER = 2;

	public Segment() {
		vehicles = new ArrayList<Vehicle>();
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

	public int getLength() {
		return length;
	}

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
	 * 
	 * @return
	 */
	public int getSize() {
		int size = 0;
		for (int i = 0; i < lineCoordsX.length - 1; i++) {
			size += TrafficSimulationUtil.distanceBetweenPoints(lineCoordsX[i],
					lineCoordsY[i], lineCoordsX[i + 1], lineCoordsY[i + 1]);
		}

		return SIZE_MODIFIER * size;
	}

	/**
	 * Returns traffic lights for current segment.
	 * 
	 * @param segment
	 * @return
	 */
	public boolean[] getTrafficLights() {

		if (intersectionOut != null
				&& intersectionOut.getSegmentNorthIn().getId() == this.id
				|| intersectionOut.getSegmentNorthOut().getId() == this.id) {
			// NORTH
			return intersectionOut.getTrafficLightsNorth();
		} else if (intersectionOut != null
				&& intersectionOut.getSegmentSouthIn().getId() == this.id
				|| intersectionOut.getSegmentSouthOut().getId() == this.id) {
			// SOUTH
			return intersectionOut.getTrafficLightsSouth();
		} else if (intersectionOut != null
				&& intersectionOut.getSegmentEastIn().getId() == this.id
				|| intersectionOut.getSegmentEastOut().getId() == this.id) {
			// EAST
			return intersectionOut.getTrafficLightsEast();
		} else {
			// VEST
			return intersectionOut.getTrafficLightsVest();
		}

	}
}
