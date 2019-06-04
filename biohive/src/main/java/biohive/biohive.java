package biohive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
                String targetFileName = bInfo.out_minutiae + ".xyt";
                File targetFile = new File(targetFileName);

                if (!targetFile.exists()) {
                    MinutiaeExtractor.extract(bInfo);
                    TimeUnit.SECONDS.sleep(4);
                }

                if (targetFile.exists()) {
                    ArrayList<Minutiae> minutiaes = MinutiaeExtractor.encode(targetFileName);
                    if (minutiaes.size() > 0) {
                        if (bInfo.mode == OpMode.reg) {
                            FuzzyVault sugarVault = new FuzzyVault(minutiaes);
                            if (sugarVault.create()) {
                                HoneyMinutiae hMinutiae = new HoneyMinutiae(bInfo.getMinutiae_probdist());
                                HoneyvaultGenerator hGenerator = new HoneyvaultGenerator(sugarVault, hMinutiae);
                                if (hGenerator.generate()) {
                                    if (bInfo.getClearDb()) {
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

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException 
    {
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

        try 
        {
            ArrayList<String> bestFingerprints = new ArrayList<String>();

            FileWriter ir = new FileWriter("/home/sashidhar/course-work/cs203/uci.cs203/biohive/honeyvault/Analysis/degree/6/ir.txt", true);
            String folderName = "/home/sashidhar/course-work/cs203/uci.cs203/biohive/src/database/fingerprint/DB2_B";
    
            File folder = new File(folderName);
            File[] listOfFiles = folder.listFiles();
            Arrays.sort(listOfFiles);

            ArrayList<Double> regTime = new ArrayList<Double>();
            ArrayList<Double> authTime = new ArrayList<Double>();

            ArrayList<ArrayList<File>> people = new ArrayList<ArrayList<File>>();
            for (int j = 0; j < listOfFiles.length; j+=8) {

                ArrayList<File> people2 = new ArrayList<File>();
                for(int k = j; k < j+8; k++)
                {
                    people2.add(listOfFiles[k]);
                }

                people.add(people2);
            }

            Double insultRatetot = 0.0;
            for(int j=0;j<people.size();j++)  
            {
                Double personInsult = 1.0;

                baselineInfo.userId = people.get(j).get(0).getName().split("_")[0];
                ArrayList<File> person = people.get(j);
                
                ir.write(baselineInfo.userId + "\n");
                double regTime_person = 0;
                double authTime_person = 0;
                String bestfingerprint = "";
                for(int k = 0; k<person.size(); k++)
                {
                    Integer insult=0;
                    baselineInfo.setFingerprint(person.get(k).getName());
                    baselineInfo.setAction("reg");
                    baselineInfo.prepareOutputIdentifiers();

                    long startTimeReg = System.currentTimeMillis();
                    run(baselineInfo);
                    long endTimeReg = System.currentTimeMillis();

                    long startTimeAuth = System.currentTimeMillis();
                    long endTimeAuth = System.currentTimeMillis();
                    for(int l = 0; l<person.size(); l++)
                    {
                        System.out.println("Auth "+l);
                        if(l!=k)
                        {
                            baselineInfo.setFingerprint(person.get(l).getName());
                            baselineInfo.setAction("auth");
                            baselineInfo.prepareOutputIdentifiers();

                            startTimeAuth = System.currentTimeMillis();
                            if(!run(baselineInfo))
                            {
                                endTimeAuth = System.currentTimeMillis();
                                System.out.println("insult");
                                insult++;
                            }
                            else 
                            {
                                endTimeAuth = System.currentTimeMillis();
                                System.out.println("authenticated");
                            }
                        } 
                    }

                    Double insultrate = (Double)(insult * 1.0/7.0);
                    if(insultrate < personInsult)
                    {
                        personInsult = insultrate;
                        bestfingerprint = person.get(k).getName();
                    }

                    authTime_person += (endTimeAuth - startTimeAuth); 
                    regTime_person += (endTimeReg - startTimeReg);
                }

                bestFingerprints.add(bestfingerprint);

                regTime.add(regTime_person/person.size()*1.0);
                authTime.add(authTime_person/person.size()*1.0);
                ir.write("insult rate   " + personInsult.toString() + "\n");
                insultRatetot += personInsult;
            }
            insultRatetot = insultRatetot/people.size()*1.0;
            ir.write("\n" + "avg ir   " + insultRatetot.toString() + "\n");
            ir.close();

            saveToFile(regTime, "RegTimes");
            saveToFile(authTime, "AuthTimes");

            FileWriter fr = new FileWriter("/home/sashidhar/course-work/cs203/uci.cs203/biohive/honeyvault/Analysis/degree/6/fr.txt", true);
            Double fraudratetot = 0.0;
            for (String fingerString : bestFingerprints) 
            {
                baselineInfo.setFingerprint(fingerString);
                baselineInfo.setAction("reg");
                baselineInfo.prepareOutputIdentifiers();
                run(baselineInfo);
                
                Integer fraud = 0;
                for (String oFingerString : bestFingerprints) 
                {
                    if(!oFingerString.equals(fingerString))
                    {
                        baselineInfo.setFingerprint(oFingerString);
                        baselineInfo.setAction("auth");
                        baselineInfo.prepareOutputIdentifiers();

                        if(!run(baselineInfo))
                        {
                            System.out.println("expected");
                        }
                        else 
                        {
                            System.out.println("fraud");
                            fraud++;
                        }
                    }    
                }

                Double fraudrate = (Double)(fraud * 1.0/7.0);
                fraudratetot += fraudrate;
                fr.write("fr: " + fraudrate.toString() + "\n");
            }
            fraudratetot = fraudratetot/bestFingerprints.size()*1.0;
            fr.write("\n" + "avg fr   " + fraudratetot.toString() + "\n");
            fr.close();
        }
        catch(Exception e){}
        System.out.println("done!!");
    } 


    static void saveToFile(ArrayList<Double> data, String name)
    {
        PrintWriter writer;
        try 
        {
            writer = new PrintWriter("/home/sashidhar/course-work/cs203/uci.cs203/biohive/honeyvault/Analysis/degree/6/"+name+".txt", "UTF-8");
            Double avg = 0.0;
            for(Double d: data)
            {
                writer.print(d.toString() + "\n");
                avg += d;
            }
            avg = avg/data.size();
            writer.print("\n" + avg.toString() + "\n");
            writer.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
    }
}
