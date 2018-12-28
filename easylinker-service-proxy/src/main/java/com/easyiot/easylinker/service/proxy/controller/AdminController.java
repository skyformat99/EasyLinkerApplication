package com.easyiot.easylinker.service.proxy.controller;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.config.security.user.model.AppUser;
import com.easyiot.easylinker.service.proxy.config.security.user.service.AppUserService;
import com.easyiot.easylinker.service.proxy.service.SystemLogService;
import com.easyiot.easylinker.service.proxy.config.jwt.JwtAuthRole;
import com.easyiot.easylinker.service.proxy.config.mvc.WebReturnResult;
import com.easyiot.easylinker.service.proxy.model.charge.ChargeBill;
import com.easyiot.easylinker.service.proxy.service.ChargeBillService;
import com.easyiot.easylinker.service.proxy.utils.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    private final SystemLogService systemLogService;
    private final ChargeBillService chargeBillService;

    @Autowired
    public AdminController(CacheHelper cacheHelper, AppUserService appUserService, SystemLogService systemLogService, ChargeBillService chargeBillService) {
        this.cacheHelper = cacheHelper;
        this.appUserService = appUserService;
        this.systemLogService = systemLogService;
        this.chargeBillService = chargeBillService;
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

    /**
     * 获取日志
     *
     * @param httpServletRequest
     * @param page
     * @param size
     * @return
     */

    @RequestMapping(value = "/listLog/{page}/{size}", method = RequestMethod.GET)
    public JSONObject listLog(HttpServletRequest httpServletRequest,
                              @PathVariable int page,
                              @PathVariable int size) {
        //从缓存中拿出用户ID
        if (cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest) == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }

        return WebReturnResult.returnDataMessage(1, "查询成功!", systemLogService.getAll(
                PageRequest.of(page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createTime"))));

    }

    /**
     * 充值
     *
     * @param httpServletRequest
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/charge", method = RequestMethod.POST)
    public JSONObject charge(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody) {
        if (cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest) == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }

        if (StringUtils.hasText(requestBody.getString("userId"))
                && StringUtils.hasText(requestBody.getString("chargeCount"))) {
            AppUser appUser = appUserService.findById(requestBody.getLongValue("userId"));
            if (appUser == null) {
                return WebReturnResult.returnTipMessage(0, "用户不存在!");
            } else {
                appUser.setClientCount(appUser.getClientCount() + requestBody.getLongValue("chargeCount"));
                appUserService.save(appUser);
                //打印一个账单
                ChargeBill chargeBill = new ChargeBill();
                chargeBill.setUserId(appUser.getId());
                chargeBill.setGoodsName("客户端充值");
                chargeBill.setInfo("充值客户端数 :" + requestBody.getLongValue("chargeCount"));
                chargeBillService.save(chargeBill);
                return WebReturnResult.returnTipMessage(1, "充值成功!");
            }
        } else {
            return WebReturnResult.returnTipMessage(0, "参数缺少!");

        }
    }
}
