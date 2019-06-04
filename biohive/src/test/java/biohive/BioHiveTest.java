package biohive;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import biohive.minutiaeExtraction.Minutiae;

public class BioHiveTest 
{
    @Test
    public void minutiaeEncodeDecodeTest()
    {
        Minutiae m1 = new Minutiae(16, 177, 259, 21);
        m1.encode();
        m1.decode();

        Minutiae m2 = new Minutiae(m1.code);
        m2.decode();

        assertTrue(m1.code == m2.code);
        assertTrue(m1.x == m2.x);
        assertTrue(m1.y == m2.y);
        assertTrue(m1.o == m2.o);
    }
}
