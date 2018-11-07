package com.easylinker.proxy.server.app.config.jwt;


import com.alibaba.fastjson.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        String token = request.getHeader("token");
        if (token != null) {
            try {
                System.out.println("解析JWT:" + JwtHelper.validateToken(token));
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                JSONObject returnJson = new JSONObject();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                returnJson.put("state", 100);
                returnJson.put("message", "令牌过期!请重新登录获取.");
                response.getWriter().write(returnJson.toJSONString());
                response.getWriter().flush();

            }
        }

    }

}

