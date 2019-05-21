package biohive.authentication;

import java.util.*;

import biohive.fuzzyVault.Tuple;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Utils;

public class MinutiaeMatcher
{
    public ArrayList<Tuple<Integer, Integer>> vault;
    public ArrayList<Minutiae> minutiaes;
    private HashMap<Integer, Tuple<Integer, Double>> catalouge;

    PriorityQueue<GenSet> queue;

    public MinutiaeMatcher(ArrayList<Tuple<Integer, Integer>> vault, ArrayList<Minutiae> minutiaes)
    {
        this.vault = vault;
        this.minutiaes = minutiaes;
        catalouge = new HashMap<Integer, Tuple<Integer, Double>>();
    }

    public void initialize()
    {
        buildCatalouge();
        genQueue();
        return;
    }

    public void genQueue()      //create the queue for storing all possible sets 
    {
        queue = new PriorityQueue<GenSet>(new SetComparator());
        GenSet newset = new GenSet();
        
        int n =catalouge.size();
        int r = n>=5?5: n/2;

        recurse(catalouge, n, r , 0, 0, newset);
        return;
    }

    class SetComparator implements Comparator<GenSet>
    { 
        public int compare(GenSet s1, GenSet s2) 
        { 
            if (s1.avg < s2.avg) 
            {
                return 1; 
            }
            else if (s1.avg > s2.avg) 
            {
                return -1;           
            }
            else 
            {
                return 0; 
            }
        } 
    } 

    private void recurse(HashMap<Integer, Tuple<Integer,Double>> catalouge, int n, int r, int index, int i,GenSet newset)
    {
        if(index==r)
        {
            GenSet dupset = new GenSet();    //create duplicate set to add to queue - since newset is being passed by reference

            for(Tuple<Integer, Double> tuple : newset.topfive) 
            {
               dupset.topfive.add(new Tuple<Integer, Double>(tuple.x, tuple.y));
            }

            dupset.calculateAvg();        
            dupset.setNo = queue.size() + 1;

            queue.add(dupset);

            return;
        }
        if(i>=n) return;

        Object keyAtI = catalouge.keySet().toArray()[i];
        newset.add(index,new Tuple<Integer, Double>((Integer)keyAtI,catalouge.get(keyAtI).y)); //new set stores code(ketAtI), score 
     
        recurse(catalouge, n, r, index + 1, i + 1, newset); //current element is included 
        recurse(catalouge, n, r, index, i + 1, newset);  //current element is not included 
    }

    public ArrayList<Tuple<Integer, Integer>> getNextSet(ArrayList<Tuple<Integer, Integer>> vault)
    {
        HashMap<Integer, Integer> vaultMap = Utils.convertToMap(vault);
        ArrayList<Tuple<Integer, Integer>> list = new ArrayList<Tuple<Integer, Integer>>();
        
        GenSet gs = queue.poll();

        for(Tuple<Integer, Double> tuple : gs.topfive)
        {
            Tuple<Integer, Double> catalogueValue = catalouge.get(tuple.x);
            list.add(new Tuple<Integer, Integer>(tuple.x,vaultMap.get(catalogueValue.x)));
        }
        return list;
    }

    public boolean isSetEmpty()
    {
        return queue.isEmpty();
    }

    private HashMap<Integer, Tuple<Integer, Double>> buildCatalouge()
    {
        HashMap<Integer, Integer> vaultMap = Utils.convertToMap(vault);
        
        for (Minutiae m : minutiaes) 
        {
            Double minScore = Double.MAX_VALUE;
            for (Integer key : vaultMap.keySet()) 
            {
                Minutiae vaultM = new Minutiae(key);
                vaultM.decode();
                Double score = m.distance(vaultM);
                if(score < minScore)
                {
                    minScore = score;
                    catalouge.put(m.code, new Tuple<Integer, Double>(key, score));
                }
            }
        }

        return catalouge;
    }
}