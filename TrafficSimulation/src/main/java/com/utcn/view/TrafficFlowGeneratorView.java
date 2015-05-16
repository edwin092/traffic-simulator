package com.utcn.view;

import com.utcn.flow.TrafficFlow;
import org.codehaus.jackson.map.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrafficFlowGeneratorView extends JFrame {

    private static final String[] FLOW_TYPES = new String[]{"Fast", "Medium",
            "Slow"};

    private JPanel contentPane;
    private int currentY;
    private int numberOfIntersections;

    private List<JComboBox> typeComboBoxes;
    private List<JComboBox> startingPointComboBoxes;
    private List<JTextField> startTimeTextFields;
    private List<JTextField> endTimeTextFields;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TrafficFlowGeneratorView frame = new TrafficFlowGeneratorView(6);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TrafficFlowGeneratorView(int numberOfIntersections) {
        this.numberOfIntersections = numberOfIntersections;

        typeComboBoxes = new ArrayList<>();
        startingPointComboBoxes = new ArrayList<>();
        startTimeTextFields = new ArrayList<>();
        endTimeTextFields = new ArrayList<>();

        setTitle("Vehicle Flows");
        setBounds(100, 100, 579, 352);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblDescription = new JLabel("Define vehicle flows for your simulation");
        lblDescription.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDescription.setBounds(5, 1, 260, 14);
        contentPane.add(lblDescription);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> typeComboBox = new JComboBox<>();
                typeComboBox.setModel(new DefaultComboBoxModel<>(FLOW_TYPES));
                typeComboBox.setBounds(20, currentY + 23 + 20, 108, 23);
                contentPane.add(typeComboBox);

                typeComboBoxes.add(typeComboBox);

                JComboBox<Integer> startingPointComboBox = new JComboBox<>();
                startingPointComboBox.setModel(new DefaultComboBoxModel<>(getIntersectionIdList()));
                startingPointComboBox.setBounds(155, currentY + 23 + 20, 108,
                        23);
                contentPane.add(startingPointComboBox);

                startingPointComboBoxes.add(startingPointComboBox);

                JTextField startTimeTextField = new JTextField();
                startTimeTextField.setBounds(308, currentY + 23 + 20, 60, 20);
                contentPane.add(startTimeTextField);
                startTimeTextField.setColumns(10);

                startTimeTextFields.add(startTimeTextField);

                JTextField endTimeTextField = new JTextField();
                endTimeTextField.setColumns(10);
                endTimeTextField.setBounds(391, currentY + 23 + 20, 60, 20);
                contentPane.add(endTimeTextField);

                endTimeTextFields.add(endTimeTextField);

                currentY += 23 + 20;

                contentPane.revalidate();
                contentPane.repaint();
            }
        });
        btnAdd.setBounds(10, 20, 89, 23);
        contentPane.add(btnAdd);

        currentY = 68;

        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.setModel(new DefaultComboBoxModel<>(FLOW_TYPES));
        typeComboBox.setBounds(20, 68, 108, 23);
        contentPane.add(typeComboBox);

        typeComboBoxes.add(typeComboBox);

        final JComboBox<Integer> startingPointComboBox = new JComboBox<>();
        startingPointComboBox.setModel(new DefaultComboBoxModel<>(getIntersectionIdList()));
        startingPointComboBox.setBounds(155, 68, 108, 23);
        contentPane.add(startingPointComboBox);

        startingPointComboBoxes.add(startingPointComboBox);

        JTextField startTimeTextField = new JTextField();
        startTimeTextField.setBounds(308, 69, 60, 20);
        contentPane.add(startTimeTextField);
        startTimeTextField.setColumns(10);

        startTimeTextFields.add(startTimeTextField);

        JTextField endTimeTextField = new JTextField();
        endTimeTextField.setColumns(10);
        endTimeTextField.setBounds(391, 69, 60, 20);
        contentPane.add(endTimeTextField);

        endTimeTextFields.add(endTimeTextField);

        JLabel lblType = new JLabel("Type");
        lblType.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblType.setBounds(20, 47, 60, 14);
        contentPane.add(lblType);

        JLabel lblStartingPoint = new JLabel("Starting point");
        lblStartingPoint.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblStartingPoint.setBounds(155, 47, 108, 14);
        contentPane.add(lblStartingPoint);

        JLabel lblStartTime = new JLabel("Start Time");
        lblStartTime.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblStartTime.setBounds(308, 47, 74, 14);
        contentPane.add(lblStartTime);

        JLabel lblEndTime = new JLabel("End Time");
        lblEndTime.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblEndTime.setBounds(391, 47, 69, 14);
        contentPane.add(lblEndTime);

        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<TrafficFlow> trafficFlowList = new ArrayList<>();
                for (int i = 0; i < typeComboBoxes.size(); i++) {
                    trafficFlowList.add(new TrafficFlow((String) typeComboBoxes.get(i).getSelectedItem(),
                            (Integer) startingPointComboBoxes.get(i).getSelectedItem(),
                            Integer.valueOf(startTimeTextFields.get(i).getText()),
                            Integer.valueOf(endTimeTextFields.get(i).getText())));
                }

                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
                fc.setAcceptAllFileFilterUsed(false);
                fc.showOpenDialog(null);

                if (fc.getSelectedFile() != null) {
                    boolean res = exportToJSON(fc.getSelectedFile().getPath(), trafficFlowList);

                    if (!res) {
                        JOptionPane.showMessageDialog(null,
                                "Export failed.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        btnGenerate.setBounds(464, 279, 89, 23);
        contentPane.add(btnGenerate);

        JButton btnDelete = new JButton("Remove");
        btnDelete.setBounds(464, 68, 89, 23);
        contentPane.add(btnDelete);
    }

    /**
     * @return
     */
    private Integer[] getIntersectionIdList() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= numberOfIntersections; i++) {
            ids.add(i);
        }

        return ids.toArray(new Integer[ids.size()]);
    }

    /**
     * @param filepath
     * @param trafficFlowList
     * @return
     */
    private static boolean exportToJSON(String filepath, List<TrafficFlow> trafficFlowList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!filepath.contains(".json")) {
                filepath += ".json";
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filepath), trafficFlowList);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }
}