package com.kai.common.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.bbs.entity.BbsUser;
import com.example.demo.sys.entity.SysRole;
import com.example.demo.sys.entity.SysUser;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class BbsUserJwt {
    public static final String Key_BbsUser = "Key_BbsUser";

    public static String createToken(@NonNull BbsUser sysUser) {
        if (sysUser.getId() == null)
            throw new RuntimeException("user id 不能为空");

        BbsUser u = new BbsUser();
        u.setId(sysUser.getId());

        Map<String, Object> map = new HashMap<>();
        map.put(Key_BbsUser, u);
        return JwtUtils.createToken(map);
    }

    public static BbsUser getUser(@NonNull String token) throws  JWTVerificationException, IllegalArgumentException{
        return JwtUtils.verifyToken(token).getObject(Key_BbsUser, BbsUser.class);
    }
}
