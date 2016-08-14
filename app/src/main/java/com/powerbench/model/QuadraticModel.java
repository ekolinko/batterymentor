package com.powerbench.model;

import android.util.Log;

import com.powerbench.datamanager.Point;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents a quadratic model.
 */
public class QuadraticModel extends Model implements Serializable {

    /**
     * The first coefficient of the model. The coefficient a in y = ax^2 + bx + c.
     */
    protected double mCoeff1;

    /**
     * The second coefficient of the model. The coefficient b in y = ax^2 + bx + c.
     */
    protected double mCoeff2;

    /**
     * The intercept of the line in the model.
     */
    protected double mIntercept;

    public QuadraticModel(ArrayList<Point> data) {
        super(data);
    }

    /**
     * Create a linear model using the simple linear regression formula on the specified data.
     *
     * @param input the data to use to create the model.
     */
    @Override
    public void createModel(ArrayList<Point> input) {
        Log.d("tstatic", "createModel");
        for (Point point : input) {
            Log.d("tstatic", "x = " + point.getX() + " y = " + point.getY());
        }
        int itemCount = input.size();
        int validItems = 0;
        double[][] data = new double[2][itemCount];
        for (int item = 0; item < itemCount; item++) {
            double x = input.get(item).getX();
            double y = input.get(item).getY();
            if (!Double.isNaN(x) && !Double.isNaN(y)) {
                data[0][validItems] = x;
                data[1][validItems] = y;
                validItems++;
            }
        }
        int equations = 3;
        int coefficients = 4;
        double[] result = new double[equations];
        double[][] matrix = new double[equations][coefficients];
        double sumX = 0.0;
        double sumY = 0.0;

        for (int item = 0; item < validItems; item++) {
            sumX += data[0][item];
            sumY += data[1][item];
            for (int eq = 0; eq < equations; eq++) {
                for (int coe = 0; coe < coefficients - 1; coe++) {
                    matrix[eq][coe] += Math.pow(data[0][item], eq + coe);
                }
                matrix[eq][coefficients - 1] += data[1][item]
                        * Math.pow(data[0][item], eq);
            }
        }
        double[][] subMatrix = calculateSubMatrix(matrix);
        for (int eq = 1; eq < equations; eq++) {
            matrix[eq][0] = 0;
            for (int coe = 1; coe < coefficients; coe++) {
                matrix[eq][coe] = subMatrix[eq - 1][coe - 1];
            }
        }
        for (int eq = equations - 1; eq > -1; eq--) {
            double value = matrix[eq][coefficients - 1];
            for (int coe = eq; coe < coefficients - 1; coe++) {
                value -= matrix[eq][coe] * result[coe];
            }
            result[eq] = value / matrix[eq][eq];
        }
        mCoeff1 = result[2];
        mCoeff2 = result[1];
        mIntercept = result[0];
        int i = 0;
        for (Double r : result) {
            Log.d("tstatic", "r[ " + i + "] = " + r);
        }
    }

    /**
     * Returns a matrix with the following features: (1) the number of rows
     * and columns is 1 less than that of the original matrix; (2)the matrix
     * is triangular, i.e. all elements a (row, column) with column &gt; row are
     * zero.  This method is used for calculating a polynomial regression.
     *
     * @param matrix the start matrix.
     * @return The new matrix.
     */
    private static double[][] calculateSubMatrix(double[][] matrix) {
        int equations = matrix.length;
        int coefficients = matrix[0].length;
        double[][] result = new double[equations - 1][coefficients - 1];
        for (int eq = 1; eq < equations; eq++) {
            double factor = matrix[0][0] / matrix[eq][0];
            for (int coe = 1; coe < coefficients; coe++) {
                result[eq - 1][coe - 1] = matrix[0][coe] - matrix[eq][coe]
                        * factor;
            }
        }
        if (equations == 1) {
            return result;
        }
        // check for zero pivot element
        if (result[0][0] == 0) {
            boolean found = false;
            for (int i = 0; i < result.length; i++) {
                if (result[i][0] != 0) {
                    found = true;
                    double[] temp = result[0];
                    System.arraycopy(result[i], 0, result[0], 0,
                            result[i].length);
                    System.arraycopy(temp, 0, result[i], 0, temp.length);
                    break;
                }
            }
            if (!found) {
                return new double[equations - 1][coefficients - 1];
            }
        }
        double[][] subMatrix = calculateSubMatrix(result);
        for (int eq = 1; eq < equations - 1; eq++) {
            result[eq][0] = 0;
            for (int coe = 1; coe < coefficients - 1; coe++) {
                result[eq][coe] = subMatrix[eq - 1][coe - 1];
            }
        }
        return result;
    }

    /**
     * Return the modeled y-value for the specified x-value. Currently, the y-intercept is not
     * used for prediction.
     *
     * @param x the x-value for which to get the y-value.
     * @return the modeled y-value for the specified x-value.
     */
    public double getY(double x) {
        return mCoeff1 * Math.pow(x, 2) + mCoeff2 * x + mIntercept;
    }

    /**
     * Return the y-intercept for this model.
     *
     * @return the y-intercept for this model.
     */
    public double getIntercept() {
        return mIntercept;
    }

    /**
     * Return the first coefficient of this model.
     *
     * @return the first coefficient of this model.
     */
    public double getFirstCoefficient() {
        return mCoeff1;
    }

    /**
     * Return the second coefficient of this model.
     *
     * @return the second coefficient of this model.
     */
    public double getSecondCoefficient() {
        return mCoeff2;
    }
}
