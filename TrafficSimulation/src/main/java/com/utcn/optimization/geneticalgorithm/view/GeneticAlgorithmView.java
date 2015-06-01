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
	private JButton btnOptimize;

	/**
	 * Create the frame.
	 */
	public GeneticAlgorithmView(TrafficSimulationView trafficSimulationView) {
        GeneticAlgorithmView.trafficSimulationView = trafficSimulationView;

		setTitle("Genetic Algorithm");
		setBounds(100, 100, 340, 300);
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

		JLabel lblNewLabel_3 = new JLabel(
				"<html>WARNING!<br>A large population size and/or a high number <br>of evolutions will result in a slower optimization!<html>");
		lblNewLabel_3.setForeground(Color.RED);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_3.setBounds(10, 183, 262, 53);
		contentPane.add(lblNewLabel_3);

		btnOptimize = new JButton("Optimize");
		btnOptimize.setBounds(225, 227, 89, 23);
		contentPane.add(btnOptimize);

        new GeneticAlgorithmController(this);
	}

	public void addActionListenerToOptimize(ActionListener actionListener) {
		btnOptimize.addActionListener(actionListener);
	}
}
