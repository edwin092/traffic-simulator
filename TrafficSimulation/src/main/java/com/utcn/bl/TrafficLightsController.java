package com.utcn.bl;


import com.utcn.models.Intersection;

import java.util.List;

public class TrafficLightsController {

    /**
     * Manage traffic lights from the simulation.
     */
    public static void manageIntersectionsTrafficLights(List<Intersection> intersections) {
        for (Intersection intersection : intersections) {
            if (intersection.isFourPhased()) {
                if (intersection.getPhaseCounter() == intersection.getPhaseTimes()[intersection.getCurrentPhase() - 1]) {
                    // reset counter
                    intersection.setPhaseCounter(0);
                    // switch to next phase
                    intersection.nextPhase();

                    TrafficLightsController.configureIntersectionTrafficLights(intersection);
                } else {
                    intersection
                            .setPhaseCounter(intersection.getPhaseCounter() + 1);
                }
            }
        }
    }

    /**
     * Manage intersection traffic lights depending on current phase.
     *
     * @param intersection current intersection.
     */
    public static void configureIntersectionTrafficLights(Intersection intersection) {
        if (intersection.isFourPhased()) {
            // check current phase
            if (intersection.getCurrentPhase() == 1) {
                // PHASE 1
                intersection.setTrafficLightsSouth(new boolean[]{false, true, true});
                intersection.setTrafficLightsNorth(new boolean[]{false, true, true});
                intersection.setTrafficLightsEast(new boolean[]{false, false, false});
                intersection.setTrafficLightsVest(new boolean[]{false, false, false});
            } else if (intersection.getCurrentPhase() == 2) {
                // PHASE 2
                intersection.setTrafficLightsSouth(new boolean[]{false, false, false});
                intersection.setTrafficLightsNorth(new boolean[]{false, false, false});
                intersection.setTrafficLightsEast(new boolean[]{false, true, true});
                intersection.setTrafficLightsVest(new boolean[]{false, true, true});
            } else if (intersection.getCurrentPhase() == 3) {
                // PHASE 3
                intersection.setTrafficLightsSouth(new boolean[]{false, false, true});
                intersection.setTrafficLightsNorth(new boolean[]{false, false, true});
                intersection.setTrafficLightsEast(new boolean[]{true, false, false});
                intersection.setTrafficLightsVest(new boolean[]{true, false, false});
            } else {
                // PHASE 4
                intersection.setTrafficLightsSouth(new boolean[]{true, false, false});
                intersection.setTrafficLightsNorth(new boolean[]{true, false, false});
                intersection.setTrafficLightsEast(new boolean[]{false, false, true});
                intersection.setTrafficLightsVest(new boolean[]{false, false, true});
            }
        }
    }
}
