package com.mbajdowski.utils;

import com.mbajdowski.fft.Complex;

import java.util.List;

public class MathHelper {

    private static final double EXP_POW = 0.23;
    private static final int BIAS = 25;
    public static final float INDEX_MAX = 29;

    public static Complex[] convertToComplexArray(int[] intArr) {
        Complex[] result = new Complex[intArr.length];
        for (int i = 0; i < intArr.length; i++) {
            result[i] = new Complex(intArr[i], 0);
        }

        return result;
    }

    public static int[] convertToIntArray(Complex[] complex) {
        int[] result = new int[complex.length];
        for (int i = 0; i < complex.length; i++) {
            result[i] = (int) complex[i].abs();
        }
        return result;
    }

    public static int[] byteArrayToIntArray(byte[] bytes) {
        int[] result = new int[bytes.length / 2];

        for (int i = 0; i < result.length; i++) {
            result[i] = byteToInt(bytes[i * 2], bytes[i * 2 + 1]);
        }

        return result;
    }

    public static int findNearestPowerOfTwo(int desiredNo) {
        int result = 1;
        int i = 0;
        while (result < desiredNo) {
            result = (int) Math.pow(2, ++i);
        }
        return result / 2;
    }

    public static int[] findExtrema(int[] array) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int value : array) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        return new int[]{min, max};
    }

    public static int scaleValueToRange(int x, int maxInput, int minInput, int maxOut) {
        if (x < minInput) return 0;
        if (x > maxInput) return maxInput;
        return (int) (maxOut * ((double) x - minInput) / (maxInput - minInput));
    }

    public static int[] findExtrema(List<int[]> range) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int[] ints : range) {
            for (int j = 0; j < range.get(0).length; j++) {
                int value = ints[j];
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
        }

        return new int[]{min, max};
    }

    public static int calculateExpFunction(float x){
       return (int)(Math.exp(EXP_POW*x)*BIAS);
    }

    public static int[] generateFreqBuckets(int noOfIndexes){
        if(noOfIndexes < 1) {
            throw new IllegalArgumentException("Number of indexes needs to be grater than 1");
        }
        float delta = INDEX_MAX/(noOfIndexes-1);
        int[] result = new int[noOfIndexes];

        for (int i = 0; i < noOfIndexes; i++) {
            result[i] = calculateExpFunction(i*delta);
        }

        return result;
    }

    private static int byteToInt(byte... bytes) {
        if (bytes.length != 2) {
            throw new IllegalArgumentException("Byte array must have size of 2");
        }
        int result = 0;
        int littleMask = 255;
        for (byte aByte : bytes) {
            int intFromByte = littleMask & aByte;
            result <<= 8;
            result |= intFromByte;
        }

        return result;
    }
}
