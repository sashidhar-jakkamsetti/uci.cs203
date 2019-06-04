package biohive.utility;

import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD2Digest;

import biohive.fuzzyVault.Tuple;
import java.math.BigInteger;

public class Utils
{
    public static int extractBits(int number, int k, int p) 
    { 
        return (((1 << k) - 1) & (number >> (p - 1))); 
    }

    public static BigInteger hashMe(ArrayList<Integer> keyMaterial)
    {
        if(keyMaterial.size() > 0) 
        {
            Digest digest = new MD2Digest();
            byte[] finValue = new byte[digest.getDigestSize()];
            for (Integer token : keyMaterial) 
            {
                byte[] tokenBytes = toBytes(token);
                digest.update(tokenBytes, 0, tokenBytes.length);
            }
            digest.doFinal(finValue, 0);
            return new BigInteger(finValue);
        }

        return new BigInteger("-1");
    }

    private static byte[] toBytes(int i)
    {
        byte[] result = new byte[2];

        result[0] = (byte) (i >> 8);
        result[1] = (byte) (i);

        return result;
    }

    public static HashMap<Integer, Integer> convertToMap(ArrayList<Tuple<Integer, Integer>> list)
    {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Tuple<Integer, Integer> item : list) 
        {
            map.put(item.x, item.y);
        }  
        return map;
    }
}