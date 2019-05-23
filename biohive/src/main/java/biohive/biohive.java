package biohive;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import biohive.authentication.Authenticator;
import biohive.fuzzyVault.*;
import biohive.honeywordGeneration.HoneywordGenerator;
import biohive.minutiaeExtraction.*;
import biohive.utility.*;
import biohive.utility.Baseline.OpMode;

public class biohive 
{
    public static boolean run(Baseline bInfo)
    {
        if(bInfo.mode == OpMode.attack)
        {
            File biometricDb = new File(bInfo.biodb);
            // --> brute force: write code in BruteForce.java
        }

        try 
        {
            System.out.println("Extracting minutiae from fingerprint image.");
            String targetFileName = bInfo.out_minutiae + ".xyt";
            File targetFile = new File(targetFileName);

            if(!targetFile.exists())
            {
                MinutiaeExtractor.extract(bInfo);
                TimeUnit.SECONDS.sleep(4);
            }

            if(targetFile.exists())
            {
                System.out.println("Encoding minutiae.");
                ArrayList<Minutiae> minutiaes = MinutiaeExtractor.encode(targetFileName);
                if(minutiaes.size() > 0)
                {
                    if(bInfo.mode == OpMode.reg)
                    {
                        System.out.println("Generating honey vaults.");
                        FuzzyVault sugarVault = new FuzzyVault(minutiaes);
                        if(sugarVault.create())
                        {
                            HoneywordGenerator hGenerator = new HoneywordGenerator(sugarVault);
                            if(hGenerator.generate())
                            {
                                if(bInfo.getClearDb())
                                {
                                    System.out.println("Clearing the database.");
                                    DatabaseIO.clearDb(bInfo.biodb);
                                    DatabaseIO.clearDb(bInfo.honeydb);
                                }

                                System.out.println("Registering vaults with userId: " + bInfo.userId);
                                DatabaseIO.setHoneyVaults(bInfo.userId, hGenerator.getHoneyVaults(), bInfo.biodb);
                                DatabaseIO.setHoneyChecker(bInfo.userId, hGenerator.getHoneyChecker(), bInfo.honeydb);
    
                                return true;
                            }
                        }
                    }
                    else if(bInfo.mode == OpMode.auth)
                    {
                        System.out.println("Quering minutiae with userId: " + bInfo.userId);
                        ArrayList<FuzzyVault> hVaults = DatabaseIO.getHoneyVaults(bInfo.userId, bInfo.biodb);

                        if(hVaults.size() == Constants.NUMBER_OF_HONEY_WORDS + 1)
                        {
                            Authenticator authenticator = new Authenticator(bInfo.userId, hVaults, bInfo.honeydb);
                            return authenticator.authenticate(minutiaes);
                        }
                    }
                }
            }

            return false;
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
            baselineFile = "/home/sashidhar/course-work/cs203/uci.cs203/biohive/biohive.xml";
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
            if(baselineInfo.mode == OpMode.reg)
            {
                System.out.println("Biometric successfully registerd!");
            }
            else if(baselineInfo.mode == OpMode.auth)
            {
                System.out.println("Biometric successfully authenticated!");
            }
            else 
            {
                System.out.println("Biometric successfully attacked!");
            }
        }
        else
        {
            System.out.println("Error occured!");
        }
    }
}
