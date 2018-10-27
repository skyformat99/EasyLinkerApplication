package com.easylinker.proxy.server.app;

import com.easylinker.proxy.server.app.config.security.user.service.AppUserService;
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
        MqttRemoteClient vertxMqttRemoteClient = new MqttRemoteClient();
        vertxMqttRemoteClient.setId(System.currentTimeMillis());
        vertxMqttRemoteClient.setClientId("testClientId002");
        vertxMqttRemoteClient.setUsername("username1");
        vertxMqttRemoteClient.setPassword("password2");
        vertxMqttRemoteClient.setName("GPS");
        vertxMqttRemoteClient.setInfo("This is some info");
        vertxMqttRemoteClient.setLocation(new String[]{"120", "200"});
        vertxMqttRemoteClient.setTopics(new String[]{"/1", "/2", "/3"});
        MqttRemoteClientService.save(vertxMqttRemoteClient);
//        Page<VertXMqttRemoteClient> page = vertXMqttRemoteClientService.getAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "_id")));
//        for (VertXMqttRemoteClient client : page.getContent()) {
//            System.out.println(client.toString());
//        }


//
//        AppUser appUser = new AppUser();
//        appUser.setUsername("username");
//        appUser.setPassword(Md5Util.encodingMD5("password"));
//        appUser.setEmail("test@test.com");
//        appUser.setPhone("1101101101");
//        appUserService.save(appUser);


    }


}
