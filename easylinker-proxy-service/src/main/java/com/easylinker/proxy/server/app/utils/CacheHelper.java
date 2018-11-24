package com.easylinker.proxy.server.app.utils;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.jwt.JwtHelper;
import com.easylinker.proxy.server.app.config.redis.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CacheHelper {
    private final RedisService redisService;

    @Autowired
    public CacheHelper(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 从缓存拿出用户的ID来做TOKEN对比
     *
     * @param httpServletRequest
     * @return
     * @throws ExpiredJwtException
     */

    public Long getCurrentUserIdFromRedisCache(HttpServletRequest httpServletRequest) throws ExpiredJwtException {

        return Long.parseLong(JwtHelper.validateToken(httpServletRequest.getHeader("token")).get("userId").toString());
    }

    /**
     * get user info from redis!
     *
     * @param httpServletRequest
     * @return
     */

    public JSONObject getCurrentUserInfoFromRedisCache(HttpServletRequest httpServletRequest) {
        return JSONObject.parseObject(redisService.get("user_info_" + JwtHelper.validateToken(httpServletRequest.getHeader("token")).get("userId").toString()));

    }
}
