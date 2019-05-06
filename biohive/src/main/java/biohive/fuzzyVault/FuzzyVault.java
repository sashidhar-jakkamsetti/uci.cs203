package biohive.fuzzyVault;

import java.util.ArrayList;
import biohive.minutiaeExtraction.Minutiae;

public class FuzzyVault 
{
    private ArrayList<Minutiae> minutiaes;

    public FuzzyVault(ArrayList<Minutiae> minutiaes)
    {
        this.minutiaes = new ArrayList<Minutiae>();
        this.minutiaes.addAll(minutiaes);
    }

    public boolean create()
    {

        return true;
    }

    public FuzzyVault clone()
    {
        FuzzyVault newVault = new FuzzyVault(minutiaes);

        return newVault;
    }
    
    //testing - surmeet
}