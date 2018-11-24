package com.easylinker.proxy.server.app.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 使用redis缓存连接信息
     *
     * @param key
     * @throws Exception
     */

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 删除redis缓存
     *
     * @param key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Redis缓存的Info
     *
     * @param key
     * @return
     * @throws Exception
     */
    public String get(String key) {

        return stringRedisTemplate.opsForValue().get(key);

    }

    /**
     * 设置过期时间的K V
     *
     * @param key
     * @param value
     * @param time  过期时间，后面的参数是单位
     */

    public void setExpires(String key, String value, Long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

}
