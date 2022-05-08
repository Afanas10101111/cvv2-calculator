package com.github.afanas10101111.cvv2calculator;

import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class CVV2CalculatorTest {
    private static final String DEFAULT_KEY_PART_ONE = "0011223344556677";
    private static final String DEFAULT_KEY_PART_TWO = "8899AABBCCDDEEFF";

    @Test
    public void calculate()
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        assertEquals("081", CVV2Calculator.calculateCVV2(
                "4000000000000001", "2209", DEFAULT_KEY_PART_ONE, DEFAULT_KEY_PART_TWO
        ));
        assertEquals("984", CVV2Calculator.calculateCVV2(
                "4000000000000002", "2301", DEFAULT_KEY_PART_ONE, DEFAULT_KEY_PART_TWO
        ));
        assertEquals("550", CVV2Calculator.calculateCVV2(
                "4000000000000003", "2411", DEFAULT_KEY_PART_ONE, DEFAULT_KEY_PART_TWO
        ));
        assertEquals("649", CVV2Calculator.calculateCVV2(
                "4000000000000004", "2510", DEFAULT_KEY_PART_ONE, DEFAULT_KEY_PART_TWO
        ));
    }
}
