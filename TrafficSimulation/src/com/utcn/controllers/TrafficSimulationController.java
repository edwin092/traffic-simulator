package com.utcn.controllers;

import com.utcn.application.TrafficSimulationView;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.utils.TrafficSimulationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class TrafficSimulationController {

    private TrafficSimulationView simView;

    public TrafficSimulationController(TrafficSimulationView simView) {
        this.simView = simView;

        simView.addMouseClickListener(new MouseClickListener());

        // TODO
        // not working with swingWorker
        // simView.addSimulationStartListener(new SimulationStartListener());
    }

    class MouseClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
//			simView.setxClick(e.getX());
//			simView.setyClick(e.getY());
//
//			/* SEGMENT SELECTED */
//			if (simView.isSegmentSelected()) {
//
//				checkSegmentClick();
//
//				simView.getPanelSimulation().repaint();
//			}
//
//			/* INTERSECTION SELECTED */
//			if (simView.isIntersectionSelected()) {
//
//				checkIntersectionClick();
//
//				simView.getPanelSimulation().repaint();
//				simView.setIntersectionSelected(false);
//			}
        }

        @Override
        public void mousePressed(MouseEvent e) {
            simView.setxClick(e.getX());
            simView.setyClick(e.getY());

			/* SEGMENT SELECTED */
            if (simView.isSegmentSelected()) {

                checkSegmentClick();

                simView.getPanelSimulation().repaint();
            }

			/* INTERSECTION SELECTED */
            if (simView.isIntersectionSelected()) {

                checkIntersectionClick();

                simView.getPanelSimulation().repaint();
                simView.setIntersectionSelected(false);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        /**
         * @return
         */
        private Intersection getFirstSelectedIntersection() {
            for (Intersection intersection : simView.getIntersectionButtons()) {
                if (intersection.isAnySegmentSelected()) {
                    return intersection;
                }
            }
            return null;
        }

        /**
         *
         */
        private void resetAllSegments() {
            for (Intersection intersection : simView.getIntersectionButtons()) {
                intersection.setAllSegmentsFalse();
            }
        }

        /**
         * Get the length of the click zone based on the segment direction.
         *
         * @param isAdd true - add ; false - subtract
         * @return length of click zone
         */
        private int getClickZoneLength(boolean isAdd) {
            if (isAdd) {
                return TrafficSimulationView.INTERSECTION_SIZE / 2
                        + TrafficSimulationView.INTERSECTION_SIZE / 4;
            } else {
                return TrafficSimulationView.INTERSECTION_SIZE / 2
                        - TrafficSimulationView.INTERSECTION_SIZE / 4;
            }
        }

        /**
         * Set the segment out field for the current intersection based on the
         * selected side.
         *
         * @param intersection current intersection
         * @param segment      the segment to be set
         */
        private void setSegmentOutForIntersection(Intersection intersection,
                                                  Segment segment) {
            if (intersection.isSegmentNorthSelected()) {

                intersection.setSegmentNorthOut(segment);
            } else if (intersection.isSegmentSouthSelected()) {

                intersection.setSegmentSouthOut(segment);
            } else if (intersection.isSegmentEastSelected()) {

                intersection.setSegmentEastOut(segment);
            } else if (intersection.isSegmentVestSelected()) {

                intersection.setSegmentVestOut(segment);
            }
        }

        /**
         * Check to see if segment can be created and if true create it.
         */
        private void checkSegmentClick() {

            Intersection firstSelectedIntersection = getFirstSelectedIntersection();
            boolean entered = false;

            for (Intersection intersection : simView.getIntersectionButtons()) {

                // North
                if ((simView.getxClick() >= intersection.getX()
                        && simView.getxClick() <= (intersection.getX() + intersection
                        .getWidth()) && (simView.getyClick() <= intersection
                        .getY() && simView.getyClick() >= intersection.getY()
                        - TrafficSimulationView.INTERSECTION_CLICK_SIZE))) {

                    if (firstSelectedIntersection == null
                            && intersection.getSegmentNorthOut() == null) {
                        // no intersection has been selected

                        intersection.setSegmentNorthSelected(true);

                        // set segment starting point
                        int firstX = intersection.getX()
                                + getClickZoneLength(true);
                        int firstY = intersection.getY();

                        // save coordinates
                        simView.saveXYValues(firstX, firstY);
                    } else if (firstSelectedIntersection != null
                            && intersection.getSegmentNorthOut() == null) {
                        // starting intersection has already been selected

                        // create new segment
                        Segment segment = new Segment();
                        // set segment id
                        segment.setId(simView.getCurrentSegId());
                        simView.incrementCurrentSegId();

                        // set segment intersections
                        segment.setIntersectionIn(firstSelectedIntersection);
                        segment.setIntersectionOut(intersection);

                        // set segment end point
                        int finalX = intersection.getX()
                                + getClickZoneLength(false);
                        int finalY = intersection.getY();
                        // save coordinates
                        simView.saveXYValues(finalX, finalY);

                        // get current segment coordinates
                        List<Integer> xCoords = simView
                                .getCurrentSegmentXCoords();
                        List<Integer> yCoords = simView
                                .getCurrentSegmentYCoords();

                        // set coordinates for segment
                        segment.setLineCoordsX(TrafficSimulationUtil
                                .convertList(xCoords));
                        segment.setLineCoordsY(TrafficSimulationUtil
                                .convertList(yCoords));

                        // set segment for intersections
                        intersection.setSegmentNorthIn(segment);
                        setSegmentOutForIntersection(firstSelectedIntersection,
                                segment);

                        simView.getSegments().add(segment);
                        simView.incrementCurrentSegement();
                        resetAllSegments();
                        simView.setSegmentSelected(false);
                    }

                    entered = true;
                    break;
                }

                // South
                if ((simView.getxClick() >= intersection.getX()
                        && simView.getxClick() <= (intersection.getX() + intersection
                        .getWidth()) && (simView.getyClick() >= (intersection
                        .getHeight() + intersection.getY()) && simView
                        .getyClick() <= (intersection.getHeight() + intersection
                        .getY())
                        + TrafficSimulationView.INTERSECTION_CLICK_SIZE))) {

                    if (firstSelectedIntersection == null
                            && intersection.getSegmentSouthOut() == null) {
                        // no intersection has been selected

                        intersection.setSegmentSouthSelected(true);

                        // set segment starting point
                        int firstX = intersection.getX()
                                + getClickZoneLength(false);
                        int firstY = intersection.getY()
                                + TrafficSimulationView.INTERSECTION_SIZE;

                        // save coordinates
                        simView.saveXYValues(firstX, firstY);
                    } else if (firstSelectedIntersection != null
                            && intersection.getSegmentSouthIn() == null) {
                        // starting intersection has already been selected

                        // create new segment
                        Segment segment = new Segment();

                        // set segment id
                        segment.setId(simView.getCurrentSegId());
                        simView.incrementCurrentSegId();

                        // set segment intersections
                        segment.setIntersectionIn(firstSelectedIntersection);
                        segment.setIntersectionOut(intersection);

                        // set segment end point
                        int finalX = intersection.getX()
                                + getClickZoneLength(true);
                        int finalY = intersection.getY()
                                + TrafficSimulationView.INTERSECTION_SIZE;
                        // save coordinates
                        simView.saveXYValues(finalX, finalY);

                        // get current segment coordinates
                        List<Integer> xCoords = simView
                                .getCurrentSegmentXCoords();
                        List<Integer> yCoords = simView
                                .getCurrentSegmentYCoords();

                        // set coordinates for segment
                        segment.setLineCoordsX(TrafficSimulationUtil
                                .convertList(xCoords));
                        segment.setLineCoordsY(TrafficSimulationUtil
                                .convertList(yCoords));

                        // set segment for intersections
                        intersection.setSegmentSouthIn(segment);
                        setSegmentOutForIntersection(firstSelectedIntersection,
                                segment);

                        simView.getSegments().add(segment);
                        simView.incrementCurrentSegement();
                        resetAllSegments();
                        simView.setSegmentSelected(false);
                    }

                    entered = true;
                    break;
                }

                // Vest
                if ((simView.getxClick() <= intersection.getX()
                        && simView.getxClick() >= intersection.getX()
                        - TrafficSimulationView.INTERSECTION_CLICK_SIZE && (simView
                        .getyClick() >= intersection.getY() && simView
                        .getyClick() <= (intersection.getHeight() + intersection
                        .getY())))) {

                    if (firstSelectedIntersection == null
                            && intersection.getSegmentVestOut() == null) {
                        // no intersection has been selected

                        intersection.setSegmentVestSelected(true);

                        // set segment starting point
                        int firstX = intersection.getX();
                        int firstY = intersection.getY()
                                + getClickZoneLength(false);

                        // save coordinates
                        simView.saveXYValues(firstX, firstY);
                    } else if (firstSelectedIntersection != null
                            && intersection.getSegmentVestIn() == null) {
                        // starting intersection has already been selected

                        // create new segment
                        Segment segment = new Segment();

                        // set segment id
                        segment.setId(simView.getCurrentSegId());
                        simView.incrementCurrentSegId();

                        // set segment intersections
                        segment.setIntersectionIn(firstSelectedIntersection);
                        segment.setIntersectionOut(intersection);

                        // set segment end point
                        int finalX = intersection.getX();
                        int finalY = intersection.getY()
                                + getClickZoneLength(true);
                        // save coordinates
                        simView.saveXYValues(finalX, finalY);

                        // get current segment coordinates
                        List<Integer> xCoords = simView
                                .getCurrentSegmentXCoords();
                        List<Integer> yCoords = simView
                                .getCurrentSegmentYCoords();

                        // set coordinates for segment
                        segment.setLineCoordsX(TrafficSimulationUtil
                                .convertList(xCoords));
                        segment.setLineCoordsY(TrafficSimulationUtil
                                .convertList(yCoords));

                        // set segment for intersections
                        intersection.setSegmentVestIn(segment);
                        setSegmentOutForIntersection(firstSelectedIntersection,
                                segment);

                        simView.getSegments().add(segment);
                        simView.incrementCurrentSegement();
                        resetAllSegments();
                        simView.setSegmentSelected(false);
                    }

                    entered = true;
                    break;
                }

                // East
                if ((simView.getxClick() >= (intersection.getX() + intersection
                        .getWidth())
                        && simView.getxClick() <= (intersection.getX() + intersection
                        .getWidth())
                        + TrafficSimulationView.INTERSECTION_CLICK_SIZE && (simView
                        .getyClick() >= intersection.getY() && simView
                        .getyClick() <= (intersection.getHeight() + intersection
                        .getY())))) {

                    if (firstSelectedIntersection == null
                            && intersection.getSegmentEastOut() == null) {
                        // no intersection has been selected

                        intersection.setSegmentEastSelected(true);

                        // set segment starting point
                        int firstX = intersection.getX()
                                + TrafficSimulationView.INTERSECTION_SIZE;
                        int firstY = intersection.getY()
                                + getClickZoneLength(true);

                        // save coordinates
                        simView.saveXYValues(firstX, firstY);
                    } else if (firstSelectedIntersection != null
                            && intersection.getSegmentEastIn() == null) {
                        // starting intersection has already been selected

                        // create new segment
                        Segment segment = new Segment();

                        // set segment id
                        segment.setId(simView.getCurrentSegId());
                        simView.incrementCurrentSegId();

                        // set segment intersections
                        segment.setIntersectionIn(firstSelectedIntersection);
                        segment.setIntersectionOut(intersection);

                        // set segment end point
                        int finalX = intersection.getX()
                                + TrafficSimulationView.INTERSECTION_SIZE;
                        int finalY = intersection.getY()
                                + getClickZoneLength(false);
                        // save coordinates
                        simView.saveXYValues(finalX, finalY);

                        // get current segment coordinates
                        List<Integer> xCoords = simView
                                .getCurrentSegmentXCoords();
                        List<Integer> yCoords = simView
                                .getCurrentSegmentYCoords();

                        // set coordinates for segment
                        segment.setLineCoordsX(TrafficSimulationUtil
                                .convertList(xCoords));
                        segment.setLineCoordsY(TrafficSimulationUtil
                                .convertList(yCoords));

                        // set segment for intersections
                        intersection.setSegmentEastIn(segment);
                        setSegmentOutForIntersection(firstSelectedIntersection,
                                segment);

                        simView.getSegments().add(segment);
                        simView.incrementCurrentSegement();
                        resetAllSegments();
                        simView.setSegmentSelected(false);
                    }

                    entered = true;
                    break;
                }
            }

            if (!entered && firstSelectedIntersection != null) {
                // save click coordinates
                simView.saveXYValues(null, null);
            }
        }

        /**
         * Check to see if intersection can be created and if true create it.
         */
        private void checkIntersectionClick() {

            // check intersection size
            if (simView.getxClick() + TrafficSimulationView.INTERSECTION_SIZE > simView
                    .getFrame().getWidth()
                    || simView.getyClick()
                    + TrafficSimulationView.INTERSECTION_SIZE > simView
                    .getFrame().getHeight()) {
                JOptionPane.showMessageDialog(null,
                        "Size of Intersection exceeds panel!", "Warning",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Intersection intersection = new Intersection();
            // set id
            intersection.setId(simView.getCurrentIntersId());
            simView.incrementCurrentIntersId();

            intersection.setEnabled(false);
            intersection.setBounds(simView.getxClick(), simView.getyClick(),
                    TrafficSimulationView.INTERSECTION_SIZE,
                    TrafficSimulationView.INTERSECTION_SIZE);
            simView.getIntersectionButtons().add(intersection);
//            simView.getPanelSimulation().add(intersection);

            simView.getPanelSimulation().repaint();

//            simView.getPanelSimulation().setComponentZOrder(intersection, 0);
        }

    }

    class SimulationStartListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Add traffic lights to intersections
            for (Intersection intersection : simView.getIntersectionButtons()) {
                // NORTH
//                if (intersection.getSegmentNorthIn() != null) {
                JTextField trafficLight = new JTextField();
                trafficLight.setBackground(Color.RED);
                trafficLight.setEditable(false);
                trafficLight.setBounds(intersection.getX(),
                        intersection.getY() - 20,
                        TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
                        TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
                simView.getPanelSimulation().add(trafficLight);

                simView.getPanelSimulation().repaint();
//                }
                // SOUTH
//                if (intersection.getSegmentSouthIn() != null) {
                JTextField trafficLight2 = new JTextField();
                trafficLight2.setBackground(Color.RED);
                trafficLight2.setEditable(false);
                trafficLight2.setBounds(
                        intersection.getX() + intersection.getWidth() - 15,
                        intersection.getY() + intersection.getHeight() + 7,
                        TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
                        TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
                simView.getPanelSimulation().add(trafficLight2);

                simView.getPanelSimulation().repaint();
//                }
                // VEST
//                if (intersection.getSegmentVestIn() != null) {
                JTextField trafficLight3 = new JTextField();
                trafficLight3.setBackground(Color.RED);
                trafficLight3.setEditable(false);
                trafficLight3
                        .setBounds(
                                intersection.getX() - 20,
                                intersection.getY()
                                        + intersection.getHeight() - 15,
                                TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
                                TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
                simView.getPanelSimulation().add(trafficLight3);

                simView.getPanelSimulation().repaint();
//                }
                // EAST
//                if (intersection.getSegmentEastIn() != null) {
                JTextField trafficLight4 = new JTextField();
                trafficLight4.setBackground(Color.RED);
                trafficLight4.setEditable(false);
                trafficLight4.setBounds(
                        intersection.getX() + intersection.getWidth() + 7,
                        intersection.getY(),
                        TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
                        TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
                simView.getPanelSimulation().add(trafficLight4);

                simView.getPanelSimulation().repaint();
//                }
            }

            simView.simulate();

        }
    }
}
