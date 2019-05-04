package biohive.utility;

public class Utils
{
    public static int extractBits(int number, int k, int p) 
    { 
        return (((1 << k) - 1) & (number >> (p - 1))); 
    }
}