package com.easylinker.proxy.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在V3里面，所有的连接进来的东西都是客户端
 * 不管你是C  Cpp 还是Java Python
 */
@RestController
@RequestMapping(value = "/api/v1")
public class ClientController {

    /**
     * {
     * "_id" : NumberLong(1541252071675),
     * "username" : "username",
     * "password" : "password",
     * "clientId" : "testClientId001",
     * "onLine" : false,
     * "name" : "GPS",
     * "info" : "This is some info",
     * "location" : [
     * "0",
     * "0"
     * ],
     * "aclEntry" : [
     * {
     * "_id" : NumberLong(1541252119849),
     * "topic" : "/test",
     * "acl" : 2,
     * "group" : [
     * "DEFAULT_GROUP"
     * ]
     * }
     * ]
     * }
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)

    public JSONObject add(@RequestBody JSONObject requestBody) {
        System.out.println(requestBody);


        return new JSONObject();
    }



    public JSONObject deleteById(@RequestBody Long id) {
        return null;
    }


    public JSONObject deleteByIds(@RequestBody Long[] ids) {
        return null;
    }


    public JSONObject update(@RequestBody JSONObject requestBody) {
        return null;
    }


    public JSONObject list() {
        return null;
    }


    public JSONObject list(@RequestBody Long paramId) {
        return null;
    }


    public JSONObject list(@RequestBody JSONObject condition) {
        return null;
    }
}
