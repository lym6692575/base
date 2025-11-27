package com.example.demo.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {
    private static final String ALGORITHM = "AES";
    private static final String MODE_PADDING = "AES/CBC/PKCS5Padding";
    private static final String KEY = "1234567890123456"; // 密钥，16字符

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE_PADDING);

            byte[] iv = new byte[cipher.getBlockSize()];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密错误: " + e.toString());
        }
    }

    public static String decrypt(String cipherText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE_PADDING);

            String[] parts = cipherText.split(":");
            IvParameterSpec ivParams = new IvParameterSpec(Base64.getDecoder().decode(parts[0]));

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(parts[1]));

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密错误: " + e.toString());
        }
    }
}
