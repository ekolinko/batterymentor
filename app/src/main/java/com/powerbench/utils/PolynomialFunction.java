package com.powerbench.utils;

public class PolynomialFunction {

    private final double coefficients[];

    public PolynomialFunction(double c[]) {
        super();
        int n = c.length;
        while ((n > 1) && (c[n - 1] == 0)) {
            --n;
        }
        this.coefficients = new double[n];
        System.arraycopy(c, 0, this.coefficients, 0, n);
    }

    public double value(double x) {
        return evaluate(coefficients, x);
    }

    /**
     * Uses Horner's Method to evaluate the polynomial with the given coefficients at
     * the argument.
     */
    protected static double evaluate(double[] coefficients, double argument) {
        int n = coefficients.length;
        double result = coefficients[n - 1];
        for (int j = n - 2; j >= 0; j--) {
            result = argument * result + coefficients[j];
        }
        return result;
    }

}
