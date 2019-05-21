package biohive.utility;

import java.math.*;

public class ModularMatrix 
{
    private int nrows;
    private int ncols;
    private BigInteger[][] data;
    private BigInteger mod;
    private Integer fieldOrder;

    public ModularMatrix(BigInteger[][] mat, Integer fieldOrder) 
    {
        this.fieldOrder = fieldOrder;
        this.mod = new BigInteger(this.fieldOrder.toString());
        this.data = mat;
        this.nrows = mat.length;
        this.ncols = mat[0].length;
    }

    public ModularMatrix(int nrow, int ncol, Integer fieldOrder) 
    {
        this.fieldOrder = fieldOrder;
        this.mod = new BigInteger(this.fieldOrder.toString());
        this.nrows = nrow;
        this.ncols = ncol;
        data = new BigInteger[nrow][ncol];
    }

    public int getNrows() 
    {
        return nrows;
    }

    public void setNrows(int nrows) 
    {
        this.nrows = nrows;
    }

    public int getNcols() 
    {
        return ncols;
    }

    public void setNcols(int ncols) 
    {
        this.ncols = ncols;
    }

    public BigInteger[][] getData() 
    {
        return data;
    }

    public void setData(BigInteger[][] data) 
    {
        this.data = data;
    }

    public BigInteger getValueAt(int i, int j) 
    {
        return data[i][j];
    }

    public void setValueAt(int i, int j, BigInteger value) 
    {
        data[i][j] = value;
    }

    public int size() 
    {
        return ncols;
    }

    // Take the transpose of the Matrix..
    public ModularMatrix transpose(ModularMatrix matrix) 
    {
        ModularMatrix transposedMatrix = new ModularMatrix(matrix.getNcols(), matrix.getNrows(), matrix.fieldOrder);
        for (int i = 0; i < matrix.getNrows(); i++) 
        {
            for (int j = 0; j < matrix.getNcols(); j++) 
            {
                transposedMatrix.setValueAt(j, i, matrix.getValueAt(i, j));
            }
        }
        return transposedMatrix;
    }

    // All operations are using Big Integers... Not Modular of anything
    public BigInteger determinant(ModularMatrix matrix) 
    {

        if (matrix.size() == 1) 
        {
            return matrix.getValueAt(0, 0);
        }
        if (matrix.size() == 2) 
        {
            //return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) - (matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0));
            return (matrix.getValueAt(0, 0).multiply(matrix.getValueAt(1, 1))).subtract((matrix.getValueAt(0, 1).multiply(matrix.getValueAt(1, 0))));
        }
        BigInteger sum = new BigInteger("0"); 
        for (int i = 0; i < matrix.getNcols(); i++) 
        {
            sum = sum.add(changeSign(i).multiply(matrix.getValueAt(0, i).multiply(determinant(createSubMatrix(matrix, 0, i)))));
        }
        return sum;
    }

    private BigInteger changeSign(int i) 
    {
        if (i % 2 == 0) 
        {
            return new BigInteger("1"); 
        }
        else 
        {
            return new BigInteger("-1");
        }
    }

    public ModularMatrix createSubMatrix(ModularMatrix matrix, int excluding_row, int excluding_col) 
    {
        ModularMatrix mat = new ModularMatrix(matrix.getNrows() - 1, matrix.getNcols() - 1, matrix.fieldOrder);
        int r = -1;
        for (int i = 0; i < matrix.getNrows(); i++) 
        {
            if (i == excluding_row) 
            {
                continue;
            }
            r++;
            int c = -1;
            for (int j = 0; j < matrix.getNcols(); j++) 
            {
                if (j == excluding_col) 
                {
                    continue;
                }
                mat.setValueAt(r, ++c, matrix.getValueAt(i, j));
            }
        }
        return mat;
    }

    public ModularMatrix cofactor(ModularMatrix matrix) 
    {
        ModularMatrix mat = new ModularMatrix(matrix.getNrows(), matrix.getNcols(), matrix.fieldOrder);
        for (int i = 0; i < matrix.getNrows(); i++) 
        {
            for (int j = 0; j < matrix.getNcols(); j++) 
            {
                mat.setValueAt(i, j, (changeSign(i).multiply(changeSign(j)).multiply(determinant(createSubMatrix(matrix, i, j)))).mod(mod));
            }
        }

        return mat;
    }

    public ModularMatrix inverse(ModularMatrix matrix) 
    {
        return (transpose(cofactor(matrix)).dc(determinant(matrix)));
    }

    private ModularMatrix dc(BigInteger d) 
    {
        BigInteger inv = d.modInverse(mod);
        for (int i = 0; i < nrows; i++) 
        {
            for (int j = 0; j < ncols; j++) 
            {
                data[i][j] = (data[i][j].multiply(inv)).mod(mod); 
            }
        }
        return this;
    }
}