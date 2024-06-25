package com.kai.common.utils.cipher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1Cipher {

    public static byte[] digest(byte[] content) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return messageDigest.digest(content);
    }

    public static String digestWithBase64(String content) {
        byte[] result = digest(content.getBytes(StandardCharsets.UTF_8));
        return Base64Encoding.encodeToString(result);
    }
}
