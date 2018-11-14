package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.config.quartz.MessageJob;
import com.easylinker.proxy.server.app.config.quartz.service.MessageJobService;
import com.easylinker.proxy.server.app.utils.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 任务调度
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
            return WebReturnResult.returnTipMessage(0, "参数缺少!");
        } else {
            MessageJob scheduleJob = new MessageJob();
            scheduleJob.setCronExpression(cronExpression);
            scheduleJob.setJobJson(jobJson.toJSONString());
            try {
                messageJobService.add(scheduleJob);
                return WebReturnResult.returnTipMessage(1, "任务添加成功!");
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof RuntimeException) {
                    return WebReturnResult.returnTipMessage(0, "CRON表达式格式错误!");
                }
                return WebReturnResult.returnTipMessage(0, "任务添加失败!");
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
    JSONObject deleteById(@PathVariable Long id) {
        return null;
    }

    /**
     * 更新一个计划任务
     *
     * @param httpServletRequest
     * @param requestBody
     * @return
     */
    @Transactional
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public JSONObject update(HttpServletRequest httpServletRequest,
                             @RequestBody JSONObject requestBody) {
        return null;
    }


}
