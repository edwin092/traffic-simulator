package com.utcn.utils;

import com.utcn.application.TrafficSimulationView;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;

public class ImportExportHelper {

    /**
     * Export simulation environment to a JSON file.
     */
    public static boolean exportToJSON(String folderpath, TrafficSimulationView view) {

        return TrafficSimulationUtil.exportToJSON(folderpath, convertToCustomImportExportClass(view));
    }

    /**
     * Create a custom object for the export method.
     */
    private static CustomImportExportClass convertToCustomImportExportClass(TrafficSimulationView view) {
        CustomImportExportClass customImportExportClass = new CustomImportExportClass();

        customImportExportClass.setCurrentSegId(view.getCurrentSegId());
        customImportExportClass.setCurrentIntersId(view.getCurrentIntersId());
        customImportExportClass.setCurrentSegment(view.getCurrentSegment());
        customImportExportClass.setSegmentCoordsX(view.getSegmentCoordsX());
        customImportExportClass.setSegmentCoordsY(view.getSegmentCoordsY());

        for (Intersection intersection : view.getIntersectionButtons()) {
            CustomImportExportClass.CustomIntersection customIntersection = new CustomImportExportClass.CustomIntersection();

            customIntersection.setId(intersection.getId());

            customIntersection.setSegmentEastInId(intersection.getSegmentEastIn() != null ? intersection.getSegmentEastIn().getId() : 0);
            customIntersection.setSegmentEastOutId(intersection.getSegmentEastOut() != null ? intersection.getSegmentEastOut().getId() : 0);
            customIntersection.setSegmentVestInId(intersection.getSegmentVestIn() != null ? intersection.getSegmentVestIn().getId() : 0);
            customIntersection.setSegmentVestOutId(intersection.getSegmentVestOut() != null ? intersection.getSegmentVestOut().getId() : 0);
            customIntersection.setSegmentSouthInId(intersection.getSegmentSouthIn() != null ? intersection.getSegmentSouthIn().getId() : 0);
            customIntersection.setSegmentSouthOutId(intersection.getSegmentSouthOut() != null ? intersection.getSegmentSouthOut().getId() : 0);
            customIntersection.setSegmentNorthOutId(intersection.getSegmentNorthIn() != null ? intersection.getSegmentNorthIn().getId() : 0);
            customIntersection.setSegmentNorthInId(intersection.getSegmentNorthOut() != null ? intersection.getSegmentNorthOut().getId() : 0);

            customIntersection.setX(intersection.getX());
            customIntersection.setY(intersection.getY());

            customImportExportClass.getIntersections().add(customIntersection);
        }

        for (Segment segment : view.getSegments()) {
            CustomImportExportClass.CustomSegment customSegment = new CustomImportExportClass.CustomSegment();

            customSegment.setId(segment.getId());
            customSegment.setIntersectionInId(segment.getIntersectionIn().getId());
            customSegment.setIntersectionOutId(segment.getIntersectionOut().getId());

            customImportExportClass.getSegments().add(customSegment);
        }

        return customImportExportClass;
    }

    /**
     * Import an already existing simulation environment.
     */
    public static boolean importFromJSON(String filepath, TrafficSimulationView view) {
        CustomImportExportClass customImportExportClass = TrafficSimulationUtil.importFromJSON(filepath);

        if (customImportExportClass != null) {
            convertFromCustomImportExportClass(customImportExportClass, view);

            return true;
        }
        return false;
    }

    /**
     * Create simulation environment from custom object.
     */
    private static void convertFromCustomImportExportClass(CustomImportExportClass customImportExportClass, TrafficSimulationView view) {
        view.setCurrentSegId(customImportExportClass.getCurrentSegId());
        view.setCurrentIntersId(customImportExportClass.getCurrentIntersId());
        view.setCurrentSegment(customImportExportClass.getCurrentSegment());
        view.setSegmentCoordsX(customImportExportClass.getSegmentCoordsX());
        view.setSegmentCoordsY(customImportExportClass.getSegmentCoordsY());

        // generate intersections without segments
        for (CustomImportExportClass.CustomIntersection customIntersection : customImportExportClass.getIntersections()) {
            Intersection intersection = new Intersection();

            intersection.setId(customIntersection.getId());
            intersection.setEnabled(false);
            intersection.setBounds(customIntersection.getX(), customIntersection.getY(),
                    TrafficSimulationView.INTERSECTION_SIZE,
                    TrafficSimulationView.INTERSECTION_SIZE);

            view.getIntersectionButtons().add(intersection);
        }

        // generate segments
        for (CustomImportExportClass.CustomSegment customSegment : customImportExportClass.getSegments()) {
            Segment segment = new Segment();

            segment.setId(customSegment.getId());


            int[] coordsX = TrafficSimulationUtil.convertList(customImportExportClass.getSegmentCoordsX().get(customSegment.getId()));
            int[] coordsY = TrafficSimulationUtil.convertList(customImportExportClass.getSegmentCoordsY().get(customSegment.getId()));

            segment.setLineCoordsX(coordsX);
            segment.setLineCoordsY(coordsY);

            for (Intersection intersection : view.getIntersectionButtons()) {
                if (customSegment.getIntersectionInId() == intersection.getId()) {
                    segment.setIntersectionIn(intersection);
                } else if (customSegment.getIntersectionOutId() == intersection.getId()) {
                    segment.setIntersectionOut(intersection);
                }
            }

            view.getSegments().add(segment);
        }

        // add segments to intersections
        for (CustomImportExportClass.CustomIntersection customIntersection : customImportExportClass.getIntersections()) {
            for (Intersection intersection : view.getIntersectionButtons()) {
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
