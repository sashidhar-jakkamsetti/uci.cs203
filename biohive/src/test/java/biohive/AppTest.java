package biohive;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import biohive.minutiaeExtraction.Minutiae;

public class AppTest 
{
    @Test
    public void minutiaeEncodeDecodeTest()
    {
        Minutiae m1 = new Minutiae(16, 177, 259, 21);
        m1.encode();

        Minutiae m2 = new Minutiae(m1.code);
        m2.decode();

        assertTrue(m1.code == m2.code);
    }
}
