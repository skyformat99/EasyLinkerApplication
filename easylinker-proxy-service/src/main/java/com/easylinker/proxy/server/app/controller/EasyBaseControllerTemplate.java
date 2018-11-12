package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;

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
     * @param t
     * @return
     */

    JSONObject add(JSONObject t);

    /**
     * 不同的删除和¥重载
     *
     * @param
     */

    JSONObject deleteById(Long id);

    /*
     * 更新的各种重载
     **/

    JSONObject update(JSONObject t);
    /*
     * 列出所有
     * */


    JSONObject list(Long paramId);

    JSONObject list(JSONObject t);


}
