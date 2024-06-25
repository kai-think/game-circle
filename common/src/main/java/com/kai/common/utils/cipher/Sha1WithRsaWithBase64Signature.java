package com.kai.common.utils.cipher;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Sha1WithRsaWithBase64Signature {
    public final static String MODE = CipherAlgorithmConstant.AES_ECB_PKCS5Padding;
    public final static Charset CHARSET = StandardCharsets.UTF_8;

    public static String sign(String plaintext, String privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        byte[] base64Key = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(base64Key);
        PrivateKey privateKey2 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8KeySpec);
        signature.initSign(privateKey2);
        signature.update(plaintext.getBytes(CHARSET));
        byte[] signed = signature.sign();
        return Base64Encoding.encodeToString(signed);
    }

    public static boolean verify(String plaintext, String signature, String publicKey) throws Exception {
        Signature signature2 = Signature.getInstance("SHA1withRSA");
        byte[] base64Key = Base64Utils.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64Key);
        PublicKey publicKey2 = KeyFactory.getInstance("RSA").generatePublic(keySpec);
        signature2.initVerify(publicKey2);
        signature2.update(plaintext.getBytes(CHARSET));
        return signature2.verify(Base64Encoding.decode(signature));
    }

    public static boolean verifyByPrivateKey(String plaintext, String signature, String privateKey) {
        String sign = RSACipher.decryptWithBase64(signature, privateKey);
        String sign2 = Sha1Cipher.digestWithBase64(plaintext);
        return sign.equals(sign2);
    }
}
