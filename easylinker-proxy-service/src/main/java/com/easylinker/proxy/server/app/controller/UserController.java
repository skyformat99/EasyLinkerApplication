package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.jwt.JwtHelper;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.config.redis.RedisService;
import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import com.easylinker.proxy.server.app.config.security.user.service.AppUserService;
import com.easylinker.proxy.server.app.utils.CacheHelper;
import com.easylinker.proxy.server.app.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户相关
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final CacheHelper cacheHelper;
    private final AppUserService appUserService;
    private final RedisService redisService;

    @Autowired
    public UserController(CacheHelper cacheHelper, AppUserService appUserService, RedisService redisService) {
        this.cacheHelper = cacheHelper;
        this.appUserService = appUserService;
        this.redisService = redisService;
    }


    /**
     * 新增一个用户
     *
     * @param
     * @param requestBody
     * @return
     */
    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public JSONObject register(@RequestBody JSONObject requestBody) {

        if (StringUtils.hasText(requestBody.getString("username"))
                && StringUtils.hasText(requestBody.getString("password"))
                && StringUtils.hasText(requestBody.getString("passwordRetry"))
                && StringUtils.hasText(requestBody.getString("email"))

                && StringUtils.hasText(requestBody.getString("phone"))) {

            if (appUserService.getAAppUserWithUsername(requestBody.getString("username")) != null) {
                return WebReturnResult.returnTipMessage(107, "用户已存在!");
            }

            //正则过滤
            //用户名是8-16位 字母数字下划线组合:^[a-zA-Z0-9_-]{4,16}$
            if (!requestBody.getString("username").matches("^[a-zA-Z0-9_-]{8,16}$")) {
                return WebReturnResult.returnTipMessage(101, "用户名必须为8-16位字母、数字、下划线组合!");

            }
            //email:^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$
            if (!requestBody.getString("email").matches("^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$")) {
                return WebReturnResult.returnTipMessage(102, "邮箱格式错误!");

            }
            //phone:^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\d{8}$
            if (!requestBody.getString("phone").matches("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$")) {
                return WebReturnResult.returnTipMessage(103, "无效手机号码!");

            }
            //两次密码相等
            if (!requestBody.getString("password").equals(requestBody.getString("passwordRetry"))) {

                return WebReturnResult.returnTipMessage(104, "两次密码不一样!");

            }

            //开始生成用户
            AppUser appUser = new AppUser();
            appUser.setUsername(requestBody.getString("username"));
            appUser.setPassword(Md5Util.encodingMD5(requestBody.getString("password")));
            appUser.setEmail(requestBody.getString("email"));
            appUser.setPhone(requestBody.getString("phone"));
            // 这里要有邮件发送的代码，但是因为组件没有写好，暂时放弃
            //设计思路：如果发送短信，则在redis里面插入一条数据，key是电话号码，value是验证码，用户验证的时候，在redis里面检查
            //通过就提示注册成功，并且删除验证码。
            //邮件也一样，key换成邮箱就OJBK了
            // try-> sendEmail(XXXX)->e
            // try-> sendSMS(XXX)   ->e
            appUserService.save(appUser);
            return WebReturnResult.returnTipMessage(100, "注册成功!");

        } else {
            return WebReturnResult.returnTipMessage(105, "参数缺少!");

        }


    }

    /**
     * 更新用户信息
     *
     * @param requestBody
     * @return
     */

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public JSONObject update(HttpServletRequest httpServletRequest,
                             @RequestBody JSONObject requestBody) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        if (StringUtils.hasText(requestBody.getString("username"))
                && StringUtils.hasText(requestBody.getString("email"))
                && StringUtils.hasText(requestBody.getString("id"))
                && StringUtils.hasText(requestBody.getString("phone"))) {

            if (appUserService.getAAppUserWithUsername(requestBody.getString("username")) != null) {
                return WebReturnResult.returnTipMessage(107, "用户已存在!");
            }

            //正则过滤
            //用户名是8-16位 字母数字下划线组合:^[a-zA-Z0-9_-]{4,16}$
            if (!requestBody.getString("username").matches("^[a-zA-Z0-9_-]{8,16}$")) {
                return WebReturnResult.returnTipMessage(101, "用户名必须为8-16位字母、数字、下划线组合!");

            }
            //email:^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$
            if (!requestBody.getString("email").matches("^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$")) {
                return WebReturnResult.returnTipMessage(102, "邮箱格式错误!");

            }
            //phone:^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\d{8}$
            if (!requestBody.getString("phone").matches("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$")) {
                return WebReturnResult.returnTipMessage(103, "无效手机号码!");

            }

            //开始生成用户
            AppUser appUser = appUserService.findById(requestBody.getLongValue("id"));
            if (appUser == null) {
                return WebReturnResult.returnTipMessage(106, "用户不存在!");

            }
            appUser.setUsername(requestBody.getString("username"));
            appUser.setEmail(requestBody.getString("email"));
            appUser.setPhone(requestBody.getString("phone"));
            // 这里要有邮件发送的代码，但是因为组件没有写好，暂时放弃
            //设计思路：如果发送短信，则在redis里面插入一条数据，key是电话号码，value是验证码，用户验证的时候，在redis里面检查
            //通过就提示注册成功，并且删除验证码。
            //邮件也一样，key换成邮箱就OJBK了
            // try-> sendEmail(XXXX)->e
            // try-> sendSMS(XXX)   ->e
            appUserService.save(appUser);
            return WebReturnResult.returnTipMessage(100, "更新成功!");

        } else {
            return WebReturnResult.returnTipMessage(105, "参数缺少!");

        }
    }


    /**
     * 查看用户信息
     *
     * @param
     * @return
     */

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public JSONObject get(HttpServletRequest httpServletRequest) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        AppUser appUser = appUserService.findById(userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", appUser.getEmail());
        JSONArray jsonArray = new JSONArray();
        for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
            jsonArray.add(grantedAuthority.getAuthority());
        }
        jsonObject.put("id", appUser.getId());
        jsonObject.put("authorities", jsonArray);
        jsonObject.put("phone", appUser.getPhone());
        jsonObject.put("username", appUser.getUsername());
        jsonObject.put("token", JwtHelper.generateToken(appUser.getId()));
        return WebReturnResult.returnDataMessage(100, "查询成功!", jsonObject);
    }


    /**
     * 重置密码
     *
     */

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public JSONObject resetPassword(HttpServletRequest httpServletRequest,
                                    @RequestBody JSONObject requestBody) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        AppUser appUser = appUserService.findById(userId);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }

        if (StringUtils.hasText(requestBody.getString("password"))
                && StringUtils.hasText(requestBody.getString("passwordRetry"))
                && StringUtils.hasText(requestBody.getString("oldPassword"))) {
            if (!Md5Util.encodingMD5(requestBody.getString("oldPassword")).equals(appUser.getPassword())) {
                return WebReturnResult.returnTipMessage(104, "旧密码不正确!");
            }

            //两次密码相等
            if (!requestBody.getString("password").equals(requestBody.getString("passwordRetry"))) {

                return WebReturnResult.returnTipMessage(104, "两次密码不一样!");

            }

            appUser.setPassword(Md5Util.encodingMD5(requestBody.getString("password")));
            appUserService.save(appUser);
            return WebReturnResult.returnTipMessage(100, "密码重置成功!");


        } else {
            return WebReturnResult.returnTipMessage(105, "参数缺少!");

        }

    }


}
