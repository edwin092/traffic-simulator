package com.utcn.application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import com.utcn.bl.EnvironmentSetup;
import com.utcn.controllers.TrafficSimulationController;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.utils.TrafficSimulationUtil;

public class TrafficSimulationView {

	// public static final int SEGMENT_LENGTH = 100;
	// public static final int SEGMENT_WIDTH = 40;
	public static final int INTERSECTION_SIZE = 60;
	public static final int INTERSECTION_CLICK_SIZE = 20;
	public static final int TRAFFIC_LIGHT_SIZE = 15;

	private JFrame frame;
	private EnvironmentSetup environmentSetup;
	private List<JLabel> vehicleLabels;
	private int xClick;
	private int yClick;
	private boolean isIntersectionSelected;
	private boolean isSegmentSelected;
	private JPanel panelSimulation;
	private JMenuItem startMenuItem;

	private int currentSegement = 1;

	private List<Intersection> intersectionButtons = new ArrayList<>();
	private List<Segment> segmentButtons = new ArrayList<>();

	private Map<Integer, List<Integer>> segmentCoordsX = new HashMap<>();
	private Map<Integer, List<Integer>> segmentCoordsY = new HashMap<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			TrafficSimulationView window = new TrafficSimulationView();
			TrafficSimulationController controller = new TrafficSimulationController(
					window);
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public TrafficSimulationView() {
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

		startMenuItem = new JMenuItem("Start");
		// startMenuItem.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		//
		// SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		// @Override
		// public Void doInBackground() {
		// simulate();
		// return null;
		// }
		// };
		//
		// worker.execute();
		// }
		// });
		mnSimulation.add(startMenuItem);

		JMenu mnComponents = new JMenu("Components");
		menuBar.add(mnComponents);

		JMenuItem mntmSegment = new JMenuItem("Segment");
		mntmSegment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isSegmentSelected = true;

				panelSimulation.repaint();
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

		JMenu mnImport = new JMenu("Import");
		menuBar.add(mnImport);

		JMenuItem mntmFromXml = new JMenuItem("From XML");
		mntmFromXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
				// ADD XML import here
			}
		});
		mnImport.add(mntmFromXml);

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

		panelSimulation = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				for (Integer key : segmentCoordsX.keySet()) {
					List<Integer> xCoords = segmentCoordsX.get(key);
					List<Integer> yCoords = segmentCoordsY.get(key);

					int[] x = TrafficSimulationUtil.convertList(xCoords);
					int[] y = TrafficSimulationUtil.convertList(yCoords);

					// draw polyline
					g.drawPolyline(x, y, x.length);
				}
			}
		};

		scrollPaneSimulation.setViewportView(panelSimulation);
		panelSimulation.setLayout(null);
		panelSimulation.setPreferredSize(new Dimension(1500, 1500));

		frame.getContentPane().setLayout(null);

		// JLabel lblO = new JLabel("O");
		// segment1.add(lblO);
		// lblO.setBounds(1, 13, 10, 10);

	}

	/**
	 * 
	 */
	public void saveXYValues(Integer x, Integer y) {
		List<Integer> xList = segmentCoordsX.get(currentSegement);
		List<Integer> yList = segmentCoordsY.get(currentSegement);

		if (xList == null) {
			// segmentCoordsX.put(currentSegement, new ArrayList<Integer>());
			// segmentCoordsY.put(currentSegement, new ArrayList<Integer>());

			List<Integer> newXList = new ArrayList<Integer>();
			List<Integer> newYList = new ArrayList<Integer>();

			if (x == null && y == null) {
				newXList.add(xClick);
				newYList.add(yClick);
			} else {
				newXList.add(x);
				newYList.add(y);
			}

			segmentCoordsX.put(currentSegement, newXList);
			segmentCoordsY.put(currentSegement, newYList);
		} else {
			if (x == null && y == null) {
				xList.add(xClick);
				yList.add(yClick);
			} else {
				xList.add(x);
				yList.add(y);
			}

			segmentCoordsX.put(currentSegement, xList);
			segmentCoordsY.put(currentSegement, yList);
		}
	}

	public void simulate() {
		int globalCounter = 0;
		int vehDest = 0;
		// do {
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
		// } while (globalCounter < 200);
	}

	public void addMouseClickListener(MouseListener mouseListener) {
		panelSimulation.addMouseListener(mouseListener);
	}

	public void addSimulationStartListener(ActionListener actionListener) {
		startMenuItem.addActionListener(actionListener);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public EnvironmentSetup getEnvironmentSetup() {
		return environmentSetup;
	}

	public void setEnvironmentSetup(EnvironmentSetup environmentSetup) {
		this.environmentSetup = environmentSetup;
	}

	public List<JLabel> getVehicleLabels() {
		return vehicleLabels;
	}

	public void setVehicleLabels(List<JLabel> vehicleLabels) {
		this.vehicleLabels = vehicleLabels;
	}

	public int getxClick() {
		return xClick;
	}

	public void setxClick(int xClick) {
		this.xClick = xClick;
	}

	public int getyClick() {
		return yClick;
	}

	public void setyClick(int yClick) {
		this.yClick = yClick;
	}

	public boolean isIntersectionSelected() {
		return isIntersectionSelected;
	}

	public void setIntersectionSelected(boolean isIntersectionSelected) {
		this.isIntersectionSelected = isIntersectionSelected;
	}

	public boolean isSegmentSelected() {
		return isSegmentSelected;
	}

	public void setSegmentSelected(boolean isSegmentSelected) {
		this.isSegmentSelected = isSegmentSelected;
	}

	public List<Intersection> getIntersectionButtons() {
		return intersectionButtons;
	}

	public void setIntersectionButtons(List<Intersection> intersectionButtons) {
		this.intersectionButtons = intersectionButtons;
	}

	public List<Segment> getSegmentButtons() {
		return segmentButtons;
	}

	public void setSegmentButtons(List<Segment> segmentButtons) {
		this.segmentButtons = segmentButtons;
	}

	public JPanel getPanelSimulation() {
		return panelSimulation;
	}

	public void setPanelSimulation(JPanel panelSimulation) {
		this.panelSimulation = panelSimulation;
	}

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

	public int getCurrentSegement() {
		return currentSegement;
	}

	public void setCurrentSegement(int currentSegement) {
		this.currentSegement = currentSegement;
	}

	public void incrementCurrentSegement() {
		this.currentSegement++;
	}

	public List<Integer> getCurrentSegmentXCoords() {
		return segmentCoordsX.get(currentSegement);
	}

	public List<Integer> getCurrentSegmentYCoords() {
		return segmentCoordsY.get(currentSegement);
	}
}
