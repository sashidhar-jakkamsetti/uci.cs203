package biohive.honeywordGeneration;

import java.io.BufferedReader;
import java.io.FileReader;

public class HoneyMinutiae
{
    private Integer[][] minutiaeStats;

    public HoneyMinutiae(String probDistFileName) throws Exception
    {
        minutiaeStats = new Integer[5][5];

        BufferedReader bReader = new BufferedReader(new FileReader(probDistFileName));   
        String line;
        int i = 0, j = 0;
        while ((line = bReader.readLine()) != null) 
        {
            String[] tokens = line.split(" ");
            if(tokens.length == 5)
            {
                for (String token : tokens) 
                {
                    minutiaeStats[i][j] = Integer.parseInt(token);
                    j++;
                }
            }
            i++;
        }

        bReader.close();
    }
}