package biohive.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import biohive.fuzzyVault.Tuple;

public class DatabaseIO
{
    public static void setHoneyVaults(String userid, ArrayList<ArrayList<Tuple<Integer, Integer>>> hVaults, String outFilename) throws Exception
    {
        BufferedReader bReader = new BufferedReader(new FileReader(outFilename));
        String line;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length > 1 && tokens[0] == userid)
            {
                throw new Exception("Honey vaults for the user already present!");
            }
        }
        bReader.close();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFilename, true)));
        
        for (ArrayList<Tuple<Integer, Integer>> vault : hVaults) 
        {
            StringBuilder sb = new StringBuilder();
            sb.append(userid);

            for (Tuple<Integer, Integer> tuple : vault) 
            {
                sb.append(String.format(" %s %s", tuple.x.toString(), tuple.y.toString()));
            }

            writer.println(sb.toString().trim());
        }
        writer.close();
    }

    public static void setHoneyChecker(String userid, Integer hChecker, String outFilename) throws Exception
    {
        BufferedReader bReader = new BufferedReader(new FileReader(outFilename));
        String line;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length > 1 && tokens[0] == userid)
            {
                throw new Exception("Honey id for the user already present!");
            }
        }
        bReader.close();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFilename, true)));
        writer.println(String.format("%s %s", userid, hChecker.toString()));
        writer.close();
    }

    public static ArrayList<ArrayList<Tuple<Integer, Integer>>> getHoneyVaults(String userid, String inFilename) throws Exception
    {
        ArrayList<ArrayList<Tuple<Integer, Integer>>> hVaults = new ArrayList<ArrayList<Tuple<Integer, Integer>>>();
        BufferedReader bReader = new BufferedReader(new FileReader(inFilename));
        
        String line;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length > 1 && tokens[0].equals(userid))
            {
                ArrayList<Tuple<Integer, Integer>> hVault = new ArrayList<Tuple<Integer, Integer>>();
                for(int i = 1; i < tokens.length;)
                {
                    hVault.add(new Tuple<Integer, Integer>(Integer.parseInt(tokens[i++]), Integer.parseInt(tokens[i++])));
                }
                hVaults.add(hVault);
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
            if(tokens.length > 1 && tokens[0] == userid)
            {
                bReader.close();
                return Integer.parseInt(tokens[1]);
            }
        }

        bReader.close();
        return -1;
    }
}