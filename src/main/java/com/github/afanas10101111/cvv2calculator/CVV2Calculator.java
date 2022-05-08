package com.github.afanas10101111.cvv2calculator;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CVV2Calculator {
    private static final String SVC = "000";
    private static final String DES = "DES";
    private static final String DES_ECB_NO_PADDING = "DES/ECB/NoPadding";
    private static final String DESEDE = "DESede";
    private static final String DESEDE_ECB_NO_PADDING = "DESede/ECB/NoPadding";

    private static final char[] decTable = "0123456789012345".toCharArray();

    private final Cipher desCipher;
    private final Cipher tripleDesCipher;

    private CVV2Calculator() throws NoSuchPaddingException, NoSuchAlgorithmException {
        desCipher = Cipher.getInstance(DES_ECB_NO_PADDING);
        tripleDesCipher = Cipher.getInstance(DESEDE_ECB_NO_PADDING);
    }

    public static String calculateCVV2(String pan, String expiry, String cvk1, String cvk2)
            throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        return new CVV2Calculator().calculate(pan, expiry, cvk1, cvk2);
    }

    private String calculate(String pan, String expiry, String cvk1, String cvk2)
            throws DecoderException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        initCiphers(cvk1, cvk2);
        DataPair dataPair = getDataPair(pan, expiry);

        byte[] dataPairFirstEncrypted = desCipher.doFinal(dataPair.first);
        byte[] xorData = ByteUtils.xor(dataPairFirstEncrypted, dataPair.second);
        String encryptedResult = ByteUtils.toHexString(tripleDesCipher.doFinal(xorData));

        return decimalizeFirstThreeDigits(encryptedResult);
    }

    private void initCiphers(String cvk1, String cvk2) throws InvalidKeyException {
        SecretKey secretDES = new SecretKeySpec(ByteUtils.fromHexString(cvk1), DES);
        desCipher.init(Cipher.ENCRYPT_MODE, secretDES);
        SecretKey secretTripleDES = new SecretKeySpec(ByteUtils.fromHexString(cvk1 + cvk2 + cvk1), DESEDE);
        tripleDesCipher.init(Cipher.ENCRYPT_MODE, secretTripleDES);
    }

    private DataPair getDataPair(String pan, String expiry) {
        int dataLength = 32;
        StringBuilder builder = new StringBuilder(dataLength).append(pan).append(expiry).append(SVC);
        int padLength = dataLength - builder.length();
        for (int i = 0; i < padLength; i++) {
            builder.append('0');
        }
        return new DataPair(
                ByteUtils.fromHexString(builder.substring(0, 16)),
                ByteUtils.fromHexString(builder.substring(16))
        );
    }

    private String decimalizeFirstThreeDigits(String data) {
        int outLength = 3;
        StringBuilder selected = new StringBuilder(outLength);
        int selectionCounter = 0;
        int dataLength = data.length();
        for (int i = 0; i < dataLength && selectionCounter < outLength; i++) {
            if (hexToByte(data.charAt(i)) < 10) {
                selected.append(data.charAt(i));
                selectionCounter++;
            }
        }
        if (selectionCounter < outLength) {
            for (int i = 0; i < dataLength && selectionCounter < outLength; i++) {
                if (hexToByte(data.charAt(i)) > 9) {
                    selected.append(decTable[hexToByte(data.charAt(i))]);
                    selectionCounter++;
                }
            }
        }
        return selected.toString();
    }

    private int hexToByte(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        }
        if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        }
        return -1;
    }

    private static class DataPair {
        private final byte[] first;
        private final byte[] second;

        public DataPair(byte[] first, byte[] second) {
            this.first = first;
            this.second = second;
        }
    }
}
