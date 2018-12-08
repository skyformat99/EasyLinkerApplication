package com.easyiot.easylinker.service.proxy.config.jwt;


import com.easyiot.easylinker.service.proxy.config.mvc.WebReturnResult;
import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
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



        //配置编码类型
        response.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type","text/html;charset=UTF-8");
        String token = request.getHeader("token");
        if (token != null) {
            try {
                /**
                 * 设计思路：
                 * 先从缓存里面拿出user_id的key 对应的Value
                 * 然后和Http过来的token对比，token是否和Value相等
                 * 相等就通过，不相等就拒绝并且认为token过期，直接清除缓存
                 */

                String cacheToken = redisService.get("user_" + JwtHelper.validateToken(token).get("userId").toString());
                if (!token.equals(cacheToken)){
                    redisService.delete("user_" + JwtHelper.validateToken(token).get("userId").toString());

                    try {

                        response.getWriter().write(WebReturnResult.returnTipMessage(402, "令牌过期!请重新登录获取").toJSONString());
                        response.getWriter().flush();

                    } catch (IOException e1) {
                        //e1.printStackTrace();
                    }
                }else {
                    filterChain.doFilter(request, response);

                }

            } catch (Exception e) {

                try {
                    response.getWriter().write(WebReturnResult.returnTipMessage(402, "令牌过期!请重新登录获取").toJSONString());
                    response.getWriter().flush();

                } catch (IOException e1) {
                    //e1.printStackTrace();
                }

            }
        } else {

            try {
                response.getWriter().write(WebReturnResult.returnTipMessage(402, "令牌过期!请重新登录获取").toJSONString());
                response.getWriter().flush();

            } catch (IOException e1) {
                //
            }
        }

    }

}

