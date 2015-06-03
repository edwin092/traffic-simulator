package com.utcn.importexport.trafficlight;

import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.configurator.trafficlight.utils.TrafficLightsConfiguratorUtil;
import com.utcn.view.TrafficSimulationView;

import java.util.List;


public class TrafficLightImportExport {

    /**
     * Import traffic lights config.
     */
    public static boolean importFromJSON(String filepath, TrafficSimulationView view) {
        List<TrafficLightPhases> trafficLightPhases = TrafficLightsConfiguratorUtil.importFromJSON(filepath);

        if (trafficLightPhases == null || trafficLightPhases.isEmpty()) {
            return false;
        }

        view.setTrafficLightPhaseses(trafficLightPhases);
        return true;
    }

}
