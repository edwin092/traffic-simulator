package com.utcn.bl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.utcn.application.BriefFormatter;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.models.Vehicle;

public class EnvironmentSetup {

	private Logger logger;

	// constante vehicle
	public static final int ACCELERATION = 1;
	public static final int DESIRED_SPEED = 50;
	public static final int VEHICLES_GAP = 1;
	public static final int PHASE_TIME = 15;

	private VehicleGenerator vehicleGenerator;
	private List<Segment> segments;
	private List<Intersection> intersections;
	private int globalCounter;

	public EnvironmentSetup() {
		logger = Logger.getLogger("MyLog");
		FileHandler fh;

		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler("MyLogFile.log");
			logger.addHandler(fh);
			BriefFormatter formatter = new BriefFormatter();
			fh.setFormatter(formatter);
			logger.setUseParentHandlers(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		vehicleGenerator = new VehicleGenerator();
		segments = new ArrayList<Segment>();
		intersections = new ArrayList<Intersection>();

		Segment seg1 = new Segment();
		Segment seg2 = new Segment();
		Intersection intersection = new Intersection();

		// 2 faze
		intersection.setPhases(2);
		intersection.setTrafficLightEast(true);

		seg1.setIntersectionIn(intersection);
		seg2.setIntersectionOut(intersection);
		intersection.setSegmentEastIn(seg1);
		intersection.setSegmentVestOut(seg2);

		segments.add(seg1);
		segments.add(seg2);

		intersections.add(intersection);
	}

	public void generateVehicle() {
		if (vehicleGenerator.isCounterZero()) {

			logger.info("New Vehicle generated!");

			Vehicle newVehicle = vehicleGenerator.generateNewVehicle();

			newVehicle.setCurrentSegment(segments.get(0));
			newVehicle.setDestination(segments.get(1));
			newVehicle.setRouteList(segments);

			segments.get(0).getVehicles().add(newVehicle);
		}
	}

	public void checkSegments(int vehDest) {
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

	public void checkIntersections() {
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
	 */
	public void simulate() {

		logger.info("---SIMULATION STARTED---");
		logger.info("------------------------");
		logger.info("--- NUMBER OF SEGMENTS: " + segments.size() + "---");
		logger.info("--- NUMBER OF INTERSECTIONS: " + intersections.size()
				+ "---");
		logger.info("------------------------");

		int vehDest = 0;

		do {

			logger.info("\n\nSIMULATION TIME: " + globalCounter);

			// generator masini
			if (vehicleGenerator.isCounterZero()) {

				logger.info("New Vehicle generated!");

				Vehicle newVehicle = vehicleGenerator.generateNewVehicle();

				newVehicle.setCurrentSegment(segments.get(0));
				newVehicle.setDestination(segments.get(1));
				newVehicle.setRouteList(segments);

				segments.get(0).getVehicles().add(newVehicle);
			}

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
						segmentVehicles.get(0)
								.setCurrentDistance(
										segmentVehicles.get(0)
												.getCurrentDistance() + 1);
					} else {
						for (int i = 0; i < segmentVehicles.size() - 1; i++) {

							if (segmentVehicles.get(i + 1).getCurrentDistance() < seg
									.getLength()) {
								if ((segmentVehicles.get(i)
										.getCurrentDistance() - seg
										.getVehicles().get(i + 1)
										.getCurrentDistance()) > VEHICLES_GAP) {
									// go 1 m
									segmentVehicles
											.get(i + 1)
											.setCurrentDistance(
													segmentVehicles
															.get(i + 1)
															.getCurrentDistance() + 1);
								}
							}
						}

						// last vehicle -> first generated
						if (segmentVehicles.get(0).getCurrentDistance() == seg
								.getLength()) {
							if (seg.getIntersectionOut() == null) {

								// am ajuns la dest
								seg.getVehicles()
										.remove(segmentVehicles.get(0));

								vehDest++;
							} else {
								// semafor verde
								if (seg.getIntersectionOut()
										.isTrafficLightVest()) {

									Segment nextSegment = segmentVehicles
											.get(0)
											.getRouteList()
											.get(segmentVehicles.get(0)
													.getRouteList()
													.indexOf(seg) + 1);

									segmentVehicles.get(0)
											.setCurrentDistance(0);

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

			// verificare intersectii
			for (Intersection intersection : intersections) {

				logger.info("\nTRAFFIC LIGHT: "
						+ intersection.isTrafficLightVest());
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

			// increment global counter
			globalCounter++;
			System.out.println(globalCounter);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (globalCounter != 200);

		logger.info("\nNumber of vehicles at destination: " + vehDest);
		logger.info("---SIMULATION ENDED---");
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
