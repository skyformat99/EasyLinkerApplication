package com.easyiot.easylinker.service.proxy.config.security.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.config.security.user.model.AppUser;
import com.easyiot.easylinker.service.proxy.config.jwt.JwtHelper;
import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RedisService redisService;

    @Autowired
    public LoginSuccessHandler(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        JSONObject returnJson = new JSONObject();
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", appUser.getEmail());
        JSONArray jsonArray = new JSONArray();
        for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
            jsonArray.add(grantedAuthority.getAuthority());
        }
        jsonObject.put("id", appUser.getId());
        jsonObject.put("roles", jsonArray);
        jsonObject.put("phone", appUser.getPhone());
        jsonObject.put("username", appUser.getUsername());
        returnJson.put("state", 1);
        returnJson.put("token", JwtHelper.generateToken(appUser.getId()));
        //把Token放进Redis
        //Key:userId
        //Value:token
        //刷新Token
        redisService.delete("user_" + appUser.getId().toString());
        redisService.delete("user_info_" + appUser.getId());
        //redisService.delete("user_roles_" + appUser.getId().toString());
        ///
        //redisService.set("user_roles_" + appUser.getId().toString(), jsonArray.toJSONString());
        redisService.set("user_" + appUser.getId().toString(), JwtHelper.generateToken(appUser.getId()));
        redisService.set("user_info_" + appUser.getId(), JSONObject.toJSONString(appUser));
        ///
        returnJson.put("data", jsonObject);
        returnJson.put("message", "登陆成功!");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(returnJson.toJSONString());
        httpServletResponse.getWriter().flush();


    }
}
