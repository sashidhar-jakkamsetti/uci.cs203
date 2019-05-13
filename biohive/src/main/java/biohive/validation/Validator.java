package biohive.validation;

import biohive.alarm.Alarm;
import biohive.utility.DatabaseIO;

public class Validator
{
    public static boolean validate(String userId, String honeydb, Integer observedIdx) throws Exception
    {
        Integer hChecker = DatabaseIO.getHoneyChecker(userId, honeydb);
        if(hChecker >= 0)
        {
            if(hChecker.equals(observedIdx))
            {
                return true;
            }
            else
            {
                Alarm.shout();
            }
        }

        return false;
    }
}