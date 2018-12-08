package com.easyiot.easylinker.service.proxy.config.security.handler;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        JSONObject resultJson = new JSONObject();
        resultJson.put("state", 0);
        if (e instanceof BadCredentialsException) {
            resultJson.put("message", "登录失败!密码错误!");
        } else if (e instanceof DisabledException) {
            resultJson.put("message", "登录失败!用户没有激活!");
        } else if (e instanceof UsernameNotFoundException) {
            resultJson.put("message", "登录失败!用户不存在!");
        } else {
            resultJson.put("message", "登录失败!");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(resultJson.toJSONString());
        response.getWriter().flush();


    }
}
