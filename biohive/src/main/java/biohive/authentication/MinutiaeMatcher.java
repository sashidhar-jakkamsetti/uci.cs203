package biohive.authentication;

import java.util.ArrayList;
import java.util.HashMap;

import biohive.fuzzyVault.Tuple;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Utils;

public class MinutiaeMatcher
{
    public ArrayList<Tuple<Integer, Integer>> vault;
    public ArrayList<Minutiae> minutiaes;

    private HashMap<Integer, Tuple<Integer, Double>> catalouge;

    public MinutiaeMatcher(ArrayList<Tuple<Integer, Integer>> vault, ArrayList<Minutiae> minutiaes)
    {
        this.vault = vault;
        this.minutiaes = minutiaes;
        catalouge = new HashMap<Integer, Tuple<Integer, Double>>();
    }

    public void initialize()
    {
        buildCatalouge();
    }

    public boolean getNextSet(ArrayList<Tuple<Integer, Integer>> outGoingVault)
    {
        
        return true;
    }

    private HashMap<Integer, Tuple<Integer, Double>> buildCatalouge()
    {
        HashMap<Integer, Integer> vaultMap = Utils.convertToMap(vault);
        for (Minutiae m : minutiaes) 
        {
            Double minScore = 1.0;
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