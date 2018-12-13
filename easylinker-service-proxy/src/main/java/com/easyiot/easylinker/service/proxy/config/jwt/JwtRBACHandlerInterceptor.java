package com.easyiot.easylinker.service.proxy.config.jwt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.config.mvc.WebReturnResult;
import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
import com.easyiot.easylinker.service.proxy.utils.CacheHelper;
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
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);

            if (userId != null) {
                JwtAuthRole jwtAnnotation = handlerMethod.getBeanType().getAnnotation(JwtAuthRole.class);
                if (jwtAnnotation != null) {
                    //获取控制器上的注解里面的Roles
                    String[] annotationRoleList = jwtAnnotation.roles();
                    //遍历用户的Roles
                    JSONObject userInfo = JSONObject.parseObject(redisService.get("user_info_" + userId));
                    //System.out.println("userInfo " + userInfo.getJSONArray("authorities"));
                    for (String annotation : annotationRoleList) {
                        JSONArray authorities = userInfo.getJSONArray("authorities");
                        for (Object role : authorities) {
                            access = ((JSONObject) role).getString("authority").equals(annotation);
                            //System.out.println("注解:" + annotation + " 权限:" + ((JSONObject) role).getString("authority") + "  " + access);

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

                httpServletResponse.setContentType("application/json");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.getWriter().write(WebReturnResult.returnTipMessage(405, "角色权限不足!").toJSONString());
                httpServletResponse.getWriter().flush();

            }
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
