package com.utcn.configurator.flow.utils;


import com.utcn.configurator.flow.model.TrafficFlow;
import com.utcn.models.Segment;
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

    /**
     * @param routeList
     * @param segments
     * @return
     */
    public static boolean validateRouteList(List<Integer> routeList, List<Segment> segments) {
        boolean valid;
        for (int i = 0; i < routeList.size() - 1; i++) {
            valid = false;
            for (Segment segment : segments) {
                if (segment.getIntersectionFrom().getId() == routeList.get(i) &&
                        segment.getIntersectionTo().getId() == routeList.get(i + 1)) {
                    if (i == 0 && segment.getIntersectionFrom().getSegmentsNumber() <= 2) {
                        // start point
                        valid = true;
                    } else if (i == routeList.size() - 2 && segment.getIntersectionTo().getSegmentsNumber() <= 2) {
                        // end point
                        valid = true;
                    } else if (i > 0 && i < routeList.size() - 2) {
                        // normal segment
                        valid = true;
                    }
                }
            }
            if (!valid) {
                return false;
            }
        }
        return true;
    }
}
