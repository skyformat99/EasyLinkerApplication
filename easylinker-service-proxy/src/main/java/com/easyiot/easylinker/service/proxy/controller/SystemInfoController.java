package com.easyiot.easylinker.service.proxy.controller;

import com.easyiot.easylinker.service.proxy.config.mvc.WebReturnResult;
import com.easyiot.easylinker.service.proxy.service.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("serverInfo")
public class SystemInfoController {

    @Autowired
    private SystemInfoService infoService;

    @GetMapping
    public Object Info(){
        Map ramInfo = infoService.ramInfo();
        Map ram = new HashMap(16);
        ram.put("ram", ramInfo);

        return WebReturnResult.returnDataMessage(20000, "查询成功", ram);
    }
}
