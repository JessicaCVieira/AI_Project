package pacman;

import java.util.Random;

import utils.*;

/**
 * PacmanController is the
 * {@link utils.GameController Game Controller}
 * for the Pacman game.
 */
public class PacmanController implements GameController, Comparable<PacmanController>
{
    private static final int HIDDEN_DIM = 30;

    private static final int INPUT_DIM = Commons.PACMAN_STATE_SIZE;
    private static final int OUTPUT_DIM = Commons.PACMAN_NUM_ACTIONS;

    private double fitness = 0;
    private double[] values;
    public static final int NN_SIZE = (HIDDEN_DIM * (INPUT_DIM + OUTPUT_DIM)) + HIDDEN_DIM + OUTPUT_DIM;

    private double[][] hiddenWeights = new double[INPUT_DIM][HIDDEN_DIM];
    private double[] hiddenBiases = new double[HIDDEN_DIM];

    private double[][] outputWeights = new double[HIDDEN_DIM][OUTPUT_DIM];
    private double[] outputBiases = new double[OUTPUT_DIM];

    /**
     * Random weights and biases
     */
    public PacmanController()
    {
        this(randomizeParameters());
    }

    /**
	 * @param  values   	Weights and biases of the neural network
	 */
    public PacmanController(double[] values)
    {
        initializeParameters(values);
    }

    @Override
    public int nextMove(int[] currentState)
    {
        var outputValues = feedforward(currentState);
        int move = PacmanBoard.NONE;

        for (int i = 1; i < OUTPUT_DIM; i++)
            if (outputValues[i] > outputValues[move])
                move = i;

        return move;
    }

    /**
     * Activation function for hidden layer
     */
    private double hiddenLayerAF(double value)
    {
        return sigmoid(value);
    }

    /**
     * Activation function for output layer
     */
    private double outputLayerAF(double value)
    {
        return sigmoid(value);
    }

    private double sigmoid(double value)
    {
        return 1 / (1 + Math.exp(-value));
    }

    /**
	 * Initializes weights and biases from array
	 * @param  values	Weights and biases of the neural network
	 */
    private void initializeParameters(double[] values)
    {
        if (values.length != NN_SIZE)
            throw new IllegalArgumentException("Invalid array size!");
        
        int v = 0;
        this.values = values;

        for (int i = 0; i < INPUT_DIM; i++)
            for (int j = 0; j < HIDDEN_DIM; j++)
                hiddenWeights[i][j] = values[v++];
        
        for (int i = 0; i < HIDDEN_DIM; i++)
            hiddenBiases[i] = values[v++];
            
        for (int i = 0; i < HIDDEN_DIM; i++)
            for (int j = 0; j < OUTPUT_DIM; j++)
                outputWeights[i][j] = values[v++];

        for (int i = 0; i < OUTPUT_DIM; i++)
            outputBiases[i] = values[v++];
    }
    
    /**
	 * @return  Array of weights and biases
	 */
    public double[] getParameters()
    {
        return values;
    }

    /**
	 * @return  Fitness value
	 */
    public double getFitness()
    {
        return fitness;
    }

    /**
	 * @param  fitness  Fitness value
	 */
    public void setFitness(double fitness)
    {
        this.fitness = fitness;
    }

    /**
	 * @return  Output values after feedforward
	 */
    private double[] feedforward(int[] inputValues)
    {
        var hiddenNeurons = new double[HIDDEN_DIM];
        var outputNeurons = new double[OUTPUT_DIM];

        for (int i = 0; i < HIDDEN_DIM; i++)
        {
            hiddenNeurons[i] = hiddenBiases[i];

            for (int j = 0; j < INPUT_DIM; j++)
                hiddenNeurons[i] += inputValues[j] * hiddenWeights[j][i];
            
            hiddenNeurons[i] = hiddenLayerAF(hiddenNeurons[i]);
        }

        for (int i = 0; i < OUTPUT_DIM; i++)
        {
            outputNeurons[i] = outputBiases[i];

            for (int j = 0; j < HIDDEN_DIM; j++)
                outputNeurons[i] += hiddenNeurons[j] * outputWeights[j][i];
            
            outputNeurons[i] =  outputLayerAF(outputNeurons[i]);
        }

        return outputNeurons;
    }

    /**
	 * Randomizes values for weights and biases
     * @return  Array of weights and biases
	 */
    private static double[] randomizeParameters()
    {
        var rdm = new Random();
        var values = new double[NN_SIZE];
    
        for (int i = 0; i < NN_SIZE; i++)
            values[i] = rdm.nextDouble() * 2 - 1;

        return values;
    }
    
    @Override
    public int compareTo(PacmanController p)
    {
        return Double.compare(p.getFitness(), getFitness());
    }
    
    @Override
    public String toString()
    {
        var s = "[ ";

        for (var v : values)
            s += String.format("%.2f ", v);
        
        return s + "]";
    }
}
