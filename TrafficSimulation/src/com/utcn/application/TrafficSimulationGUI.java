package com.utcn.application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import com.utcn.bl.EnvironmentSetup;

public class TrafficSimulationGUI {

	private JFrame frame;
	private EnvironmentSetup environmentSetup;
	private List<JLabel> vehicleLabels;
	private int xClick;
	private int yClick;
	private boolean isIntersectionSelected;
	private boolean isSegmentSelected;
	private List<JButton> intersectionButtons = new ArrayList<JButton>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			TrafficSimulationGUI window = new TrafficSimulationGUI();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public TrafficSimulationGUI() {
		environmentSetup = new EnvironmentSetup();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1337, 803);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		JMenu mnSimulation = new JMenu("Simulation");
		menuBar.add(mnSimulation);

		JMenuItem mntmNewSimulation = new JMenuItem("New Simulation");
		mnSimulation.add(mntmNewSimulation);

		JMenuItem startMenuItem = new JMenuItem("Start");
		startMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SwingWorker worker = new SwingWorker<Void, Void>() {
					@Override
					public Void doInBackground() {
						simulate();
						return null;
					}
				};

				worker.execute();
			}
		});
		mnSimulation.add(startMenuItem);

		JMenu mnComponents = new JMenu("Components");
		menuBar.add(mnComponents);

		JMenuItem mntmSegment = new JMenuItem("Segment");
		mntmSegment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isSegmentSelected = true;
			}
		});
		mnComponents.add(mntmSegment);

		JMenuItem mntmIntersection = new JMenuItem("Intersection");
		mntmIntersection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isIntersectionSelected = true;
			}
		});
		mnComponents.add(mntmIntersection);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(10, 11, 1301, 721);
		splitPane.setDividerLocation(800);
		frame.getContentPane().add(splitPane);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		splitPane_1.setDividerLocation(350);

		JScrollPane scrollPaneLog = new JScrollPane();
		scrollPaneLog
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneLog
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		splitPane_1.setLeftComponent(scrollPaneLog);
		scrollPaneLog.setBounds(50, 30, 300, 500);

		JTextPane textPaneLog = new JTextPane();
		scrollPaneLog.setViewportView(textPaneLog);

		JScrollPane scrollPaneStatistics = new JScrollPane();
		scrollPaneStatistics
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneStatistics
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		splitPane_1.setRightComponent(scrollPaneStatistics);

		JPanel panelStatistics = new JPanel();
		scrollPaneStatistics.setViewportView(panelStatistics);
		panelStatistics.setLayout(null);
		panelStatistics.setPreferredSize(new Dimension(500, 500));

		JScrollPane scrollPaneSimulation = new JScrollPane();
		scrollPaneSimulation
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneSimulation
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		splitPane.setLeftComponent(scrollPaneSimulation);

		final JPanel panelSimulation = new JPanel();
		panelSimulation.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				xClick = e.getX();
				yClick = e.getY();

				if (isSegmentSelected) {
					for (JButton intersection : intersectionButtons) {

						// North
						if ((xClick >= intersection.getX() + 20
								&& xClick <= (intersection.getX() + intersection
										.getWidth()) - 20 && (yClick <= intersection
								.getY() && yClick >= intersection.getY() - 20))) {
							JPanel selectionPanel = new JPanel();
							// selectionPanel.setLayout(new
							// BoxLayout(selectionPanel, BoxLayout.LINE_AXIS));

							JTextField lengthField = new JTextField(5);

							selectionPanel.add(new JLabel("Length:"));
							selectionPanel.add(lengthField);
							selectionPanel.add(Box.createHorizontalStrut(15));

							selectionPanel.add(new JLabel("Direction:"));

							ButtonGroup buttonGroup = new ButtonGroup();

							JCheckBox inCheckBox = new JCheckBox(
									"--> Intersection");
							buttonGroup.add(inCheckBox);
							// chckbxNewCheckBox.setBounds(91, 188, 122, 23);
							selectionPanel.add(inCheckBox);

							JCheckBox outCheckBox = new JCheckBox(
									"Intersection -->");
							buttonGroup.add(outCheckBox);
							// chckbxNewCheckBox_1.setBounds(91, 230, 122, 23);
							selectionPanel.add(outCheckBox);

							int result = JOptionPane.showConfirmDialog(null,
									selectionPanel,
									"Please Enter Direction and Length Values",
									JOptionPane.OK_CANCEL_OPTION);

							if (result == JOptionPane.OK_OPTION) {
								int length = Integer.parseInt(lengthField
										.getText());

								if (outCheckBox.isSelected()) {
									JButton segment = new JButton("");
									segment.setBounds(intersection.getX() + 60,
											intersection.getY() - length, 40,
											length);
									segment.setLayout(null);

									panelSimulation.add(segment);

									panelSimulation.repaint();
								} else if (inCheckBox.isSelected()) {

								} else {

								}
							}
						}

						// South
						if ((xClick >= intersection.getX() + 20
								&& xClick <= (intersection.getX() + intersection
										.getWidth()) - 20 && (yClick >= (intersection
								.getHeight() + intersection.getY()) && yClick <= (intersection
								.getHeight() + intersection.getY()) + 20))) {
							System.out.println("South");
						}

						// Vest
						if ((xClick <= intersection.getX()
								&& xClick >= intersection.getX() - 20 && (yClick >= intersection
								.getY() + 20 && yClick <= (intersection
								.getHeight() + intersection.getY()) - 20))) {
							System.out.println("Vest");
						}

						// East
						if ((xClick >= (intersection.getX() + intersection
								.getWidth())
								&& xClick <= (intersection.getX() + intersection
										.getWidth()) + 20 && (yClick >= intersection
								.getY() + 20 && yClick <= (intersection
								.getHeight() + intersection.getY()) - 20))) {
							System.out.println("East");
						}

						break;
					}

					panelSimulation.repaint();
				}

				if (isIntersectionSelected) {
					JButton intersection = new JButton("");
					intersection.setBounds(xClick, yClick, 120, 120);
					intersectionButtons.add(intersection);
					panelSimulation.add(intersection);

					panelSimulation.repaint();
					isIntersectionSelected = false;
				}

			}
		});
		scrollPaneSimulation.setViewportView(panelSimulation);
		panelSimulation.setLayout(null);
		panelSimulation.setPreferredSize(new Dimension(1500, 1500));

		frame.getContentPane().setLayout(null);

		// JLabel lblO = new JLabel("O");
		// segment1.add(lblO);
		// lblO.setBounds(1, 13, 10, 10);

	}

	private void simulate() {
		int globalCounter = 0;
		int vehDest = 0;
		do {
			// segment1.removeAll();
			// segment1.revalidate();
			// segment1.repaint();
			// segment2.removeAll();
			// segment2.revalidate();
			// segment2.repaint();

			// environmentSetup.generateVehicle();
			// environmentSetup.checkSegments(vehDest);
			// environmentSetup.checkIntersections();
			//
			// for (int i = 0; i < environmentSetup.getSegments().size(); i++) {
			// for (Vehicle veh : environmentSetup.getSegments().get(i)
			// .getVehicles()) {
			// JLabel lblO = new JLabel("O");
			// if (i == 0) {
			// segment1.add(lblO);
			// } else {
			// segment2.add(lblO);
			// }
			// lblO.setBounds((veh.getCurrentDistance() * 15), 13, 10, 10);
			// }
			// }
			// System.out.println(globalCounter);
			// globalCounter++;
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		} while (globalCounter < 200);
	}
}
