package com.kai.common.intercepter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.sys.entity.PermissionPath;
import com.example.demo.sys.entity.SysRole;
import com.example.demo.sys.entity.SysUser;
import com.example.demo.sys.mapper.MenuMapper;
import com.example.demo.utils.IValidator;
import com.example.demo.utils.SysJwt;
import com.example.demo.utils.httpresult.FailResult;
import com.example.demo.utils.httpresult.ResultType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthorityIntercepter extends HandlerInterceptorAdapter {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String Key_PathRoleIdsMapper = "PathRoleIdsMapper_Zhou";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        log.info("Method: " + request.getMethod());
        if (request.getMethod().equals("OPTIONS"))
            return true;

        //公开资源不用授权
        String uri = request.getRequestURI();
        log.info("uri: " + uri);
        String[] publicUri = {"/public/", "/upload/", "/static/", "/front/", "/game/", "/tb/",
                "/sys/dic/page", "/sys/dic/getByName"};
        for (String u : publicUri) {
            if (uri.contains(u))
                return true;
        }

        //检查是否授权
        String authorization = request.getHeader("Authorization");

        FailResult<String> failResult = null;

        if (IValidator.empty(authorization))
            failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "没有授权");

        SysUser sysUser = null;
        if (failResult == null)
            try {
                sysUser = SysJwt.getUser(authorization);
            } catch (JWTVerificationException e1) {
                failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "授权码无效");
            } catch (IllegalArgumentException e2) {
                failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "授权码错误");
            }

        if (failResult == null)
            if (sysUser == null || sysUser.getId() == null)
                failResult = new FailResult<>(ResultType.UNAUTHORIZED.getCode(), "无效用户");

        if (failResult == null)
        {
            //登陆后就可以用的请求
            String[] loginedPublicUri = {"/sys/menu/listByCurrentUser", "/sys/menu/getByRoleId",
                        "/sys/role/getFirstByCurrentUser", "/sys/role/listByUser", "/sys/menu/page", "/sys/dic/page",
                        "/sys/dic/getByName",
                        "/sys/table/page", "/sys/table/getById", "/sys/table/getByName", "/sys/role/listUserRoleByCurrentUser"};
            boolean permit = false;
            for (String u : loginedPublicUri) {
                if (uri.contains(u))
                {
                    permit = true;
                    break;
                }
            }
//          //需要授权的请求
            if (!permit)
            {
                int idx = uri.substring(1).indexOf("/");
                uri = uri.substring(idx + 1);
                List<Integer> roleIds = SysJwt.getRoles(authorization).stream().map(SysRole::getId).collect(Collectors.toList());
                if (!hasPermission(roleIds, uri))
                    failResult = new FailResult<>("权限不足");
            }
        }

        if (failResult != null)
        {
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(failResult));
            return false;
        }

        request.setAttribute("user", sysUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    private boolean hasPermission(List<Integer> roleIds, String _path) {
        boolean hasKey = redisTemplate.hasKey(Key_PathRoleIdsMapper);
        if (!hasKey)
            reflushPermissionMapper();

        List<Integer> roleIdList = (List<Integer>) redisTemplate.opsForHash().get(Key_PathRoleIdsMapper, _path);
        if (roleIdList == null)
            return false;

        for (Integer roleId : roleIds)
            if (roleIdList.contains(roleId))
                return true;

        return false;
    }

    public void reflushPermissionMapper() {
        log.info("刷新权限缓存");
        Map<String, List<Integer>> pathRoleIdsMapper = new HashMap<>();
        List<PermissionPath> list = menuMapper.getAllPermissionPath();

        for (PermissionPath pp : list) {
            Integer roleId = pp.getRoleId();
            String path1 = pp.getPath1();
            Optional<String[]> path2s = Optional.of(pp)
                    .map(PermissionPath::getPath2)
                    .map(o -> o.replaceAll(" ", ""))
                    .map(o -> o.split(","));
            if (path2s.isPresent())
                for (String path2 : path2s.get()) {
                    String key = path1 + path2;
                    List<Integer> roleIdList = pathRoleIdsMapper.get(key);
                    if (roleIdList == null)
                        roleIdList = new LinkedList<>();

                    roleIdList.add(roleId);
                    pathRoleIdsMapper.put(key, roleIdList);
                }
        }

        redisTemplate.delete(Key_PathRoleIdsMapper);
        redisTemplate.opsForHash().putAll(Key_PathRoleIdsMapper, pathRoleIdsMapper);
    }
}
