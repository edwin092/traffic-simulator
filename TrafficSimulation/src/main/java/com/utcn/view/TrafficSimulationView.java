package com.utcn.view;

import com.utcn.bl.EnvironmentSetup;
import com.utcn.bl.VehicleGenerator;
import com.utcn.controllers.TrafficSimulationController;
import com.utcn.flow.TrafficFlow;
import com.utcn.models.Intersection;
import com.utcn.models.Segment;
import com.utcn.models.Vehicle;
import com.utcn.optimization.TrafficLightsOptimization;
import com.utcn.utils.ImportExportHelper;
import com.utcn.utils.SimulationGraph;
import com.utcn.utils.TrafficSimulationUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrafficSimulationView {

    public static final String NO_CONFIG_FILE_MSG = "No config file added";
    public static final String CONFIG_FILE_LOADED_MSG = "Config file NAME added";

    public static final int INTERSECTION_SIZE = 60;
    public static final int INTERSECTION_CLICK_SIZE = 20;
    public static final int TRAFFIC_LIGHT_SIZE = 5;
    public static final int SIMULATION_STEP_DEFAULT = 1;
    public static final int SIMULATION_TIME_DEFAULT = 200;
    public static final int GRID_SIZE_METERS = 500;

    private int simulationStep = SIMULATION_STEP_DEFAULT;
    private int simulationTime = SIMULATION_TIME_DEFAULT;

    private JFrame frame;
    private EnvironmentSetup environmentSetup;
    private List<JLabel> vehicleLabels;
    private int xClick;
    private int yClick;
    private boolean isIntersectionSelected;
    private boolean isSegmentSelected;
    private boolean isSimulationPaused;
    // simulation panel
    private JPanel panelSimulation;
    // text log areas
    private static StyledDocument textPaneSimulationLog;
    private static StyledDocument textPaneStatisticsLog;
    // menu items
    private JMenuItem startMenuItem;
    private JMenuItem pauseMenuItem;
    private JMenuItem resumeMenuItem;
    // text labels
    private JLabel lblCounter;
    private JLabel lblStep;
    private JLabel lblSimulationTime;
    private JLabel lblConfigFileText;
    // components ids
    private int currentSegment = 1;
    private int currentSegId = 1;
    private int currentIntersId = 1;
    // components
    private List<Intersection> intersections = new ArrayList<>();
    private List<Segment> segments = new ArrayList<>();
    private List<TrafficFlow> trafficFlows;

    private Map<Integer, List<Integer>> segmentCoordsX = new HashMap<>();
    private Map<Integer, List<Integer>> segmentCoordsY = new HashMap<>();

//
//    @JsonIgnore
//    private List<JLabel> labels;

//    private BufferedImage image;

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
        frame.setBounds(100, 100, 1337, 815);
        frame.setTitle("Traffic Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmToXml = new JMenuItem("Export to JSON");
        mntmToXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
                fc.setAcceptAllFileFilterUsed(false);
                fc.showOpenDialog(null);

                if (fc.getSelectedFile() != null) {
                    boolean res = ImportExportHelper.exportEnvironmentToJSON(fc.getSelectedFile().getPath(),
                            getTrafficSimulationViewInstance());

                    if (!res) {
                        JOptionPane.showMessageDialog(frame,
                                "Export failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        mnFile.add(mntmToXml);

        JMenuItem mntmFromXml = new JMenuItem("Import from JSON");
        mntmFromXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter(
                        "JSON files (*.json)", "json");
                fc.setFileFilter(jsonFilter);
                fc.setCurrentDirectory(new File("."));
                fc.showOpenDialog(null);

                if (fc.getSelectedFile() != null) {
                    boolean res = ImportExportHelper.importEnvironmentFromJSON(fc.getSelectedFile().getAbsolutePath(),
                            getTrafficSimulationViewInstance());
                    if (!res) {
                        JOptionPane.showMessageDialog(frame,
                                "Import failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    panelSimulation.repaint();
                }
            }
        });
        mnFile.add(mntmFromXml);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnFile.add(mntmExit);

        JMenu mnSimulation = new JMenu("Simulation");
        menuBar.add(mnSimulation);

        JMenuItem newSimMenuItem = new JMenuItem("New");
        newSimMenuItem.setToolTipText("Create a new simulation. Reset current environment.");
        newSimMenuItem.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                isSimulationPaused = true;
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want a new simulation? \nAll current progress will be deleted!");
                if (reply == JOptionPane.YES_OPTION) {
                    resetSimulationPanel();
                } else {
                    isSimulationPaused = false;
                }
            }
        });
        mnSimulation.add(newSimMenuItem);

        JMenuItem generateConfigMenuItem = new JMenuItem("Generate config file");
        generateConfigMenuItem.setToolTipText("Create a JSON config file for traffic flows.");
        generateConfigMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TrafficFlowGeneratorView(currentIntersId - 1).setVisible(true);
            }
        });
        mnSimulation.add(generateConfigMenuItem);

        JMenuItem addConfigMenuItem = new JMenuItem("Add config file");
        addConfigMenuItem.setToolTipText("Add a JSON file containing traffic flows.");
        addConfigMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter(
                        "JSON files (*.json)", "json");
                fc.setFileFilter(jsonFilter);
                fc.setCurrentDirectory(new File("."));
                fc.showOpenDialog(null);

                if (fc.getSelectedFile() != null) {
                    boolean res = ImportExportHelper.importFlowFromJSON(fc.getSelectedFile().getAbsolutePath(),
                            getTrafficSimulationViewInstance());
                    if (res) {
                        lblConfigFileText.setText(CONFIG_FILE_LOADED_MSG.replace("NAME", fc.getSelectedFile().getName()));
                        lblConfigFileText.setForeground(Color.BLUE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Import failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        mnSimulation.add(addConfigMenuItem);

        startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    public Void doInBackground() {
                        isSimulationPaused = false;
                        // enable Pause/Resume menu items
                        pauseMenuItem.setEnabled(true);
                        resumeMenuItem.setEnabled(true);
                        // Request simulation time from user
                        requestSimulationTime();
                        // Request simulation step from user
                        requestSimulationStep();
                        // update labels
                        lblSimulationTime.setText(String.valueOf(simulationTime));
                        lblStep.setText(String.valueOf(simulationStep));
                        // Start the simulation
                        simulate();

                        return null;
                    }
                };

                worker.execute();
            }
        });
        mnSimulation.add(startMenuItem);

        pauseMenuItem = new JMenuItem("Pause");
        pauseMenuItem.setEnabled(false);
        pauseMenuItem.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                isSimulationPaused = true;

                addNewSimulationLogEntry("\n\nSimulation paused!");
            }
        });
        mnSimulation.add(pauseMenuItem);

        resumeMenuItem = new JMenuItem("Resume");
        resumeMenuItem.setEnabled(false);
        resumeMenuItem.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                isSimulationPaused = false;
                resumeSimulation();

                addNewSimulationLogEntry("\n\nSimulation resumed!");
            }
        });
        mnSimulation.add(resumeMenuItem);

        JMenu mnComponents = new JMenu("Components");
        menuBar.add(mnComponents);

        JMenuItem mntmClear = new JMenuItem("Clear all");
        mntmClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearComponents();
            }
        });
        mnComponents.add(mntmClear);

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

        // Help Menu
        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        JMenuItem mntmAbout = new JMenuItem("About");
        mntmAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Bogdan & Edwin");
            }
        });
        mnHelp.add(mntmAbout);
        // End Help Menu

        JSplitPane splitPane = new JSplitPane();
        splitPane.setBounds(10, 33, 1301, 710);
        splitPane.setDividerLocation(800);
        frame.getContentPane().add(splitPane);

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setRightComponent(splitPane_1);
        splitPane_1.setDividerLocation(350);

        // Simulation Log Panel
        JScrollPane scrollPaneLog = new JScrollPane();
        scrollPaneLog
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneLog
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        splitPane_1.setLeftComponent(scrollPaneLog);
        scrollPaneLog.setBounds(50, 30, 300, 500);

        JTextPane textPaneLog = new JTextPane();
        textPaneLog.setEditable(false);
//        DefaultCaret caret = (DefaultCaret) textPaneLog.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPaneLog.setViewportView(textPaneLog);

        textPaneSimulationLog = textPaneLog.getStyledDocument();
        addNewSimulationLogEntry("Simulation Log");
        // End Simulation Log Panel

        // Statistics Panel
        JScrollPane scrollPaneStatistics = new JScrollPane();
        scrollPaneStatistics
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneStatistics
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        splitPane_1.setRightComponent(scrollPaneStatistics);

        JTextPane textPaneLogStatistics = new JTextPane();
        textPaneLogStatistics.setEditable(false);
        scrollPaneStatistics.setViewportView(textPaneLogStatistics);

        textPaneStatisticsLog = textPaneLogStatistics.getStyledDocument();

        try {
            textPaneStatisticsLog.insertString(textPaneStatisticsLog.getLength(), "Statistics", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        // End Statistics Panel

        // Simulation Panel
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
//                g.drawImage(image, 0, 0, null);

//                JLabel picLabel = new JLabel(new ImageIcon(image));
//                picLabel.setBounds(5, 5, 10, 10);
//                this.add(picLabel);

                createGridForSimulation(g, this.getWidth(), this.getHeight());
                addTrafficLightsToSimulation();

                for (Intersection intersection : intersections) {

                    this.add(intersection);

                    JLabel lab = new JLabel(String.valueOf(intersection.getId()));

                    int labX = intersection.getX() + INTERSECTION_SIZE / 2 - 7;
                    int labY = intersection.getY() + INTERSECTION_SIZE / 2 - 7;

                    lab.setBounds(labX, labY, 15, 15);
                    panelSimulation.add(lab);
                    panelSimulation.setComponentZOrder(lab, 0);

//                    panelSimulation.setComponentZOrder(lab, 1);
                }

                if (vehicleLabels != null) {
                    for (JLabel label : vehicleLabels) {
                        this.add(label);
                        this.setComponentZOrder(label, 0);
                    }
                }

                Color lineColor = new Color(0, 0, 0, 1f);
                g.setColor(lineColor);

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

//        try {
//            image = ImageIO.read(new File("C:\\Users\\edWin\\Desktop\\photo.jpg"));
//        } catch (IOException ex) {
//            // handle exception...
//        }


        scrollPaneSimulation.setViewportView(panelSimulation);
        panelSimulation.setLayout(null);
        panelSimulation.setPreferredSize(new Dimension(1500, 1500));

        frame.getContentPane().setLayout(null);

        JLabel lblCounterText = new JLabel("Counter:");
        lblCounterText.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblCounterText.setBounds(10, 4, 60, 22);
        frame.getContentPane().add(lblCounterText);

        lblCounter = new JLabel("0");
        lblCounter.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblCounter.setForeground(Color.RED);
        lblCounter.setBounds(70, 4, 46, 22);
        frame.getContentPane().add(lblCounter);

        JLabel lblSimulationTimeText = new JLabel("Simulation Time:");
        lblSimulationTimeText.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSimulationTimeText.setBounds(120, 9, 118, 14);
        frame.getContentPane().add(lblSimulationTimeText);

        lblSimulationTime = new JLabel("0");
        lblSimulationTime.setForeground(Color.BLUE);
        lblSimulationTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblSimulationTime.setBounds(220, 4, 46, 22);
        frame.getContentPane().add(lblSimulationTime);

        JLabel lblStepText = new JLabel("Step:");
        lblStepText.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblStepText.setBounds(260, 9, 53, 14);
        frame.getContentPane().add(lblStepText);

        lblStep = new JLabel("0");
        lblStep.setForeground(Color.BLUE);
        lblStep.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblStep.setBounds(295, 4, 46, 22);
        frame.getContentPane().add(lblStep);

        lblConfigFileText = new JLabel(NO_CONFIG_FILE_MSG);
        lblConfigFileText.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblConfigFileText.setForeground(Color.RED);
        lblConfigFileText.setBounds(340, 9, 153, 14);
        frame.getContentPane().add(lblConfigFileText);
    }

    /**
     * Reset text from each text label.
     */
    private void resetAllTextLabels() {
        lblCounter.setText("0");
        lblSimulationTime.setText("0");
        lblStep.setText("0");

        lblConfigFileText.setText(NO_CONFIG_FILE_MSG);
        lblConfigFileText.setForeground(Color.RED);
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
            xList = new ArrayList<>();
            yList = new ArrayList<>();
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
     * Adds a new log entry in the simulation logging area.
     *
     * @param text the text to be added
     */
    public static void addNewSimulationLogEntry(String text) {
        try {
            textPaneSimulationLog.insertString(textPaneSimulationLog.getLength(), text, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the simulation logging area.
     */
    public void clearSimulationLogArea() {
        try {
            textPaneSimulationLog.remove(0, textPaneSimulationLog.getLength());
            addNewSimulationLogEntry("Simulation Log");
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new log entry in the statistics area.
     *
     * @param text the text to be added
     */
    public static void addNewStatisticsLogEntry(String text) {
        try {
            textPaneStatisticsLog.insertString(textPaneStatisticsLog.getLength(), text, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the statistics area.
     */
    public void clearStatisticsArea() {
        try {
            textPaneStatisticsLog.remove(0, textPaneStatisticsLog.getLength());
            addNewStatisticsLogEntry("Statistics");
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates grid for the simulation panel.
     *
     * @param g           Graphics component
     * @param panelWidth  the width of the panel
     * @param panelHeight the height of the panel
     */
    private void createGridForSimulation(Graphics g, int panelWidth, int panelHeight) {
        int gridSizeInPixels = (int) TrafficSimulationUtil.convertMetersToPixels(GRID_SIZE_METERS);

        int x = (panelWidth / gridSizeInPixels);
        int y = (panelHeight / gridSizeInPixels);

        Color gridColor = new Color(0, 0, 0, 0.2f);
        g.setColor(gridColor);

        // draw x lines
        for (int i = 1; i < x; i++) {
            g.drawLine(i * gridSizeInPixels, 0, i * gridSizeInPixels, panelHeight);
        }
        // draw y lines
        for (int i = 0; i < y; i++) {
            g.drawLine(0, i * gridSizeInPixels, panelWidth, i * gridSizeInPixels);
        }
    }

    /**
     * Requests the simulation time from the user.
     */
    private void requestSimulationTime() {
        simulationTime = 0;
        do {
            String result = JOptionPane.showInputDialog(frame, "Enter simulation time:");

            try {
                simulationTime = Integer.parseInt(result);
            } catch (NumberFormatException ignored) {
                // ignored exception
            }
        } while (simulationTime == 0);
    }

    /**
     * Requests the simulation step from the user.
     */
    private void requestSimulationStep() {
        Object[] options = {"1X",
                "5X",
                "Just Results"};
        int n = JOptionPane.showOptionDialog(frame,
                "What simulation step would you like to have?",
                "Simulation step",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        switch (n) {
            case 0:
                simulationStep = 1;
                break;
            case 1:
                simulationStep = 5;
                break;
            case 2:
                simulationStep = SIMULATION_TIME_DEFAULT;
                break;
            default:
                simulationStep = SIMULATION_STEP_DEFAULT;
        }
    }

    /**
     *
     */
    private void setPhaseTimeAndOrderForIntersections() {
        for (Intersection intersection : intersections) {
            if (intersection.isFourPhased()) {
                intersection.setPhaseTimes(TrafficLightsOptimization.getRandomTimeList());
                intersection.setPhaseOrder(TrafficLightsOptimization.getRandomPhaseOrderList());

                TrafficSimulationUtil.initIntersectionTrafficLights(intersection);
            } else {
                intersection.setAllLightsTrue();
            }
        }
    }

    /**
     * Resumes current simulation by notifying all threads.
     */
    private synchronized void resumeSimulation() {
        this.notifyAll();
    }

    /**
     * Start the simulation.
     */
    public synchronized void simulate() {
        clearSimulationLogArea();
        clearStatisticsArea();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        addNewSimulationLogEntry("\nSimulation started at " + dateFormat.format(date) + "\n");

        // set phase time and order for intersections
        setPhaseTimeAndOrderForIntersections();

        // creates a new environment
        environmentSetup = new EnvironmentSetup(segments, intersections,
                false);

        vehicleLabels = new CopyOnWriteArrayList<>();

        int globalCounter = 1;
        int vehDest = 0;

        SimulationGraph simulationGraph =
                TrafficSimulationUtil.convertSimulaionEnvironmentToGraph(getTrafficSimulationViewInstance());

        do {
            if (isSimulationPaused) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }

            lblCounter.setText(String.valueOf(globalCounter));

            vehicleLabels.clear();

            for (int i = 0; i < simulationStep; i++) {
                // check each flow
                for (TrafficFlow trafficFlow : trafficFlows) {
                    if (trafficFlow.getStartTime() <= globalCounter && trafficFlow.getEndTime() >= globalCounter) {
                        // generate new vehicle
                        environmentSetup.generateVehicle(simulationGraph, trafficFlow);
                    }
                }
                // segment acceleration
                environmentSetup.checkSegments(vehDest);
                // manage intersections traffic lights
                environmentSetup.manageIntersectionsTrafficLights();
            }

            for (Segment segment : segments) {

                for (Vehicle veh : segment.getVehicles()) {

                    JLabel lblO = new JLabel(String.valueOf(veh.getId()));

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

                            lblO.setBounds(newValues[0], newValues[1], 15, 10);

                            vehicleLabels.add(lblO);

                            panelSimulation.removeAll();
                            panelSimulation.revalidate();
                            panelSimulation.repaint();
                        }
                    }
                }
            }

            globalCounter += simulationStep;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        } while (globalCounter < simulationTime);

        lblCounter.setText(String.valueOf(simulationTime));
        addNewSimulationLogEntry("\nSimulation finished at " + dateFormat.format(date) + "\n\n");
    }

    /**
     * Adds traffic lights to the simulation panel.
     */
    private void addTrafficLightsToSimulation() {
        // Add traffic lights to intersections
        for (Intersection intersection : getIntersections()) {
            // NORTH
            // LEFT
            JTextField trafficLightLeft = new JTextField();
            trafficLightLeft.setBackground(intersection.getTrafficLightsNorth()[0] ? Color.GREEN : Color.RED);
            trafficLightLeft.setEditable(false);
            trafficLightLeft.setBounds(
                    intersection.getX() + (intersection.getWidth() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                    intersection.getY(),
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLightLeft);
            panelSimulation.setComponentZOrder(trafficLightLeft, 0);
            // STRAIGHT
            JTextField trafficLightStraight = new JTextField();
            trafficLightStraight.setBackground(intersection.getTrafficLightsNorth()[1] ? Color.GREEN : Color.RED);
            trafficLightStraight.setEditable(false);
            trafficLightStraight.setBounds(
                    intersection.getX() + (intersection.getWidth() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                    intersection.getY(),
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLightStraight);
            panelSimulation.setComponentZOrder(trafficLightStraight, 0);
            // RIGHT
            JTextField trafficLightRight = new JTextField();
            trafficLightRight.setBackground(intersection.getTrafficLightsNorth()[2] ? Color.GREEN : Color.RED);
            trafficLightRight.setEditable(false);
            trafficLightRight.setBounds(
                    intersection.getX() + (intersection.getWidth() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                    intersection.getY(),
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLightRight);
            panelSimulation.setComponentZOrder(trafficLightRight, 0);

            // SOUTH
            // LEFT
            JTextField trafficLight2Left = new JTextField();
            trafficLight2Left.setBackground(intersection.getTrafficLightsSouth()[0] ? Color.GREEN : Color.RED);
            trafficLight2Left.setEditable(false);
            trafficLight2Left.setBounds(
                    intersection.getX() + (intersection.getWidth() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                    intersection.getY() + intersection.getHeight() - TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight2Left);
            panelSimulation.setComponentZOrder(trafficLight2Left, 0);
            // STRAIGHT
            JTextField trafficLight2Straight = new JTextField();
            trafficLight2Straight.setBackground(intersection.getTrafficLightsSouth()[1] ? Color.GREEN : Color.RED);
            trafficLight2Straight.setEditable(false);
            trafficLight2Straight.setBounds(
                    intersection.getX() + (intersection.getWidth() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                    intersection.getY() + intersection.getHeight() - TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight2Straight);
            panelSimulation.setComponentZOrder(trafficLight2Straight, 0);
            // RIGHT
            JTextField trafficLight2Right = new JTextField();
            trafficLight2Right.setBackground(intersection.getTrafficLightsSouth()[2] ? Color.GREEN : Color.RED);
            trafficLight2Right.setEditable(false);
            trafficLight2Right.setBounds(
                    intersection.getX() + (intersection.getWidth() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                    intersection.getY() + intersection.getHeight() - TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight2Right);
            panelSimulation.setComponentZOrder(trafficLight2Right, 0);

            // VEST
            // LEFT
            JTextField trafficLight3Left = new JTextField();
            trafficLight3Left.setBackground(intersection.getTrafficLightsVest()[0] ? Color.GREEN : Color.RED);
            trafficLight3Left.setEditable(false);
            trafficLight3Left
                    .setBounds(
                            intersection.getX(),
                            intersection.getY() + (intersection.getHeight() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                            TRAFFIC_LIGHT_SIZE,
                            TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight3Left);
            panelSimulation.setComponentZOrder(trafficLight3Left, 0);
            // STRAIGHT
            JTextField trafficLight3Straight = new JTextField();
            trafficLight3Straight.setBackground(intersection.getTrafficLightsVest()[1] ? Color.GREEN : Color.RED);
            trafficLight3Straight.setEditable(false);
            trafficLight3Straight
                    .setBounds(
                            intersection.getX(),
                            intersection.getY() + (intersection.getHeight() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                            TRAFFIC_LIGHT_SIZE,
                            TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight3Straight);
            panelSimulation.setComponentZOrder(trafficLight3Straight, 0);
            // RIGHT
            JTextField trafficLight3Right = new JTextField();
            trafficLight3Right.setBackground(intersection.getTrafficLightsVest()[2] ? Color.GREEN : Color.RED);
            trafficLight3Right.setEditable(false);
            trafficLight3Right
                    .setBounds(
                            intersection.getX(),
                            intersection.getY() + (intersection.getHeight() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                            TRAFFIC_LIGHT_SIZE,
                            TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight3Right);
            panelSimulation.setComponentZOrder(trafficLight3Right, 0);

            // EAST
            // LEFT
            JTextField trafficLight4Left = new JTextField();
            trafficLight4Left.setBackground(intersection.getTrafficLightsEast()[0] ? Color.GREEN : Color.RED);
            trafficLight4Left.setEditable(false);
            trafficLight4Left.setBounds(
                    intersection.getX() + intersection.getWidth() - TRAFFIC_LIGHT_SIZE,
                    intersection.getY() + (intersection.getHeight() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight4Left);
            panelSimulation.setComponentZOrder(trafficLight4Left, 0);
            // STRAIGHT
            JTextField trafficLight4Straight = new JTextField();
            trafficLight4Straight.setBackground(intersection.getTrafficLightsEast()[1] ? Color.GREEN : Color.RED);
            trafficLight4Straight.setEditable(false);
            trafficLight4Straight.setBounds(
                    intersection.getX() + intersection.getWidth() - TRAFFIC_LIGHT_SIZE,
                    intersection.getY() + (intersection.getHeight() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight4Straight);
            panelSimulation.setComponentZOrder(trafficLight4Straight, 0);
            // RIGHT
            JTextField trafficLight4Right = new JTextField();
            trafficLight4Right.setBackground(intersection.getTrafficLightsEast()[2] ? Color.GREEN : Color.RED);
            trafficLight4Right.setEditable(false);
            trafficLight4Right.setBounds(
                    intersection.getX() + intersection.getWidth() - TRAFFIC_LIGHT_SIZE,
                    intersection.getY() + (intersection.getHeight() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                    TRAFFIC_LIGHT_SIZE,
                    TRAFFIC_LIGHT_SIZE);

            panelSimulation.add(trafficLight4Right);
            panelSimulation.setComponentZOrder(trafficLight4Right, 0);
        }
    }

    /**
     * Resets all elements from the simulation panel.
     */
    public synchronized void resetSimulationPanel() {
        // cleat components
        clearComponents();
        // reset vehicle ids
        VehicleGenerator.currentId = 0;
        // clear log areas
        clearSimulationLogArea();
        clearStatisticsArea();
        // reset text labels
        resetAllTextLabels();
        // disable Pause/Resume menu items
        pauseMenuItem.setEnabled(false);
        resumeMenuItem.setEnabled(false);
    }

    /**
     * Removes all the components from the simulation view.
     */
    public void clearComponents() {
        vehicleLabels = null;
        currentSegment = 1;
        currentSegId = 1;
        currentIntersId = 1;

        panelSimulation.removeAll();
        panelSimulation.revalidate();

        intersections = new ArrayList<>();
        segments = new ArrayList<>();
        trafficFlows = null;

        segmentCoordsX = new HashMap<>();
        segmentCoordsY = new HashMap<>();

        panelSimulation.repaint();
    }

    // setters and getters
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

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<Intersection> intersections) {
        this.intersections = intersections;
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

    public int getSimulationStep() {
        return simulationStep;
    }

    public void setSimulationStep(int simulationStep) {
        this.simulationStep = simulationStep;
    }

    public TrafficSimulationView getTrafficSimulationViewInstance() {
        return this;
    }

    public List<TrafficFlow> getTrafficFlows() {
        return trafficFlows;
    }

    public void setTrafficFlows(List<TrafficFlow> trafficFlows) {
        this.trafficFlows = trafficFlows;
    }
}
