package com.utcn.optimization;

import com.utcn.bl.MinimizingWaitingTimeFitnessFunction;
import com.utcn.models.Intersection;
import com.utcn.view.TrafficSimulationView;
import org.jgap.*;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithmOptimization {

    public static void optimize(TrafficSimulationView view) throws InvalidConfigurationException {
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

        Gene[] sampleGenes = new Gene[ids.size()];

        for (int i = 0; i < ids.size(); i++) {
            CompositeGene intersectionGene = new CompositeGene();
            Gene p1 = new IntegerGene(conf, 5, 30);
            Gene p2 = new IntegerGene(conf, 5, 30);
            Gene p3 = new IntegerGene(conf, 5, 30);
            Gene p4 = new IntegerGene(conf, 5, 30);
            intersectionGene.addGene(p1);
            intersectionGene.addGene(p2);
            intersectionGene.addGene(p3);
            intersectionGene.addGene(p4);

            sampleGenes[i] = intersectionGene;
        }

        Chromosome sampleChromosome = new Chromosome(conf, sampleGenes);

        conf.setSampleChromosome(sampleChromosome);

        conf.setPopulationSize(5);

        Genotype population = Genotype.randomInitialGenotype(conf);
        for (int i = 0; i < 20; i++) {
            population.evolve();
        }
        IChromosome bestSolutionSoFar = population.getFittestChromosome();
    }
}
