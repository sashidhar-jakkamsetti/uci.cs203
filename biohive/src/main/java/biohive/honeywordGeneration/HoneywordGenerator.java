package biohive.honeywordGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import biohive.fuzzyVault.FuzzyVault;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;
import cc.redberry.rings.IntegersZp64;

public class HoneywordGenerator
{
    private FuzzyVault sugarVault;
    private IntegersZp64 field31;
    private IntegersZp64 field61;
    private int honeyChecker;
    private ArrayList<FuzzyVault> honeyVaults;

    public HoneywordGenerator(FuzzyVault sugarVault)
    {
        this.sugarVault = sugarVault;
        field31 = new IntegersZp64(Constants.FIELD_ORDER_5);
        field61 = new IntegersZp64(Constants.FIELD_ORDER_6);
        honeyVaults = new ArrayList<FuzzyVault>();
    }

    public boolean generate()
    {
        HashMap<Integer, FuzzyVault> unlockedHVaults = new HashMap<Integer, FuzzyVault>();
        unlockedHVaults.put(0, sugarVault);
        
        for(int i = 1; i < Constants.NUMBER_OF_HONEY_WORDS + 1; i++) 
        {
            ArrayList<Minutiae> hMinutiaes = generateHoneyMinutiae();
            FuzzyVault hVault = new FuzzyVault(hMinutiaes);
            if(hVault.create())
            {
                unlockedHVaults.put(i, hVault);
            }
        }
        lock(unlockedHVaults);
        //honeyVaults.addAll(unlockedHVaults.values());

        return true;
    }

    private void lock(HashMap<Integer, FuzzyVault> unlockedHVaults)
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

    public ArrayList<FuzzyVault> getHoneyVaults()
    {
        return honeyVaults;
    }

    public Integer getHoneyChecker()
    {
        return honeyChecker;
    }
}