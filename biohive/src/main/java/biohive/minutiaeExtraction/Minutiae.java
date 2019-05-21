package biohive.minutiaeExtraction;

import biohive.utility.Constants;
import biohive.utility.Utils;

public class Minutiae
{
    public int x;
    public int y;
    public int o;
    public Integer cnf;
    public int code;

    public Minutiae()
    {}

    public Minutiae(int code)
    {
        this.code = code;
    }

    public Minutiae(int x, int y, int o, Integer cnf)
    {
        this.x = x;
        this.y = y;
        this.o = o;
        this.cnf = cnf;
    }

    public Minutiae(String line)
    {
        String[] tokens = line.split(" ");
        x = Integer.parseInt(tokens[0]);
        y = Integer.parseInt(tokens[1]);
        o = Integer.parseInt(tokens[2]);
        cnf = Integer.parseInt(tokens[3]);
    }

    public void encode()
    {
        int xShifted = x >> (Constants.BITS_OF_X_MINUTIAE - Constants.CAP_BITS_OF_X_MINUTIAE);
        int yShifted = y >> (Constants.BITS_OF_Y_MINUTIAE - Constants.CAP_BITS_OF_Y_MINUTIAE);
        int oShifted = o >> (Constants.BITS_OF_O_MINUTIAE - Constants.CAP_BITS_OF_O_MINUTIAE);

        code = code | oShifted;
        code = code | yShifted << Constants.CAP_BITS_OF_O_MINUTIAE;
        code = code | xShifted << Constants.CAP_BITS_OF_O_MINUTIAE + Constants.CAP_BITS_OF_Y_MINUTIAE;
    }

    public void decode()
    {
        x = Utils.extractBits(
            code, 
            Constants.CAP_BITS_OF_X_MINUTIAE,
            Constants.CAP_BITS_OF_O_MINUTIAE + Constants.CAP_BITS_OF_Y_MINUTIAE + 1
            );

        y = Utils.extractBits(
            code, 
            Constants.CAP_BITS_OF_Y_MINUTIAE,
            Constants.CAP_BITS_OF_O_MINUTIAE + 1
            );

        o = Utils.extractBits(
            code, 
            Constants.CAP_BITS_OF_O_MINUTIAE,
            1
            );
    }

    public double distance(Minutiae m)
    {
        return Math.sqrt((Math.pow((x - m.x), 2) - Math.pow((y - m.y), 2))) + (0.3 * Math.min(Math.abs(o - m.o), 360 - Math.abs(o - m.o)));
    }
}