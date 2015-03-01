package com.utcn.models;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

	private int currentSpeed;
	private Segment destination;
	private List<Segment> routeList;
	private Segment currentSegment;
	private int currentDistance; // distanta parcursa pe tronsonul curent
	private double distanceToObstacle;
	private double speed;
	private Engine engine;

	public Vehicle() {
		routeList = new ArrayList<Segment>();
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public Segment getDestination() {
		return destination;
	}

	public void setDestination(Segment destination) {
		this.destination = destination;
	}

	public List<Segment> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<Segment> routeList) {
		this.routeList = routeList;
	}

	public Segment getCurrentSegment() {
		return currentSegment;
	}

	public void setCurrentSegment(Segment currentSegment) {
		this.currentSegment = currentSegment;
	}

	public int getCurrentDistance() {
		return currentDistance;
	}

	public void setCurrentDistance(int currentDistance) {
		this.currentDistance = currentDistance;
	}

	public double getDistanceToObstacle() {
		return distanceToObstacle;
	}

	public void setDistanceToObstacle(double distanceToObstacle) {
		this.distanceToObstacle = distanceToObstacle;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	/**
	 * 
	 * @return
	 */
	public double accelerate() {
		return this.engine.accelerate(this);
	}
}
