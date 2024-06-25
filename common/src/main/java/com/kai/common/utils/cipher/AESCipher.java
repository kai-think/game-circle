package com.kai.common.utils.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AESCipher {
    public final static String MODE = CipherAlgorithmConstant.AES_ECB_PKCS5Padding;
    public final static Charset CHARSET = StandardCharsets.UTF_8;

    public static byte[] encrypt(byte[] plaintext, Key publicKey) {
        byte[] ciphertext = null;
        try {
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            ciphertext = cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return ciphertext;
    }

    public static String decrypt(byte[] ciphertext, Key privateKey) throws BadPaddingException, IllegalBlockSizeException {
        String plaintext = null;
        try {
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            plaintext = new String(cipher.doFinal(ciphertext), CHARSET);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return plaintext;
    }

    public static String encryptWithBase64(String plaintext, String key) {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET), "AES");
        byte[] encrypted = encrypt(plaintext.getBytes(CHARSET), keySpec);
        return Base64Encoding.encodeToString(encrypted);
    }

    public static String decryptWithBase64(String ciphertext, String  key) throws BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET), "AES");
        byte[] decodeBase64 = Base64Encoding.decode(ciphertext);
        return decrypt(decodeBase64, keySpec);
    }
}
