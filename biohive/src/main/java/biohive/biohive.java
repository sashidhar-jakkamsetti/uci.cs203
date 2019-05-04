package biohive;

import java.util.ArrayList;

import biohive.fuzzyVault.FuzzyVault;
import biohive.honeywordGeneration.HoneywordGenerator;
import biohive.minutiaeExtraction.BiometricExtractor;
import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Baseline;
import biohive.utility.ConfigLoader;

public class biohive 
{
    public static boolean run(Baseline bInfo)
    {
        try 
        {
            String execString = String.format("python %s %s %s %s %s %s", 
                                                bInfo.getMinutiae_extractor(), 
                                                bInfo.getTarp_location(), 
                                                bInfo.getMindtct_location(),
                                                bInfo.in_fingerprint,
                                                bInfo.out_fingerprintAligned,
                                                bInfo.out_minutiae);

            System.out.println("Extracting minutiae from fingerprint image....");

            if(BiometricExtractor.extract(execString, bInfo.out_minutiae))
            {
                ArrayList<Minutiae> minutiaes = BiometricExtractor.encode(bInfo.out_minutiae + ".xyt");
                if(minutiaes.size() > 0)
                {
                    FuzzyVault sugarVault = new FuzzyVault(minutiaes);
                    if(sugarVault.create())
                    {
                        HoneywordGenerator hGenerator = new HoneywordGenerator(sugarVault);
                        if(hGenerator.generate())
                        {
                            
                        }
                    }
                }
            }

            return true;
        } 
        catch (Exception e) 
        {
            System.out.println(String.format("%s : %s\n", e.toString(), e.getMessage()));
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) 
    {
        System.out.println("Initiating sequence.");

        String baselineFile;
        if(args.length > 0) 
        {
            baselineFile = args[0];
        }
        else
        {
            baselineFile = "/Users/sashidharjakkamsetti/workspace/cs203/biohive/biohive.xml";
        }

        ConfigLoader loader = new ConfigLoader(baselineFile);
        Baseline baselineInfo;
        try
        {
            baselineInfo = loader.load();
        }
        catch(Exception e)
        {
            System.out.println(String.format("%s : %s\n", e.toString(), e.getMessage()));
            e.printStackTrace();
            return;
        }

        if(run(baselineInfo))
        {
            System.out.println("Biometric successfully saved!");
        }
        else
        {
            System.out.println("Error occured!");
        }
    }
}
