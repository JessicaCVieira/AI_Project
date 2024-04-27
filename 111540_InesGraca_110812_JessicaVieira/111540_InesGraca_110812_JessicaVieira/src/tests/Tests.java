package tests;

import breakout.*;
import pacman.*;

public class Tests {
    
    public static void main(String[] args)
    {
        new BreakoutLearning();
        var nn = new FileNN("bestBreakout");
        for (var seed : nn.getSeeds())
            new Breakout(new BreakoutController(nn.getParameters()), seed);

        new PacmanLearning();
        nn = new FileNN("bestPacman");
        for (var seed : nn.getSeeds())
            new Pacman(new PacmanController(nn.getParameters()), true, seed);
        
        nn = new FileNN("3k_8k_breakout");
        for (var seed : nn.getSeeds())
            new Breakout(new BreakoutController(nn.getParameters()), seed);
    
        nn = new FileNN("49k_75k_pacman");
        for (var seed : nn.getSeeds())
            new Pacman(new PacmanController(nn.getParameters()), true, seed);
    }
}
