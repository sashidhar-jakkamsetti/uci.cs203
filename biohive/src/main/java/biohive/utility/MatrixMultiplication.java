package biohive.utility;

import cc.redberry.rings.IntegersZp64;

public class MatrixMultiplication
{
    private static IntegersZp64 field = new IntegersZp64(Constants.FIELD_ORDER_16);
    
    public static int[][] multiply(int[][] A, int[][] B) 
    {
        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;
    
        if (aColumns != bRows) 
        {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }
    
        int[][] C = new int[aRows][bColumns];
        for (int i = 0; i < aRows; i++) 
        {
            for (int j = 0; j < bColumns; j++) 
            {
                C[i][j] = 0;
            }
        }
    
        for (int i = 0; i < aRows; i++) 
        {
            for (int j = 0; j < bColumns; j++) 
            { 
                for (int k = 0; k < aColumns; k++) 
                { 
                    C[i][j] = (int)field.add(C[i][j], field.multiply(A[i][k], B[k][j]));
                }
            }
        }
    
        return C;
    }
}
