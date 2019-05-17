package biohive.authentication;

import java.util.ArrayList;
import java.util.HashMap;

import biohive.fuzzyVault.Tuple;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Utils;

public class GenSet
{
    
    public ArrayList<Tuple<Integer, Double>> topfive = new ArrayList<Tuple<Integer, Double>>();
    public double avg;
    public int setNo;

    GenSet()
    {

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

    public void CalculateAvg()
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