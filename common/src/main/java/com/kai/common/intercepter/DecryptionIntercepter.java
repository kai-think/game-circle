package com.kai.common.intercepter;

import com.alibaba.fastjson.JSON;
import com.example.demo.config.GlobalBean;
import com.example.demo.config.encrypt.EncryptedForm;
import com.example.demo.config.encrypt.EncryptedRequestBody;
import com.example.demo.utils.httpresult.FailResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DecryptionIntercepter extends HandlerInterceptorAdapter {
    public static final String Attr_Random = "Attr_Random";
    public static final String Attr_Content = "Attr_Content";

    @Autowired
    GlobalBean globalBean;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equals("POST"))
            return true;

        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            if (handlerMethod.hasMethodAnnotation(EncryptedRequestBody.class))
            {
                InputStream is = request.getInputStream();
                String data = StreamUtils.copyToString(is, StandardCharsets.UTF_8);

                EncryptedForm form = JSON.parseObject(data, EncryptedForm.class);
                try {
                    form.decrypt(globalBean.PrivateKey());
                    request.setAttribute(Attr_Content, form.getContent());
                    request.setAttribute(Attr_Random, form.getRandom());
                } catch (Exception e) {
                    log.warn(e.getMessage());
                    //处理解密错误
                    FailResult<String> failResult = new FailResult<>(e.getMessage());
                    response.setContentType("application/json");
                    response.getWriter().write(JSON.toJSONString(failResult));
                    return false;
                }

                return true;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
