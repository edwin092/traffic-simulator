package com.utcn.configurator.trafficlight.view;

import com.utcn.configurator.trafficlight.controller.TrafficLightsConfiguratorController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficLightsConfiguratorView extends JFrame {

    private final JButton btnGenerate;

    public static Map<Integer, List<JTextField>> phasesTextFields = new HashMap<>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TrafficLightsConfiguratorView frame = new TrafficLightsConfiguratorView(new int[]{1, 2, 4, 6, 7});
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
    public TrafficLightsConfiguratorView(int[] intersectionIds) {
        setTitle("Traffic Lights Configurator");
        setBounds(100, 100, 624, 546);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblPhase1 = new JLabel("Phase 1");
        lblPhase1.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase1.setForeground(Color.RED);
        lblPhase1.setBounds(10, 11, 75, 14);
        contentPane.add(lblPhase1);

        JLabel lblPhase1Dir = new JLabel("<html>S -> N<br>S -> E<br>N -> S<br>N -> V</html>");
        lblPhase1Dir.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase1Dir.setBounds(10, 28, 54, 67);
        contentPane.add(lblPhase1Dir);

        JLabel lblPhase2 = new JLabel("Phase 2");
        lblPhase2.setForeground(Color.RED);
        lblPhase2.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase2.setBounds(95, 11, 75, 14);
        contentPane.add(lblPhase2);

        JLabel lblPhase3 = new JLabel("Phase 3");
        lblPhase3.setForeground(Color.RED);
        lblPhase3.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase3.setBounds(180, 11, 75, 14);
        contentPane.add(lblPhase3);

        JLabel lblPhase4 = new JLabel("Phase 4");
        lblPhase4.setForeground(Color.RED);
        lblPhase4.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase4.setBounds(265, 11, 75, 14);
        contentPane.add(lblPhase4);

        JLabel lblPhase2Dir = new JLabel("<html>E -> V<br>E -> N<br>V -> E<br>V -> S</html>");
        lblPhase2Dir.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase2Dir.setBounds(95, 28, 54, 67);
        contentPane.add(lblPhase2Dir);

        JLabel lblPhase3Dir = new JLabel("<html>S -> E<br>E -> S<br>N -> V<br>V -> N</html>");
        lblPhase3Dir.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase3Dir.setBounds(180, 28, 54, 67);
        contentPane.add(lblPhase3Dir);

        JLabel lblPhase4Dir = new JLabel("<html>S -> V<br>V -> S<br>N -> E<br>E -> N</html>");
        lblPhase4Dir.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblPhase4Dir.setBounds(265, 28, 54, 67);
        contentPane.add(lblPhase4Dir);

        JLabel lblDesc = new JLabel("Configure times for phases from each intersection");
        lblDesc.setBounds(10, 112, 309, 14);
        contentPane.add(lblDesc);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 137, 591, 332);
        contentPane.add(scrollPane);

        JPanel phasesPanel = new JPanel();
        scrollPane.setViewportView(phasesPanel);
        phasesPanel.setLayout(null);
        phasesPanel.setPreferredSize(new Dimension(591, 1500));


        int height = 20;
        int totalHeight = 110;
        int currentHeight = height;
        int width = 138;
        int yValue;
        int xValue = 10;

        for (int i = 0; i < intersectionIds.length; i++) {
            if (i == 0) {
                xValue = 10;
                yValue = currentHeight;
            } else if (i % 4 == 0) {
                // new line
                xValue = 10;
                currentHeight += totalHeight;
                yValue = currentHeight;
            } else {
                xValue += width;
                yValue = currentHeight;
            }

            JLabel lblIntersection = new JLabel("Intersection " + intersectionIds[i]);
            lblIntersection.setForeground(Color.RED);
            lblIntersection.setFont(new Font("Tahoma", Font.BOLD, 12));
            lblIntersection.setBounds(xValue, yValue, 108, 15);
            phasesPanel.add(lblIntersection);

            yValue += height;

            List<JTextField> phases = new ArrayList<>();

            JLabel lblPhase = new JLabel("Phase 1:");
            lblPhase.setFont(new Font("Tahoma", Font.BOLD, 11));
            lblPhase.setBounds(xValue, yValue, 60, 21);
            phasesPanel.add(lblPhase);

            JTextField textField = new JTextField();
            textField.setBounds(xValue + 53, yValue, 70, 20);
            phasesPanel.add(textField);
            textField.setColumns(10);

            phases.add(textField);

            yValue += height;

            JLabel lblPhase_1 = new JLabel("Phase 2:");
            lblPhase_1.setFont(new Font("Tahoma", Font.BOLD, 11));
            lblPhase_1.setBounds(xValue, yValue, 60, 21);
            phasesPanel.add(lblPhase_1);

            JTextField textField_2 = new JTextField();
            textField_2.setColumns(10);
            textField_2.setBounds(xValue + 53, yValue, 70, 20);
            phasesPanel.add(textField_2);

            phases.add(textField_2);

            yValue += height;

            JLabel lblPhase_2 = new JLabel("Phase 3:");
            lblPhase_2.setFont(new Font("Tahoma", Font.BOLD, 11));
            lblPhase_2.setBounds(xValue, yValue, 60, 21);
            phasesPanel.add(lblPhase_2);

            JTextField textField_1 = new JTextField();
            textField_1.setColumns(10);
            textField_1.setBounds(xValue + 53, yValue, 70, 20);
            phasesPanel.add(textField_1);

            phases.add(textField_1);

            yValue += height;

            JLabel lblPhase_3 = new JLabel("Phase 4:");
            lblPhase_3.setFont(new Font("Tahoma", Font.BOLD, 11));
            lblPhase_3.setBounds(xValue, yValue, 60, 21);
            phasesPanel.add(lblPhase_3);

            JTextField textField_3 = new JTextField();
            textField_3.setColumns(10);
            textField_3.setBounds(xValue + 53, yValue, 70, 20);
            phasesPanel.add(textField_3);

            phases.add(textField_3);

            phasesTextFields.put(intersectionIds[i], phases);
        }

        btnGenerate = new JButton("Generate");
        btnGenerate.setBounds(512, 480, 89, 23);
        contentPane.add(btnGenerate);

        new TrafficLightsConfiguratorController(this);
    }

    public void addActionListenerToGenerate(ActionListener actionListener) {
        btnGenerate.addActionListener(actionListener);
    }
}
