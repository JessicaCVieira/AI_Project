package breakout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import tests.FileNN;

public class BreakoutLearning
{
    private static final int POPULATION_SIZE = 100;
    private static final int NUM_GENERATIONS = 500;
    
    private static final double FIT_POPULATION_RATE = 0.2;
    private static final int FIT_POPULATION_SIZE = (int)(POPULATION_SIZE * FIT_POPULATION_RATE);

    private static final double MUTATION_RATE = 0.5;
    private static final int NUM_GENERATIONS_STUCK = 5;

    private double maxFitness = 0;
    private int maxFitnessGen = 0;
    private int currentGen = 0;

    private static Random rdm = new Random();
    private ArrayList<BreakoutController> pop;

    public BreakoutLearning()
    {
        rdm.setSeed(290343857);
        createPopulation();
        search();

        var nn = new FileNN(new int[]{5,7}, pop.get(0), "bestBreakout");
        new FileNN(nn, "bestBreakout");
    }

    /**
     * Creates population of {@value #POPULATION_SIZE}
     * elements with randomized values
     */
    private void createPopulation()
    {
        pop = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++)
            pop.add(new BreakoutController());
    }

    /**
     * Repeats for {@value #NUM_GENERATIONS} generations
     * or until it reaches the solution
     */
    private void search()
    {
        for (int i = 0; i < NUM_GENERATIONS; i++)
        {
            // evaluate each individual
            for (int j = 0; j < POPULATION_SIZE; j++)
            {
                var b1 = new BreakoutBoard(pop.get(j), false, 5);
                b1.runSimulation();
                var b2 = new BreakoutBoard(pop.get(j), false, 7);
                b2.runSimulation();

                var b1fitness = b1.getFitness();
                var b2fitness = b2.getFitness();

                var min = Math.min(b1fitness, b2fitness);
                var avg = (b1fitness + b2fitness) / 2;
                
                // one game good and other bad
                // fitness = worst score
                pop.get(j).setFitness(min + 100000 < avg ? min : avg);
            }

            Collections.sort(pop);
            var best = pop.get(0);

            if (best.getFitness() > maxFitness)
            {
                System.out.println((i+1) + " - " + best.getFitness());
                maxFitness = best.getFitness();
                maxFitnessGen = i;
            }

            // end if solution is reached
            if (best.getFitness() >= 1000000)
                break;

            currentGen = i;
            createNextGen();
        }
    }

    /**
     * Creates next generation by keeping
     * fit individuals and generating
     * children for remaining population
     */
    private void createNextGen()
    {
        var newPop = new ArrayList<BreakoutController>();

        // keep fit population
        for (int i = 0; i < FIT_POPULATION_SIZE; i++)
            newPop.add(pop.get(i));

        // create new population
        if (currentGen - maxFitnessGen > NUM_GENERATIONS_STUCK)
        {
            // randomize population
            for (int i = FIT_POPULATION_SIZE; i < POPULATION_SIZE; i++)
                newPop.add(new BreakoutController());
            
            System.out.println((currentGen+1) + " - ! ");
            maxFitnessGen = currentGen;
        }
        else
        {
            // create children
            for (int i = FIT_POPULATION_SIZE; i < POPULATION_SIZE; i = i + 2)
                for (var child : createChild())
                    newPop.add(child);
        }

        pop = newPop;
    }

    private ArrayList<BreakoutController> createChild()
    {
        var child = crossover();
        var b = new ArrayList<BreakoutController>();

        for (int i = 0; i < 2; i++)
        {
            mutation(child[i]);
            b.add(new BreakoutController(child[i]));
        }

        return b;
    }

    private double[][] crossover()
    {
        var p = new double[2][];
        int j = 0;

        // pick parents by tournament
        for (var parent : chooseParents())
            p[j++] = parent.getParameters();

        double[][] child = new double[2][p[0].length];

        // one point crossover
        int point = rdm.nextInt(child[0].length);

        for (int i = 0; i < child[0].length; i++)
        {
            child[0][i] = i <= point ? p[0][i] : p[1][i];
            child[1][i] = i <= point ? p[1][i] : p[0][i];
        }

        return child;
    }

    private BreakoutController[] chooseParents()
    {
        var p = new BreakoutController[4];

        for (int i = 0; i < p.length; i++)
            p[i] = pop.get(rdm.nextInt(POPULATION_SIZE));

        Arrays.sort(p);
        return Arrays.copyOfRange(p, 0, 2);
    }

    private void mutation(double[] child)
    {
        if (rdm.nextDouble() <= MUTATION_RATE)
            for (int i = 0; i < 5; i++)
                child[rdm.nextInt(child.length)] = rdm.nextDouble() * 2 - 1;
    }
}
