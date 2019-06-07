package biohive.authentication;

import java.util.*;

import biohive.fuzzyVault.FuzzyVault;
import biohive.fuzzyVault.Tuple;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;
import biohive.utility.Utils;

public class MinutiaeMatcher
{
    public FuzzyVault vault;
    public ArrayList<Minutiae> minutiaes;

    private HashMap<Integer, Integer> vaultMap;
    private HashMap<Integer, Tuple<Integer, Double>> catalouge;
    private PriorityQueue<GenSet> queue;
    private Integer queueSize = 0;
    private Double reqSize;

    public MinutiaeMatcher(FuzzyVault vault, ArrayList<Minutiae> minutiaes, Double reqSize)
    {
        this.vault = vault;
        this.minutiaes = minutiaes;
        vaultMap = Utils.convertToMap(vault.getVault());
        catalouge = new HashMap<Integer, Tuple<Integer, Double>>();
        queue = new PriorityQueue<GenSet>(new SetComparator());
        this.reqSize = reqSize;
    }

    public void initialize(Boolean buildQueue)
    {
        buildCatalouge();

        if(buildQueue)
        {
            genQueue();
            queueSize = queue.size();
        }
        return;
    }

    public void genQueue()
    {
        GenSet newset = new GenSet();
        
        int n = catalouge.size();
        int r = catalouge.size() >= Constants.POLY_DEGREE + 1 ? Constants.POLY_DEGREE + 1: n/2;

        recurse(catalouge, n, r , 0, 0, newset);
        return;
    }

    class SetComparator implements Comparator<GenSet>
    { 
        public int compare(GenSet s1, GenSet s2) 
        { 
            return s1.avg.compareTo(s2.avg);
        } 
    } 

    private void recurse(HashMap<Integer, Tuple<Integer,Double>> catalouge, int n, int r, int index, int i,GenSet newset)
    {
        if(index == r)
        {
            GenSet dupset = new GenSet();

            for(Tuple<Integer, Double> tuple : newset.topfive) 
            {
               dupset.topfive.add(new Tuple<Integer, Double>(tuple.x, tuple.y));
            }

            dupset.calculateAvg();        
            dupset.setNo = queue.size() + 1;

            queue.add(dupset);

            return;
        }

        if(i >= n) 
        {
            return;
        }

        Object keyAtI = catalouge.keySet().toArray()[i];
        newset.add(index, new Tuple<Integer, Double>((Integer)keyAtI, catalouge.get(keyAtI).y));
     
        recurse(catalouge, n, r, index + 1, i + 1, newset);
        recurse(catalouge, n, r, index, i + 1, newset);
    }

    public Double getClosenessScore() 
    {
        Double aggregateScore = 0.0;

        for (Tuple<Integer, Double> item : catalouge.values()) 
        {  
            aggregateScore += item.y;
        }

        return aggregateScore / catalouge.size();
    }

    public ArrayList<Tuple<Integer, Integer>> getNextSet()
    {
        ArrayList<Tuple<Integer, Integer>> returnVault = new ArrayList<Tuple<Integer, Integer>>();

        if(queue.size() < queueSize * reqSize)
        {
            queue.clear();
        }
        else if(!queue.isEmpty())
        {
            GenSet gs = queue.poll();
            for(Tuple<Integer, Double> tuple : gs.topfive)
            {
                Tuple<Integer, Double> catalogueValue = catalouge.get(tuple.x);
                returnVault.add(new Tuple<Integer, Integer>(catalogueValue.x, vaultMap.get(catalogueValue.x)));
            }
        }

        return returnVault;
    }

    private HashMap<Integer, Tuple<Integer, Double>> buildCatalouge()
    {
        Integer matchCount = 0;
        for (Minutiae m : minutiaes) 
        {
            Double minScore = Double.MAX_VALUE;
            Integer pointX = m.code;
            for (Tuple<Integer, Integer> point : vault.getVault()) 
            {
                Minutiae vaultM = new Minutiae(point.x);
                vaultM.decode();
                Double score = m.distance(vaultM);
                if(score < minScore)
                {
                    minScore = score;
                    pointX = point.x;
                    catalouge.put(m.code, new Tuple<Integer, Double>(point.x, score));
                }
            }

            /* Debug code.
            if(pointX.equals(m.code))
            {
                matchCount++;
                System.out.println(m.code);
            }
            */
        }

        return catalouge;
    }

    public boolean isSetEmpty()
    {
        return queue.isEmpty();
    }
}