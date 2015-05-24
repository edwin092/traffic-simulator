package com.utcn.configurator.trafficlight.utils;

import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class TrafficLightsConfiguratorUtil {

    /**
     * Export config to JSON file.
     *
     * @param filepath   the file path.
     * @param phasesList the list of phases fro intersections.
     */
    public static boolean exportToJSON(String filepath, List<TrafficLightPhases> phasesList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!filepath.contains(".json")) {
                filepath += ".json";
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filepath), phasesList);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Import traffic lights config.
     *
     * @param filepath the file path.
     */
    public static List<TrafficLightPhases> importFromJSON(String filepath) {
        ObjectMapper mapper = new ObjectMapper();
        List<TrafficLightPhases> phases;
        try {
            phases = mapper.readValue(new File(filepath),
                    mapper.getTypeFactory().constructCollectionType(List.class, TrafficLightPhases.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return phases;
    }
}
