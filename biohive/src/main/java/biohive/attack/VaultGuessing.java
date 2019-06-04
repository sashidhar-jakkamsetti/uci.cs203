package biohive.attack;

import java.util.ArrayList;

import biohive.fuzzyVault.FuzzyVault;

public class VaultGuessing
{
    private ArrayList<FuzzyVault> hVaults;
    private Integer sugarIdx;

    public VaultGuessing(ArrayList<FuzzyVault> hVaults)
    {
        this.hVaults = hVaults;
        sugarIdx = -1;
    }

    public FuzzyVault guess()
    {
        // Try to guess which one is the right vault from the points.
        sugarIdx = 0;
        return hVaults.get(0);
    }

    public Integer getSugarIdx()
    {
        return sugarIdx;
    }
    
}