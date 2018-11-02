package com.easylinker.proxy.server.app.controller;

import com.easylinker.proxy.server.app.config.security.user.service.AppUserService;
import com.easylinker.proxy.server.app.model.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Autowired
    com.easylinker.proxy.server.app.service.MqttRemoteClientService MqttRemoteClientService;
    @Autowired
    AppUserService appUserService;

    @RequestMapping(value = "/")
    public String index() {

        return "index";
    }


}
