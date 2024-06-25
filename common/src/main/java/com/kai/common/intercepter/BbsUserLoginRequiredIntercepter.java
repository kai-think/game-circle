package com.kai.common.intercepter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.bbs.entity.BbsUser;
import com.example.demo.config.encrypt.EncryptedForm;
import com.example.demo.config.encrypt.EncryptedRequestBody;
import com.example.demo.sys.entity.PermissionPath;
import com.example.demo.sys.entity.SysRole;
import com.example.demo.sys.mapper.MenuMapper;
import com.example.demo.utils.BbsUserJwt;
import com.example.demo.utils.IValidator;
import com.example.demo.utils.SysJwt;
import com.example.demo.utils.httpresult.FailResult;
import com.example.demo.utils.httpresult.ResultType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class BbsUserLoginRequiredIntercepter extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS"))
            return true;

        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //没注解必须登陆就放行
        if (!handlerMethod.hasMethodAnnotation(BbsUserLoginRequired.class) && !handlerMethod.hasMethodAnnotation(InjectBbsUser.class))
            return true;

        //检查是否授权
        String authorization = request.getHeader("Authorization");

        FailResult<String> failResult = null;

        if (IValidator.empty(authorization))
            failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "没有授权");

        BbsUser bbsUser = new BbsUser();
        if (failResult == null)
            try {
                bbsUser = BbsUserJwt.getUser(authorization);
            } catch (JWTVerificationException e1) {
                failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "授权码无效");
            } catch (IllegalArgumentException e2) {
                failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "授权码错误");
            }

        if (failResult == null)
            if (bbsUser == null || bbsUser.getId() == null)
                failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "无效用户");


        //有授权的用户
        if (failResult == null || handlerMethod.hasMethodAnnotation(InjectBbsUser.class)) {
            request.setAttribute("bbsUser", bbsUser);
            return true;
        }

        //没授权的用户，并且注解要求是必须
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(failResult));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


}
