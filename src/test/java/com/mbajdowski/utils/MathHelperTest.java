package com.mbajdowski.utils;

import com.mbajdowski.fft.Complex;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mbajdowski.utils.MathHelper.BIAS;
import static com.mbajdowski.utils.MathHelper.EXP_POW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class MathHelperTest {

    @Test
    void convertToComplexArrayShouldCorrectlyConvertValidArray() {
        int[] intArr = {1,2,3};
        Complex[] expected = {new Complex(1, 0), new Complex(2,0), new Complex(3,0)};

        Complex[] actual = MathHelper.convertToComplexArray(intArr);

        assertThat(actual, arrayContainingInAnyOrder(expected));
    }

    @Test
    void convertToComplexArrayShouldReturnEmptyArrayOnEmptyInput() {
        int[] intArr = {};

        Complex[] actual = MathHelper.convertToComplexArray(intArr);

        assertEquals(0, actual.length);
    }

    @Test
    void convertToIntArrayShouldCorrectlyConvertValidArray() {
        Complex[] complexArr = {new Complex(1, 1), new Complex(2,2), new Complex(3,3)};
        int[] expected = {(int)complexArr[0].abs(), (int)complexArr[1].abs(), (int)complexArr[2].abs()};

        int[] actual = MathHelper.convertToIntArray(complexArr);

        assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    void byteArrayToIntArray() {
        byte[] twoInts = {0x00, 0x32, 0x05, (byte)0xDC};
        int[] expected = {50, 1500};

        int[] actual = MathHelper.byteArrayToIntArray(twoInts);

        assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    void findNearestPowerOfTwo() {
        int input = 65;
        int expected = 64;

        int actual = MathHelper.findNearestPowerOfTwo(input);

        assertEquals(expected, actual);
    }

    @Test
    void findExtrema() {
        int min = 0;
        int max = 100;
        int[] expected = {min, max};
        List<int[]> input = new ArrayList<>();
        input.add(new int[]{1,2,3});
        input.add(new int[]{min,2,3});
        input.add(new int[]{5,10,max});

        int [] actual = MathHelper.findExtrema(input);

        assertArrayEquals(expected, actual);
    }

    @Test
    void scaleValueToRangeShouldIncreaseValue() {
        int value = 10;
        int maxIn = 100;
        int minIn = 0;
        int maxOut = 1000;

        int actual = MathHelper.scaleValueToRange(value, maxIn, minIn, maxOut);

        assertEquals(100, actual);
    }

    @Test
    void scaleValueToRangeShouldDecreaseValue() {
        int value = 10;
        int maxIn = 100;
        int minIn = 0;
        int maxOut = 10;

        int actual = MathHelper.scaleValueToRange(value, maxIn, minIn, maxOut);

        assertEquals(1, actual);
    }

    @Test
    void calculateExpFunctionShouldUseCorrectFunction() {
        float value = 10;
        int expected = (int)(Math.exp(EXP_POW*value)*BIAS);

        int actual = MathHelper.calculateExpFunction(value);

        assertEquals(expected, actual);
    }

    @Test
    void generateFreqBuckets() {
        int[] expected = {25, 52, 110, 230, 484, 1016, 2133, 4476, 9393, 19709};

        int[] actual = MathHelper.generateFreqBuckets(10);

        assertArrayEquals(expected, actual);
    }

    @Test
    void generateFreqBucketsShouldThrowExceptionIfInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            MathHelper.generateFreqBuckets(-1);
        });
    }
}