package com.kai.common.controller;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.example.demo.common.BaseController;
import com.example.demo.config.GlobalBean;
import com.example.demo.utils.httpresult.HttpResult;
import com.example.demo.utils.httpresult.SuccessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cipher")
public class CipherController extends BaseController {
    @Autowired
    GlobalBean globalBean;

    @GetMapping("/getPublicKey")
    public HttpResult<byte[]> getPublicKey() {
        byte[] bytes = globalBean.RSAKeyPair().getPublic().getEncoded();
        return new SuccessResult<>(bytes);
    }

    @PostMapping("/aesDecrypt")
    public HttpResult<String> decrypt(String ciphertext, String key) {
        String plaintext = AES.decrypt(ciphertext, key);
        return new SuccessResult<>(plaintext);
    }
}
