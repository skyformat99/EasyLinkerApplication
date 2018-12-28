package com.easyiot.easylinker.service.proxy.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

//一个模板

/**
 * C
 * U
 * R
 * D
 */
public interface EasyBaseControllerTemplate {
    /**
     * 添加一个
     *
     * @param
     * @return
     */

    JSONObject add(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody);

    /**
     * 不同的删除和¥重载
     *
     * @param
     */

    JSONObject deleteById(HttpServletRequest httpServletRequest, @PathVariable Long id);

    /*
     * 更新的各种重载
     **/

    JSONObject update(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody);

    /*
     * 列出所有
     * */
    JSONObject list(HttpServletRequest httpServletRequest, JSONObject requestBody);

}
