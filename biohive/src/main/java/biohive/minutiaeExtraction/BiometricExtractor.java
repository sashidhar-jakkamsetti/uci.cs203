package biohive.minutiaeExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import biohive.utility.Constants;

public class BiometricExtractor
{
    public static Boolean extract(String execString, String minutiaeFile) throws Exception
    {
        try 
        {
            Process p = Runtime.getRuntime().exec(execString);
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }

        return true;
    }

    public static ArrayList<Minutiae> encode(String minutiaeFileName) throws Exception
    {
        File minutiaeFile = new File(minutiaeFileName);
        FileReader fReader = new FileReader(minutiaeFile);
        BufferedReader bReader = new BufferedReader(fReader);

        String line;
        ArrayList<Minutiae> minutiaeHolder = new ArrayList<Minutiae>();
        while ((line = bReader.readLine()) != null) 
        {
            Minutiae m = new Minutiae(line);
            minutiaeHolder.add(m);
        }

        Stream<Minutiae> minutiaesSorted = minutiaeHolder.stream().sorted((m1, m2) -> -1 * (m1.cnf.compareTo(m2.cnf)));
        minutiaesSorted = minutiaesSorted.limit(Constants.NUMBER_OF_MINUTIAE);

        ArrayList<Minutiae> minutiaes = new ArrayList<Minutiae>();
        for (Minutiae m : minutiaesSorted.collect(Collectors.toList())) 
        {
            m.encode();
            minutiaes.add(m);
        }

        return minutiaes;
    }
}