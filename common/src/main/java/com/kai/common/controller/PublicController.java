package com.kai.common.controller;

import com.example.demo.config.encrypt.EncryptedRequestBody;
import com.example.demo.config.resolver.JsonRequestBody;
import com.example.demo.utils.cipher.AESCipher;
import com.example.demo.config.GlobalBean;
import com.example.demo.utils.cipher.RSACipher;
import com.example.demo.utils.cipher.Sha1WithRsaWithBase64Signature;
import com.example.demo.utils.httpresult.HttpResult;
import com.example.demo.utils.httpresult.SuccessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    GlobalBean globalBean;

    @GetMapping("/getPublicKey")
    public HttpResult<String> getPublicKey() {
        return new SuccessResult<>(globalBean.PublicKey());
    }

    @PostMapping("/uploadCiphertext")
    @JsonRequestBody
    public HttpResult<String> uploadCiphertext(String ciphertext) {
        String plainttext = RSACipher.decryptWithBase64(ciphertext, globalBean.PrivateKey());
        return new SuccessResult<>("成功！");
    }

    @GetMapping("/aesEncrypt2")
    public HttpResult<String> encryptText() {
        String text = "今天不是个好日子";
        String key = "1234567890123456";
        String base64Ciphertext = AESCipher.encryptWithBase64(text, key);
        return  new SuccessResult<>(base64Ciphertext);
    }

    @PostMapping("/aesDecrypt2")
    public HttpResult<String> decrypt(String base64Ciphertext, String key) throws BadPaddingException, IllegalBlockSizeException {
        String decrypted = AESCipher.decryptWithBase64(base64Ciphertext, key);
        return new SuccessResult<>(decrypted);
    }
    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcd+0zTY9Gn94iqkQJTlxYnEnCeFsLkk0a7hoAvi2B74VzDVV3xH0ZO9RkXvo1SgCB+uzbEWdrgQkzTqyjfTtgOguu3OnkVxIMJF34ibchTY0LWHGxq1m2gLGuVVqrlu1LtdV0X7xo/5zc8Mr+46veWb86kSpqe6rOAm69WWo5GwIDAQAB";
    String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJx37TNNj0af3iKqRAlOXFicScJ4WwuSTRruGgC+LYHvhXMNVXfEfRk71GRe+jVKAIH67NsRZ2uBCTNOrKN9O2A6C67c6eRXEgwkXfiJtyFNjQtYcbGrWbaAsa5VWquW7Uu11XRfvGj/nNzwyv7jq95ZvzqRKmp7qs4Cbr1ZajkbAgMBAAECgYAHp349EkA+DjgJrhah9elilFKvZr/dcwy+koNHIgaL4rG+jRpvP3d3MowTVOocjUA1G5dWqCVNBwTyM5kSbl/nIxSCYwdUoDid4r0JbqkXkTTsIq3euHG8eiWr9rr3SDmwDojWoJEc4liVlfme8dQuMfgxe1QKq7wTrJwCKwbeMQJBAPwpknRPRK8W9hefbbtEu8mlbzUy+ER8Puq6dvS+lnWzJ8n2chJcHRYQFwWpjl4+SZuKeEcDmYmuQ7xuqEIayO0CQQCe2YeaxcU4uuDC45RAwCcMaNw1nDJuA+Gi47lXbroBXoeOiNZunViSZVUgDgrV/Ku6V54TaZIzZ21QFjf7mXEnAkEA7dZwMpAJonOvzfwrzbQ4wyrsx2q5zC68UT1qsdGJrJ48azutwC9tp7+pV0fj5nQtjS1/4Ms+aCQb84ET5rXIyQJAM0m45tgEHZT5DPO94kooUXFp6EVOYwcNyzILnZc6p0aGLhcwZPaYqmvdWEQwa3bxW3D+sPXdJou2V61U1f9s8QJALccvYwwWlCTq1iTmegYk9fOoc+isZKH+Z0YW70kFi94AYEO+utYwmXBEAqQ5VC/bywa1O71xdL4/RGCOSxBf2A==";

    @GetMapping("/rsaEncryptText")
    public HttpResult<String> rsaEncryptText() {
        String text = "今天不是个好日子";
        String base64Ciphertext = RSACipher.encryptWithBase64(text, publicKey);
        return  new SuccessResult<>(base64Ciphertext);
    }

    @PostMapping("/rsaDecrypt")
    public HttpResult<String> rsaDecrypt(String base64Ciphertext) throws BadPaddingException, IllegalBlockSizeException {
        String decrypted = RSACipher.decryptWithBase64(base64Ciphertext, privateKey);
        return new SuccessResult<>(decrypted);
    }

    @GetMapping("/signedText")
    public HttpResult<String> signedText() {
        String text = "今天不是个好日子";
        String signature = null;
        try {
            signature = Sha1WithRsaWithBase64Signature.sign(text, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  new SuccessResult<>(signature);
    }

    @PostMapping("/verify")
    public HttpResult<Boolean> verify(String plaintext, String signature) throws BadPaddingException, IllegalBlockSizeException {
        boolean verify = false;
        try {
            verify = Sha1WithRsaWithBase64Signature.verify(plaintext, signature, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SuccessResult<>(verify);
    }

    @PostMapping("/testSecret")
    @EncryptedRequestBody
    public HttpResult<String> test(String encrypted) {
        System.out.println(encrypted);
        return new SuccessResult<>("成功接受数据");
    }
}
