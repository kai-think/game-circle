package com.kai.common.config.encrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.common.intercepter.DecryptionIntercepter;
import com.example.demo.config.GlobalBean;
import com.example.demo.utils.cipher.AESCipher;
import com.example.demo.utils.cipher.Sha1WithRsaWithBase64Signature;
import com.example.demo.utils.httpresult.HttpResult;
import com.example.demo.utils.httpresult.SuccessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;


@ControllerAdvice
public class EncryptedRestResponseBodyAdvice implements ResponseBodyAdvice {
    @Autowired
    GlobalBean globalBean;

    @Override
    public boolean supports(MethodParameter methodParameter, Class converterType) {
        return methodParameter.hasMethodAnnotation(EncryptedRequestBody.class);
    }

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body == null)
            return new SuccessResult<String>("成功");

        String content, random = null, sign = null;
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            random = (String) request.getServletRequest()
                                     .getAttribute(DecryptionIntercepter.Attr_Random);
        }

        content = JSON.toJSONString(body, SerializerFeature.MapSortField);;
        //对内容摘要并签名
        //先使用SHA1摘要
        //再使用私钥 对摘要进行签名
        try {
            sign = Sha1WithRsaWithBase64Signature.sign(content, globalBean.PrivateKey());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //对内容加密，密钥为前端传过来的随机数
        content = AESCipher.encryptWithBase64(content, random);

        //返回加密表单，不能放随机数
        EncryptedForm form = new EncryptedForm();
        form.setContent(content);
        form.setSignature(sign);

        return form;
    }

    HttpResult<?> getFailResult(ServerHttpRequest serverHttpRequest) {
        HttpResult<?> result = null;
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            result = (HttpResult<?>) request.getServletRequest().getAttribute("FailResult");
        }

        return result;
    }
}
