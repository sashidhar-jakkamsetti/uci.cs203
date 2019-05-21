package biohive.authentication;

import java.util.ArrayList;

import biohive.fuzzyVault.Tuple;

public class GenSet
{
    public ArrayList<Tuple<Integer, Double>> topfive;
    public double avg;
    public int setNo;

    GenSet()
    {
        topfive = new ArrayList<Tuple<Integer, Double>>();
    }

    GenSet(GenSet s)
    {
        this.topfive = s.topfive;
        this.avg = s.avg;
        this.setNo = s.setNo;
    }
    
    public void add(int index, Tuple<Integer, Double> tuple)
    {
        if(topfive.size()>index)
        {    
            topfive.set(index, tuple);
        }
        else 
        {
            topfive.add(tuple);
        }
    }

    public void calculateAvg()
    {
        double sum = 0;
        double count = 0;
        for(Tuple<Integer, Double> tuple: topfive)
        {
            sum += tuple.y;
            count++;
        }

        this.avg = sum/count;
    }

    public int size()
    {
        return topfive.size();
    }
}