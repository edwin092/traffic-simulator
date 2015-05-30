package com.utcn.optimization;

import com.utcn.bl.VehicleGenerator;
import com.utcn.configurator.trafficlight.model.TrafficLightPhases;
import com.utcn.models.Intersection;
import com.utcn.view.TrafficSimulationView;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import java.util.ArrayList;
import java.util.List;

public class MinimizingWaitingTimeFitnessFunction extends FitnessFunction {

    private static final int BIG_NUMBER = 1000;

    private TrafficSimulationView view;

    public MinimizingWaitingTimeFitnessFunction(TrafficSimulationView view) {
        this.view = view;
    }

    /**
     * Determine the fitness of the given Chromosome instance. The higher the
     * return value, the more fit the instance. This method should always
     * return the same fitness value for two equivalent Chromosome instances.
     *
     * @param a_subject: The Chromosome instance to evaluate.
     * @return A positive integer reflecting the fitness rating of the given
     * Chromosome.
     */
    public double evaluate(IChromosome a_subject) {

        List<Integer> ids = new ArrayList<>();
        for (Intersection intersection : view.getIntersections()) {
            if (intersection.isFourPhased()) {
                ids.add(intersection.getId());
            }
        }

        List<TrafficLightPhases> trafficLightPhaseses = new ArrayList<>();
        for (int i = 0; i < a_subject.getGenes().length; i++) {
            CompositeGene intersectionGene = (CompositeGene) a_subject.getGenes()[i];

            trafficLightPhaseses.add(new TrafficLightPhases(ids.get(i),
                    (int) intersectionGene.getGenes().get(0).getAllele(),
                    (int) intersectionGene.getGenes().get(1).getAllele(),
                    (int) intersectionGene.getGenes().get(2).getAllele(),
                    (int) intersectionGene.getGenes().get(3).getAllele()));
        }

        // reset vehicle ids
        VehicleGenerator.currentId = 0;
        view.setTrafficLightPhaseses(trafficLightPhaseses);
        // simulation step = simulation time
        view.setSimulationStep(view.getSimulationTime());
        // start simulation
        view.simulate();

        return BIG_NUMBER - view.getVehicleStatisticsManager().getVehiclesAverageWaitingTime();
    }
}
