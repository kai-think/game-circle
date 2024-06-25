package com.kai.common.aspect;

import com.example.demo.common.intercepter.AuthorityIntercepter;
import com.example.demo.sys.controller.SysMenuController;
import com.example.demo.sys.entity.Menu;
import com.example.demo.utils.httpresult.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PermissionChangeAspect {
    @Autowired
    AuthorityIntercepter authorityIntercepter;

    @Pointcut("execution(* (*..SysMenuController||*..SysRoleMenuController||*..SysUserRoleController).save*(..)) || execution(* (*..SysRoleController||*..SysUserController).assign*(..))")
    public void change() {}

    @Before("change(), change2()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        log.info("前置通知");
        // 接收到请求，记录请求内容
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
//        log.info("URL : " + request.getRequestURL().toString());
//        log.info("HTTP_METHOD : " + request.getMethod());
//        log.info("IP : " + request.getRemoteAddr());
//        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

    }

    @AfterReturning(pointcut = "change()", returning = "result")
//    @Async
    public void doAfterReturning(JoinPoint joinPoint, HttpResult<?> result) throws Throwable {
        // 处理完请求，返回内容
        log.info("后置通知");
        //菜单修改后比较差异，决定是否刷新权限缓存
        if (!result.getSuccess())
            return;

        authorityIntercepter.reflushPermissionMapper();
    }

    @After("change()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("后置最终通知");

    }

    @Around("change()")
    public Object doAroundAdvice(ProceedingJoinPoint pjp) {
        log.info("环绕通知");
        try {
            Object[] args = pjp.getArgs();
            Object obj = pjp.proceed(args);
            return obj;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }
}
