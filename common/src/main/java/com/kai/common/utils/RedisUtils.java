package com.kai.common.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RedisUtils {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public Boolean hasKey(String key1, String key2) {
        return redisTemplate.hasKey(key1) && redisTemplate.opsForHash().hasKey(key1, key2);
    }

    public <T> Page<T> getPage(String key1, String key2, Class<T> clazz) {
        return (Page<T>) redisTemplate.opsForHash().get(key1, key2);
    }

    public <T> List<T> getList(String key1, String key2, Class<T> clazz) {
        return (List<T>) redisTemplate.opsForHash().get(key1, key2);
    }

    public Boolean delete(String key1) {
        return redisTemplate.delete(key1);
    }

    public void delete(String key1, String key2) {
        redisTemplate.opsForHash().put(key1, key2, null);
    }

    public void set(String key1, String key2, Object obj) {
        redisTemplate.opsForHash().put(key1, key2, obj);
    }

    public String generateKey(@NonNull Map<String, Object> params) {
        return JSON.toJSONString(params);
    }
}
