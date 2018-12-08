package com.easyiot.easylinker.service.proxy.config.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
import com.easyiot.easylinker.service.proxy.utils.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {
    private final RedisService redisService;
    private final CacheHelper cacheHelper;

    @Autowired
    public UserLogoutSuccessHandler(RedisService redisService, CacheHelper cacheHelper) {
        this.redisService = redisService;
        this.cacheHelper = cacheHelper;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(request);
        if (userId != null) {
            redisService.delete("user_" + userId);
            redisService.delete("user_info_" + userId);

        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("state", 107);
        returnJson.put("message", "注销成功!");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(returnJson.toJSONString());
        httpServletResponse.getWriter().flush();
    }
}
