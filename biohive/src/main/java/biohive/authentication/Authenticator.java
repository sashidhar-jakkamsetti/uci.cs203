package biohive.authentication;

import java.util.ArrayList;
import java.util.HashMap;

import biohive.fuzzyVault.*;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Utils;
import biohive.validation.Validator;

public class Authenticator
{
    private ArrayList<ArrayList<Tuple<Integer, Integer>>> hVaults;
    private String userId;
    private String honeydb;

    public Authenticator(String userId, ArrayList<ArrayList<Tuple<Integer, Integer>>> hVaults, String honeydb)
    {
        this.userId = userId;
        this.hVaults = hVaults;
        this.honeydb = honeydb;
    }
    
    public boolean authenticate(ArrayList<Minutiae> minutiaes) throws Exception
    {
        ArrayList<Minutiae> redecodedMinutiaes = decodeMinutiae(minutiaes);
        Integer idx = 0;
        for ( ArrayList<Tuple<Integer, Integer>> vault : hVaults) 
        {
            if(isSugarVault(vault, redecodedMinutiaes))
            {
                return Validator.validate(userId, honeydb, idx);
            }
            idx++;
        }

        return false;
    }

    private ArrayList<Minutiae> decodeMinutiae(ArrayList<Minutiae> minutiaes)
    {
        ArrayList<Minutiae> newMinutiaes = new ArrayList<Minutiae>();
        for (Minutiae m : minutiaes) 
        {
            Minutiae newM = new Minutiae(m.code);
            newM.cnf = m.cnf;
            newM.decode();
            newMinutiaes.add(newM);
        }

        return newMinutiaes;
    }

    private boolean isSugarVault(ArrayList<Tuple<Integer, Integer>> vault, ArrayList<Minutiae> minutiaes)
    {
        HashMap<Integer, Integer> vaultMap = Utils.convertToMap(vault);
        HashMap<Integer, Tuple<Integer, Double>> catalouge = buildCatalouge(minutiaes, vaultMap);
        

        return true;
    }

    private HashMap<Integer, Tuple<Integer, Double>> buildCatalouge(ArrayList<Minutiae> minutiaes, HashMap<Integer, Integer> vaultMap)
    {
        HashMap<Integer, Tuple<Integer, Double>> catalouge = new HashMap<Integer, Tuple<Integer, Double>>();
        for (Minutiae m : minutiaes) 
        {
            Double minScore = 1.0;
            for (Integer key : vaultMap.keySet()) 
            {
                Minutiae vaultM = new Minutiae(key);
                vaultM.decode();
                Double score = m.compare(vaultM);

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