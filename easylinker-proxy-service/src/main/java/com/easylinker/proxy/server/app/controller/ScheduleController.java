package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 任务调度
 */
@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {
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
        return null;
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
