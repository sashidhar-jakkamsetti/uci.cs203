package biohive.fuzzyVault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.*;
import cc.redberry.rings.IntegersZp64;

public class FuzzyVault 
{
    private ArrayList<Minutiae> minutiaes;
    private IntegersZp64 field;
    private ArrayList<Tuple<Integer, Integer>> vault;

    public FuzzyVault(ArrayList<Minutiae> minutiaes)
    {
        field = new IntegersZp64(Constants.FIELD_ORDER_16);
        this.minutiaes = new ArrayList<Minutiae>();
        this.minutiaes.addAll(minutiaes);
        vault = new ArrayList<Tuple<Integer, Integer>>();
    }

    public boolean create()
    {
        HashMap<Integer, Tuple<Integer, Integer>> unlockedVault = new HashMap<Integer, Tuple<Integer, Integer>>();

        ArrayList<Integer> key = pickRandomKey();
        int idx = 0;
        for (Minutiae m : minutiaes) 
        {
            unlockedVault.put(idx++, new Tuple<Integer, Integer>(m.code, pX(key, m.code)));
        }

        for(int i = 0; i < Constants.NUMBER_OF_CHAFF; i++)
        {
            Tuple<Integer, Integer> chaff = getChaffPoint(key);
            unlockedVault.put(idx++, new Tuple<Integer, Integer>(chaff.x, chaff.y));
        }

        lock(unlockedVault);
        //vault.addAll(unlockedVault.values());
        return true;
    }

    private ArrayList<Integer> pickRandomKey()
    {
        ArrayList<Integer> key = new ArrayList<Integer>();

        for(int i = 0; i < Constants.POLY_DEGREE; i++)
        {
            key.add((int)field.randomElement());
        }

        key.add((int)field.modulus(Utils.hashMe(key)));
        return key;
    }

    private int pX(ArrayList<Integer> key, int value)
    {
        int variate = 1;
        int result = 0;

        for (int coefficient : key) 
        {
            result = (int)field.add(result, field.multiply(coefficient, variate));
            variate = (int)field.multiply(variate, value);
        }

        return result;
    }

    private Tuple<Integer, Integer> getChaffPoint(ArrayList<Integer> key)
    {
        int chaffX = (int)field.randomElement();
        int chaffY = (int)field.randomElement();

        while(pX(key, chaffX) == chaffY)
        {
            chaffY = (int)field.randomElement();
        }

        return new Tuple<Integer,Integer>(chaffX, chaffY);
    }

    private void lock(HashMap<Integer, Tuple<Integer, Integer>> unlockedVault)
    {
        ArrayList<Integer> keys = new ArrayList<Integer>(unlockedVault.keySet());
        Collections.shuffle(keys);
        
        for (int key : keys) 
        {
            vault.add(unlockedVault.get(key));
        }
    }

    public ArrayList<Tuple<Integer, Integer>> getVault()
    {
        return vault;
    }

    public FuzzyVault clone()
    {
        FuzzyVault newVault = new FuzzyVault(minutiaes);
        newVault.vault.addAll(this.vault);
        return newVault;
    }
}