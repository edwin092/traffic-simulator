package com.utcn.bl;

import com.utcn.application.BriefFormatter;
import com.utcn.flow.TrafficFlow;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.models.Vehicle;
import com.utcn.utils.BreadthFirstSearch;
import com.utcn.utils.SimulationGraph;
import com.utcn.utils.TrafficSimulationUtil;
import com.utcn.view.TrafficSimulationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class EnvironmentSetup {

    private Logger logger;

    // constante vehicle
    public static final int VEHICLES_GAP = 1;
    public static final int PHASE_TIME = 15;

    private List<Segment> segments;
    private List<Intersection> intersections;
    private int globalCounter;

//    public EnvironmentSetup() {
//        logger = Logger.getLogger("MyLog");
//        FileHandler fh;
//
//        try {
//            // This block configure the logger with handler and formatter
//            fh = new FileHandler("MyLogFile.log");
//            logger.addHandler(fh);
//            BriefFormatter formatter = new BriefFormatter();
//            fh.setFormatter(formatter);
//            logger.setUseParentHandlers(false);
//        } catch (SecurityException | IOException e) {
//            e.printStackTrace();
//        }
//
//        randomVehicleGenerator = new RandomVehicleGenerator();
//        this.segments = new ArrayList<>();
//        this.intersections = new ArrayList<>();
//
//        Segment seg1 = new Segment();
//        Segment seg2 = new Segment();
//        Intersection intersection = new Intersection();
//
//        // 2 faze
//        intersection.setPhases(2);
//        intersection.setTrafficLightEast(true);
//
//        seg1.setIntersectionIn(intersection);
//        seg2.setIntersectionOut(intersection);
//        intersection.setSegmentEastIn(seg1);
//        intersection.setSegmentVestOut(seg2);
//
//        segments.add(seg1);
//        segments.add(seg2);
//
//        intersections.add(intersection);
//    }

    public EnvironmentSetup(List<Segment> segments,
                            List<Intersection> intersections, boolean isFileLoggingEnabled) {

        logger = Logger.getLogger("MyLog");

        if (isFileLoggingEnabled) {
            // File logging
            try {
                // This block configure the logger with handler and formatter
                FileHandler fh = new FileHandler("MyLogFile.log");
                logger.addHandler(fh);
                BriefFormatter formatter = new BriefFormatter();
                fh.setFormatter(formatter);
                logger.setUseParentHandlers(false);
            } catch (SecurityException | IOException e) {
                e.printStackTrace();
            }
        } else {
            // Console logging
            try {
                // This block configure the logger with handler
                ConsoleHandler handler = new ConsoleHandler();
                logger.addHandler(handler);
                logger.setUseParentHandlers(false);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        this.segments = segments;
        this.intersections = intersections;
    }

    /**
     * @return
     */
    private int getRandomIntersectionId() {
        int random = TrafficSimulationUtil.randInt(1, intersections.size() - 1);

        // TODO
        // check if dest is reachable
        // use graph algorithms (cost min)

        return random;
    }

    /**
     * Generate a new vehicle.
     */
    public void generateVehicle(SimulationGraph simulationGraph, TrafficFlow trafficFlow) {
        if (trafficFlow.getVehicleGenerator().isCounterZero()) {

            logger.info("New Vehicle generated!");

            Vehicle newVehicle = trafficFlow.getVehicleGenerator().initNewVehicle();

            List<Integer> intersectionIdsSolution = null;
            int startId = trafficFlow.getStartingPoint();
            int endId;
            do {
//                startId = getRandomIntersectionId();

                do {
                    endId = getRandomIntersectionId();
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


            List<Segment> routeList = new ArrayList<>();
            for (int i = 0; i < intersectionIdsSolution.size() - 1; i++) {
                for (Segment segment : segments) {
                    if (segment.getIntersectionIn().getId() == intersectionIdsSolution.get(i) &&
                            segment.getIntersectionOut().getId() == intersectionIdsSolution.get(i + 1)) {
                        routeList.add(segment);
                        break;
                    }
                }
            }

            configureVehicleParams(newVehicle, routeList.get(0));
            newVehicle.setDestination(routeList.get(routeList.size() - 1));
            newVehicle.setRouteList(routeList);
            routeList.get(0).getVehicles().add(newVehicle);


            TrafficSimulationView.addNewSimulationLogEntry("\n\nVehicle " + newVehicle.getId() + ":");
            TrafficSimulationView.addNewSimulationLogEntry("\n  Starting point: Intersection " + startId);
            TrafficSimulationView.addNewSimulationLogEntry("\n  End point:      Intersection " + endId);
        }
    }

    /**
     * @param vehicle
     * @param segment
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
     * @param vehDest
     */
    public void checkSegments(int vehDest) {
        // verificare tronsoane

        int k = 1;
        for (Segment seg : segments) {

//			logger.info("\nSEGEMENT " + k + " STATUS:");
//			logger.info("---VEHICLES:");

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

//                    segmentVehicles.get(segmentVehicles.size() - 1).setDistanceToObstacle(
//                            segmentVehicles.get(segmentVehicles.size() - 1)
//                                    .getDistanceToObstacle()
//                                    + segmentVehicles.get(segmentVehicles.size() - 2)
//                                    .getSpeed());
                }

                if (segmentVehicles.get(0).getDistanceToObstacle() == 0 &&
                        segmentVehicles.get(0).getCurrentDistance() == segmentVehicles.get(0).getCurrentSegment().getSize()) {
                    // first vehicle reached end of segment

                    if (segmentVehicles.get(0).getDestination().getId() == seg
                            .getId()) {
                        // Destination reached
                        segmentVehicles.remove(0);
                        if (!segmentVehicles.isEmpty()) {
                            segmentVehicles.get(0).setDistanceToObstacle(segmentVehicles.get(0).getDistanceToObstacle() + Vehicle.SIZE);
                        }

                    } else {
                        Segment nextSegment = getNextSegmentFromRoute(
                                segmentVehicles.get(0), seg);

                        boolean[] trafficLights = seg.getTrafficLights();

                        // get direction of next segment (0-left, 1-straight,
                        // 2-right)
                        int dir = seg.getIntersectionOut().getDirection(seg,
                                nextSegment);

                        if (trafficLights[dir]) {
                            // traffic light is GREEN
                            configureVehicleParams(segmentVehicles.get(0),
                                    nextSegment);

                            nextSegment.getVehicles().add(segmentVehicles.get(0));

                            segmentVehicles.remove(0);
                            if (!segmentVehicles.isEmpty()) {
                                segmentVehicles.get(0).setDistanceToObstacle(segmentVehicles.get(0).getDistanceToObstacle() + Vehicle.SIZE);
                            }
                        }
                    }
                }
            }

            k++;
        }
    }

    /**
     *
     */
    public void manageIntersectionsTrafficLights() {
        for (Intersection intersection : intersections) {

            if (intersection.getPhaseCounter() == intersection.getPhaseTimes()[intersection.getCurrentPhase() - 1]) {
                // reset counter
                intersection.setPhaseCounter(0);
                // switch to next phase
                intersection.nextPhase();

                TrafficSimulationUtil.initIntersectionTrafficLights(intersection);
            } else {
                intersection
                        .setPhaseCounter(intersection.getPhaseCounter() + 1);
            }

//            if (intersection.getPhaseCounter() == PHASE_TIME) {
//                // reset counter
//                intersection.setPhaseCounter(0);
//                // switch to next phase
//                intersection.nextPhase();
//
//                // check current phase
//                if (intersection.getCurrentPhase() == 1) {
//                    // PHASE 1
//                    intersection.setTrafficLightsSouth(new boolean[]{false,
//                            true, true});
//                    intersection.setTrafficLightsNorth(new boolean[]{false,
//                            true, true});
//                    intersection.setTrafficLightsEast(new boolean[]{false,
//                            false, false});
//                    intersection.setTrafficLightsVest(new boolean[]{false,
//                            false, false});
//                } else if (intersection.getCurrentPhase() == 2) {
//                    // PHASE 2
//                    intersection.setTrafficLightsSouth(new boolean[]{false,
//                            false, false});
//                    intersection.setTrafficLightsNorth(new boolean[]{false,
//                            false, false});
//                    intersection.setTrafficLightsEast(new boolean[]{false,
//                            true, true});
//                    intersection.setTrafficLightsVest(new boolean[]{false,
//                            true, true});
//                } else if (intersection.getCurrentPhase() == 3) {
//                    // PHASE 3
//                    intersection.setTrafficLightsSouth(new boolean[]{false,
//                            false, true});
//                    intersection.setTrafficLightsNorth(new boolean[]{false,
//                            false, true});
//                    intersection.setTrafficLightsEast(new boolean[]{true,
//                            false, false});
//                    intersection.setTrafficLightsVest(new boolean[]{true,
//                            false, false});
//                } else {
//                    // PHASE 4
//                    intersection.setTrafficLightsSouth(new boolean[]{true,
//                            false, false});
//                    intersection.setTrafficLightsNorth(new boolean[]{true,
//                            false, false});
//                    intersection.setTrafficLightsEast(new boolean[]{false,
//                            false, true});
//                    intersection.setTrafficLightsVest(new boolean[]{false,
//                            false, true});
//                }
//            } else {
//                intersection
//                        .setPhaseCounter(intersection.getPhaseCounter() + 1);
//            }
        }
    }

    /**
     * @param vehDest
     * @deprecated
     */
    public void checkSegmentsTest(int vehDest) {
        // verificare tronsoane
        int k = 1;
        for (Segment seg : segments) {

            logger.info("\nSEGEMENT " + k + " STATUS:");
            logger.info("---VEHICLES:");

            // vehicles list from current segment
            List<Vehicle> segmentVehicles = seg.getVehicles();
            if (!segmentVehicles.isEmpty()) {
                if (segmentVehicles.size() == 1) {
                    // go 1 m
                    segmentVehicles.get(0).setCurrentDistance(
                            segmentVehicles.get(0).getCurrentDistance() + 1);
                } else {
                    for (int i = 0; i < segmentVehicles.size() - 1; i++) {

                        if (segmentVehicles.get(i + 1).getCurrentDistance() < seg
                                .getLength()) {
                            if ((segmentVehicles.get(i).getCurrentDistance() - seg
                                    .getVehicles().get(i + 1)
                                    .getCurrentDistance()) > VEHICLES_GAP) {
                                // go 1 m
                                segmentVehicles.get(i + 1).setCurrentDistance(
                                        segmentVehicles.get(i + 1)
                                                .getCurrentDistance() + 1);
                            }
                        }
                    }

                    // last vehicle -> first generated
                    if (segmentVehicles.get(0).getCurrentDistance() == seg
                            .getLength()) {
                        if (seg.getIntersectionOut() == null) {

                            // am ajuns la dest
                            seg.getVehicles().remove(segmentVehicles.get(0));

                            vehDest++;
                        } else {
                            // semafor verde
                            if (seg.getIntersectionOut().isTrafficLightVest()) {

                                Segment nextSegment = segmentVehicles
                                        .get(0)
                                        .getRouteList()
                                        .get(segmentVehicles.get(0)
                                                .getRouteList().indexOf(seg) + 1);

                                segmentVehicles.get(0).setCurrentDistance(0);

                                segmentVehicles.get(0).setCurrentSegment(
                                        nextSegment);
                                nextSegment.getVehicles().add(
                                        segmentVehicles.get(0));

                                segmentVehicles.remove(0);
                            }
                        }
                    } else if (segmentVehicles.get(0).getCurrentDistance() < seg
                            .getLength()) {
                        segmentVehicles.get(0)
                                .setCurrentDistance(
                                        segmentVehicles.get(0)
                                                .getCurrentDistance() + 1);
                    }
                }

                // log vehicles
                for (int i = 0; i < segmentVehicles.size(); i++) {
                    logger.info("VEHICLE " + i + " distance: "
                            + segmentVehicles.get(i).getCurrentDistance());
                }
            }

            k++;
        }
    }

    /**
     * @deprecated
     */
    public void checkIntersectionTests() {
        // verificare intersectii
        for (Intersection intersection : intersections) {

            logger.info("\nTRAFFIC LIGHT: " + intersection.isTrafficLightVest());
            logger.info("CURRENT PHASE: " + intersection.getCurrentPhase());
            logger.info("PHASE COUNTER: " + intersection.getPhaseCounter());

            if (intersection.getPhaseCounter() == PHASE_TIME) {
                // reset counter
                intersection.setPhaseCounter(0);
                // switch to next phase
                intersection.nextPhase();

                // check current phase
                if (intersection.getCurrentPhase() == 1) {
                    // GREEN
                    intersection.setTrafficLightVest(true);
                } else {
                    // phase 2
                    // RED
                    intersection.setTrafficLightVest(false);
                }
            } else {
                intersection
                        .setPhaseCounter(intersection.getPhaseCounter() + 1);
            }
        }

    }

    /**
     * Start simulation
     *
     * @deprecated
     */
    public void simulate() {

//        logger.info("---SIMULATION STARTED---");
//        logger.info("------------------------");
//        logger.info("--- NUMBER OF SEGMENTS: " + segments.size() + "---");
//        logger.info("--- NUMBER OF INTERSECTIONS: " + intersections.size()
//                + "---");
//        logger.info("------------------------");
//
//        int vehDest = 0;
//
//        do {
//
//            logger.info("\n\nSIMULATION TIME: " + globalCounter);
//
//            // generator masini
//            if (randomVehicleGenerator.isCounterZero()) {
//
//                logger.info("New Vehicle generated!");
//
//                Vehicle newVehicle = randomVehicleGenerator.initNewVehicle();
//
//                newVehicle.setCurrentSegment(segments.get(0));
//                newVehicle.setDestination(segments.get(1));
//                newVehicle.setRouteList(segments);
//
//                segments.get(0).getVehicles().add(newVehicle);
//            }
//
//            // verificare tronsoane
//            int k = 1;
//            for (Segment seg : segments) {
//
//                logger.info("\nSEGEMENT " + k + " STATUS:");
//                logger.info("---VEHICLES:");
//
//                // vehicles list from current segment
//                List<Vehicle> segmentVehicles = seg.getVehicles();
//                if (!segmentVehicles.isEmpty()) {
//                    if (segmentVehicles.size() == 1) {
//                        // go 1 m
//                        segmentVehicles.get(0)
//                                .setCurrentDistance(
//                                        segmentVehicles.get(0)
//                                                .getCurrentDistance() + 1);
//                    } else {
//                        for (int i = 0; i < segmentVehicles.size() - 1; i++) {
//
//                            if (segmentVehicles.get(i + 1).getCurrentDistance() < seg
//                                    .getLength()) {
//                                if ((segmentVehicles.get(i)
//                                        .getCurrentDistance() - seg
//                                        .getVehicles().get(i + 1)
//                                        .getCurrentDistance()) > VEHICLES_GAP) {
//                                    // go 1 m
//                                    segmentVehicles
//                                            .get(i + 1)
//                                            .setCurrentDistance(
//                                                    segmentVehicles
//                                                            .get(i + 1)
//                                                            .getCurrentDistance() + 1);
//                                }
//                            }
//                        }
//
//                        // last vehicle -> first generated
//                        if (segmentVehicles.get(0).getCurrentDistance() == seg
//                                .getLength()) {
//                            if (seg.getIntersectionOut() == null) {
//
//                                // am ajuns la dest
//                                seg.getVehicles()
//                                        .remove(segmentVehicles.get(0));
//
//                                vehDest++;
//                            } else {
//                                // semafor verde
//                                if (seg.getIntersectionOut()
//                                        .isTrafficLightVest()) {
//
//                                    Segment nextSegment = segmentVehicles
//                                            .get(0)
//                                            .getRouteList()
//                                            .get(segmentVehicles.get(0)
//                                                    .getRouteList()
//                                                    .indexOf(seg) + 1);
//
//                                    segmentVehicles.get(0)
//                                            .setCurrentDistance(0);
//
//                                    segmentVehicles.get(0).setCurrentSegment(
//                                            nextSegment);
//                                    nextSegment.getVehicles().add(
//                                            segmentVehicles.get(0));
//
//                                    segmentVehicles.remove(0);
//                                }
//                            }
//                        } else if (segmentVehicles.get(0).getCurrentDistance() < seg
//                                .getLength()) {
//                            segmentVehicles.get(0)
//                                    .setCurrentDistance(
//                                            segmentVehicles.get(0)
//                                                    .getCurrentDistance() + 1);
//                        }
//                    }
//
//                    // log vehicles
//                    for (int i = 0; i < segmentVehicles.size(); i++) {
//                        logger.info("VEHICLE " + i + " distance: "
//                                + segmentVehicles.get(i).getCurrentDistance());
//                    }
//                }
//
//                k++;
//            }
//
//            // check intersections
//            for (Intersection intersection : intersections) {
//
//                logger.info("\nTRAFFIC LIGHT: "
//                        + intersection.isTrafficLightVest());
//                logger.info("CURRENT PHASE: " + intersection.getCurrentPhase());
//                logger.info("PHASE COUNTER: " + intersection.getPhaseCounter());
//
//                if (intersection.getPhaseCounter() == PHASE_TIME) {
//                    // reset counter
//                    intersection.setPhaseCounter(0);
//                    // switch to next phase
//                    intersection.nextPhase();
//
//                    // check current phase
//                    if (intersection.getCurrentPhase() == 1) {
//                        // GREEN
//                        intersection.setTrafficLightVest(true);
//                    } else {
//                        // phase 2
//                        // RED
//                        intersection.setTrafficLightVest(false);
//                    }
//                } else {
//                    intersection
//                            .setPhaseCounter(intersection.getPhaseCounter() + 1);
//                }
//            }
//
//            // increment global counter
//            globalCounter++;
//            System.out.println(globalCounter);
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        } while (globalCounter != 200);
//
//        logger.info("\nNumber of vehicles at destination: " + vehDest);
//        logger.info("---SIMULATION ENDED---");
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
}
