package com.easylinker.proxy.server.app.utils;

import com.easylinker.proxy.server.app.config.jwt.JwtHelper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CacheHelper {

    public Long getCurrentUserIdFromRedisCache(HttpServletRequest httpServletRequest) throws ExpiredJwtException {

        return Long.parseLong(JwtHelper.validateToken(httpServletRequest.getHeader("token")).get("userId").toString());


    }
}
