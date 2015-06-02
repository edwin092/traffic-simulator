package com.utcn.optimization.geneticalgorithm.controller;

import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.configurator.trafficlight.utils.TrafficLightsConfiguratorUtil;
import com.utcn.optimization.geneticalgorithm.model.GeneticAlgorithmOptimization;
import com.utcn.optimization.geneticalgorithm.view.GeneticAlgorithmView;
import com.utcn.view.TrafficSimulationView;
import org.jgap.InvalidConfigurationException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class GeneticAlgorithmController {

    public GeneticAlgorithmController(GeneticAlgorithmView view) {
        view.addActionListenerToOptimize(new OptimizeButtonListener());
    }

    class OptimizeButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int simulationTime = Integer.valueOf(GeneticAlgorithmView.simTimeTextField.getText());
                final int populationSize = Integer.valueOf(GeneticAlgorithmView.popSizeTextField.getText());
                final int evolutions = Integer.valueOf(GeneticAlgorithmView.evolTextField.getText());
                final int mutationRate = Integer.valueOf(GeneticAlgorithmView.mutationTextField.getText());
                final double crossoverRate = Double.valueOf(GeneticAlgorithmView.crossoverTextField.getText());
                final int tournamentSize = Integer.valueOf(GeneticAlgorithmView.tourSizeTextField.getText());
                final double tournamentProbabity = Double.valueOf(GeneticAlgorithmView.tourProbTextField.getText());

                if (tournamentProbabity < 0.0 || tournamentProbabity > 1.0) {
                    JOptionPane.showMessageDialog(null,
                            "Tournament selection probability must be in (0.0, 1.0]!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TrafficSimulationView.SIMULATION_TIME = simulationTime;

                // show progress bar
                final JFrame progressFrame = new JFrame("Optimization process");
                Container content = progressFrame.getContentPane();
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                Border border = BorderFactory.createTitledBorder("Optimizing...");
                progressBar.setBorder(border);
                content.add(progressBar, BorderLayout.NORTH);
                progressFrame.setSize(300, 100);
                progressFrame.setVisible(true);


                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    public Void doInBackground() {
                        List<TrafficLightPhases> phasesList = null;
                        try {
                            // start optimization process
                            phasesList = GeneticAlgorithmOptimization.optimize(
                                    GeneticAlgorithmView.trafficSimulationView,
                                    populationSize, evolutions, mutationRate, crossoverRate, tournamentSize, tournamentProbabity);
                        } catch (InvalidConfigurationException e1) {
                            e1.printStackTrace();
                        } finally {
                            progressFrame.setVisible(false);
                            progressFrame.dispose();
                        }

                        // we have an optimal configuration file
                        JFileChooser fc = new JFileChooser();
                        fc.setDialogTitle("Save optimal configuration");
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

                        return null;
                    }
                };
                worker.execute();


            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Invalid input!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
