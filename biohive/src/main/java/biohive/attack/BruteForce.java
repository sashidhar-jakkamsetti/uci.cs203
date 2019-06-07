package biohive.attack;

import java.util.ArrayList;

import biohive.authentication.MinutiaeMatcher;
import biohive.fuzzyVault.FuzzyVault;
import biohive.fuzzyVault.Tuple;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;
import biohive.utility.MatrixMultiplication;
import biohive.utility.ModularMatrix;
import biohive.utility.Utils;
import cc.redberry.rings.IntegersZp64;

import java.math.BigInteger;

public class BruteForce
{
    private IntegersZp64 field;
    
    public BruteForce()
    {
        field = new IntegersZp64(Constants.FIELD_ORDER_16);
    }

    public ArrayList<Integer> bruteForce(FuzzyVault vault)
    {        
        ArrayList<Minutiae> qMinutiaes = new ArrayList<Minutiae>();
        for (Tuple<Integer, Integer> item : vault.getVault()) 
        {
            Minutiae m = new Minutiae(item.x);
            m.decode();
            qMinutiaes.add(m);  
        }

        MinutiaeMatcher mMatcher = new MinutiaeMatcher(vault, qMinutiaes, 1.0);
        mMatcher.initialize(true);

        int guessCounter = 0;
        while(!mMatcher.isSetEmpty())
        {
            ArrayList<Tuple<Integer, Integer>> selectedVault = mMatcher.getNextSet();
            ArrayList<Integer> predictedKey = solvePx(selectedVault);

            if(verifyKey(predictedKey, vault.getHashKey()))
            {
                return predictedKey;
            }
            guessCounter++;
        }

        return new ArrayList<Integer>();
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