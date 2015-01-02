package com.utcn.models;

import java.util.ArrayList;
import java.util.List;

public class Segment {

	// private static final long serialVersionUID = 1L;

	private List<Vehicle> vehicles;
	private int length = 20;
	// legatura intersectii
	private Intersection intersectionIn;
	private Intersection intersectionOut;
	/**
	 * If way = true then [Intersection ->] else [-> Intersection]
	 */
	private boolean way;

	// polyline coordinates
	private int[] lineCoordsX;
	private int[] lineCoordsY;

	public Segment() {
		vehicles = new ArrayList<Vehicle>();
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
}
