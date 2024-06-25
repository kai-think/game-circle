package com.kai.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * @author zqy
 * @date 2021-8-8
 * @description JWT工具类
 */

public class JwtUtils {
    /**
     * 设置过期时间及密匙
     * CALENDAR_FIELD 时间单位
     * CALENDAR_INTERVAL 有效时间
     * SECRET_KEY 密匙
     */

    public static String SECRET_KEY = "6A50A18D70FA63636645C65459F1D78";
    public static Integer EFFECTIVE_TIME = 24 * 60 * 60 * 1000;    //有效时间，24小时
    public static String SUBJECT = "主题";
    public static String ISSUSER = "auth0";

    /**
     * 创建Token
     *
     * @param data 自己需要存储进token中的信息
     * @return token
     */
    public static String createToken(@NonNull Object data) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);  //算法

        Long current = System.currentTimeMillis();

        String token = JWT.create()
                .withSubject(SUBJECT)
                .withIssuer(ISSUSER)
                .withIssuedAt(new Date(current))
                .withExpiresAt(new Date(current + EFFECTIVE_TIME))
                .withClaim("data", JSON.toJSONString(data))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);

        return token;
    }

    /**
     * 验证、解析Token
     *
     * @param token 用户提交的token
     * @return 该token中的信息
     */
    public static JSONObject verifyToken(@NonNull String token) throws com.auth0.jwt.exceptions.JWTVerificationException {
        DecodedJWT decodedJWT = null;
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        decodedJWT = JWT
                .require(algorithm)
                .withIssuer(ISSUSER)
                .build()
                .verify(token);

        String jsonString = decodedJWT.getClaim("data").asString();
        return JSONObject.parseObject(jsonString);
    }
}