package biohive;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import biohive.attack.BruteForce;
import biohive.attack.VaultGuessing;
import biohive.authentication.Authenticator;
import biohive.fuzzyVault.FuzzyVault;
import biohive.honeyvaultGeneration.HoneyMinutiae;
import biohive.honeyvaultGeneration.HoneyvaultGenerator;
import biohive.minutiaeExtraction.Minutiae;
import biohive.minutiaeExtraction.MinutiaeExtractor;
import biohive.utility.Baseline;
import biohive.utility.Baseline.OpMode;
import biohive.utility.ConfigLoader;
import biohive.utility.Constants;
import biohive.utility.DatabaseIO;
import biohive.validation.Validator;

public class biohive 
{
    public static boolean run(Baseline bInfo)
    {
        try 
        {
            if(bInfo.mode == OpMode.attack)
            {
                ArrayList<FuzzyVault> hVaults = DatabaseIO.getHoneyVaults(bInfo.userId, bInfo.biodb);

                VaultGuessing guessing = new VaultGuessing(hVaults);
                FuzzyVault sugarVault = guessing.guess();

                BruteForce bForce = new BruteForce();
                ArrayList<Integer> key = bForce.bruteForce(sugarVault);

                return Validator.validate(bInfo.userId, bInfo.honeydb, guessing.getSugarIdx());
            }
            else
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
                                HoneyMinutiae hMinutiae = new HoneyMinutiae(bInfo.getMinutiae_probdist());
                                HoneyvaultGenerator hGenerator = new HoneyvaultGenerator(sugarVault, hMinutiae);
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
    
                            if(hVaults.size() == Constants.NUMBER_OF_HONEY_VAULTS + 1)
                            {
                                Authenticator authenticator = new Authenticator(bInfo.userId, hVaults, bInfo.honeydb);
                                Integer sugarIdx = authenticator.authenticate(minutiaes);
                                return Validator.validate(bInfo.userId, bInfo.honeydb, sugarIdx);
                            }
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
