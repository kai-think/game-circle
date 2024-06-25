package com.kai.common.config.encrypt;

import com.example.demo.common.intercepter.DecryptionIntercepter;
import com.example.demo.config.GlobalBean;
import com.example.demo.config.resolver.PostMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class EncryptedPostMethodArgumentResolver extends PostMethodArgumentResolver {

    GlobalBean globalBean = new GlobalBean();

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
//        return false;
        return methodParameter.hasMethodAnnotation(EncryptedRequestBody.class);    //有CustomRequestBody注解的才进行解析
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String content = (String) request.getAttribute(DecryptionIntercepter.Attr_Content);
        addRequestBody(modelAndViewContainer, content);
        return super.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
    }
}
