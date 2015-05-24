package com.utcn.configurator.flow.utils;


import com.utcn.configurator.flow.model.TrafficFlow;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrafficFlowGeneratorUtil {

    /**
     * Export config to JSON file.
     *
     * @param filepath        the file path.
     * @param trafficFlowList the list of phases fro intersections.
     */
    public static boolean exportToJSON(String filepath, List<TrafficFlow> trafficFlowList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!filepath.contains(".json")) {
                filepath += ".json";
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filepath), trafficFlowList);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Import flow.
     */
    public static List<TrafficFlow> importFromJSON(String filepath) {
        ObjectMapper mapper = new ObjectMapper();
        List<TrafficFlow> trafficFlows;

        try {
            trafficFlows = mapper.readValue(new File(filepath),
                    mapper.getTypeFactory().constructCollectionType(List.class, TrafficFlow.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return trafficFlows;
    }
}
