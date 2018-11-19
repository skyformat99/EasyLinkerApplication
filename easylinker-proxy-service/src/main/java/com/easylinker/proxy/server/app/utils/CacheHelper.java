package com.easylinker.proxy.server.app.utils;

import com.easylinker.proxy.server.app.config.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CacheHelper {

    public Long getCurrentUserIdFromRedisCache(HttpServletRequest httpServletRequest) {
        return Long.parseLong(JwtHelper.validateToken(httpServletRequest.getHeader("token")).get("userId").toString());

    }
}
