package biohive.validation;

import biohive.utility.Constants;

public class Alarm
{
    public static void shout()
    {
        System.out.println("Biometric database COMPROMISED!!");
        //System.exit(Constants.ALARM_EXIT_STATUS);
    }
}