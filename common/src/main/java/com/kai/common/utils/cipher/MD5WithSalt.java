package com.kai.common.utils.cipher;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5WithSalt {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static String digest(String text, String salt) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "check jdk";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        int idx = salt.length() % text.length();
        String plaintext = text.substring(0, idx) + salt + text.substring(idx);
        System.out.println(plaintext);

        byte[] md5Bytes = md5.digest(plaintext.getBytes(CHARSET));
        StringBuilder hexVal = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexVal.append("0");
            hexVal.append(Integer.toHexString(val));
        }
        return hexVal.toString();
    }

    public static boolean verify(String text, String salt, String md5text) {
        return digest(text, salt).equals(md5text);
    }
}
