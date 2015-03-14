package com.utcn.models;

import com.utcn.utils.TrafficSimulationUtil;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private int currentSpeed;
    private Segment destination;
    private List<Segment> routeList;
    private Segment currentSegment;
    private double currentDistance; // distanta parcursa pe tronsonul curent
    private double distanceToObstacle;
    private double speed;
    private Engine engine;

    // current pos in pixels
    private double posX;
    private double posY;


    public Vehicle() {
        routeList = new ArrayList<Segment>();
    }

    /**
     * @deprecated
     */
    public int getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * @deprecated
     */
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

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    /**
     * @return
     */
    public double accelerate() {
        return this.engine.accelerate(this);
    }
}
