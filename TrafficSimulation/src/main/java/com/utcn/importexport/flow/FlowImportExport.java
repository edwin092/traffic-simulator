package com.utcn.importexport.flow;


import com.utcn.configurator.flow.model.TrafficFlow;
import com.utcn.configurator.flow.utils.TrafficFlowGeneratorUtil;
import com.utcn.view.TrafficSimulationView;

import java.util.List;

public class FlowImportExport {

    /**
     * Import traffic flows config.
     */
    public static boolean importFromJSON(String filepath, TrafficSimulationView view) {
        List<TrafficFlow> trafficFlows = TrafficFlowGeneratorUtil.importFromJSON(filepath);

        if (trafficFlows == null || trafficFlows.isEmpty()) {
            return false;
        }

        view.setTrafficFlows(trafficFlows);
        return true;
    }
}
