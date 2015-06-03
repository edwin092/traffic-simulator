package com.utcn.bl;

import com.utcn.configurator.flow.model.TrafficFlow;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.models.Vehicle;
import com.utcn.statistics.IntersectionStatisticsManager;
import com.utcn.statistics.VehicleStatisticsManager;
import com.utcn.utils.BreadthFirstSearch;
import com.utcn.utils.SimulationGraph;
import com.utcn.utils.TrafficSimulationUtil;
import com.utcn.view.TrafficSimulationView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO change name
public class EnvironmentSetup {

    private List<Segment> segments;
    private List<Intersection> intersections;
    private IntersectionStatisticsManager intersectionStatisticsManager;

    public EnvironmentSetup(List<Segment> segments, List<Intersection> intersections) {
        this.segments = segments;
        this.intersections = intersections;
        intersectionStatisticsManager = new IntersectionStatisticsManager();
    }

    /**
     * Returns a random end point intersection id.
     *
     * @return id of an end point intersection
     */
    private int getRandomEndPointId() {
        boolean endPointFound = false;
        int randomId;
        do {
            randomId = TrafficSimulationUtil.randInt(1, intersections.size() - 1);

            for (Intersection intersection : intersections) {
                if ((intersection.getId() == randomId) &&
                        (intersection.getSegmentsNumber() <= 2)) {
                    // valid end point in simulation
                    endPointFound = true;
                }
            }
        } while (!endPointFound);

        return randomId;
    }

    /**
     * Generate a new vehicle.
     */
    public Vehicle generateVehicle(SimulationGraph simulationGraph, TrafficFlow trafficFlow, boolean isViewEnabled) {
        if (trafficFlow.getVehicleGenerator().isCounterZero()) {
            Vehicle newVehicle = trafficFlow.getVehicleGenerator().initNewVehicle();

            List<Integer> intersectionIdsSolution = null;
            if (trafficFlow.getStartingPoint() > 0) {
                int startId = trafficFlow.getStartingPoint();
                int endId;
                do {
                    do {
                        endId = getRandomEndPointId();
                    } while (startId == endId);

                    LinkedList<Integer> visited = new LinkedList<>();
                    visited.add(startId);
                    Map<Integer, List<Integer>> solutions = BreadthFirstSearch.breadthFirst(simulationGraph, visited, endId);

                    for (int i = 1; i <= solutions.size(); i++) {
                        if (solutions.get(i).size() > 2) {
                            intersectionIdsSolution = solutions.get(i);
                        }
                    }
                } while (intersectionIdsSolution == null);

            } else {
                intersectionIdsSolution = trafficFlow.getRouteList();
            }

            List<Segment> routeList = new ArrayList<>();
            for (int i = 0; i < intersectionIdsSolution.size() - 1; i++) {
                for (Segment segment : segments) {
                    if (segment.getIntersectionFrom().getId() == intersectionIdsSolution.get(i) &&
                            segment.getIntersectionTo().getId() == intersectionIdsSolution.get(i + 1)) {
                        routeList.add(segment);
                        break;
                    }
                }
            }

            configureVehicleParams(newVehicle, routeList.get(0));
            newVehicle.setDestination(routeList.get(routeList.size() - 1));
            newVehicle.setRouteList(routeList);
            routeList.get(0).getVehicles().add(newVehicle);

            if (isViewEnabled) {
                TrafficSimulationView.addNewSimulationLogEntry("\n\nVehicle " + newVehicle.getId() + ":");
                TrafficSimulationView.addNewSimulationLogEntry("\n  Starting point: Intersection "
                        + intersectionIdsSolution.get(0));
                TrafficSimulationView.addNewSimulationLogEntry("\n  End point:      Intersection "
                        + intersectionIdsSolution.get(intersectionIdsSolution.size() - 1));
            }

            return newVehicle;
        }
        return null;
    }

    /**
     * Configure parameters for a vehicle.
     */
    private void configureVehicleParams(Vehicle vehicle, Segment segment) {
        vehicle.setCurrentSegment(segment);
        vehicle.setDistanceToObstacle(segment.getSize());
        vehicle.setCurrentDistance(0);
    }

    /**
     * @param vehicle
     * @param currentSegment
     * @return
     */
    private Segment getNextSegmentFromRoute(Vehicle vehicle,
                                            Segment currentSegment) {
        return vehicle.getRouteList().get(
                vehicle.getRouteList().indexOf(currentSegment) + 1);
    }

    /**
     * Checks all segments and vehicles from them from simulation.
     */
    public void checkSegments(VehicleStatisticsManager vehicleStatisticsManager, int counter) {
        for (Segment seg : segments) {
            // vehicles list from current segment
            List<Vehicle> segmentVehicles = seg.getVehicles();
            if (!segmentVehicles.isEmpty()) {

                if (segmentVehicles.size() == 1) {
                    // only one vehicle on segment
                    segmentVehicles.get(0).accelerate();
                } else {
                    for (int i = 0; i < segmentVehicles.size() - 1; i++) {

                        segmentVehicles.get(i).accelerate();

                        if (segmentVehicles.get(i + 1).getCurrentDistance() == 0) {
                            segmentVehicles.get(i + 1).setDistanceToObstacle(
                                    segmentVehicles.get(i + 1)
                                            .getDistanceToObstacle()
                                            - segmentVehicles.get(i)
                                            .getDistanceToObstacle() -
                                            segmentVehicles.get(i).getSize());
                        } else {
                            segmentVehicles.get(i + 1).setDistanceToObstacle(
                                    segmentVehicles.get(i)
                                            .getCurrentDistance()
                                            - segmentVehicles.get(i + 1)
                                            .getCurrentDistance() -
                                            segmentVehicles.get(i).getSize());
                        }
                    }

                    // accelerate last vehicle from segment
                    segmentVehicles.get(segmentVehicles.size() - 1)
                            .accelerate();
                }

                if (segmentVehicles.get(0).getDistanceToObstacle() == 0 &&
                        segmentVehicles.get(0).getCurrentDistance() == segmentVehicles.get(0).getCurrentSegment().getSize()) {
                    // first vehicle reached end of segment

                    if (segmentVehicles.get(0).getDestination().getId() == seg
                            .getId()) {
                        // Destination reached
                        Vehicle removedVehicle = segmentVehicles.remove(0);
                        if (!segmentVehicles.isEmpty()) {
                            segmentVehicles.get(0).setDistanceToObstacle(segmentVehicles.get(0).getDistanceToObstacle() + Vehicle.SIZE);
                        }

                        vehicleStatisticsManager.addVehicleEndTime(removedVehicle.getId(), counter);

                    } else {
                        Segment nextSegment = getNextSegmentFromRoute(
                                segmentVehicles.get(0), seg);

                        boolean[] trafficLights = seg.getTrafficLights();

                        // get direction of next segment (0-left, 1-straight,
                        // 2-right)
                        int dir = seg.getIntersectionTo().getDirection(seg,
                                nextSegment);

                        if (trafficLights[dir]) {
                            // traffic light is GREEN
                            configureVehicleParams(segmentVehicles.get(0),
                                    nextSegment);

                            nextSegment.getVehicles().add(segmentVehicles.get(0));

                            Vehicle removedVehicle = segmentVehicles.remove(0);
                            if (!segmentVehicles.isEmpty()) {
                                segmentVehicles.get(0).setDistanceToObstacle(segmentVehicles.get(0).getDistanceToObstacle() + Vehicle.SIZE);
                            }

                            // record vehicle statistics
                            vehicleStatisticsManager.incrementIntersectionsPassed(removedVehicle.getId());

                            if (nextSegment.getIntersectionFrom().isFourPhased()) {
                                // record intersection statistics
                                intersectionStatisticsManager
                                        .addNewVehPassedIntersection(nextSegment.getIntersectionFrom().getId());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Manage traffic lights from the simulation.
     */
    public void manageIntersectionsTrafficLights() {
        TrafficLightsController.manageIntersectionsTrafficLights(intersections);
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<Intersection> intersections) {
        this.intersections = intersections;
    }

    public IntersectionStatisticsManager getIntersectionStatisticsManager() {
        return intersectionStatisticsManager;
    }

    public void setIntersectionStatisticsManager(IntersectionStatisticsManager intersectionStatisticsManager) {
        this.intersectionStatisticsManager = intersectionStatisticsManager;
    }
}
