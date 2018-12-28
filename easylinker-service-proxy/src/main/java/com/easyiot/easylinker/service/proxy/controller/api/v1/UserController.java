package com.easyiot.easylinker.service.proxy.controller.api.v1;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.easyiot.easylinker.service.proxy.config.jwt.JwtAuthRole;
import com.easyiot.easylinker.service.proxy.config.jwt.JwtHelper;
import com.easyiot.easylinker.service.proxy.config.mvc.WebReturnResult;
import com.easyiot.easylinker.service.proxy.config.redis.RedisService;
import com.easyiot.easylinker.service.proxy.config.security.user.model.AppUser;
import com.easyiot.easylinker.service.proxy.config.security.user.service.AppUserService;
import com.easyiot.easylinker.service.proxy.service.COAPRemoteClientService;
import com.easyiot.easylinker.service.proxy.service.HttpRemoteClientService;
import com.easyiot.easylinker.service.proxy.service.MqttRemoteClientService;
import com.easyiot.easylinker.service.proxy.service.SystemLogService;
import com.easyiot.easylinker.service.proxy.utils.AliYunSMSHelper;
import com.easyiot.easylinker.service.proxy.utils.CacheHelper;
import com.easyiot.easylinker.service.proxy.utils.Md5Util;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 用户相关
 */
@RestController
@JwtAuthRole
@RequestMapping(value = "/api/v_1_0/user")
public class UserController {

    private final AliYunSMSHelper aliYunSMSHelper;
    private final CacheHelper cacheHelper;
    private final AppUserService appUserService;
    private final RedisService redisService;

    @Autowired
    public UserController(CacheHelper cacheHelper, AppUserService appUserService, RedisService redisService, AliYunSMSHelper aliYunSMSHelper) {
        this.cacheHelper = cacheHelper;
        this.appUserService = appUserService;
        this.redisService = redisService;
        this.aliYunSMSHelper = aliYunSMSHelper;
    }


    /**
     * 新增一个用户
     *
     * @param
     * @param requestBody
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)

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

            try {
                String phone = requestBody.getString("phone");
                String code = String.valueOf(((int) (Math.random() * 1000000)));
                if (aliYunSMSHelper.sendSms(phone, code)) {
                    //验证码10分钟过期
                    redisService.setExpires("sms_" + phone, code, 10L, TimeUnit.MINUTES);
                    appUserService.save(appUser);
                } else {
                    return WebReturnResult.returnTipMessage(108, "验证码发送失败!");
                }

            } catch (ClientException e) {
                //e.printStackTrace();
                return WebReturnResult.returnTipMessage(108, "验证码发送失败!");
            }

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

    /**
     * 激活用户
     *
     * @param requestBody
     * @return
     */


    @RequestMapping(value = "/active", method = RequestMethod.POST)
    public JSONObject active(@RequestBody JSONObject requestBody) {

        if (StringUtils.hasText(requestBody.getString("phone"))
                && StringUtils.hasText(requestBody.getString("code"))) {
            if (redisService.get("sms_" + requestBody.getString("phone"))
                    .equals(requestBody.getString("code"))) {
                AppUser appUser = appUserService.getAAppUserWithPhone(requestBody.getString("phone"));
                if (appUser == null) {
                    return WebReturnResult.returnTipMessage(106, "用户不存在!");

                } else {
                    appUser.setEnabled(true);
                    appUserService.save(appUser);
                    redisService.delete("sms_" + requestBody.getString("phone"));
                    return WebReturnResult.returnTipMessage(100, "用户激活成功!");

                }

            } else {
                return WebReturnResult.returnTipMessage(108, "验证码错误!");
            }


        } else {
            return WebReturnResult.returnTipMessage(105, "参数缺少!");
        }
    }

    @Autowired
    MqttRemoteClientService mqttRemoteClientService;
    @Autowired
    HttpRemoteClientService httpRemoteClientService;
    @Autowired
    COAPRemoteClientService coapRemoteClientService;

    /**
     * @return 设备信息概览
     */
    @RequestMapping(value = "/deviceOverViewInfo", method = RequestMethod.GET)
    public JSONObject deviceOverViewInfo(HttpServletRequest httpServletRequest) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        JSONObject returnDataJson = new JSONObject();
        JSONObject mqttInfo = new JSONObject();
        mqttInfo.put("total", mqttRemoteClientService.count(userId));
        mqttInfo.put("online", mqttRemoteClientService.onlineCount(userId));
        returnDataJson.put("mqtt", mqttInfo);
        returnDataJson.put("http", httpRemoteClientService.count(userId));
        returnDataJson.put("coap", coapRemoteClientService.count(userId));
        return WebReturnResult.returnDataMessage(1, "查询成功", returnDataJson);


    }

    /**
     * 获取当前服务器的一些状态
     *
     * @return
     */
    @RequestMapping(value = "/serverInfo", method = RequestMethod.GET)
    public JSONObject serverInfo() {
        JSONObject systemProperty = new JSONObject();
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        Properties sysProperty = System.getProperties();
        systemProperty.put("javaVersion", sysProperty.getProperty("java.version"));
        systemProperty.put("javaVendor", sysProperty.getProperty("java.vendor"));
        systemProperty.put("javaHome", sysProperty.getProperty("java.home").replace("\\", "_"));
        systemProperty.put("os", sysProperty.getProperty("os.name"));
        systemProperty.put("osArch", sysProperty.getProperty("os.arch"));
        systemProperty.put("osVersion", sysProperty.getProperty("os.version"));
        systemProperty.put("totalRAM", mem.getTotalPhysicalMemorySize() / 1024 / 1024);
        systemProperty.put("availableRAM", mem.getFreePhysicalMemorySize() / 1024 / 1024);
        systemProperty.put("totalMemory", mem.getFreePhysicalMemorySize() / 1024 / 1024);
        systemProperty.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024);
        systemProperty.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024);
        systemProperty.put("time", new Date());
        return WebReturnResult.returnDataMessage(1, "获取成功!", systemProperty);
    }

    /**
     * 获取系统日志
     *
     * @return
     */
    @Autowired
    SystemLogService systemLogService;

    @RequestMapping(value = "/getLog/{page}/{size}", method = RequestMethod.GET)
    public JSONObject getLog(HttpServletRequest httpServletRequest,
                             @PathVariable int page,
                             @PathVariable int size) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(402, "Token已过期!");
        }

        return WebReturnResult.returnDataMessage(1, "查询成功!", systemLogService.findAllByUserId(
                userId,
                PageRequest.of(page,
                        size,
                        Sort.by(Sort.Direction.DESC, "create_time"))));

    }

}
