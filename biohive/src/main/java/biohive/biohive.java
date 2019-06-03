package biohive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import biohive.attack.BruteForce;
import biohive.attack.VaultGuessing;
import biohive.authentication.Authenticator;
import biohive.fuzzyVault.*;
import biohive.honeywordGeneration.HoneyMinutiae;
import biohive.honeywordGeneration.HoneyvaultGenerator;
import biohive.minutiaeExtraction.*;
import biohive.utility.*;
import biohive.utility.Baseline.OpMode;
import biohive.validation.Validator;

public class biohive {
    public static boolean run(Baseline bInfo) {
        try {
            if (bInfo.mode == OpMode.attack) {
                ArrayList<FuzzyVault> hVaults = DatabaseIO.getHoneyVaults(bInfo.userId, bInfo.biodb);

                VaultGuessing guessing = new VaultGuessing(hVaults);
                FuzzyVault sugarVault = guessing.guess();

                BruteForce bForce = new BruteForce();
                ArrayList<Integer> key = bForce.bruteForce(sugarVault);

                return Validator.validate(bInfo.userId, bInfo.honeydb, guessing.getSugarIdx());
            } else {
                System.out.println("Extracting minutiae from fingerprint image.");
                String targetFileName = bInfo.out_minutiae + ".xyt";
                File targetFile = new File(targetFileName);

                if (!targetFile.exists()) {
                    MinutiaeExtractor.extract(bInfo);
                    TimeUnit.SECONDS.sleep(4);
                }

                if (targetFile.exists()) {
                   // System.out.println("Encoding minutiae.");
                    ArrayList<Minutiae> minutiaes = MinutiaeExtractor.encode(targetFileName);
                    if (minutiaes.size() > 0) {
                        if (bInfo.mode == OpMode.reg) {
                        //    System.out.println("Generating honey vaults.");
                            FuzzyVault sugarVault = new FuzzyVault(minutiaes);
                            if (sugarVault.create()) {
                                HoneyMinutiae hMinutiae = new HoneyMinutiae(bInfo.getMinutiae_probdist());
                                HoneyvaultGenerator hGenerator = new HoneyvaultGenerator(sugarVault, hMinutiae);
                                if (hGenerator.generate()) {
                                    if (bInfo.getClearDb()) {
                        //                System.out.println("Clearing the database.");
                                        DatabaseIO.clearDb(bInfo.biodb);
                                        DatabaseIO.clearDb(bInfo.honeydb);
                                    }

                                    System.out.println("Registering vaults with userId: " + bInfo.userId);
                                    DatabaseIO.setHoneyVaults(bInfo.userId, hGenerator.getHoneyVaults(), bInfo.biodb);
                                    DatabaseIO.setHoneyChecker(bInfo.userId, hGenerator.getHoneyChecker(),
                                            bInfo.honeydb);

                                    return true;
                                }
                            }
                        } else if (bInfo.mode == OpMode.auth) {
                            System.out.println("Quering minutiae with userId: " + bInfo.userId);
                            ArrayList<FuzzyVault> hVaults = DatabaseIO.getHoneyVaults(bInfo.userId, bInfo.biodb);

                            if (hVaults.size() == Constants.NUMBER_OF_HONEY_VAULTS + 1) {
                                Authenticator authenticator = new Authenticator(bInfo.userId, hVaults, bInfo.honeydb);
                                Integer sugarIdx = authenticator.authenticate(minutiaes);
                                return Validator.validate(bInfo.userId, bInfo.honeydb, sugarIdx);
                            }
                        }
                    }
                }
            }

            return false;
        } catch (Exception e) {
            System.out.println(String.format("%s : %s\n", e.toString(), e.getMessage()));
            e.printStackTrace();
        }

        return false;
    }

    static void saveToFile(ArrayList<ArrayList<Double>> data, String name)
    {
        PrintWriter writer;
        try {
            writer = new PrintWriter("C:\\Users\\Dell\\Desktop\\uci.cs203\\biohive\\output\\"+name+".txt", "UTF-8");
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            for(int i=0;i<data.size();i++)
            {
                writer.print("DB "+(i+1)+": ");
                ArrayList<Double> dataForDb = data.get(i);
                for(Double d: dataForDb)
                {
                    writer.print(numberFormat.format(d)+"  ");
                }
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException 
    {
        

        DecimalFormat numberFormat = new DecimalFormat("#.00");
        String baselineFile;
        if(args.length > 0) 
        {
            baselineFile = args[0];
        }
        else
        {
            baselineFile = "C:\\Users\\Dell\\Desktop\\uci.cs203\\biohive\\biohive.xml";
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

        ArrayList<ArrayList<Double>> insultRates_db = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> regTime_db = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> authTime_db = new ArrayList<ArrayList<Double>>();
    
        ArrayList<Long> timesPerDb = new ArrayList<Long>();

        long startTimeAnalysis = System.currentTimeMillis();


        for(int i=2;i<=2;i++) //1-4
        {
            File folder = new File("C:\\Users\\Dell\\Desktop\\uci.cs203\\biohive\\src\\database\\fingerprint\\DB"+i+"_B");
            File[] listOfFiles = folder.listFiles();

            ArrayList<Double> insultRate = new ArrayList<Double>();
            ArrayList<Double> regTime = new ArrayList<Double>();
            ArrayList<Double> authTime = new ArrayList<Double>();

            ArrayList<ArrayList<File>> people = new ArrayList<ArrayList<File>>();

            for (int j = 0, l = 0 ; j < listOfFiles.length; j+=8) {

                ArrayList<File> people2 = new ArrayList<File>();
               // System.out.println(j + " " + l);
                for(int k = j; k < j+8; k++)
                {
                    people2.add(listOfFiles[k]);
                }

                people.add(people2);
                // String a = listOfFiles[j].getName().split("_")[0];
                // people.get(Integer.parseInt(a)-100).add(listOfFiles[j]);
            }

            long startTimeDB = System.currentTimeMillis();
            //PrintWriter writer = new PrintWriter("C:\\Users\\Dell\\Desktop\\uci.cs203\\biohive\\output\\log.txt", "UTF-8");
            for(int j=0;j<people.size();j++)  //people.size()
            {
                baselineInfo.userId = people.get(j).get(0).getName().split("_")[0];
                ArrayList<File> person = people.get(j);
                int insult=0;
                
                double regTime_person = 0;
                double authTime_person = 0;
                
                //for(int outer = 0; outer<10; outer++)
                //{
                    for(int k = 0; k<person.size(); k++)
                    {
                        // Registering user
                        long startTimeReg = System.currentTimeMillis();
                        baselineInfo.setFingerprint(person.get(k).getName());
                        baselineInfo.mode = OpMode.reg;
                        run(baselineInfo);
                        long endTimeReg = System.currentTimeMillis();

                        //Authenticating User
                        long startTimeAuth = System.currentTimeMillis();
                        for(int l = 0; l<person.size(); l++)
                        {
                            System.out.println("Auth "+l);
                            if(l!=k)
                            {
                                baselineInfo.setFingerprint(person.get(l).getName());
                                baselineInfo.mode = OpMode.auth;
                                if(!run(baselineInfo))
                                {
                                    //System.out.println("insult");
                                    insult++;
                                }
                                {
                                    //System.out.println("Authenticated");
                                }
                            } 
                        }
                        long endTimeAuth = System.currentTimeMillis();

                        authTime_person += (endTimeAuth - startTimeAuth)/7.0; 
                        regTime_person += (endTimeReg - startTimeReg);
                    }
                    
                insultRate.add(insult/7.0);
                regTime.add(regTime_person/8.0);
                authTime.add(authTime_person/8.0);
              //  }   
            }

           // writer.close();

            insultRates_db.add(insultRate);
            regTime_db.add(regTime);
            authTime_db.add(authTime);
            
            long endTimeDB = System.currentTimeMillis();

            timesPerDb.add(endTimeDB-startTimeDB);
        }

        long endTimeAnalysis = System.currentTimeMillis();

        saveToFile(insultRates_db, "insultRates");
        saveToFile(regTime_db, "RegTimes");
        saveToFile(authTime_db, "AuthTimes");

        PrintWriter writerTime;
        try {
            writerTime = new PrintWriter("C:\\Users\\Dell\\Desktop\\uci.cs203\\biohive\\output\\times.txt", "UTF-8");
            for(int i=0;i<timesPerDb.size();i++)
            {
                writerTime.print("DB "+(i+1)+": "+numberFormat.format(timesPerDb.get(i))+"       ");
            }
            writerTime.println();
            writerTime.print(numberFormat.format(endTimeAnalysis-startTimeAnalysis));
            writerTime.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("done!!");
    } 

        // if(run(baselineInfo))
        // {
        //     if(baselineInfo.mode == OpMode.reg)
        //     {
        //         System.out.println("Biometric successfully registerd!");
        //     }
        //     else if(baselineInfo.mode == OpMode.auth)
        //     {
        //         System.out.println("Biometric successfully authenticated!");
        //     }
        //     else 
        //     {
        //         System.out.println("Biometric successfully attacked!");
        //     }
        // }
        // else
        // {
        //     System.out.println("Error occured!");
        // }
}
