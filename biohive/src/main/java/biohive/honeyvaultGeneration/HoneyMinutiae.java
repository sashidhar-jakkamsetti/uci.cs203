package biohive.honeyvaultGeneration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

import biohive.minutiaeExtraction.Minutiae;
import biohive.utility.Constants;
import cc.redberry.rings.IntegersZp64;

public class HoneyMinutiae
{
    private Integer[][] minutiaeStats;
    private IntegersZp64 field31;
    private IntegersZp64 field61;

    public HoneyMinutiae(String probDistFileName) throws Exception
    {
        minutiaeStats = new Integer[5][5];

        BufferedReader bReader = new BufferedReader(new FileReader(probDistFileName));   
        String line;
        int i = 0;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length == 5)
            {
                int j = 0;
                for (String token : tokens) 
                {
                    if(i == 0)
                    {
                        minutiaeStats[i][j] = Integer.parseInt(token) >> (Constants.BITS_OF_X_MINUTIAE - Constants.CAP_BITS_OF_X_MINUTIAE);
                    }
                    else if(i == 1)
                    {
                        minutiaeStats[i][j] = Integer.parseInt(token) >> (Constants.BITS_OF_Y_MINUTIAE - Constants.CAP_BITS_OF_Y_MINUTIAE);
                    }
                    else if(i == 2)
                    {
                        minutiaeStats[i][j] = Integer.parseInt(token) >> (Constants.BITS_OF_O_MINUTIAE - Constants.CAP_BITS_OF_O_MINUTIAE);
                    }
                    j++;
                }
            }
            i++;
        }

        bReader.close();

        field31 = new IntegersZp64(Constants.FIELD_ORDER_5);
        field61 = new IntegersZp64(Constants.FIELD_ORDER_6);
    }

    public ArrayList<Minutiae> generateGaussian()
    {
        ArrayList<Minutiae> hMinutiaes = new ArrayList<Minutiae>();
        
        HashSet<Integer> existingMinutiae = new HashSet<Integer>();
        while(hMinutiaes.size() < Constants.NUMBER_OF_MINUTIAE)
        {
            Minutiae m = new Minutiae();

            // 1 std dev.
            if(hMinutiaes.size() < Constants.STD_DEV_1 * Constants.NUMBER_OF_MINUTIAE)
            {                 
                m.x = pickXInGaussian1std();
                m.y = pickYInGaussian1std();
                m.o = (int)field61.randomElement();
            }
            // 2/3 std dev.
            else
            {
                m.x = pickXInGaussian2std();
                m.y = pickYInGaussian2std();
                m.o = (int)field61.randomElement();
            }

            m.encodeNatural();

            if(!existingMinutiae.contains(new Integer(m.code)))
            {
                hMinutiaes.add(m);
            }
        }

        return hMinutiaes;
    }

    private int pickXInGaussian1std()
    {
        while(true)
        {
            int x = (int)field31.randomElement();
            if(x <= minutiaeStats[0][0] + minutiaeStats[0][2] && x >= minutiaeStats[0][0] - minutiaeStats[0][2])
            {
                return x;
            }
        }
    }

    private int pickXInGaussian2std()
    {
        while(true)
        {
            int x = (int)field31.randomElement();
            if((x >= minutiaeStats[0][3] && x <= minutiaeStats[0][0] - minutiaeStats[0][2]) || 
                    (x <= minutiaeStats[0][4] && x >= minutiaeStats[0][0] + minutiaeStats[0][2]))
            {
                return x;
            }
        }
    }

    private int pickYInGaussian1std()
    {
        while(true)
        {
            int y = (int)field31.randomElement();
            if(y <= minutiaeStats[1][0] + minutiaeStats[1][2] && y >= minutiaeStats[1][0] - minutiaeStats[1][2])
            {
                return y;
            }
        }
    }

    private int pickYInGaussian2std()
    {
        while(true)
        {
            int y = (int)field31.randomElement();
            if((y >= minutiaeStats[1][3] && y <= minutiaeStats[1][0] - minutiaeStats[1][2]) || 
                    (y <= minutiaeStats[1][4] && y >= minutiaeStats[1][0] + minutiaeStats[1][2]))
            {
                return y;
            }
        }
    }
    
    public ArrayList<Minutiae> generateRandom()
    {
        ArrayList<Minutiae> hMinutiaes = new ArrayList<Minutiae>();
        
        HashSet<Integer> existingMinutiae = new HashSet<Integer>();
        while(hMinutiaes.size() < Constants.NUMBER_OF_MINUTIAE)
        {
            Minutiae m = new Minutiae();
            m.x = (int)field31.randomElement();
            m.y = (int)field31.randomElement();
            m.o = (int)field61.randomElement();
            m.encodeNatural();

            if(!existingMinutiae.contains(new Integer(m.code)))
            {
                hMinutiaes.add(m);
            }
        }

        return hMinutiaes;
    }
}