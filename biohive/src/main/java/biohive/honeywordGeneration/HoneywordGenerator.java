package biohive.honeywordGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import biohive.fuzzyVault.FuzzyVault;
import biohive.fuzzyVault.Tuple;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;
import cc.redberry.rings.IntegersZp64;

public class HoneywordGenerator
{
    private FuzzyVault sugarVault;
    private IntegersZp64 field31;
    private IntegersZp64 field61;
    private int honeyChecker;
    private ArrayList<ArrayList<Tuple<Integer, Integer>>> honeyVaults;

    public HoneywordGenerator(FuzzyVault sugarVault)
    {
        this.sugarVault = sugarVault.clone();
        field31 = new IntegersZp64(Constants.FIELD_ORDER_5);
        field61 = new IntegersZp64(Constants.FIELD_ORDER_6);
        honeyVaults = new ArrayList<ArrayList<Tuple<Integer, Integer>>>();
    }

    public boolean generate()
    {
        HashMap<Integer, ArrayList<Tuple<Integer, Integer>>> unlockedHVaults = new HashMap<Integer, ArrayList<Tuple<Integer, Integer>>>();
        unlockedHVaults.put(0, sugarVault.getVault());
        
        for(int i = 1; i < Constants.NUMBER_OF_HONEY_WORDS + 1; i++) 
        {
            ArrayList<Minutiae> hMinutiaes = generateHoneyMinutiae();
            FuzzyVault hVault = new FuzzyVault(hMinutiaes);
            if(hVault.create())
            {
                unlockedHVaults.put(i, hVault.getVault());
            }
        }
        lock(unlockedHVaults);
        //honeyVaults.addAll(unlockedHVaults.values());

        return true;
    }

    private void lock(HashMap<Integer, ArrayList<Tuple<Integer, Integer>>> unlockedHVaults)
    {
        ArrayList<Integer> keys = new ArrayList<Integer>(unlockedHVaults.keySet());
        Collections.shuffle(keys);

        int idx = 0;
        for (int key : keys) 
        {
            honeyVaults.add(unlockedHVaults.get(key));
            if(key == 0)
            {
                honeyChecker = idx;
            }
            idx++;
        }
    }

    private ArrayList<Minutiae> generateHoneyMinutiae()
    {
        ArrayList<Minutiae> hMinutiaes = new ArrayList<Minutiae>();
        
        for(int i = 0; i < Constants.NUMBER_OF_MINUTIAE; i++)
        {
            Minutiae m = new Minutiae();
            m.x = (int)field31.randomElement();
            m.y = (int)field31.randomElement();
            m.o = (int)field61.randomElement();
            m.encode();

            hMinutiaes.add(m);
        }

        return hMinutiaes;
    }

    public ArrayList<ArrayList<Tuple<Integer, Integer>>> getHoneyVaults()
    {
        return honeyVaults;
    }

    public Integer getHoneyChecker()
    {
        return honeyChecker;
    }
}