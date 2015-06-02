package com.utcn.optimization.geneticalgorithm.model;

import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.models.Intersection;
import com.utcn.view.TrafficSimulationView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jgap.*;
import org.jgap.event.EventManager;
import org.jgap.impl.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithmOptimization {

    public static int MINIMUM_PHASE_TIME = 5;
    public static int MAXIMUM_PHASE_TIME = 30;

    private static List<Double> fitnessValues;

    /**
     * Start genetic algorithm.
     *
     * @param view                 TrafficSimulationView instance.
     * @param populationSize       the size of the population.
     * @param evolutions           the number of evolutions.
     * @param mutationRate
     * @param crossoverPercentage
     * @param tournamentSize
     * @param selectionProbability
     * @return
     * @throws InvalidConfigurationException
     */
    public static List<TrafficLightPhases> optimize(TrafficSimulationView view, int populationSize, int evolutions,
                                                    int mutationRate, double crossoverPercentage, int tournamentSize,
                                                    double selectionProbability)
            throws InvalidConfigurationException {
        fitnessValues = new ArrayList<>();

        // set default configuration for GA
//        Configuration conf = new DefaultConfiguration();
        Configuration conf = new Configuration("MyConfiguration");

        conf.setBreeder(new GABreeder());
        conf.setRandomGenerator(new StockRandomGenerator());
        conf.setEventManager(new EventManager());

//        TournamentSelector e = new TournamentSelector(conf, 50, 0.9D);
        TournamentSelector e = new TournamentSelector(conf, tournamentSize, selectionProbability);
        conf.addNaturalSelector(e, false);

        conf.setMinimumPopSizePercent(0);
        conf.setSelectFromPrevGen(1.0D);
        conf.setKeepPopulationSizeConstant(true);
        conf.setFitnessEvaluator(new DefaultFitnessEvaluator());
        conf.setChromosomePool(new ChromosomePool());

//        conf.addGeneticOperator(new CrossoverOperator(conf, 0.35D));
//        conf.addGeneticOperator(new MutationOperator(conf, 12));
        conf.addGeneticOperator(new CrossoverOperator(conf, crossoverPercentage));
        conf.addGeneticOperator(new MutationOperator(conf, mutationRate));

        FitnessFunction myFitnessFunction = new MinimizingWaitingTimeFitnessFunction(view);
        conf.setFitnessFunction(myFitnessFunction);

        Genotype.setStaticConfiguration(conf);

        List<Integer> ids = new ArrayList<>();
        for (Intersection intersection : view.getIntersections()) {
            if (intersection.isFourPhased()) {
                ids.add(intersection.getId());
            }
        }

        Gene[] intersectionGenes = new Gene[ids.size()];

        // crete genes
        for (int i = 0; i < ids.size(); i++) {
            CompositeGene intersectionGene = new CompositeGene();
            Gene p1 = new IntegerGene(conf, MINIMUM_PHASE_TIME, MAXIMUM_PHASE_TIME);
            Gene p2 = new IntegerGene(conf, MINIMUM_PHASE_TIME, MAXIMUM_PHASE_TIME);
            Gene p3 = new IntegerGene(conf, MINIMUM_PHASE_TIME, MAXIMUM_PHASE_TIME);
            Gene p4 = new IntegerGene(conf, MINIMUM_PHASE_TIME, MAXIMUM_PHASE_TIME);
            intersectionGene.addGene(p1);
            intersectionGene.addGene(p2);
            intersectionGene.addGene(p3);
            intersectionGene.addGene(p4);

            intersectionGenes[i] = intersectionGene;
        }

        Chromosome chromosome = new Chromosome(conf, intersectionGenes);

        conf.setSampleChromosome(chromosome);
        conf.setPopulationSize(populationSize);
        Genotype population = Genotype.randomInitialGenotype(conf);

        for (int i = 0; i < evolutions; i++) {
            fitnessValues.add(population.getFittestChromosome().getFitnessValue());
            System.out.println(i);
            population.evolve();
        }
        IChromosome bestSolution = population.getFittestChromosome();
        fitnessValues.add(bestSolution.getFitnessValue());

        // generate and show line chart
        generateLineChart();

        List<TrafficLightPhases> trafficLightPhaseses = new ArrayList<>();

        for (int i = 0; i < bestSolution.getGenes().length; i++) {
            CompositeGene intersectionGene = (CompositeGene) bestSolution.getGenes()[i];

            trafficLightPhaseses.add(new TrafficLightPhases(ids.get(i),
                    (int) intersectionGene.getGenes().get(0).getAllele(),
                    (int) intersectionGene.getGenes().get(1).getAllele(),
                    (int) intersectionGene.getGenes().get(2).getAllele(),
                    (int) intersectionGene.getGenes().get(3).getAllele()));
        }

        return trafficLightPhaseses;
    }

    /**
     * Generate and show line chart of waiting times.
     */
    private static void generateLineChart() {
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (int i = 0; i < fitnessValues.size(); i++) {
            line_chart_dataset.addValue(MinimizingWaitingTimeFitnessFunction.BIG_NUMBER - fitnessValues.get(i),
                    "avg waiting time", String.valueOf(i + 1));
        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Average Waiting Time Optimization",
                "Populations",
                "Average Waiting Time",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1024; /* Width of the image */
        int height = 768; /* Height of the image */

        File lineChart = new File("GALineChart.jpeg");
        try {
            ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);

            JLabel label = new JLabel(new ImageIcon("GALineChart.jpeg"));
            JFrame f = new JFrame();
            f.setTitle("Genetic Algorithm Results");
            f.getContentPane().add(label);
            f.pack();
            f.setLocation(100, 100);
            f.setVisible(true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
