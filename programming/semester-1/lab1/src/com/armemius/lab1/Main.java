package com.armemius.lab1;

public class Main {
        public static void main(String[] args) {
                long[] a = new long[19];
                for (long it = 4; it < 22; ++it) {
                        a[(int)it - 4] = it;
                }
                double[] x = new double[11];
                for (int it = 0; it < 11; ++it) {
                        x[it] = Math.random() * 29f - 14f;
                }
                double[][] arr = new double[10][11];
                for (int it = 0; it < 10; ++it) {
                        for (int jt = 0; jt < 11; ++jt) {
                                double v = x[jt];
                                if (a[it] == 10) {
                                        arr[it][jt] = Math.tan(Math.atan((1.0 / 2.0) * ((v + 0.5) / 29.0)));
                                } else if (a[it] == 6
                                           || a[it] == 8
                                           || a[it] == 12
                                           || a[it] == 16
                                           || a[it] == 18) {
                                        arr[it][jt] = Math.sin(Math.pow((Math.pow(v, v)), (3.0 / Math.pow(0.25 / (v + (1.0 / 4.0)), v))));

                                } else {
                                        arr[it][jt] = Math.atan(Math.cos(Math.pow(1 - Math.pow(2.0 * Math.asin((v + 0.5) / 29.0), 2), 3.0)));
                                }
                        System.out.printf("%.5f%s", arr[it][jt], "   ");
                        }
                System.out.println();
                }
        }
}
