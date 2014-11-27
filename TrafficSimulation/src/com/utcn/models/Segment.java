package com.utcn.models;

import java.util.ArrayList;
import java.util.List;

public class Segment {

	private List<Vehicle> vehicles;
	private int length = 20;
	// legatura intersectii
	private Intersection intersectionVest;
	private Intersection intersectionEast;

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

	public Intersection getIntersectionVest() {
		return intersectionVest;
	}

	public void setIntersectionVest(Intersection intersectionVest) {
		this.intersectionVest = intersectionVest;
	}

	public Intersection getIntersectionEast() {
		return intersectionEast;
	}

	public void setIntersectionEast(Intersection intersectionEast) {
		this.intersectionEast = intersectionEast;
	}
}
