package com.easyiot.easylinker.service.proxy;

import com.easyiot.easylinker.service.proxy.model.log.SystemLog;
import com.easyiot.easylinker.service.proxy.model.log.SystemLogType;
import com.easyiot.easylinker.service.proxy.service.SystemLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    /**
     * Test system info.
     */
    @Autowired
    SystemLogService systemLogService;

    @Test
    public void testCentralProcessor() {
        for (int i = 0; i < 52; i++) {
            SystemLog systemLog = new SystemLog();
            systemLog.setSystemLogType(SystemLogType.MQTT_CLIENT_ONLINE);
            systemLog.setCause("设备下线");
            systemLog.setUserId(1543584572005L);
            systemLog.setInfo(System.currentTimeMillis() + "号设备下线");
            systemLogService.save(systemLog);
        }
        for (int i = 0; i < 34; i++) {
            SystemLog systemLog = new SystemLog();
            systemLog.setSystemLogType(SystemLogType.MQTT_CLIENT_OFFLINE);
            systemLog.setCause("设备上线");
            systemLog.setUserId(1543584572005L);
            systemLog.setInfo(System.currentTimeMillis() + "号设备下线");
            systemLogService.save(systemLog);
        }


    }

}
