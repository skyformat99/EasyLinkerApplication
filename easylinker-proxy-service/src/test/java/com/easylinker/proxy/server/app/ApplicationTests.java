package com.easylinker.proxy.server.app;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.security.user.model.AppUser;
import com.easylinker.proxy.server.app.config.security.user.service.AppUserService;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLEntry;
import com.easylinker.proxy.server.app.model.mqtt.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    MqttRemoteClientService MqttRemoteClientService;
    @Autowired
    AppUserService appUserService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    //@Test
    public void test() throws Exception {
        stringRedisTemplate.opsForValue().set("name", "wwhai");
        System.out.println(stringRedisTemplate.opsForValue().get("name"));
    }


    //@Test
    public void contextLoads() {
        MqttRemoteClient mqttRemoteClient = new MqttRemoteClient();
        mqttRemoteClient.setId(System.currentTimeMillis());
        mqttRemoteClient.setClientId("testClientId001");
        mqttRemoteClient.setUsername("username");
        mqttRemoteClient.setPassword("password");
        mqttRemoteClient.setName("GPS");
        mqttRemoteClient.setInfo("This is some info");
        //mqttRemoteClient.setLocation(new String[]{"0", "0"});
        ClientACLEntry defaultACLEntry = new ClientACLEntry();
        defaultACLEntry.setTopic("/test");
        MqttRemoteClientService.save(mqttRemoteClient);


    }

    //@Test
    public void addTestUser() {
        AppUser appUser = new AppUser();
        appUser.setUsername("username");
        appUser.setPassword("14c4b06b824ec593239362517f538b29");
        appUserService.save(appUser);
    }

    //
    @Test
    public void testMqtt() {
        System.out.println("Test success");
    }




}
