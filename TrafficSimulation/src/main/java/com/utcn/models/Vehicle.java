package com.utcn.models;

import com.utcn.utils.TrafficSimulationUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Vehicle {
    // size of a vehicle
    public static final double SIZE = 3;

    // if of the vehicle
    private int id;
    // destination point
    private Segment destination;
    // the route list
    private List<Segment> routeList;
    // the current segment of the vehicle
    private Segment currentSegment;
    // current distance on the segment
    private double currentDistance;
    // distance to next obstacle
    private double distanceToObstacle;
    // current speed
    private double speed;
    // the engine of the vehicle
    private Engine engine;
    // the size of the vehicle
    private double size;
    // the color of the vehicle
    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vehicle() {
        routeList = new ArrayList<>();
    }

    public double accelerate() {
        return this.engine.accelerate(this);
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

    public double getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(double currentDistance) {
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
        this.speed = TrafficSimulationUtil.round(speed, 2);
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
