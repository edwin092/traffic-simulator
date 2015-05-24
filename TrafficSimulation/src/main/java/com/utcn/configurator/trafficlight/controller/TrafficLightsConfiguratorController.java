package com.utcn.configurator.trafficlight.controller;

import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.configurator.trafficlight.utils.TrafficLightsConfiguratorUtil;
import com.utcn.configurator.trafficlight.view.TrafficLightsConfiguratorView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TrafficLightsConfiguratorController {

    public TrafficLightsConfiguratorController(TrafficLightsConfiguratorView view) {
        view.addActionListenerToGenerate(new GenerateButtonListener());
    }

    class GenerateButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            List<TrafficLightPhases> phasesList = new ArrayList<>();
            for (Map.Entry pair : TrafficLightsConfiguratorView.phasesTextFields.entrySet()) {

                phasesList.add(new TrafficLightPhases((Integer) pair.getKey(),
                        Integer.valueOf(((JTextField) ((ArrayList) pair.getValue()).get(0)).getText()),
                        Integer.valueOf(((JTextField) ((ArrayList) pair.getValue()).get(1)).getText()),
                        Integer.valueOf(((JTextField) ((ArrayList) pair.getValue()).get(2)).getText()),
                        Integer.valueOf(((JTextField) ((ArrayList) pair.getValue()).get(3)).getText())));
            }

            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("."));
            fc.setAcceptAllFileFilterUsed(false);
            fc.showOpenDialog(null);

            if (fc.getSelectedFile() != null) {
                boolean res = TrafficLightsConfiguratorUtil.exportToJSON(fc.getSelectedFile().getPath(), phasesList);

                if (!res) {
                    JOptionPane.showMessageDialog(null,
                            "Export failed.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
