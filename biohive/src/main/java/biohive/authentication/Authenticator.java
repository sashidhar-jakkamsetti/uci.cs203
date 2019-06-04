package biohive.authentication;

import java.util.*;
import java.math.BigInteger;

import biohive.fuzzyVault.*;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.*;
import cc.redberry.rings.IntegersZp64;

public class Authenticator
{
    private ArrayList<FuzzyVault> hVaults;
    private String userId;
    private String honeydb;
    private IntegersZp64 field;

    public Authenticator(String userId, ArrayList<FuzzyVault> hVaults, String honeydb)
    {
        field = new IntegersZp64(Constants.FIELD_ORDER_16);
        this.userId = userId;
        this.hVaults = hVaults;
        this.honeydb = honeydb;
    }
    
    public Integer authenticate(ArrayList<Minutiae> minutiaes) throws Exception
    {
        ArrayList<Minutiae> redecodedMinutiaes = decodeMinutiae(minutiaes);
        
        Integer idx = 0;
        for ( FuzzyVault vault : hVaults) 
        {
            if(isSugarVault(vault, redecodedMinutiaes))
            {
                return idx;
            }
            idx++;
        }

        return -1;
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

    private boolean isSugarVault(FuzzyVault vault, ArrayList<Minutiae> minutiaes)
    {
        MinutiaeMatcher mMatcher = new MinutiaeMatcher(vault, minutiaes);
        mMatcher.initialize();

        while(!mMatcher.isSetEmpty())
        {
            ArrayList<Tuple<Integer, Integer>> selectedVault = mMatcher.getNextSet();
            if(selectedVault.size() > 0)
            {
                ArrayList<Integer> predictedKey = solvePx(selectedVault);

                if(verifyKey(predictedKey, vault.getHashKey()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    
    private ArrayList<Integer> solvePx(ArrayList<Tuple<Integer, Integer>> vault)
    {
        BigInteger[][] mat = new BigInteger[Constants.POLY_DEGREE + 1][Constants.POLY_DEGREE + 1];
        int[][] yMat = new int[Constants.POLY_DEGREE + 1][1];

        for (int i = 0; i < Constants.POLY_DEGREE + 1; i++) 
        {
            Tuple<Integer, Integer> tuple = vault.get(i);
            yMat[i][0] = tuple.y;

            Integer variate = 1;
            for (int j = 0; j < Constants.POLY_DEGREE + 1; j++)
            {
                mat[i][j] = new BigInteger(variate.toString());
                variate = (int)field.multiply(variate, tuple.x);
            }
        }
        
        int[][] invMat = new int[Constants.POLY_DEGREE + 1][Constants.POLY_DEGREE + 1];
        try
        {
            ModularMatrix xMat = new ModularMatrix(mat, Constants.FIELD_ORDER_16);
            ModularMatrix inverseXMat = xMat.inverse(xMat);
            
            for (int i = 0; i < Constants.POLY_DEGREE + 1; i++) 
            {
                for (int j = 0; j < Constants.POLY_DEGREE + 1; j++)
                {
                    invMat[i][j] = inverseXMat.getData()[i][j].intValue();
                }
            }
        }
        catch(Exception e)
        {
            return new ArrayList<Integer>();
        }

        int[][] coefficients = new int[Constants.POLY_DEGREE + 1][1];
        coefficients = MatrixMultiplication.multiply(invMat, yMat, Constants.FIELD_ORDER_16);
        ArrayList<Integer> key = new ArrayList<Integer>();

        for(int i = 0; i < Constants.POLY_DEGREE + 1; i++)
        {
            key.add((int)field.modulus(coefficients[i][0]));
        }

        return key;
    }


    private boolean verifyKey(ArrayList<Integer> key, BigInteger verifierKey)
    {
        BigInteger proverKey = Utils.hashMe(key);
        if(verifierKey.equals(proverKey)) 
        {
            return true;
        }

        return false;
    }
}