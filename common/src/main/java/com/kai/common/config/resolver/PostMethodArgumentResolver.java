package com.kai.common.config.resolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.encrypt.EncryptedRequestBody;
import org.springframework.core.MethodParameter;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Logger;

public class PostMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        if (methodParameter.hasMethodAnnotation(EncryptedRequestBody.class))
            return false;

        return methodParameter.hasMethodAnnotation(JsonRequestBody.class);    //有CustomRequestBody注解的才进行解析
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        JSONObject body = getRequestBody(nativeWebRequest, modelAndViewContainer);

        if (body == null)   //得到请求体，转换为JSONObject并放到 ModelAndView 里面
            return null;

        String name = methodParameter.getParameter().getName();
        Class<?> paramClass = methodParameter.getParameter().getType();
        if (methodParameter.hasParameterAnnotation(JsonRequestBody.class))
        {
            String nativeBodyString = (String) modelAndViewContainer.getModel().getAttribute("nativeBodyString");
            return JSON.parseObject(nativeBodyString, paramClass);
        }

        Object value = body.get(name);
        if (value == null)
            return null;

        if (paramClass.equals(LocalDateTime.class)) {
            if (value instanceof CharSequence) {
                return LocalDateTime.parse((CharSequence) value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            if (value instanceof Number) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), ZoneId.systemDefault());
            }

            return null;
        }

        if (paramClass.equals(LocalDate.class)) {
            if (value instanceof CharSequence) {
                return LocalDate.parse((CharSequence) value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            else if (value instanceof Number) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), ZoneId.systemDefault()).toLocalDate();
            }
            return null;
        }

        if (paramClass.equals(LocalTime.class)) {
            if (value instanceof CharSequence) {
                return LocalTime.parse((CharSequence) value, DateTimeFormatter.ofPattern("HH:mm:ss"));
            }

            return null;
        }

        if (value.getClass().isPrimitive() || value instanceof CharSequence
                || value instanceof Number || value instanceof Date || value instanceof Boolean)
        {
            if (paramClass.equals(value.getClass()))
                return value;

            //常见Number类型先转成 Double， 再强转为对应类型（因为Double比较 ’大‘ ，大转小不会报错）
            if (value.getClass().getSuperclass().getTypeName().equals("java.lang.Number"))
                value = Double.parseDouble(value.toString());

            //强制转换
            return paramClass.cast(value);
        }

        if (value instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) value;
            return jsonObject.toJavaObject(paramClass);
        }
        else if (value instanceof JSONArray) {

            JsonToList listAnnotaion = methodParameter.getParameterAnnotation(JsonToList.class);
            if (listAnnotaion == null || listAnnotaion.value() == null)
            {
               return null;
            }

            Class<?> beanCls = listAnnotaion.value();

            JSONArray jsonArray = (JSONArray) value;
            return jsonArray.toJavaList(beanCls);
        }

        return null;
    }

    private String readRequestBody(NativeWebRequest nativeWebRequest) {
        String body = null;
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

        body = (String) request.getAttribute("nativeRequestBody");
        if (body != null)
            return body;

        BufferedReader reader = null;
        try {
            reader = request.getReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (reader == null)
            return null;

        body = reader.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        request.setAttribute("nativeRequestBody", body);
        return body;
    }

    protected void addRequestBody(ModelAndViewContainer modelAndViewContainer, String body) {
        ModelMap mm = modelAndViewContainer.getModel();
        JSONObject requestBody = null;
        try {
            requestBody = JSON.parseObject(body);
        } catch (JSONException e) {
            requestBody = new JSONObject();
        }

        requestBody.put("modelMap", mm);
        mm.addAttribute("nativeBodyString", body);
        mm.addAttribute("requestBody", requestBody);
    }

    protected JSONObject getRequestBody(NativeWebRequest nativeWebRequest, ModelAndViewContainer modelAndViewContainer) {
        ModelMap mm = modelAndViewContainer.getModel();
        JSONObject body = (JSONObject) mm.getAttribute("requestBody");

        if (body == null)   //得到请求体，转换为JSONObject并放到 ModelAndView 里面
        {
            String json = readRequestBody(nativeWebRequest);

            if (JSON.isValid(json)) //是否为有效 json 数据
            {
                addRequestBody(modelAndViewContainer, json);
                body = (JSONObject) mm.getAttribute("requestBody");
            }
            else
            {
                Logger.getLogger("自定义参数解析器").warning("请求体数据不是JSON格式");
                return null;
            }
        }

        return body;
    }
}
