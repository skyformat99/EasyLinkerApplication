package com.easylinker.proxy.server.app;

import com.easylinker.proxy.server.app.config.security.user.service.AppUserService;
import com.easylinker.proxy.server.app.model.ClientACLEntry;
import com.easylinker.proxy.server.app.model.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    MqttRemoteClientService MqttRemoteClientService;
    @Autowired
    AppUserService appUserService;

    @Test
    public void contextLoads() {
        MqttRemoteClient mqttRemoteClient = new MqttRemoteClient();
        mqttRemoteClient.setId(System.currentTimeMillis());
        mqttRemoteClient.setClientId("testClientId001");
        mqttRemoteClient.setUsername("username");
        mqttRemoteClient.setPassword("password");
        mqttRemoteClient.setName("GPS");
        mqttRemoteClient.setInfo("This is some info");
        mqttRemoteClient.setLocation(new String[]{"120", "200"});
        ClientACLEntry defaultACLEntry = new ClientACLEntry();
        defaultACLEntry.setTopic("/test");
        defaultACLEntry.setAcl(2);
        mqttRemoteClient.setAclEntry(new ClientACLEntry[]{defaultACLEntry});
        MqttRemoteClientService.save(mqttRemoteClient);


    }


}
