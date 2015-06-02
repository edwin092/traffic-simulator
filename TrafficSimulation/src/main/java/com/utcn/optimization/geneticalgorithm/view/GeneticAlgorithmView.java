package com.utcn.optimization.geneticalgorithm.view;

import com.utcn.optimization.geneticalgorithm.controller.GeneticAlgorithmController;
import com.utcn.view.TrafficSimulationView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class GeneticAlgorithmView extends JFrame {

    public static TrafficSimulationView trafficSimulationView;
    public static JTextField popSizeTextField;
	public static JTextField evolTextField;
	public static JTextField simTimeTextField;
    public static JTextField mutationTextField;
    public static JTextField crossoverTextField;
    public static JTextField tourSizeTextField;
    public static JTextField tourProbTextField;

    private JButton btnOptimize;

	/**
	 * Create the frame.
	 */
	public GeneticAlgorithmView(TrafficSimulationView trafficSimulationView) {
        GeneticAlgorithmView.trafficSimulationView = trafficSimulationView;

		setTitle("Genetic Algorithm");
		setBounds(100, 100, 466, 396);
        JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblSimulationParameters = new JLabel("Simulation parameters");
		lblSimulationParameters.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSimulationParameters.setBounds(10, 22, 185, 15);
		contentPane.add(lblSimulationParameters);

		JLabel lblGeneticAlgorithmParameters = new JLabel(
				"Genetic Algorithm parameters");
		lblGeneticAlgorithmParameters
				.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblGeneticAlgorithmParameters.setBounds(10, 93, 217, 14);
		contentPane.add(lblGeneticAlgorithmParameters);

		popSizeTextField = new JTextField();
		popSizeTextField.setBounds(141, 118, 86, 20);
		contentPane.add(popSizeTextField);
		popSizeTextField.setColumns(10);

		evolTextField = new JTextField();
		evolTextField.setBounds(141, 149, 86, 20);
		contentPane.add(evolTextField);
		evolTextField.setColumns(10);

		simTimeTextField = new JTextField();
		simTimeTextField.setBounds(109, 48, 86, 20);
		contentPane.add(simTimeTextField);
		simTimeTextField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Population size");
		lblNewLabel.setBounds(10, 118, 95, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Number of evolutions");
		lblNewLabel_1.setBounds(10, 152, 122, 14);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Simulation Time");
		lblNewLabel_2.setBounds(10, 51, 95, 14);
		contentPane.add(lblNewLabel_2);

		btnOptimize = new JButton("Optimize");
		btnOptimize.setBounds(354, 323, 89, 23);
		contentPane.add(btnOptimize);
		
		JLabel lblMutationRate = new JLabel("Mutation Rate");
		lblMutationRate.setBounds(10, 187, 95, 14);
		contentPane.add(lblMutationRate);
		
		JLabel lblCrossoverRate = new JLabel("Crossover Rate");
		lblCrossoverRate.setBounds(10, 220, 95, 14);
		contentPane.add(lblCrossoverRate);
		
		JLabel lblTournamentSelectorParameters = new JLabel("Tournament Selector parameters");
		lblTournamentSelectorParameters.setForeground(Color.RED);
		lblTournamentSelectorParameters.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTournamentSelectorParameters.setBounds(10, 248, 217, 14);
		contentPane.add(lblTournamentSelectorParameters);
		
		JLabel lblTournamentSize = new JLabel("Size");
		lblTournamentSize.setBounds(10, 273, 95, 14);
		contentPane.add(lblTournamentSize);
		
		JLabel lblSelectionProbability = new JLabel("Selection probability");
		lblSelectionProbability.setBounds(10, 306, 122, 14);
		contentPane.add(lblSelectionProbability);
		
		mutationTextField = new JTextField();
		mutationTextField.setColumns(10);
		mutationTextField.setBounds(141, 184, 86, 20);
		contentPane.add(mutationTextField);
		
		crossoverTextField = new JTextField();
		crossoverTextField.setColumns(10);
		crossoverTextField.setBounds(141, 217, 86, 20);
		contentPane.add(crossoverTextField);
		
		JLabel lblDenominatorOfThe = new JLabel("int (Denominator of the 1 / X fraction)");
		lblDenominatorOfThe.setForeground(Color.RED);
		lblDenominatorOfThe.setBounds(237, 187, 206, 14);
		contentPane.add(lblDenominatorOfThe);
		
		tourSizeTextField = new JTextField();
		tourSizeTextField.setColumns(10);
		tourSizeTextField.setBounds(141, 270, 86, 20);
		contentPane.add(tourSizeTextField);
		
		tourProbTextField = new JTextField();
		tourProbTextField.setColumns(10);
		tourProbTextField.setBounds(141, 303, 86, 20);
		contentPane.add(tourProbTextField);
		
		JLabel lblPercentage = new JLabel("percentage");
		lblPercentage.setForeground(Color.RED);
		lblPercentage.setBounds(237, 220, 106, 14);
		contentPane.add(lblPercentage);
		
		JLabel lblInt_1 = new JLabel("int");
		lblInt_1.setForeground(Color.RED);
		lblInt_1.setBounds(237, 152, 106, 14);
		contentPane.add(lblInt_1);
		
		JLabel lblInt = new JLabel("int");
		lblInt.setForeground(Color.RED);
		lblInt.setBounds(237, 121, 106, 14);
		contentPane.add(lblInt);
		
		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setForeground(Color.RED);
		lblSeconds.setBounds(205, 51, 106, 14);
		contentPane.add(lblSeconds);
		
		JLabel label = new JLabel("int");
		label.setForeground(Color.RED);
		label.setBounds(237, 273, 106, 14);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("(0.0, 1.0]");
		label_1.setForeground(Color.RED);
		label_1.setBounds(237, 306, 106, 14);
		contentPane.add(label_1);

        new GeneticAlgorithmController(this);
	}

	public void addActionListenerToOptimize(ActionListener actionListener) {
		btnOptimize.addActionListener(actionListener);
	}
}
