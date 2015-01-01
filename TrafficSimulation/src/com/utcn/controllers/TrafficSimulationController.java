package com.utcn.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.utcn.application.TrafficSimulationView;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;

public class TrafficSimulationController {

	private TrafficSimulationView simView;

	public TrafficSimulationController(TrafficSimulationView simView) {
		this.simView = simView;

		simView.addMouseClickListener(new MouseClickListener());
		simView.addSimulationStartListener(new SimulationStartListener());
	}

	class MouseClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			simView.setxClick(e.getX());
			simView.setyClick(e.getY());

			/* SEGMENT SELECTED */
			if (simView.isSegmentSelected()) {

				checkSegmentClick();

				simView.getPanelSimulation().repaint();
				simView.setSegmentSelected(false);
			}

			/* INTERSECTION SELECTED */
			if (simView.isIntersectionSelected()) {

				checkIntersectionClick();

				simView.getPanelSimulation().repaint();
				simView.setIntersectionSelected(false);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
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
		 * Check to see if segment can be created and if true create it
		 */
		public void checkSegmentClick() {
			for (Intersection intersection : simView.getIntersectionButtons()) {

				/* Create selection panel for direction */
				JPanel selectionPanel = new JPanel();
				selectionPanel.add(Box.createHorizontalStrut(15));

				selectionPanel.add(new JLabel("Direction:"));

				ButtonGroup buttonGroup = new ButtonGroup();

				JCheckBox inCheckBox = new JCheckBox("--> Intersection");
				buttonGroup.add(inCheckBox);

				selectionPanel.add(inCheckBox);

				JCheckBox outCheckBox = new JCheckBox("Intersection -->");
				buttonGroup.add(outCheckBox);

				selectionPanel.add(outCheckBox);

				// North
				if ((simView.getxClick() >= intersection.getX() + 20
						&& simView.getxClick() <= (intersection.getX() + intersection
								.getWidth()) - 20 && (simView.getyClick() <= intersection
						.getY() && simView.getyClick() >= intersection.getY() - 20))) {
					// JPanel selectionPanel = new JPanel();
					//
					//
					// // selectionPanel.setLayout(new
					// // BoxLayout(selectionPanel, BoxLayout.LINE_AXIS));
					//
					// // JTextField lengthField = new JTextField(5);
					// //
					// // selectionPanel.add(new JLabel("Length:"));
					// // selectionPanel.add(lengthField);
					// selectionPanel.add(Box.createHorizontalStrut(15));
					//
					// selectionPanel.add(new JLabel("Direction:"));
					//
					// ButtonGroup buttonGroup = new ButtonGroup();
					//
					// JCheckBox inCheckBox = new
					// JCheckBox("--> Intersection");
					// buttonGroup.add(inCheckBox);
					// // chckbxNewCheckBox.setBounds(91, 188, 122, 23);
					// selectionPanel.add(inCheckBox);
					//
					// JCheckBox outCheckBox = new JCheckBox(
					// "Intersection -->");
					// buttonGroup.add(outCheckBox);
					// // chckbxNewCheckBox_1.setBounds(91, 230, 122, 23);
					// selectionPanel.add(outCheckBox);

					// check segment size
					if (intersection.getY() < TrafficSimulationView.SEGMENT_LENGTH) {
						JOptionPane.showMessageDialog(null,
								"Size of segment exceeds panel!", "Warning",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int result = JOptionPane.showConfirmDialog(null,
							selectionPanel,
							"Please Enter Direction and Length Values",
							JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {
						// int length = Integer.parseInt(lengthField
						// .getText());

						if (outCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(
									intersection.getX() + 60,
									intersection.getY()
											- TrafficSimulationView.SEGMENT_LENGTH,
									TrafficSimulationView.SEGMENT_WIDTH,
									TrafficSimulationView.SEGMENT_LENGTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentNorthOut(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										&& intersectionOther.getY() == intersection
												.getY()
												- TrafficSimulationView.SEGMENT_LENGTH
												- TrafficSimulationView.INTERSECTION_SIZE) {
									intersectionOther
											.setSegmentSouthIn(segment);
								}
							}

							break;
						} else if (inCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(
									intersection.getX() + 20,
									intersection.getY()
											- TrafficSimulationView.SEGMENT_LENGTH,
									TrafficSimulationView.SEGMENT_WIDTH,
									TrafficSimulationView.SEGMENT_LENGTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentNorthIn(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										&& intersectionOther.getY() == intersection
												.getY()
												- TrafficSimulationView.SEGMENT_LENGTH
												- TrafficSimulationView.INTERSECTION_SIZE) {
									intersectionOther
											.setSegmentSouthOut(segment);
								}
							}

							break;
						}
					}
				}

				// South
				if ((simView.getxClick() >= intersection.getX() + 20
						&& simView.getxClick() <= (intersection.getX() + intersection
								.getWidth()) - 20 && (simView.getyClick() >= (intersection
						.getHeight() + intersection.getY()) && simView
						.getyClick() <= (intersection.getHeight() + intersection
						.getY()) + 20))) {

					// check segment size
					if (intersection.getY() + intersection.getHeight()
							+ TrafficSimulationView.SEGMENT_LENGTH > simView
							.getFrame().getHeight()) {
						JOptionPane.showMessageDialog(null,
								"Size of segment exceeds panel!", "Warning",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int result = JOptionPane.showConfirmDialog(null,
							selectionPanel,
							"Please Enter Direction and Length Values",
							JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {

						if (outCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(
									intersection.getX() + 20,
									intersection.getY()
											+ intersection.getHeight(),
									TrafficSimulationView.SEGMENT_WIDTH,
									TrafficSimulationView.SEGMENT_LENGTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentSouthOut(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										&& intersectionOther.getY() == intersection
												.getY()
												+ TrafficSimulationView.SEGMENT_LENGTH
												+ TrafficSimulationView.INTERSECTION_SIZE) {
									intersectionOther
											.setSegmentNorthIn(segment);
								}
							}

							break;
						} else if (inCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(
									intersection.getX() + 60,
									intersection.getY()
											+ intersection.getHeight(),
									TrafficSimulationView.SEGMENT_WIDTH,
									TrafficSimulationView.SEGMENT_LENGTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentSouthIn(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										&& intersectionOther.getY() == intersection
												.getY()
												+ TrafficSimulationView.SEGMENT_LENGTH
												+ TrafficSimulationView.INTERSECTION_SIZE) {
									intersectionOther
											.setSegmentNorthOut(segment);
								}
							}

							break;
						}
					}
				}

				// Vest
				if ((simView.getxClick() <= intersection.getX()
						&& simView.getxClick() >= intersection.getX() - 20 && (simView
						.getyClick() >= intersection.getY() + 20 && simView
						.getyClick() <= (intersection.getHeight() + intersection
						.getY()) - 20))) {

					// check segment size
					if (intersection.getX() < TrafficSimulationView.SEGMENT_LENGTH) {
						JOptionPane.showMessageDialog(null,
								"Size of segment exceeds panel!", "Warning",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int result = JOptionPane.showConfirmDialog(null,
							selectionPanel,
							"Please Enter Direction and Length Values",
							JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {

						if (outCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(intersection.getX()
									- TrafficSimulationView.SEGMENT_LENGTH,
									intersection.getY() + 20,
									TrafficSimulationView.SEGMENT_LENGTH,
									TrafficSimulationView.SEGMENT_WIDTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentVestOut(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										- TrafficSimulationView.SEGMENT_LENGTH
										- TrafficSimulationView.INTERSECTION_SIZE
										&& intersectionOther.getY() == intersection
												.getY()) {
									intersectionOther.setSegmentEastIn(segment);
								}
							}

							break;
						} else if (inCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(intersection.getX()
									- TrafficSimulationView.SEGMENT_LENGTH,
									intersection.getY() + 60,
									TrafficSimulationView.SEGMENT_LENGTH,
									TrafficSimulationView.SEGMENT_WIDTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentVestIn(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										- TrafficSimulationView.SEGMENT_LENGTH
										- TrafficSimulationView.INTERSECTION_SIZE
										&& intersectionOther.getY() == intersection
												.getY()) {
									intersectionOther
											.setSegmentEastOut(segment);
								}
							}

							break;
						}
					}
				}

				// East
				if ((simView.getxClick() >= (intersection.getX() + intersection
						.getWidth())
						&& simView.getxClick() <= (intersection.getX() + intersection
								.getWidth()) + 20 && (simView.getyClick() >= intersection
						.getY() + 20 && simView.getyClick() <= (intersection
						.getHeight() + intersection.getY()) - 20))) {

					// check segment size
					if (intersection.getX() + intersection.getWidth()
							+ TrafficSimulationView.SEGMENT_LENGTH > simView
							.getFrame().getWidth()) {
						JOptionPane.showMessageDialog(null,
								"Size of segment exceeds panel!", "Warning",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					int result = JOptionPane.showConfirmDialog(null,
							selectionPanel,
							"Please Enter Direction and Length Values",
							JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {

						if (outCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(intersection.getX()
									+ intersection.getWidth(),
									intersection.getY() + 60,
									TrafficSimulationView.SEGMENT_LENGTH,
									TrafficSimulationView.SEGMENT_WIDTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentEastOut(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										+ TrafficSimulationView.SEGMENT_LENGTH
										+ TrafficSimulationView.INTERSECTION_SIZE
										&& intersectionOther.getY() == intersection
												.getY()) {
									intersectionOther.setSegmentVestIn(segment);
								}
							}

							break;
						} else if (inCheckBox.isSelected()) {
							Segment segment = new Segment();
							// Length is hard-coded to 100m
							segment.setBounds(intersection.getX()
									+ intersection.getWidth(),
									intersection.getY() + 20,
									TrafficSimulationView.SEGMENT_LENGTH,
									TrafficSimulationView.SEGMENT_WIDTH);
							segment.setLayout(null);

							simView.getPanelSimulation().add(segment);

							simView.getSegmentButtons().add(segment);

							simView.getPanelSimulation().repaint();

							intersection.setSegmentEastIn(segment);

							// check to see if there is a intersection
							for (Intersection intersectionOther : simView
									.getIntersectionButtons()) {
								if (intersectionOther.getX() == intersection
										.getX()
										+ TrafficSimulationView.SEGMENT_LENGTH
										+ TrafficSimulationView.INTERSECTION_SIZE
										&& intersectionOther.getY() == intersection
												.getY()) {
									intersectionOther.setSegmentVestIn(segment);
								}
							}

							break;
						}
					}
				}
			}
		}

		/**
		 * Check to see if intersection can be created and if true create it
		 * TODO CHECK POSSIBLE CONNECTED SEGMENTS
		 */
		public void checkIntersectionClick() {
			if (simView.getIntersectionButtons().isEmpty()) {
				// no intersection available
				// create a new one

				// check intersection size
				if (simView.getxClick()
						+ TrafficSimulationView.INTERSECTION_SIZE > simView
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
				intersection.setEnabled(false);
				intersection.setBounds(simView.getxClick(),
						simView.getyClick(),
						TrafficSimulationView.INTERSECTION_SIZE,
						TrafficSimulationView.INTERSECTION_SIZE);
				simView.getIntersectionButtons().add(intersection);
				simView.getPanelSimulation().add(intersection);
			} else {
				// create intersection only after segment
				for (Intersection intersection : simView
						.getIntersectionButtons()) {

					// NORTH
					if (intersection.getSegmentNorthIn() != null
							|| intersection.getSegmentNorthOut() != null) {

						if ((simView.getxClick() >= (intersection.getX() + 20))
								&& (simView.getxClick() <= (intersection.getX()
										+ intersection.getWidth() - 20))
								&& (simView.getyClick() <= (intersection.getY() - TrafficSimulationView.SEGMENT_LENGTH))
								&& (simView.getyClick() >= (intersection.getY()
										- TrafficSimulationView.SEGMENT_LENGTH - 20))) {

							// check intersection size
							if (intersection.getY()
									- TrafficSimulationView.SEGMENT_LENGTH
									- TrafficSimulationView.INTERSECTION_SIZE < 0) {
								JOptionPane.showMessageDialog(null,
										"Size of Intersection exceeds panel!",
										"Warning", JOptionPane.ERROR_MESSAGE);
								return;
							}

							Intersection newIntersection = new Intersection();
							newIntersection
									.setBounds(
											intersection.getX(),
											intersection.getY()
													- TrafficSimulationView.SEGMENT_LENGTH
													- TrafficSimulationView.INTERSECTION_SIZE,
											TrafficSimulationView.INTERSECTION_SIZE,
											TrafficSimulationView.INTERSECTION_SIZE);

							// connect segments to new intersection
							newIntersection.setSegmentSouthIn(intersection
									.getSegmentNorthOut());
							newIntersection.setSegmentSouthOut(intersection
									.getSegmentNorthIn());

							simView.getIntersectionButtons().add(
									newIntersection);
							simView.getPanelSimulation().add(newIntersection);
							break;
						}
					}

					// SOUTH
					if (intersection.getSegmentSouthIn() != null
							|| intersection.getSegmentSouthOut() != null) {
						if ((simView.getxClick() >= (intersection.getX() + 20))
								&& (simView.getxClick() <= (intersection.getX()
										+ intersection.getWidth() - 20))
								&& (simView.getyClick() >= (intersection.getY()
										+ intersection.getHeight() + TrafficSimulationView.SEGMENT_LENGTH))
								&& (simView.getyClick() <= (intersection.getY()
										+ intersection.getHeight()
										+ TrafficSimulationView.SEGMENT_LENGTH + 20))) {

							// check intersection size
							if (intersection.getY()
									+ TrafficSimulationView.SEGMENT_LENGTH + 2
									* TrafficSimulationView.INTERSECTION_SIZE > simView
									.getFrame().getHeight()) {
								JOptionPane.showMessageDialog(null,
										"Size of Intersection exceeds panel!",
										"Warning", JOptionPane.ERROR_MESSAGE);
								return;
							}

							Intersection newIntersection = new Intersection();
							newIntersection
									.setBounds(
											intersection.getX(),
											intersection.getY()
													+ intersection.getHeight()
													+ TrafficSimulationView.SEGMENT_LENGTH,
											TrafficSimulationView.INTERSECTION_SIZE,
											TrafficSimulationView.INTERSECTION_SIZE);

							// connect segments to new intersection
							newIntersection.setSegmentNorthIn(intersection
									.getSegmentSouthOut());
							newIntersection.setSegmentNorthOut(intersection
									.getSegmentSouthIn());

							simView.getIntersectionButtons().add(
									newIntersection);
							simView.getPanelSimulation().add(newIntersection);
							break;
						}
					}

					// EAST
					if (intersection.getSegmentEastIn() != null
							|| intersection.getSegmentEastOut() != null) {
						if ((simView.getxClick() >= (intersection.getX()
								+ intersection.getWidth() + TrafficSimulationView.SEGMENT_LENGTH))
								&& (simView.getxClick() <= (intersection.getX()
										+ intersection.getWidth()
										+ TrafficSimulationView.SEGMENT_LENGTH + 20))
								&& (simView.getyClick() >= (intersection.getY() + 20))
								&& (simView.getyClick() <= (intersection.getY()
										+ intersection.getHeight() - 20))) {

							// check intersection size
							if (intersection.getX()
									+ TrafficSimulationView.SEGMENT_LENGTH + 2
									* TrafficSimulationView.INTERSECTION_SIZE > simView
									.getFrame().getWidth()) {
								JOptionPane.showMessageDialog(null,
										"Size of Intersection exceeds panel!",
										"Warning", JOptionPane.ERROR_MESSAGE);
								return;
							}

							Intersection newIntersection = new Intersection();
							newIntersection.setBounds(intersection.getX()
									+ intersection.getWidth()
									+ TrafficSimulationView.SEGMENT_LENGTH,
									intersection.getY(),
									TrafficSimulationView.INTERSECTION_SIZE,
									TrafficSimulationView.INTERSECTION_SIZE);

							// connect segments to new intersection
							newIntersection.setSegmentVestIn(intersection
									.getSegmentEastOut());
							newIntersection.setSegmentVestOut(intersection
									.getSegmentEastIn());

							simView.getIntersectionButtons().add(
									newIntersection);
							simView.getPanelSimulation().add(newIntersection);
							break;
						}
					}

					// VEST
					if (intersection.getSegmentVestIn() != null
							|| intersection.getSegmentVestOut() != null) {
						if ((simView.getxClick() >= (intersection.getX()
								- TrafficSimulationView.SEGMENT_LENGTH - 20))
								&& (simView.getxClick() <= (intersection.getX() - TrafficSimulationView.SEGMENT_LENGTH))
								&& (simView.getyClick() >= (intersection.getY() + 20))
								&& (simView.getyClick() <= (intersection.getY()
										+ intersection.getHeight() - 20))) {

							// check intersection size
							if (intersection.getX()
									- TrafficSimulationView.SEGMENT_LENGTH
									- TrafficSimulationView.INTERSECTION_SIZE < 0) {
								JOptionPane.showMessageDialog(null,
										"Size of Intersection exceeds panel!",
										"Warning", JOptionPane.ERROR_MESSAGE);
								return;
							}

							Intersection newIntersection = new Intersection();
							newIntersection.setBounds(intersection.getX()
									- intersection.getWidth()
									- TrafficSimulationView.SEGMENT_LENGTH,
									intersection.getY(),
									TrafficSimulationView.INTERSECTION_SIZE,
									TrafficSimulationView.INTERSECTION_SIZE);

							// connect segments to new intersection
							newIntersection.setSegmentEastIn(intersection
									.getSegmentVestOut());
							newIntersection.setSegmentEastOut(intersection
									.getSegmentVestIn());

							simView.getIntersectionButtons().add(
									newIntersection);
							simView.getPanelSimulation().add(newIntersection);
							break;
						}
					}
				}
			}
		}

	}

	class SimulationStartListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// Add traffic lights to intersections
			for (Intersection intersection : simView.getIntersectionButtons()) {
				// NORTH
				if (intersection.getSegmentNorthIn() != null) {
					JTextField trafficLight = new JTextField();
					trafficLight.setBackground(Color.RED);
					trafficLight.setEditable(false);
					trafficLight.setBounds(intersection.getX(),
							intersection.getY() - 20,
							TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
							TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
					simView.getPanelSimulation().add(trafficLight);

					simView.getPanelSimulation().repaint();
				}
				// SOUTH
				if (intersection.getSegmentSouthIn() != null) {
					JTextField trafficLight = new JTextField();
					trafficLight.setBackground(Color.RED);
					trafficLight.setEditable(false);
					trafficLight.setBounds(
							intersection.getX() + intersection.getWidth() - 15,
							intersection.getY() + intersection.getHeight() + 7,
							TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
							TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
					simView.getPanelSimulation().add(trafficLight);

					simView.getPanelSimulation().repaint();
				}
				// VEST
				if (intersection.getSegmentVestIn() != null) {
					JTextField trafficLight = new JTextField();
					trafficLight.setBackground(Color.RED);
					trafficLight.setEditable(false);
					trafficLight
							.setBounds(
									intersection.getX() - 20,
									intersection.getY()
											+ intersection.getHeight() - 15,
									TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
									TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
					simView.getPanelSimulation().add(trafficLight);

					simView.getPanelSimulation().repaint();
				}
				// EAST
				if (intersection.getSegmentEastIn() != null) {
					JTextField trafficLight = new JTextField();
					trafficLight.setBackground(Color.RED);
					trafficLight.setEditable(false);
					trafficLight.setBounds(
							intersection.getX() + intersection.getWidth() + 7,
							intersection.getY(),
							TrafficSimulationView.TRAFFIC_LIGHT_SIZE,
							TrafficSimulationView.TRAFFIC_LIGHT_SIZE);
					simView.getPanelSimulation().add(trafficLight);

					simView.getPanelSimulation().repaint();
				}
			}

			simView.simulate();

		}

	}
}
