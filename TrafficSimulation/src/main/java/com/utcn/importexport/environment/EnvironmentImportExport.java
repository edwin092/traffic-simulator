package com.utcn.importexport.environment;

import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.utils.TrafficSimulationUtil;
import com.utcn.view.TrafficSimulationView;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class EnvironmentImportExport {

    /**
     * Export simulation environment to a JSON file.
     */
    public static boolean exportToJSON(String filepath, TrafficSimulationView view) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!filepath.contains(".json")) {
                filepath += ".json";
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filepath), convertToCustomImportExportClass(view));
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Import an already existing simulation environment.
     */
    public static boolean importFromJSON(String filepath, TrafficSimulationView view) {
        ObjectMapper mapper = new ObjectMapper();
        CustomEnvironment customEnvironment;
        try {
            customEnvironment = mapper.readValue(new File(filepath), CustomEnvironment.class);
        } catch (IOException e) {
            customEnvironment = null;
        }

        if (customEnvironment != null) {
            convertFromCustomImportExportClass(customEnvironment, view);

            return true;
        }
        return false;
    }

    /**
     * Create a custom object for the export method.
     */
    private static CustomEnvironment convertToCustomImportExportClass(TrafficSimulationView view) {
        CustomEnvironment customEnvironment = new CustomEnvironment();

        customEnvironment.setCurrentSegId(view.getCurrentSegId());
        customEnvironment.setCurrentIntersId(view.getCurrentIntersId());
        customEnvironment.setCurrentSegment(view.getCurrentSegment());
        customEnvironment.setSegmentCoordsX(view.getSegmentCoordsX());
        customEnvironment.setSegmentCoordsY(view.getSegmentCoordsY());

        for (Intersection intersection : view.getIntersections()) {
            CustomEnvironment.CustomIntersection customIntersection = new CustomEnvironment.CustomIntersection();

            customIntersection.setId(intersection.getId());

            customIntersection.setSegmentEastInId(intersection.getSegmentEastIn() != null ? intersection.getSegmentEastIn().getId() : 0);
            customIntersection.setSegmentEastOutId(intersection.getSegmentEastOut() != null ? intersection.getSegmentEastOut().getId() : 0);
            customIntersection.setSegmentVestInId(intersection.getSegmentVestIn() != null ? intersection.getSegmentVestIn().getId() : 0);
            customIntersection.setSegmentVestOutId(intersection.getSegmentVestOut() != null ? intersection.getSegmentVestOut().getId() : 0);
            customIntersection.setSegmentSouthInId(intersection.getSegmentSouthIn() != null ? intersection.getSegmentSouthIn().getId() : 0);
            customIntersection.setSegmentSouthOutId(intersection.getSegmentSouthOut() != null ? intersection.getSegmentSouthOut().getId() : 0);
            customIntersection.setSegmentNorthInId(intersection.getSegmentNorthIn() != null ? intersection.getSegmentNorthIn().getId() : 0);
            customIntersection.setSegmentNorthOutId(intersection.getSegmentNorthOut() != null ? intersection.getSegmentNorthOut().getId() : 0);

            customIntersection.setX(intersection.getX());
            customIntersection.setY(intersection.getY());

            customEnvironment.getIntersections().add(customIntersection);
        }

        for (Segment segment : view.getSegments()) {
            CustomEnvironment.CustomSegment customSegment = new CustomEnvironment.CustomSegment();

            customSegment.setId(segment.getId());
            customSegment.setIntersectionInId(segment.getIntersectionFrom().getId());
            customSegment.setIntersectionOutId(segment.getIntersectionTo().getId());

            customEnvironment.getSegments().add(customSegment);
        }

        return customEnvironment;
    }

    /**
     * Create simulation environment from custom object.
     */
    private static void convertFromCustomImportExportClass(CustomEnvironment customEnvironment, TrafficSimulationView view) {
        view.setCurrentSegId(customEnvironment.getCurrentSegId());
        view.setCurrentIntersId(customEnvironment.getCurrentIntersId());
        view.setCurrentSegment(customEnvironment.getCurrentSegment());
        view.setSegmentCoordsX(customEnvironment.getSegmentCoordsX());
        view.setSegmentCoordsY(customEnvironment.getSegmentCoordsY());

        // generate intersections without segments
        for (CustomEnvironment.CustomIntersection customIntersection : customEnvironment.getIntersections()) {
            Intersection intersection = new Intersection();

            intersection.setId(customIntersection.getId());
            intersection.setEnabled(false);
            intersection.setBounds(customIntersection.getX(), customIntersection.getY(),
                    TrafficSimulationView.INTERSECTION_SIZE,
                    TrafficSimulationView.INTERSECTION_SIZE);

            view.getIntersections().add(intersection);
        }

        // generate segments
        for (CustomEnvironment.CustomSegment customSegment : customEnvironment.getSegments()) {
            Segment segment = new Segment();

            segment.setId(customSegment.getId());


            int[] coordsX = TrafficSimulationUtil.convertList(customEnvironment.getSegmentCoordsX().get(customSegment.getId()));
            int[] coordsY = TrafficSimulationUtil.convertList(customEnvironment.getSegmentCoordsY().get(customSegment.getId()));

            segment.setLineCoordsX(coordsX);
            segment.setLineCoordsY(coordsY);

            for (Intersection intersection : view.getIntersections()) {
                if (customSegment.getIntersectionInId() == intersection.getId()) {
                    segment.setIntersectionFrom(intersection);
                } else if (customSegment.getIntersectionOutId() == intersection.getId()) {
                    segment.setIntersectionTo(intersection);
                }
            }

            view.getSegments().add(segment);
        }

        // add segments to intersections
        for (CustomEnvironment.CustomIntersection customIntersection : customEnvironment.getIntersections()) {
            for (Intersection intersection : view.getIntersections()) {
                if (intersection.getId() == customIntersection.getId()) {

                    for (Segment segment : view.getSegments()) {
                        if (customIntersection.getSegmentEastInId() == segment.getId()) {
                            intersection.setSegmentEastIn(segment);
                        } else if (customIntersection.getSegmentEastOutId() == segment.getId()) {
                            intersection.setSegmentEastOut(segment);
                        } else if (customIntersection.getSegmentVestInId() == segment.getId()) {
                            intersection.setSegmentVestIn(segment);
                        } else if (customIntersection.getSegmentVestOutId() == segment.getId()) {
                            intersection.setSegmentVestOut(segment);
                        } else if (customIntersection.getSegmentNorthInId() == segment.getId()) {
                            intersection.setSegmentNorthIn(segment);
                        } else if (customIntersection.getSegmentNorthOutId() == segment.getId()) {
                            intersection.setSegmentNorthOut(segment);
                        } else if (customIntersection.getSegmentSouthInId() == segment.getId()) {
                            intersection.setSegmentSouthIn(segment);
                        } else if (customIntersection.getSegmentSouthOutId() == segment.getId()) {
                            intersection.setSegmentSouthOut(segment);
                        }
                    }
                    break;
                }
            }
        }
    }
}
