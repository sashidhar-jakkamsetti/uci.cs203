package biohive.minutiaeExtraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import biohive.utility.Baseline;
import biohive.utility.Constants;

public class MinutiaeExtractor
{
    public static Boolean extract(Baseline bInfo) throws Exception
    {
        String execString = String.format("python %s %s %s %s %s %s", 
                    bInfo.getMinutiae_extractor(), 
                    bInfo.getTarp_location(), 
                    bInfo.getMindtct_location(),
                    bInfo.in_fingerprint,
                    bInfo.out_fingerprintAligned,
                    bInfo.out_minutiae
                    );
        Runtime.getRuntime().exec(execString);
        return true;
    }

    public static ArrayList<Minutiae> encode(String minutiaeFileName) throws Exception
    {
        BufferedReader bReader = new BufferedReader(new FileReader(minutiaeFileName));

        String line;
        ArrayList<Minutiae> minutiaeHolder = new ArrayList<Minutiae>();
        while ((line = bReader.readLine()) != null) 
        {
            Minutiae m = new Minutiae(line);
            minutiaeHolder.add(m);
        }

        Stream<Minutiae> minutiaesSorted = minutiaeHolder.stream().sorted((m1, m2) -> -1 * (m1.cnf.compareTo(m2.cnf)));
        minutiaesSorted = minutiaesSorted.filter(m -> m.cnf > Constants.CAP_MINUTIAE_QUALITY).limit(Constants.NUMBER_OF_MINUTIAE);

        ArrayList<Minutiae> minutiaes = new ArrayList<Minutiae>();
        for (Minutiae m : minutiaesSorted.collect(Collectors.toList())) 
        {
            m.encode();
            minutiaes.add(m);
        }
        
        bReader.close();
        return minutiaes;
    }
}