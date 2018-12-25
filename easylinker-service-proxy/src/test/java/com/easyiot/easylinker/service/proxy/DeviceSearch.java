package com.easyiot.easylinker.service.proxy;

import com.easyiot.easylinker.service.proxy.model.client.MqttRemoteClient;
import com.easyiot.easylinker.service.proxy.service.MqttRemoteClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DeviceSearch {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MqttRemoteClientService mqttRemoteClientService;

    @Test
    public void demo() {
        Pattern pattern = Pattern.compile("^.*t.*$", Pattern.CASE_INSENSITIVE);
        Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(pattern),
                Criteria.where("info").regex(pattern));
        Query query = Query.query(criteria);
        List<MqttRemoteClient> mqttRemoteClients = mongoTemplate.find(query, MqttRemoteClient.class);
        System.out.println(mqttRemoteClients);
    }

    @Test
    public void select(){
        MqttRemoteClient oneByClientId = mqttRemoteClientService.findOneByClientId("507f434dd4c9");
        System.out.println(oneByClientId);
    }
}
