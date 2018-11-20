package com.easylinker.proxy.server.app.config.jwt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.redis.RedisService;
import com.easylinker.proxy.server.app.utils.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtRBACHandlerInterceptor implements HandlerInterceptor {
    private final RedisService redisService;
    private final CacheHelper cacheHelper;
    private Boolean access = true;

    @Autowired
    public JwtRBACHandlerInterceptor(RedisService redisService, CacheHelper cacheHelper) {
        this.redisService = redisService;
        this.cacheHelper = cacheHelper;
    }

    /**
     * 拦截器
     *
     * @param handler
     * @return
     * @throws Exception
     */

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);

        if (userId != null) {
            JwtAuthRole jwtAnnotation = handlerMethod.getBeanType().getAnnotation(JwtAuthRole.class);
            if (jwtAnnotation != null) {
                //获取控制器上的注解里面的Roles
                String[] annotationRoleList = jwtAnnotation.roles();
                //遍历用户的Roles
                JSONArray userRoles = JSONArray.parseArray(redisService.get("user_roles_" + userId));
                for (String role : annotationRoleList) {
                    if (userRoles.contains(role)) {
                        //System.out.println("拥有权限:" + role);
                        access = true;
                    } else {
                        //System.out.println("没有权限:" + role);
                        access = false;
                    }
                }
            } else {
                //如果没有注解默认就是可访问的
                access = true;
            }

        } else {
            //session过期
            access = false;
        }
        if (access) {
            //通过

        } else {

            JSONObject returnJson = new JSONObject();
            returnJson.put("state", 405);
            returnJson.put("message", "角色权限不足!");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.getWriter().write(returnJson.toJSONString());
            httpServletResponse.getWriter().flush();

        }

        return access;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object handler, Exception ex) throws Exception {

    }
}
