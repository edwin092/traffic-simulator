package com.utcn.optimization.geneticalgorithm.model;

import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.models.Intersection;
import com.utcn.view.TrafficSimulationView;
import org.jgap.*;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithmOptimization {

    public static int MINIMUM_PHASE_TIME = 5;
    public static int MAXIMUM_PHASE_TIME = 30;

    /**
     * Start genetic algorithm.
     *
     * @param view           TrafficSimulationView instance.
     * @param populationSize the size of the population.
     * @param evolutions     the number of evolutions.
     * @throws InvalidConfigurationException
     */
    public static List<TrafficLightPhases> optimize(TrafficSimulationView view, int populationSize, int evolutions)
            throws InvalidConfigurationException {
        Configuration conf = new DefaultConfiguration();

        FitnessFunction myFunc =
                new MinimizingWaitingTimeFitnessFunction(view);

        conf.setFitnessFunction(myFunc);

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
            System.out.println(i);
            population.evolve();
        }
        IChromosome bestSolution = population.getFittestChromosome();

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
}
