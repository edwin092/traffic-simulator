package com.utcn.utils;

import com.utcn.application.TrafficSimulationView;
import com.utcn.models.Intersection;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

public class TrafficSimulationUtil {

    public static final double CONVERSION_UNIT = 2;

    // prevent this class from being instanced
    private TrafficSimulationUtil() {
    }

    /**
     * Convert a List of Integer to a new int[].
     *
     * @param list the list of integers
     * @return the new int[]
     */
    public static int[] convertList(List<Integer> list) {
        int[] x = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            x[i] = list.get(i);
        }

        return x;
    }

    /**
     * Returns a random int from given (min, max) interval.
     *
     * @param min min
     * @param max max
     * @return random int
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static int distanceBetweenPoints(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Converts meters to pixels.
     *
     * @param x value in meters
     * @return value in pixels
     */
    public static double convertMetersToPixels(double x) {
        return x / CONVERSION_UNIT;
    }

    /**
     * Converts pixels in meters.
     *
     * @param x value in pixels
     * @return value in meters
     */
    public static double convertPixelsToMeters(double x) {
        return x * CONVERSION_UNIT;
    }

    /**
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param currentDist
     * @return
     */
    public static int[] getVehiclePosition(int x1, int y1, int x2, int y2, double currentDist) {
        int bigOrSmallX;
        int bigOrSmallY;

        if (x1 > x2) {
            // smaller
            bigOrSmallX = x2;
        } else {
            // bigger
            bigOrSmallX = -x2;
        }

        if (y1 > y2) {
            // smaller
            bigOrSmallY = y2;
        } else {
            // bigger
            bigOrSmallY = -y2;
        }

        double dx = Math.abs(x1 - x2);
        double dy = Math.abs(y1 - y2);
        double m = round(dy / dx, 2);
        double alpha = round(Math.atan(m), 2);
        double totalDist = distanceBetweenPoints(x1, y1, x2, y2);

        double dxN = round(Math.abs((totalDist - currentDist) * Math.cos(alpha)), 2);

        int xN = Math.abs((int) (bigOrSmallX + dxN));

        double dyN = round(Math.abs((totalDist - currentDist) * Math.sin(alpha)), 2);

        int yN = Math.abs((int) (bigOrSmallY + dyN));

        return new int[]{xN, yN};
    }

    /**
     * Export simulation environment to a JSON file.
     */
    public static boolean exportToJSON(String filepath, CustomImportExportClass customExportClasss) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!filepath.contains(".json")) {
                filepath += ".json";
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filepath), customExportClasss);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Import an already existing simulation environment.
     */
    public static CustomImportExportClass importFromJSON(String filepath) {
        ObjectMapper mapper = new ObjectMapper();
        CustomImportExportClass customImportExportClass;

        try {
            customImportExportClass = mapper.readValue(new File(filepath), CustomImportExportClass.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return customImportExportClass;
    }

    /**
     * @param view
     * @return
     */
    public static SimulationGraph convertSimulaionEnvironmentToGraph(TrafficSimulationView view) {
        SimulationGraph simulationGraph = new SimulationGraph();

        for (Intersection intersection : view.getIntersectionButtons()) {
            if (intersection.getSegmentNorthOut() != null) {
                simulationGraph.addEdge(intersection.getId(), intersection.getSegmentNorthOut().getIntersectionOut().getId());
            }
            if (intersection.getSegmentSouthOut() != null) {
                simulationGraph.addEdge(intersection.getId(), intersection.getSegmentSouthOut().getIntersectionOut().getId());
            }
            if (intersection.getSegmentEastOut() != null) {
                simulationGraph.addEdge(intersection.getId(), intersection.getSegmentEastOut().getIntersectionOut().getId());
            }
            if (intersection.getSegmentVestOut() != null) {
                simulationGraph.addEdge(intersection.getId(), intersection.getSegmentVestOut().getIntersectionOut().getId());
            }
        }

        return simulationGraph;
    }
}
