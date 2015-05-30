package com.utcn.configurator.flow.view;

import com.utcn.configurator.flow.model.TrafficFlow;
import com.utcn.configurator.flow.utils.TrafficFlowGeneratorUtil;
import com.utcn.models.Segment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrafficFlowGeneratorView extends JFrame {

    private JPanel contentPane;
    private int currentY;

    private List<JComboBox> startingPointComboBoxes;
    private List<JCheckBox> routeListCheckBoxes;
    private List<JTextField> durationTextFields;
    private List<JTextField> routeListTextFields;

    /**
     * Create the frame.
     */
    public TrafficFlowGeneratorView(final Integer[] intersectionIds, final List<Segment> segments) {
        startingPointComboBoxes = new ArrayList<>();
        durationTextFields = new ArrayList<>();
        routeListTextFields = new ArrayList<>();
        routeListCheckBoxes = new ArrayList<>();

        setTitle("Vehicle Flows");
        setBounds(100, 100, 579, 352);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblDescription = new JLabel(
                "Define vehicle flows for your simulation");
        lblDescription.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDescription.setBounds(5, 1, 260, 14);
        contentPane.add(lblDescription);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                final JComboBox<Integer> startingPointComboBox = new JComboBox<>();
                startingPointComboBox.setModel(new DefaultComboBoxModel<>(
                        intersectionIds));
                startingPointComboBox.setBounds(10, currentY + 23 + 30, 108,
                        23);
                contentPane.add(startingPointComboBox);

                startingPointComboBoxes.add(startingPointComboBox);

                JTextField durationTextField = new JTextField();
                durationTextField.setBounds(282, currentY + 23 + 30, 60, 20);
                contentPane.add(durationTextField);
                durationTextField.setColumns(10);

                durationTextFields.add(durationTextField);

                final JTextField routeListTextField = new JTextField();
                routeListTextField.setBounds(128, currentY + 23 + 30, 137, 20);
                routeListTextField.setEnabled(false);
                contentPane.add(routeListTextField);
                routeListTextField.setColumns(10);

                routeListTextFields.add(routeListTextField);

                final JCheckBox chckbxRouteList = new JCheckBox("Route List\r\n");
                chckbxRouteList.setFont(new Font("Tahoma", Font.BOLD, 12));
                chckbxRouteList.setBounds(128, currentY + 23, 97, 23);
                chckbxRouteList.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        if (chckbxRouteList.isSelected()) {
                            routeListTextField.setEnabled(true);
                            startingPointComboBox.setEnabled(false);
                        } else {
                            routeListTextField.setEnabled(false);
                            startingPointComboBox.setEnabled(true);
                        }

                    }
                });
                contentPane.add(chckbxRouteList);

                routeListCheckBoxes.add(chckbxRouteList);

                currentY += 23 + 30;

                contentPane.revalidate();
                contentPane.repaint();
            }
        });
        btnAdd.setBounds(10, 20, 89, 23);
        contentPane.add(btnAdd);

        currentY = 68;

        final JLabel lblStartingPoint = new JLabel("Starting point");
        lblStartingPoint.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblStartingPoint.setBounds(5, 47, 108, 14);
        contentPane.add(lblStartingPoint);

        final JComboBox<Integer> startingPointComboBox = new JComboBox<>();
        startingPointComboBox.setModel(new DefaultComboBoxModel<>(
                intersectionIds));
        startingPointComboBox.setBounds(10, 68, 108, 23);
        contentPane.add(startingPointComboBox);

        startingPointComboBoxes.add(startingPointComboBox);

        JLabel lblStartTime = new JLabel("Duration");
        lblStartTime.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblStartTime.setBounds(282, 47, 74, 14);
        contentPane.add(lblStartTime);

        JTextField durationTextField = new JTextField();
        durationTextField.setBounds(282, 69, 60, 20);
        contentPane.add(durationTextField);
        durationTextField.setColumns(10);

        durationTextFields.add(durationTextField);

        final JTextField routeListTextField = new JTextField();
        routeListTextField.setBounds(128, 69, 137, 20);
        routeListTextField.setEnabled(false);
        contentPane.add(routeListTextField);
        routeListTextField.setColumns(10);

        routeListTextFields.add(routeListTextField);

        final JCheckBox chckbxRouteList = new JCheckBox("Route List\r\n");
        chckbxRouteList.setFont(new Font("Tahoma", Font.BOLD, 12));
        chckbxRouteList.setBounds(128, 44, 97, 23);
        chckbxRouteList.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (chckbxRouteList.isSelected()) {
                    routeListTextField.setEnabled(true);
                    startingPointComboBox.setEnabled(false);
                } else {
                    routeListTextField.setEnabled(false);
                    startingPointComboBox.setEnabled(true);
                }

            }
        });
        contentPane.add(chckbxRouteList);

        routeListCheckBoxes.add(chckbxRouteList);

        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<TrafficFlow> trafficFlowList = new ArrayList<>();
                for (int i = 0; i < durationTextFields.size(); i++) {

                    if (startingPointComboBoxes.get(i).isEnabled()) {
                        trafficFlowList.add(new TrafficFlow(
                                (Integer) startingPointComboBoxes.get(i)
                                        .getSelectedItem(), null,
                                Integer.valueOf(durationTextFields.get(i).getText())));
                    } else {
                        String[] route = routeListTextFields.get(i).getText().split(" ");
                        List<Integer> routeList = new ArrayList<>();

                        for (String id : route) {
                            routeList.add(Integer.valueOf(id));
                        }

                        if (!TrafficFlowGeneratorUtil.validateRouteList(routeList, segments)) {
                            JOptionPane.showMessageDialog(null, "Invalid route list: " + routeList.toString(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        trafficFlowList.add(new TrafficFlow(0, routeList,
                                Integer.valueOf(durationTextFields.get(i).getText())));
                    }
                }

                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
                fc.setAcceptAllFileFilterUsed(false);
                fc.showOpenDialog(null);

                if (fc.getSelectedFile() != null) {
                    boolean res = TrafficFlowGeneratorUtil.exportToJSON(fc
                            .getSelectedFile().getPath(), trafficFlowList);

                    if (!res) {
                        JOptionPane.showMessageDialog(null, "Export failed.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        btnGenerate.setBounds(464, 279, 89, 23);
        contentPane.add(btnGenerate);
    }
}
