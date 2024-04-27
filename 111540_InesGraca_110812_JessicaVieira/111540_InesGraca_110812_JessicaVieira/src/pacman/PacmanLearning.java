package pacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import tests.FileNN;

public class PacmanLearning
{
    private static final int POPULATION_SIZE = 100;
    private static final int NUM_GENERATIONS = 500;
    
    private static final double FIT_POPULATION_RATE = 0.2;
    private static final int FIT_POPULATION_SIZE = (int)(POPULATION_SIZE * FIT_POPULATION_RATE);

    private static final double MUTATION_RATE = 0.3;
    private static final int NUM_GENERATIONS_STUCK = 5;

    private double maxFitness = 0;
    private int maxFitnessGen = 0;
    private int currentGen = 0;

    private static Random rdm = new Random();
    private ArrayList<PacmanController> pop;

    public PacmanLearning()
    {
        rdm.setSeed(290343857);
        createPopulation();
        search();

        var nn = new FileNN(new int[]{5,7}, pop.get(0), "bestPacman");
        new FileNN(nn, "bestPacman");
    }

    /**
     * Creates population of {@value #POPULATION_SIZE}
     * elements with randomized values
     */
    private void createPopulation()
    {
        pop = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++)
            pop.add(new PacmanController());
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
                var p1 = new PacmanBoard(pop.get(j), false, 5);
                p1.runSimulation();
                var p2 = new PacmanBoard(pop.get(j), false, 7);
                p2.runSimulation();

                var p1fitness = p1.getFitness();
                var p2fitness = p2.getFitness();

                var min = Math.min(p1fitness, p2fitness);
                var avg = (p1fitness + p2fitness) / 2;
                
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
        var newPop = new ArrayList<PacmanController>();

        // keep fit population
        for (int i = 0; i < FIT_POPULATION_SIZE; i++)
            newPop.add(pop.get(i));

        // create new population
        if (currentGen - maxFitnessGen > NUM_GENERATIONS_STUCK)
        {
            // randomize population
            for (int i = FIT_POPULATION_SIZE; i < POPULATION_SIZE; i++)
                newPop.add(new PacmanController());
            
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

    private ArrayList<PacmanController> createChild()
    {
        var child = crossover();
        var b = new ArrayList<PacmanController>();

        for (int i = 0; i < 2; i++)
        {
            mutation(child[i]);
            b.add(new PacmanController(child[i]));
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

    private PacmanController[] chooseParents()
    {
        var p = new PacmanController[4];

        for (int i = 0; i < p.length; i++)
            p[i] = pop.get(rdm.nextInt(POPULATION_SIZE));

        Arrays.sort(p);
        return Arrays.copyOfRange(p, 0, 2);
    }

    private void mutation(double[] child)
    {
        if (rdm.nextDouble() <= MUTATION_RATE)
            for (int i = 0; i < 50; i++)
                child[rdm.nextInt(child.length)] = rdm.nextDouble() * 2 - 1;
    }
}
