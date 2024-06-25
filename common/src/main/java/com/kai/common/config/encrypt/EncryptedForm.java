package com.kai.common.config.encrypt;

import com.example.demo.utils.IValidator;
import com.example.demo.utils.cipher.AESCipher;
import com.example.demo.utils.cipher.RSACipher;
import com.example.demo.utils.cipher.Sha1WithRsaWithBase64Signature;
import lombok.Data;

@Data
public class EncryptedForm {
    String content;   //加密数据，使用AesWithBase64算法，并用随机数作密钥进行加密
    String random;    //加密数据使用的对称密钥，为一个随机数，使用RsaWithBase64加密，一定要十六位才行！
    String signature;    //签名，即信息摘要，使用Sha1WithRsaWithBase64 签名算法对原始content + random进行签名

    public void decrypt(String privateKey) throws Exception {
        if (IValidator.empty(random))
            throw new Exception("没有随机数");

        random = RSACipher.decryptWithBase64(random, privateKey);
        if (IValidator.empty(random))
            throw new Exception("对随机数的解密失败");

        //使用上面的key解密
        if (IValidator.empty(content))
            throw new Exception("没有内容");

        content = AESCipher.decryptWithBase64(content, random);
        if (IValidator.empty(content))
            throw new Exception("对内容的解密失败");

        //使用私钥解密，得到数据的信息摘要
        if (IValidator.empty(signature))
            throw new Exception("没有签名");

        //数据的实际信息摘要
        boolean verify = Sha1WithRsaWithBase64Signature
                .verifyByPrivateKey(content + random, signature, privateKey);
        if (!verify)
            throw new Exception("错误签名");
    }
}
