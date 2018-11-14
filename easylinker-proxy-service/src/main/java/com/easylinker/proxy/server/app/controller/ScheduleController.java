package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.config.quartz.MessageJob;
import com.easylinker.proxy.server.app.config.quartz.service.MessageJobService;
import com.easylinker.proxy.server.app.utils.CacheHelper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 任务调度
 * 返回码从600K开始
 */
@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {


    private final CacheHelper cacheHelper;
    private final MessageJobService messageJobService;

    @Autowired
    public ScheduleController(MessageJobService messageJobService, CacheHelper cacheHelper) {
        this.messageJobService = messageJobService;
        this.cacheHelper = cacheHelper;
    }

    /**
     * 添加一个计划任务
     *
     * @param httpServletRequest
     * @param requestBody
     * @return
     */
    @Transactional
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public JSONObject add(HttpServletRequest httpServletRequest,
                          @RequestBody JSONObject requestBody) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }

        String cronExpression = requestBody.getString("cronExpression");
        JSONObject jobJson = requestBody.getJSONObject("jobJson");

        if (cronExpression == null || jobJson == null) {
            return WebReturnResult.returnTipMessage(600, "参数缺少!");
        } else {
            MessageJob scheduleJob = new MessageJob();
            scheduleJob.setCronExpression(cronExpression);
            scheduleJob.setJobJson(jobJson.toJSONString());

            try {
                messageJobService.add(userId, scheduleJob);
                return WebReturnResult.returnTipMessage(601, "任务添加成功!");
            } catch (Exception e) {
                //e.printStackTrace();
                if (e instanceof RuntimeException) {
                    return WebReturnResult.returnTipMessage(602, "CRON表达式格式错误!");
                }
                return WebReturnResult.returnTipMessage(600, "任务添加失败!");
            }

        }
    }

    /**
     * 删除一个计划任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public JSONObject delete(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        try {
            messageJobService.delete(id);
            return WebReturnResult.returnTipMessage(603, "任务删除成功!");
        } catch (SchedulerException e) {
            return WebReturnResult.returnTipMessage(600, "任务删除失败!");
        }

    }

    /**
     * 暂停计划任务
     *
     * @param httpServletRequest
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public JSONObject pause(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        try {
            messageJobService.pause(id);
            return WebReturnResult.returnTipMessage(604, "任务暂停成功!");
        } catch (SchedulerException e) {
            return WebReturnResult.returnTipMessage(600, "任务暂停失败!");
        }
    }

    /**
     * 恢复计划任务
     *
     * @param httpServletRequest
     * @param id
     * @returnid
     */
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public JSONObject resume(HttpServletRequest httpServletRequest,@PathVariable Long id) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        try {
            messageJobService.resume(id);
            return WebReturnResult.returnTipMessage(605, "任务恢复成功!");
        } catch (SchedulerException e) {
            return WebReturnResult.returnTipMessage(600, "任务恢复失败!");
        }
    }


    /**
     * 列出计划任务
     *
     * @param httpServletRequest
     * @param
     * @return
     */
    @Transactional
    @RequestMapping(value = "/{page}/{size}", method = RequestMethod.GET)
    public JSONObject list(HttpServletRequest httpServletRequest,
                           @PathVariable int page,
                           @PathVariable int size) {
        //从缓存中拿出用户ID
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }

        //查找数据
        Page<MessageJob> mqttRemoteClientPage = messageJobService.list(userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return WebReturnResult.returnDataMessage(600, "查询成功!", mqttRemoteClientPage);

    }

}
