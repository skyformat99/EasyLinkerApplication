package com.easylinker.proxy.server.app.config.mvc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Http REST API 返回结果 封装
 */
public class WebReturnResult {
    /**
     * 纯粹发没回一个字符串提示
     *
     * @param code
     * @param tipMessage
     * @return
     */
    public static JSONObject returnTipMessage(int code, String tipMessage) {
        JSONObject returnJson = new JSONObject();
        returnJson.put("state", code);
        returnJson.put("message", tipMessage);
        return returnJson;

    }

    /**
     * 返回带数据的提示信息
     *
     * @param code
     * @param tipMessage
     * @param data
     * @return
     */

    public static JSONObject returnDataMessage(int code, String tipMessage, JSONObject data) {

        JSONObject returnJson = new JSONObject();
        returnJson.put("state", code);
        returnJson.put("message", tipMessage);
        returnJson.put("data", data);
        return returnJson;
    }

    /**
     * 返回带Json数组的数据
     *
     * @param code
     * @param tipMessage
     * @param data
     * @return
     */
    public static JSONObject returnDataMessage(int code, String tipMessage, JSONArray data) {

        JSONObject returnJson = new JSONObject();
        returnJson.put("state", code);
        returnJson.put("data", data);
        returnJson.put("message", tipMessage);
        return returnJson;
    }
    /**
     * 返回带Json数组的数据
     *
     * @param code
     * @param tipMessage
     * @param data
     * @return
     */
    public static JSONObject returnDataMessage(int code, String tipMessage, Object data) {

        JSONObject returnJson = new JSONObject();
        returnJson.put("state", code);
        returnJson.put("data", data);
        returnJson.put("message", tipMessage);
        return returnJson;
    }


}