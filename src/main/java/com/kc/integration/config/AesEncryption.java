package com.kc.integration.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AesEncryption {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;
//    private static final String SECRET_KEY = "mySuperSecretKey123";
    private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(
            Base64.getDecoder().decode("kf7nT81ym5EZoVY2pD94yQ=="), "AES"
    );

    /*private SecretKeySpec getSecretKey(){
        return new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    }*/

    /*@SneakyThrows
    public List<String> bulkEncrypt(List<String> plainValues) {
        return plainValues.parallelStream().map(AesEncryption::encrypt).collect(Collectors.toList());
    }

    @SneakyThrows
    public List<String> bulkDecrypt(List<String> encryptedValues) {
        return encryptedValues.parallelStream().map(AesEncryption::decrypt).collect(Collectors.toList());
    }*/


    public String aesKeyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private static String encrypt(String plainText) throws Exception{

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

//        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), ivSpec);
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        byte[] encryptedWithIv = new byte[iv.length + plainText.length()];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, plainText.length());

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public static String decrypt(String encryptedBase64) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);
        byte[] iv = new byte[IV_SIZE];

        byte[] encryptedBytes = new byte[encrypted.length - IV_SIZE];
        System.arraycopy(encrypted, 0, iv, 0, IV_SIZE);
        System.arraycopy(encrypted, iv.length, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(iv));
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

}