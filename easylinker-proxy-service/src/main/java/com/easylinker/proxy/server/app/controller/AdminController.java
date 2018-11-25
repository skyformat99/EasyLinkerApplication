package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.jwt.JwtAuthRole;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import com.easylinker.proxy.server.app.config.security.user.service.AppUserService;
import com.easylinker.proxy.server.app.utils.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员
 * 功能：
 * 管理用户：CURD，授权
 * 配置服务器
 * 查看日志
 * 给用户充值
 *
 * @author wwhai
 */
@RestController
@RequestMapping(value = "/admin")
@JwtAuthRole(roles = {"ROLE_ADMIN"})
public class AdminController {
    private final CacheHelper cacheHelper;
    private final AppUserService appUserService;

    @Autowired
    public AdminController(CacheHelper cacheHelper, AppUserService appUserService) {
        this.cacheHelper = cacheHelper;
        this.appUserService = appUserService;
    }

    @RequestMapping(value = "/listUsers/{page}/{size}", method = RequestMethod.GET)
    public JSONObject listUsers(HttpServletRequest httpServletRequest,
                                @PathVariable int page,
                                @PathVariable int size) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }

        return WebReturnResult.returnDataMessage(1, "查询成功!", appUserService.getAll(
                PageRequest.of(page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createTime"))));


    }

    /**
     * 激活用户
     *
     * @param httpServletRequest
     * @param userId
     * @return
     */
    @RequestMapping(value = "/activeUser/{userId}", method = RequestMethod.GET)
    public JSONObject activeUser(HttpServletRequest httpServletRequest,
                                 @PathVariable Long userId) {
        if (cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest) == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        AppUser appUser = appUserService.findById(userId);
        if (appUser == null) {
            return WebReturnResult.returnTipMessage(0, "用户不存在!");
        } else {
            if (appUser.isEnabled()) {
                return WebReturnResult.returnTipMessage(0, "用户已经激活!");

            } else {
                appUser.setEnabled(true);
                return WebReturnResult.returnTipMessage(1, "用户激活成功!");

            }
        }


    }

    /**
     * 锁定用户
     *
     * @return
     */
    @RequestMapping(value = "/disableUser/{userId}", method = RequestMethod.GET)
    public JSONObject disableUser(HttpServletRequest httpServletRequest,
                                  @PathVariable Long userId) {

        if (cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest) == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        AppUser appUser = appUserService.findById(userId);
        if (appUser == null) {
            return WebReturnResult.returnTipMessage(0, "用户不存在!");
        } else {
            if (!appUser.isEnabled()) {
                return WebReturnResult.returnTipMessage(0, "用户已经锁定!");

            } else {
                appUser.setEnabled(false);
                return WebReturnResult.returnTipMessage(1, "用户锁定成功!");

            }
        }


    }


}
