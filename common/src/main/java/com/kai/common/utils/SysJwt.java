package com.kai.common.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.sys.entity.SysRole;
import com.example.demo.sys.entity.SysUser;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SysJwt {
    public static final String Key_SysUser = "Key_SysUser";
    public static final String Key_SysRoles = "Key_SysRoles";
    public static String createToken(@NonNull SysUser sysUser, @NonNull List<SysRole> sysSysRoles) {
        if (sysUser.getId() == null)
            throw new RuntimeException("user id 不能为空");

        SysUser u = new SysUser();
        u.setId(sysUser.getId());
        List<SysRole> rs = sysSysRoles.stream().map(r -> {
           SysRole role = new SysRole();
           role.setId(r.getId());
           return role;
        }).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put(Key_SysUser, u);
        map.put(Key_SysRoles, rs);
        return JwtUtils.createToken(map);
    }

    public static SysUser getUser(@NonNull String token) throws  JWTVerificationException, IllegalArgumentException{
        return JwtUtils.verifyToken(token).getObject(Key_SysUser, SysUser.class);
    }

    public static List<SysRole> getRoles(@NonNull String token) throws  JWTVerificationException, IllegalArgumentException{
        return JwtUtils.verifyToken(token).getJSONArray(Key_SysRoles).toJavaList(SysRole.class);
    }
}
