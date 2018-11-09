package com.easylinker.proxy.server.app.config.jwt;


import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private RedisService redisService;

    @Autowired
    public JwtFilter(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        String token = request.getHeader("token");
        if (token != null) {
            try {

                String cacheToken = redisService.get(JwtHelper.validateToken(token).get("userId").toString());
                System.out.println("Token是否过期:" + token.equals(cacheToken));
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                //e.printStackTrace();
                JSONObject returnJson = new JSONObject();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                returnJson.put("state", 100);
                returnJson.put("message", "令牌过期!请重新登录获取.");
                try {
                    response.getWriter().write(returnJson.toJSONString());
                    response.getWriter().flush();

                } catch (IOException e1) {
                    //e1.printStackTrace();
                }

            }
        } else {
            JSONObject returnJson = new JSONObject();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            returnJson.put("state", 100);
            returnJson.put("message", "Bad request because of token error!");
            try {
                response.getWriter().write(returnJson.toJSONString());
                response.getWriter().flush();

            } catch (IOException e1) {
                //
            }
        }

    }

}

