package com.utcn.view;

import com.utcn.bl.EnvironmentSetup;
import com.utcn.bl.VehicleGenerator;
import com.utcn.configurator.flow.model.TrafficFlow;
import com.utcn.configurator.flow.view.TrafficFlowGeneratorView;
import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.configurator.trafficlight.view.TrafficLightsConfiguratorView;
import com.utcn.controllers.TrafficSimulationController;
import com.utcn.models.*;
import com.utcn.optimization.TrafficLightsOptimization;
import com.utcn.utils.ImportExportHelper;
import com.utcn.utils.SimulationGraph;
import com.utcn.utils.TrafficSimulationUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrafficSimulationView {

    public static final String NO_FLOW_CONFIG_FILE_MSG = "No flow config file added";
    public static final String FLOW_CONFIG_FILE_LOADED_MSG = "Flow config file NAME added";
    public static final String NO_TL_CONFIG_FILE_MSG = "No traffic lights config file added";
    public static final String TL_CONFIG_FILE_LOADED_MSG = "Traffic lights config file NAME added";

    public static final int INTERSECTION_SIZE = 60;
    public static final int INTERSECTION_CLICK_SIZE = 20;
    public static final int TRAFFIC_LIGHT_SIZE = 10;
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
    private JMenu mnStatistics;
    private JMenuItem startMenuItem;
    private JMenuItem pauseMenuItem;
    private JMenuItem resumeMenuItem;
    // text labels
    private JLabel lblCounter;
    private JLabel lblStep;
    private JLabel lblSimulationTime;
    private JLabel lblFlowConfigFileText;
    private JLabel lblTLConfigFileText;
    // components ids
    private int currentSegment = 1;
    private int currentSegId = 1;
    private int currentIntersId = 1;
    // components
    private List<Intersection> intersections = new ArrayList<>();
    private List<Segment> segments = new ArrayList<>();
    private List<TrafficFlow> trafficFlows;
    private List<TrafficLightPhases> trafficLightPhaseses;

    private Map<Integer, List<Integer>> segmentCoordsX = new HashMap<>();
    private Map<Integer, List<Integer>> segmentCoordsY = new HashMap<>();

    // statistics
    private VehicleStatisticsManager vehicleStatisticsManager;

    // arrows images for traffic lights
    private BufferedImage arrowGreenLeft;
    private BufferedImage arrowGreenUp;
    private BufferedImage arrowGreenRight;
    private BufferedImage arrowGreenDown;
    private BufferedImage arrowRedLeft;
    private BufferedImage arrowRedUp;
    private BufferedImage arrowRedRight;
    private BufferedImage arrowRedDown;

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
        frame.setBounds(100, 100, 1337, 715);
        frame.setTitle("Traffic Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // loads all arrow images from the 'arrows' folder
        loadAllArrowImages();

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

        mnSimulation.addSeparator();

        JMenuItem generateFlowConfigMenuItem = new JMenuItem("Generate flow config file");
        generateFlowConfigMenuItem.setToolTipText("Create a JSON config file for traffic flows.");
        generateFlowConfigMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Integer> ids = new ArrayList<>();
                for (Intersection intersection : intersections) {
                    if (intersection.getSegmentsNumber() <= 2) {
                        ids.add(intersection.getId());
                    }
                }
                new TrafficFlowGeneratorView(TrafficSimulationUtil.convertListToIntegerList(ids), segments).setVisible(true);
            }
        });
        mnSimulation.add(generateFlowConfigMenuItem);

        JMenuItem addFlowConfigMenuItem = new JMenuItem("Add flow config file");
        addFlowConfigMenuItem.setToolTipText("Add a JSON file containing traffic flows.");
        addFlowConfigMenuItem.addActionListener(new ActionListener() {
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
                        lblFlowConfigFileText.setText(FLOW_CONFIG_FILE_LOADED_MSG.replace("NAME", fc.getSelectedFile().getName()));
                        lblFlowConfigFileText.setForeground(Color.BLUE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Import failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        mnSimulation.add(addFlowConfigMenuItem);

        mnSimulation.addSeparator();

        JMenuItem generateTLConfigMenuItem = new JMenuItem("Generate traffic lights config file");
        generateTLConfigMenuItem.setToolTipText("Create a JSON config file for traffic light phases.");
        generateTLConfigMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Integer> ids = new ArrayList<>();
                for (Intersection intersection : intersections) {
                    if (intersection.isFourPhased()) {
                        ids.add(intersection.getId());
                    }
                }
                new TrafficLightsConfiguratorView(TrafficSimulationUtil.convertList(ids)).setVisible(true);
            }
        });
        mnSimulation.add(generateTLConfigMenuItem);

        JMenuItem addTLConfigMenuItem = new JMenuItem("Add traffic lights config file");
        addTLConfigMenuItem.setToolTipText("Add a JSON file containing traffic lights phases.");
        addTLConfigMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter(
                        "JSON files (*.json)", "json");
                fc.setFileFilter(jsonFilter);
                fc.setCurrentDirectory(new File("."));
                fc.showOpenDialog(null);

                if (fc.getSelectedFile() != null) {
                    boolean res = ImportExportHelper.importTrafficLightsConfigFromJSON(fc.getSelectedFile().getAbsolutePath(),
                            getTrafficSimulationViewInstance());
                    if (res) {
                        lblTLConfigFileText.setText(TL_CONFIG_FILE_LOADED_MSG.replace("NAME", fc.getSelectedFile().getName()));
                        lblTLConfigFileText.setForeground(Color.BLUE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Import failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        mnSimulation.add(addTLConfigMenuItem);

        mnSimulation.addSeparator();

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

        final JMenuItem mntmSegment = new JMenuItem("Segment");
        mntmSegment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isSegmentSelected = true;

                panelSimulation.repaint();
            }
        });
        mnComponents.add(mntmSegment);

        final JCheckBoxMenuItem mntmIntersection = new JCheckBoxMenuItem("Intersection");
        mntmIntersection.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (mntmIntersection.isSelected()) {
                    isIntersectionSelected = true;
                    mntmSegment.setEnabled(false);
                } else {
                    isIntersectionSelected = false;
                    mntmSegment.setEnabled(true);
                }
            }
        });
        mnComponents.add(mntmIntersection);

        // Statistics Menu
        mnStatistics = new JMenu("Statistics");
        menuBar.add(mnStatistics);
        mnStatistics.setEnabled(false);

        JMenuItem mntmVehiclesInters = new JMenuItem("Vehicles per Intersection");
        mntmVehiclesInters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                for (IntersectionStatistics intersectionStatistics :
                        environmentSetup.getIntersectionStatisticsManager().getIntersectionStatisticsList()) {
                    dataset.addValue(intersectionStatistics.getNumberOfVehPassed()
                            , "Intersection " + intersectionStatistics.getIntersectionId(),
                            "Number of vehicles");
                }

                JFreeChart barChart = ChartFactory.createBarChart(
                        "The Number of vehicles that passed an intersection during " + simulationTime,
                        "", "Number of vehicles",
                        dataset, PlotOrientation.VERTICAL,
                        true, true, false);

                int width = 640; /* Width of the image */
                int height = 480; /* Height of the image */
                File BarChart = new File("BarChart.jpeg");
                try {
                    ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);

                    JLabel label = new JLabel(new ImageIcon("BarChart.jpeg"));
                    JFrame f = new JFrame();
                    f.setTitle("Vehicles per Intersection");
                    f.getContentPane().add(label);
                    f.pack();
                    f.setLocation(200, 200);
                    f.setVisible(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        mnStatistics.add(mntmVehiclesInters);
        // End Statistics Menu

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
        splitPane.setBounds(10, 33, 1301, 611);
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
                try {
                    super.paintComponent(g);
                    // colors
                    Color blackLineColor = new Color(0, 0, 0);
                    Color redLineColor = new Color(255, 0, 0);
                    Color grayLineColor = new Color(0, 0, 0, 0.2f);
                    // create grid
                    createGridForSimulation(g, this.getWidth(), this.getHeight());
                    // add traffic lights
                    addArrowTrafficLightsToSimulation();
                    // add intersections
                    for (Intersection intersection : intersections) {
                        this.add(intersection);
                        // show intersections segments click zones
                        if (isSegmentSelected) {

                            // NORTH
                            if (intersection.isSegmentNorthSelected()) {
                                g.setColor(redLineColor);
                            } else {
                                g.setColor(grayLineColor);
                            }
                            g.drawLine(intersection.getX(), intersection.getY(),
                                    intersection.getX(), intersection.getY() - INTERSECTION_CLICK_SIZE);
                            g.drawLine(intersection.getX(), intersection.getY() - INTERSECTION_CLICK_SIZE,
                                    intersection.getX() + intersection.getWidth(), intersection.getY() - INTERSECTION_CLICK_SIZE);
                            g.drawLine(intersection.getX() + intersection.getWidth(), intersection.getY() - INTERSECTION_CLICK_SIZE,
                                    intersection.getX() + intersection.getWidth(), intersection.getY());
                            // SOUTH
                            if (intersection.isSegmentSouthSelected()) {
                                g.setColor(redLineColor);
                            } else {
                                g.setColor(grayLineColor);
                            }
                            g.drawLine(intersection.getX(), intersection.getY() + intersection.getHeight(),
                                    intersection.getX(), intersection.getY() + intersection.getHeight() + INTERSECTION_CLICK_SIZE);
                            g.drawLine(intersection.getX(), intersection.getY() + intersection.getHeight() + INTERSECTION_CLICK_SIZE,
                                    intersection.getX() + intersection.getWidth(), intersection.getY() + intersection.getHeight() + INTERSECTION_CLICK_SIZE);
                            g.drawLine(intersection.getX() + intersection.getWidth(), intersection.getY() + intersection.getHeight() + INTERSECTION_CLICK_SIZE,
                                    intersection.getX() + intersection.getWidth(), intersection.getY() + intersection.getHeight());
                            // VEST
                            if (intersection.isSegmentVestSelected()) {
                                g.setColor(redLineColor);
                            } else {
                                g.setColor(grayLineColor);
                            }
                            g.drawLine(intersection.getX(), intersection.getY(),
                                    intersection.getX() - INTERSECTION_CLICK_SIZE, intersection.getY());
                            g.drawLine(intersection.getX() - INTERSECTION_CLICK_SIZE, intersection.getY(),
                                    intersection.getX() - INTERSECTION_CLICK_SIZE, intersection.getY() + intersection.getHeight());
                            g.drawLine(intersection.getX() - INTERSECTION_CLICK_SIZE, intersection.getY() + intersection.getHeight(),
                                    intersection.getX(), intersection.getY() + intersection.getHeight());
                            //EAST
                            if (intersection.isSegmentEastSelected()) {
                                g.setColor(redLineColor);
                            } else {
                                g.setColor(grayLineColor);
                            }
                            g.drawLine(intersection.getX() + intersection.getWidth(), intersection.getY(),
                                    intersection.getX() + intersection.getWidth() + INTERSECTION_CLICK_SIZE, intersection.getY());
                            g.drawLine(intersection.getX() + intersection.getWidth() + INTERSECTION_CLICK_SIZE, intersection.getY(),
                                    intersection.getX() + intersection.getWidth() + INTERSECTION_CLICK_SIZE, intersection.getY() + intersection.getHeight());
                            g.drawLine(intersection.getX() + intersection.getWidth() + INTERSECTION_CLICK_SIZE, intersection.getY() + intersection.getHeight(),
                                    intersection.getX(), intersection.getY() + intersection.getHeight());
                        }

                        // id of intersection
                        JLabel lab = new JLabel(String.valueOf(intersection.getId()));
                        int labX = intersection.getX() + INTERSECTION_SIZE / 2 - 7;
                        int labY = intersection.getY() + INTERSECTION_SIZE / 2 - 7;
                        lab.setBounds(labX, labY, 15, 15);
                        panelSimulation.add(lab);
                        panelSimulation.setComponentZOrder(lab, 0);

//                    panelSimulation.setComponentZOrder(lab, 1);
                    }
                    // draw vehicles
                    if (vehicleLabels != null) {
                        for (JLabel label : vehicleLabels) {
                            this.add(label);
                            this.setComponentZOrder(label, 0);
                        }
                    }

                    g.setColor(blackLineColor);

                    for (Integer key : segmentCoordsX.keySet()) {
                        List<Integer> xCoords = segmentCoordsX.get(key);
                        List<Integer> yCoords = segmentCoordsY.get(key);

                        int[] x = TrafficSimulationUtil.convertList(xCoords);
                        int[] y = TrafficSimulationUtil.convertList(yCoords);

                        // draw polyline
                        g.drawPolyline(x, y, x.length);
                    }
                } catch (IllegalArgumentException e) {
                    // do nothing
                }
            }
        };


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

        lblFlowConfigFileText = new JLabel(NO_FLOW_CONFIG_FILE_MSG);
        lblFlowConfigFileText.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblFlowConfigFileText.setForeground(Color.RED);
        lblFlowConfigFileText.setBounds(340, 9, 193, 14);
        frame.getContentPane().add(lblFlowConfigFileText);

        lblTLConfigFileText = new JLabel(NO_TL_CONFIG_FILE_MSG);
        lblTLConfigFileText.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblTLConfigFileText.setForeground(Color.RED);
        lblTLConfigFileText.setBounds(540, 9, 233, 14);
        frame.getContentPane().add(lblTLConfigFileText);
    }

    /**
     * Reset text from each text label.
     */
    private void resetAllTextLabels() {
        lblCounter.setText("0");
        lblSimulationTime.setText("0");
        lblStep.setText("0");

        lblFlowConfigFileText.setText(NO_FLOW_CONFIG_FILE_MSG);
        lblFlowConfigFileText.setForeground(Color.RED);

        lblTLConfigFileText.setText(NO_TL_CONFIG_FILE_MSG);
        lblTLConfigFileText.setForeground(Color.RED);
    }

    /**
     * Loads all arrow images for traffic lights.
     */
    private void loadAllArrowImages() {
        try {
            arrowGreenLeft = ImageIO.read(new File("arrows\\arrow-green-left.png"));
            arrowGreenUp = ImageIO.read(new File("arrows\\arrow-green-up.png"));
            arrowGreenRight = ImageIO.read(new File("arrows\\arrow-green-right.png"));
            arrowGreenDown = ImageIO.read(new File("arrows\\arrow-green-down.png"));
            arrowRedLeft = ImageIO.read(new File("arrows\\arrow-red-left.png"));
            arrowRedUp = ImageIO.read(new File("arrows\\arrow-red-up.png"));
            arrowRedRight = ImageIO.read(new File("arrows\\arrow-red-right.png"));
            arrowRedDown = ImageIO.read(new File("arrows\\arrow-red-down.png"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please make sure that the \"arrows\" folder exists and all the images are present!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
                simulationStep = simulationTime;
                break;
            default:
                simulationStep = SIMULATION_STEP_DEFAULT;
        }
    }

    /**
     * Set phases times and order for intersections.
     */
    private void setPhaseTimeAndOrderForIntersections() {
        for (Intersection intersection : intersections) {
            if (intersection.isFourPhased()) {
                for (TrafficLightPhases trafficLightPhases : trafficLightPhaseses) {
                    if (trafficLightPhases.getIntersectionId() == intersection.getId()) {
                        intersection.setPhaseTimes(new int[]{trafficLightPhases.getPhase1Time(),
                                trafficLightPhases.getPhase2Time(), trafficLightPhases.getPhase3Time(),
                                trafficLightPhases.getPhase4Time()});
                    }
                }
//                intersection.setPhaseTimes(TrafficLightsOptimization.getRandomTimeList());
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

        vehicleStatisticsManager = new VehicleStatisticsManager();

        int globalCounter = 1;

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
                    // generate new vehicle
                    Vehicle newVehicle = environmentSetup.generateVehicle(simulationGraph, trafficFlow);
                    if (newVehicle != null) {
                        vehicleStatisticsManager.addNewVehicle(newVehicle.getId(), globalCounter);
                    }
                }
                // segment acceleration
                environmentSetup.checkSegments(vehicleStatisticsManager, globalCounter);
                // manage intersections traffic lights
                environmentSetup.manageIntersectionsTrafficLights();
                // increment counter
                globalCounter++;
            }

            for (Segment segment : segments) {

                for (Vehicle veh : segment.getVehicles()) {

//                    JLabel lblO = new JLabel(String.valueOf(veh.getId()));
                    JLabel lblO = new JLabel("O");
                    // TODO move this into Vehicle entity
                    lblO.setForeground(new Color(TrafficSimulationUtil.randInt(0, 255),
                            TrafficSimulationUtil.randInt(0, 255),
                            TrafficSimulationUtil.randInt(0, 255)));


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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        } while (globalCounter < simulationTime);

        lblCounter.setText(String.valueOf(simulationTime));
        addNewSimulationLogEntry("\nSimulation finished at " + dateFormat.format(date) + "\n\n");
        // enable statistics only after simulation ends
        mnStatistics.setEnabled(true);
    }

    /**
     * Adds traffic lights to the simulation panel. The traffic lights are represented by arrows.
     */
    private void addArrowTrafficLightsToSimulation() {
        // Add traffic lights to intersections
        for (Intersection intersection : getIntersections()) {
            if (intersection.getSegmentsNumber() > 4) {
                // NORTH
                // LEFT
                JLabel trafficLightLeft =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsNorth()[0] ? arrowGreenLeft : arrowRedLeft));
                trafficLightLeft.setBounds(intersection.getX() + (intersection.getWidth() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                        intersection.getY() + 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLightLeft);
                panelSimulation.setComponentZOrder(trafficLightLeft, 0);
                // STRAIGHT
                JLabel trafficLightStraight =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsNorth()[1] ? arrowGreenDown : arrowRedDown));
                trafficLightStraight.setBounds(
                        intersection.getX() + (intersection.getWidth() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                        intersection.getY() + 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLightStraight);
                panelSimulation.setComponentZOrder(trafficLightStraight, 0);
                // RIGHT
                JLabel trafficLightRight =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsNorth()[2] ? arrowGreenRight : arrowRedRight));
                trafficLightRight.setBounds(
                        intersection.getX() + (intersection.getWidth() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                        intersection.getY() + 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLightRight);
                panelSimulation.setComponentZOrder(trafficLightRight, 0);

                // SOUTH
                // LEFT
                JLabel trafficLight2Left =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsSouth()[0] ? arrowGreenLeft : arrowRedLeft));
                trafficLight2Left.setBounds(
                        intersection.getX() + (intersection.getWidth() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                        intersection.getY() + intersection.getHeight() - TRAFFIC_LIGHT_SIZE - 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight2Left);
                panelSimulation.setComponentZOrder(trafficLight2Left, 0);
                // STRAIGHT
                JLabel trafficLight2Straight =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsSouth()[1] ? arrowGreenUp : arrowRedUp));
                trafficLight2Straight.setBounds(
                        intersection.getX() + (intersection.getWidth() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                        intersection.getY() + intersection.getHeight() - TRAFFIC_LIGHT_SIZE - 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight2Straight);
                panelSimulation.setComponentZOrder(trafficLight2Straight, 0);
                // RIGHT
                JLabel trafficLight2Right =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsSouth()[2] ? arrowGreenRight : arrowRedRight));
                trafficLight2Right.setBounds(
                        intersection.getX() + (intersection.getWidth() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                        intersection.getY() + intersection.getHeight() - TRAFFIC_LIGHT_SIZE - 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight2Right);
                panelSimulation.setComponentZOrder(trafficLight2Right, 0);

                // VEST
                // LEFT
                JLabel trafficLight3Left =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsVest()[0] ? arrowGreenUp : arrowRedUp));
                trafficLight3Left.setBounds(
                        intersection.getX() + 1,
                        intersection.getY() + (intersection.getHeight() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight3Left);
                panelSimulation.setComponentZOrder(trafficLight3Left, 0);
                // STRAIGHT
                JLabel trafficLight3Straight =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsVest()[1] ? arrowGreenRight : arrowRedRight));
                trafficLight3Straight.setBounds(
                        intersection.getX() + 1,
                        intersection.getY() + (intersection.getHeight() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight3Straight);
                panelSimulation.setComponentZOrder(trafficLight3Straight, 0);
                // RIGHT
                JLabel trafficLight3Right =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsVest()[2] ? arrowGreenDown : arrowRedDown));
                trafficLight3Right.setBounds(
                        intersection.getX() + 1,
                        intersection.getY() + (intersection.getHeight() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight3Right);
                panelSimulation.setComponentZOrder(trafficLight3Right, 0);

                // EAST
                // LEFT
                JLabel trafficLight4Left =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsEast()[0] ? arrowGreenDown : arrowRedDown));
                trafficLight4Left.setBounds(
                        intersection.getX() + intersection.getWidth() - TRAFFIC_LIGHT_SIZE - 1,
                        intersection.getY() + (intersection.getHeight() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight4Left);
                panelSimulation.setComponentZOrder(trafficLight4Left, 0);
                // STRAIGHT
                JLabel trafficLight4Straight =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsEast()[1] ? arrowGreenLeft : arrowRedLeft));
                trafficLight4Straight.setBounds(
                        intersection.getX() + intersection.getWidth() - TRAFFIC_LIGHT_SIZE - 1,
                        intersection.getY() + (intersection.getHeight() / 2) - (TRAFFIC_LIGHT_SIZE / 2),
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight4Straight);
                panelSimulation.setComponentZOrder(trafficLight4Straight, 0);
                // RIGHT
                JLabel trafficLight4Right =
                        new JLabel(new ImageIcon(intersection.getTrafficLightsEast()[2] ? arrowGreenUp : arrowRedUp));
                trafficLight4Right.setBounds(
                        intersection.getX() + intersection.getWidth() - TRAFFIC_LIGHT_SIZE - 1,
                        intersection.getY() + (intersection.getHeight() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
                        TRAFFIC_LIGHT_SIZE,
                        TRAFFIC_LIGHT_SIZE);
                panelSimulation.add(trafficLight4Right);
                panelSimulation.setComponentZOrder(trafficLight4Right, 0);
            }

        }
    }

    /**
     * Adds traffic lights to the simulation panel.
     *
     * @deprecated
     */
    private void addBoxTrafficLightsToSimulation() {
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
                    intersection.getY() + (intersection.getHeight() / 2) + (TRAFFIC_LIGHT_SIZE / 2) + 2,
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
                    intersection.getY() + (intersection.getHeight() / 2) - TRAFFIC_LIGHT_SIZE - (TRAFFIC_LIGHT_SIZE / 2) - 1,
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
        mnStatistics.setEnabled(false);
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

    public List<TrafficLightPhases> getTrafficLightPhaseses() {
        return trafficLightPhaseses;
    }

    public void setTrafficLightPhaseses(List<TrafficLightPhases> trafficLightPhaseses) {
        this.trafficLightPhaseses = trafficLightPhaseses;
    }

    public VehicleStatisticsManager getVehicleStatisticsManager() {
        return vehicleStatisticsManager;
    }

    public void setVehicleStatisticsManager(VehicleStatisticsManager vehicleStatisticsManager) {
        this.vehicleStatisticsManager = vehicleStatisticsManager;
    }
}
