package tests;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import breakout.BreakoutController;
import pacman.PacmanController;

public class FileNN
{
    private int[] seeds;
    private double[] values;
    private double fitness;
    private String file;
        
    public FileNN(String file)
    {
        this.file = file + ".txt";
        readFile();
    }
    
    public FileNN(FileNN nn, String file)
    {
        this.file = file + ".txt";
        readFile();
        if (nn.fitness > fitness)
            nn.writeFile();
    }

    public FileNN(int[] seeds, BreakoutController b, String file)
    {
        this.file = file + ".txt";
        this.seeds = seeds;
        this.values = b.getParameters();
        this.fitness = b.getFitness();
    }

    public FileNN(int[] seeds, PacmanController p, String file)
    {
        this.file = file + ".txt";
        this.seeds = seeds;
        this.values = p.getParameters();
        this.fitness = p.getFitness();
    }

    public void writeFile()
    {
        try
        {
            File bb = new File(file);     // false overwrite
            FileWriter writer = new FileWriter(bb, false);  // true append

            writer.write(seedsToString() + "\n" + fitness + "\n\n" + valuesToString());
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile()
    {
        try
        {
            File f = new File(file);
            f.createNewFile();		// only creates if not found
            Scanner s = new Scanner(f);

            var str = s.nextLine().split(" ");
            seeds = new int[str.length];

            for (int i = 0; i < str.length; i++)
                seeds[i] = Integer.parseInt(str[i]);
            
            fitness = Double.parseDouble(s.nextLine().toString());
            
            s.nextLine();
            str = s.nextLine().split(" ");
            values = new double[str.length];

            for (int i = 0; i < str.length; i++)
                values[i] = Double.parseDouble(str[i]);

            s.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String valuesToString()
    {
        var s = "";

        for (var v : values)
            s += v + " ";
        
        return s;
    }

    public String seedsToString()
    {
        var s = "";

        for (var v : seeds)
            s += v + " ";
        
        return s;
    }

    public double[] getParameters()
    {
        return values;
    }     
    
    public int[] getSeeds()
    {
        return seeds;
    }
}

