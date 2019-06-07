package biohive.utility;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

import biohive.fuzzyVault.FuzzyVault;
import biohive.fuzzyVault.Tuple;

public class DatabaseIO
{
    public static void setHoneyVaults(String userid, ArrayList<FuzzyVault> hVaults, String outFilename) throws Exception
    {
        checkIfUserAlreadyRegistered(userid, outFilename);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFilename, true)));
        for (FuzzyVault vault : hVaults) 
        {
            StringBuilder sb = new StringBuilder();
            sb.append(userid);

            for (Tuple<Integer, Integer> tuple : vault.getVault()) 
            {
                sb.append(String.format(" %s %s", tuple.x.toString(), tuple.y.toString()));
            }

            sb.append(" " + vault.getHashKey().toString());
            writer.println(sb.toString().trim());
        }
        writer.close();
    }

    public static void setHoneyChecker(String userid, Integer hChecker, String outFilename) throws Exception
    {
        checkIfUserAlreadyRegistered(userid, outFilename);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFilename, true)));
        writer.println(String.format("%s %s", userid, hChecker.toString()));
        writer.close();
    }

    public static ArrayList<FuzzyVault> getHoneyVaults(String userid, String inFilename) throws Exception
    {
        ArrayList<FuzzyVault> hVaults = new ArrayList<FuzzyVault>();
        BufferedReader bReader = new BufferedReader(new FileReader(inFilename));
        
        String line;
        int id = 0;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length > 1 && tokens[0].equals(userid))
            {
                ArrayList<Tuple<Integer, Integer>> hVault = new ArrayList<Tuple<Integer, Integer>>();
                for(int i = 1; i < tokens.length - 1;)
                {
                    hVault.add(new Tuple<Integer, Integer>(Integer.parseInt(tokens[i++]), Integer.parseInt(tokens[i++])));
                }

                BigInteger hashKey = new BigInteger(tokens[tokens.length - 1]);
                hVaults.add(new FuzzyVault(hVault, hashKey, id++));
            }
        }

        bReader.close();
        return hVaults;
    }

    public static Integer getHoneyChecker(String userid, String inFilename) throws Exception
    {
        BufferedReader bReader = new BufferedReader(new FileReader(inFilename));
        String line;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length > 1 && tokens[0].equals(userid))
            {
                bReader.close();
                return Integer.parseInt(tokens[1]);
            }
        }

        bReader.close();
        return -1;
    }

    public static void clearDb(String inFilename) throws Exception
    {
        PrintWriter writer = new PrintWriter(inFilename);
        writer.print("");
        writer.close();
    }

    private static void checkIfUserAlreadyRegistered(String userid, String fileName) throws Exception
    {
        BufferedReader bReader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length > 1 && tokens[0].equals(userid))
            {
                throw new Exception(fileName + " already has" + userid + " user!");
            }
        }
        bReader.close();
    }
}