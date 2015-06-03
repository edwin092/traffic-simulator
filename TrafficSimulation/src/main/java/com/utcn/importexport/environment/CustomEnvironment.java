package com.utcn.importexport.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CustomEnvironment {

    private int currentSegment = 1;
    private int currentSegId = 1;
    private int currentIntersId = 1;

    private List<CustomIntersection> intersections = new ArrayList<>();
    private List<CustomSegment> segments = new ArrayList<>();

    private Map<Integer, List<Integer>> segmentCoordsX = new HashMap<>();
    private Map<Integer, List<Integer>> segmentCoordsY = new HashMap<>();


    public Map<Integer, List<Integer>> getSegmentCoordsX() {
        return segmentCoordsX;
    }

    public void setSegmentCoordsX(Map<Integer, List<Integer>> segmentCoordsX) {
        this.segmentCoordsX = segmentCoordsX;
    }

    public Map<Integer, List<Integer>> getSegmentCoordsY() {
        return segmentCoordsY;
    }

    public void setSegmentCoordsY(Map<Integer, List<Integer>> segmentCoordsY) {
        this.segmentCoordsY = segmentCoordsY;
    }

    public int getCurrentSegment() {
        return currentSegment;
    }

    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
    }

    public int getCurrentSegId() {
        return currentSegId;
    }

    public void setCurrentSegId(int currentSegId) {
        this.currentSegId = currentSegId;
    }

    public int getCurrentIntersId() {
        return currentIntersId;
    }

    public void setCurrentIntersId(int currentIntersId) {
        this.currentIntersId = currentIntersId;
    }

    public List<CustomIntersection> getIntersections() {
        if (intersections == null) {
            return new ArrayList<>();
        }
        return intersections;
    }

    public void setIntersections(List<CustomIntersection> intersections) {
        this.intersections = intersections;
    }

    public List<CustomSegment> getSegments() {
        if (segments == null) {
            return new ArrayList<>();
        }
        return segments;
    }

    public void setSegments(List<CustomSegment> segments) {
        this.segments = segments;
    }

    static class CustomIntersection {
        private int id;
        private int x;
        private int y;
        private int segmentNorthInId;
        private int segmentNorthOutId;
        private int segmentSouthInId;
        private int segmentSouthOutId;
        private int segmentVestInId;
        private int segmentVestOutId;
        private int segmentEastInId;
        private int segmentEastOutId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getSegmentNorthInId() {
            return segmentNorthInId;
        }

        public void setSegmentNorthInId(int segmentNorthInId) {
            this.segmentNorthInId = segmentNorthInId;
        }

        public int getSegmentNorthOutId() {
            return segmentNorthOutId;
        }

        public void setSegmentNorthOutId(int segmentNorthOutId) {
            this.segmentNorthOutId = segmentNorthOutId;
        }

        public int getSegmentSouthInId() {
            return segmentSouthInId;
        }

        public void setSegmentSouthInId(int segmentSouthInId) {
            this.segmentSouthInId = segmentSouthInId;
        }

        public int getSegmentSouthOutId() {
            return segmentSouthOutId;
        }

        public void setSegmentSouthOutId(int segmentSouthOutId) {
            this.segmentSouthOutId = segmentSouthOutId;
        }

        public int getSegmentVestInId() {
            return segmentVestInId;
        }

        public void setSegmentVestInId(int segmentVestInId) {
            this.segmentVestInId = segmentVestInId;
        }

        public int getSegmentVestOutId() {
            return segmentVestOutId;
        }

        public void setSegmentVestOutId(int segmentVestOutId) {
            this.segmentVestOutId = segmentVestOutId;
        }

        public int getSegmentEastInId() {
            return segmentEastInId;
        }

        public void setSegmentEastInId(int segmentEastInId) {
            this.segmentEastInId = segmentEastInId;
        }

        public int getSegmentEastOutId() {
            return segmentEastOutId;
        }

        public void setSegmentEastOutId(int segmentEastOutId) {
            this.segmentEastOutId = segmentEastOutId;
        }
    }

    static class CustomSegment {
        private int id;
        private int intersectionInId;
        private int intersectionOutId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIntersectionInId() {
            return intersectionInId;
        }

        public void setIntersectionInId(int intersectionInId) {
            this.intersectionInId = intersectionInId;
        }

        public int getIntersectionOutId() {
            return intersectionOutId;
        }

        public void setIntersectionOutId(int intersectionOutId) {
            this.intersectionOutId = intersectionOutId;
        }
    }


}
