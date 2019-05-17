package biohive.authentication;

import java.util.*;
import java.util.stream.Collectors;

import biohive.fuzzyVault.*;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;
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

        //isSugarVault(hVaults.get(0), redecodedMinutiaes);
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
        MinutiaeMatcher mMatcher = new MinutiaeMatcher(vault, minutiaes);
        mMatcher.initialize();

        //ArrayList<Tuple<Integer, Integer>> selectedVault = new ArrayList<Tuple<Integer, Integer>>();
        while(!mMatcher.SetisEmpty())
        {
            ArrayList<Tuple<Integer, Integer>> selectedVault = mMatcher.getNextSet(vault);
            
            ArrayList<Integer> predictedKey = lagrangeInterpolation(selectedVault);
            if(verifyKey(predictedKey))
            {
                return true;
            }
        }

        return false;
    }

    private ArrayList<Integer> lagrangeInterpolation(ArrayList<Tuple<Integer, Integer>> vault)
    {
        ArrayList<Integer> key = new ArrayList<Integer>();
        //coeffs of polynomial
        return key;
    }

    private boolean verifyKey(ArrayList<Integer> key)
    {
        return Utils.hashMe(new ArrayList<Integer>(key.stream().limit(Constants.POLY_DEGREE).collect(Collectors.toList()))) == 
                key.get(Constants.POLY_DEGREE);
    }
}