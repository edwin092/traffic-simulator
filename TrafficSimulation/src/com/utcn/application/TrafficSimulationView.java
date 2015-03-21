package com.utcn.application;

import com.utcn.bl.EnvironmentSetup;
import com.utcn.controllers.TrafficSimulationController;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.models.Vehicle;
import com.utcn.utils.TrafficSimulationUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrafficSimulationView {

    // public static final int SEGMENT_LENGTH = 100;
    // public static final int SEGMENT_WIDTH = 40;
    public static final int SIMULATION_TIME = 200;
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
    private StyledDocument textPaneSimulationLog;
    private JMenuItem startMenuItem;

    private int currentSegment = 1;

    private int currentSegId = 1;
    private int currentIntersId = 1;

    private List<Intersection> intersectionButtons = new ArrayList<>();
    private List<Segment> segments = new ArrayList<>();

    private Map<Integer, List<Integer>> segmentCoordsX = new HashMap<>();
    private Map<Integer, List<Integer>> segmentCoordsY = new HashMap<>();

    private List<JLabel> labels;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        try {
            TrafficSimulationView window = new TrafficSimulationView();
            new TrafficSimulationController(window);
            window.frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the application.
     */
    public TrafficSimulationView() {
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
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
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
        textPaneLog.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textPaneLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPaneLog.setViewportView(textPaneLog);

        textPaneSimulationLog = textPaneLog.getStyledDocument();

        try {
            textPaneSimulationLog.insertString(textPaneSimulationLog.getLength(), "Simulation Log", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

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

                    for (Intersection intersection : intersectionButtons) {
                        panelSimulation.add(intersection);
                    }

                    if (labels != null) {
                        for (JLabel lable : labels) {
                            panelSimulation.add(lable);
                            panelSimulation.setComponentZOrder(lable, 0);
                        }
                    }
                }
            }
        };

        scrollPaneSimulation.setViewportView(panelSimulation);
        panelSimulation.setLayout(null);
        panelSimulation.setPreferredSize(new Dimension(1500, 1500));

        frame.getContentPane().setLayout(null);
    }

    /**
     * Save current x, y coordinates for the polylines.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public void saveXYValues(Integer x, Integer y) {
        List<Integer> xList = segmentCoordsX.get(currentSegment);
        List<Integer> yList = segmentCoordsY.get(currentSegment);

        if (xList == null) {
            xList = new ArrayList<Integer>();
            yList = new ArrayList<Integer>();
        }

        if (x == null && y == null) {
            xList.add(xClick);
            yList.add(yClick);
        } else {
            xList.add(x);
            yList.add(y);
        }

        segmentCoordsX.put(currentSegment, xList);
        segmentCoordsY.put(currentSegment, yList);
    }

    /**
     * Adds a new log entry in the logging area.
     *
     * @param text the text to be added
     */
    public void addNewLogEntry(String text) {
        try {
            textPaneSimulationLog.insertString(textPaneSimulationLog.getLength(), text, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the logging area.
     */
    public void clearLogArea() {
        try {
            textPaneSimulationLog.remove(0, textPaneSimulationLog.getLength());
            addNewLogEntry("Simulation Log");
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the simulation.
     */
    public synchronized void simulate() {
        clearLogArea();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        addNewLogEntry("\nSimulation started at " + dateFormat.format(date) + "\n\n");

        // creates a new environment
        environmentSetup = new EnvironmentSetup(segments, intersectionButtons,
                false);

        labels = new CopyOnWriteArrayList<>();

        int globalCounter = 1;
        int vehDest = 0;
        do {
            addNewLogEntry("\n--------------------------------------\nCounter: " + globalCounter +
                    "\n--------------------------------------\n");
            labels.clear();

            // generate new vehicle
            environmentSetup.generateVehicle();
            // segment acceleration
            environmentSetup.checkSegments(vehDest);
            // manage intersections traffic lights
            environmentSetup.manageIntersectionsTrafficLights();

            for (Segment segment : segments) {

                addNewLogEntry("\nSegment " + segment.getId() + ":" + "\n");

                for (Vehicle veh : segment.getVehicles()) {

                    addNewLogEntry("\nVehicle: \nCurrent distance: " + veh.getCurrentDistance() + "\nCurrent speed: "
                            + veh.getSpeed() + "\nDistance to next obstacle: " + veh.getDistanceToObstacle() + "\n");

                    JLabel lblO = new JLabel("O");

                    int[] lineCoordsX = veh.getCurrentSegment()
                            .getLineCoordsX();
                    int[] lineCoordsY = veh.getCurrentSegment()
                            .getLineCoordsY();

                    int segSize = 0;
                    for (int i = 0; i < lineCoordsX.length - 1; i++) {

                        segSize += TrafficSimulationUtil.distanceBetweenPoints(
                                lineCoordsX[i], lineCoordsY[i],
                                lineCoordsX[i + 1], lineCoordsY[i + 1]);

                        double currentDistInPixels = TrafficSimulationUtil.convertMetersToPixels(veh.getCurrentDistance());

                        if (currentDistInPixels <= segSize) {
                            int[] newValues = TrafficSimulationUtil.getVehiclePosition(
                                    lineCoordsX[i], lineCoordsY[i], lineCoordsX[i + 1], lineCoordsY[i + 1], currentDistInPixels);

                            lblO.setBounds(newValues[0], newValues[1], 10, 10);

                            labels.add(lblO);

                            panelSimulation.removeAll();
                            panelSimulation.revalidate();
                            panelSimulation.repaint();
                        }
                    }
                }
            }

            globalCounter++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (globalCounter < SIMULATION_TIME);

        addNewLogEntry("\nSimulation finished at " + dateFormat.format(date) + "\n\n");
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

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public JPanel getPanelSimulation() {
        return panelSimulation;
    }

    public void setPanelSimulation(JPanel panelSimulation) {
        this.panelSimulation = panelSimulation;
    }

    public StyledDocument getTextPaneSimulationLog() {
        return textPaneSimulationLog;
    }

    public void setTextPaneSimulationLog(StyledDocument textPaneSimulationLog) {
        this.textPaneSimulationLog = textPaneSimulationLog;
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

    public int getCurrentSegment() {
        return currentSegment;
    }

    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
    }

    public void incrementCurrentSegement() {
        this.currentSegment++;
    }

    public int getCurrentSegId() {
        return currentSegId;
    }

    public void setCurrentSegId(int currentSegId) {
        this.currentSegId = currentSegId;
    }

    public void incrementCurrentSegId() {
        this.currentSegId++;
    }

    public int getCurrentIntersId() {
        return currentIntersId;
    }

    public void setCurrentIntersId(int currentIntersId) {
        this.currentIntersId = currentIntersId;
    }

    public void incrementCurrentIntersId() {
        this.currentIntersId++;
    }

    public List<Integer> getCurrentSegmentXCoords() {
        return segmentCoordsX.get(currentSegment);
    }

    public List<Integer> getCurrentSegmentYCoords() {
        return segmentCoordsY.get(currentSegment);
    }
}
