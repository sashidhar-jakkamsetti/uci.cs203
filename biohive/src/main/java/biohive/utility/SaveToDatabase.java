package biohive.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import biohive.fuzzyVault.Tuple;

public class SaveToDatabase
{
    public static void saveHoneyVaults(String userid, ArrayList<ArrayList<Tuple<Integer, Integer>>> hVaults, String outFilename) throws Exception
    {
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

    public static void saveHoneyChecker(String userid, Integer hChecker, String outFilename) throws Exception
    {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFilename, true)));
        writer.println(String.format("%s %s", userid, hChecker.toString()));
        writer.close();
    }
}