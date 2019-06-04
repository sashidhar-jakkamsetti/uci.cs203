package biohive.honeyvaultGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import biohive.fuzzyVault.FuzzyVault;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;

public class HoneyvaultGenerator
{
    private FuzzyVault sugarVault;
    private int honeyChecker;
    private ArrayList<FuzzyVault> honeyVaults;
    private HoneyMinutiae hMinutiae;

    public HoneyvaultGenerator(FuzzyVault sugarVault, HoneyMinutiae hMinutiae)
    {
        this.sugarVault = sugarVault;
        this.hMinutiae = hMinutiae;
        honeyVaults = new ArrayList<FuzzyVault>();
    }

    public boolean generate()
    {
        HashMap<Integer, FuzzyVault> unlockedHVaults = new HashMap<Integer, FuzzyVault>();
        unlockedHVaults.put(0, sugarVault);
        
        for(int i = 1; i < Constants.NUMBER_OF_HONEY_VAULTS + 1; i++) 
        {
            ArrayList<Minutiae> hMinutiaes = hMinutiae.generateGaussian();
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

    public ArrayList<FuzzyVault> getHoneyVaults()
    {
        return honeyVaults;
    }

    public Integer getHoneyChecker()
    {
        return honeyChecker;
    }
}