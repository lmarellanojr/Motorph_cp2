package com.group33.cp2.motorph;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility class providing AES encryption and decryption for password storage.
 *
 * @author Group13
 * @version 1.0
 */
public class CryptoUtil {

    /** Secret AES key — must be exactly 16, 24, or 32 characters for AES-128/192/256. */
    private static final String SECRET_KEY = "Group13SecretKEY"; // 16 characters = AES-128

    /**
     * Encrypts a plain-text string using AES and encodes the result as Base64.
     *
     * @param strToEncrypt the plain-text string to encrypt
     * @return the Base64-encoded encrypted string, or null if encryption fails
     */
    public static String encrypt(String strToEncrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypts a Base64-encoded AES-encrypted string back to plain text.
     *
     * @param strToDecrypt the Base64-encoded encrypted string
     * @return the decrypted plain-text string, or null if decryption fails
     */
    public static String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(strToDecrypt);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
