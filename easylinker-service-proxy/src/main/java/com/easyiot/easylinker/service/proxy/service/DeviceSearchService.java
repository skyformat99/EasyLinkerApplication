package com.easyiot.easylinker.service.proxy.service;

import com.easyiot.easylinker.service.proxy.model.client.CoapRemoteClient;
import com.easyiot.easylinker.service.proxy.model.client.DeviceSearchEntry;
import com.easyiot.easylinker.service.proxy.model.client.HttpRemoteClient;
import com.easyiot.easylinker.service.proxy.model.client.MqttRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 设备模糊查询
 * @Date:     2018/12/26 13:12
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Service
public class DeviceSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * MQTT设备模糊查询
     * @param deviceSearchEntry 查询参数
     * @param userId 用户ID
     * @return
     */
    public List<MqttRemoteClient> queryMQTT(DeviceSearchEntry deviceSearchEntry, Long userId) {

        Query query = queryHelper(deviceSearchEntry, userId);
        return mongoTemplate.find(query, MqttRemoteClient.class);
    }

    /**
     * HTTP设备模糊查询
     * @param deviceSearchEntry 查询参数
     * @param userId 用户ID
     * @return
     */
    public List<HttpRemoteClient> queryHTTP(DeviceSearchEntry deviceSearchEntry, Long userId) {

        Query query = queryHelper(deviceSearchEntry, userId);
        mongoTemplate.find(query, HttpRemoteClient.class);
        return mongoTemplate.find(query, HttpRemoteClient.class);
    }

    /**
     * COAP设备模糊查询
     * @param deviceSearchEntry 查询参数
     * @param userId 用户ID
     * @return
     */
    public List<CoapRemoteClient> queryCOAP(DeviceSearchEntry deviceSearchEntry, Long userId) {

        Query query = queryHelper(deviceSearchEntry, userId);
        return mongoTemplate.find(query, CoapRemoteClient.class);
    }

    private Query queryHelper(DeviceSearchEntry deviceSearchEntry, Long userId) {
        String keyword = "^.*" + deviceSearchEntry.getKeyword() + ".*$";
        Pageable pageable = PageRequest.of(
                deviceSearchEntry.getPageNum() - 1,
                deviceSearchEntry.getPageSize());

        Criteria criteria = Criteria
                .where(deviceSearchEntry.getMode()).regex(keyword, "i")
                .and("userId").is(userId);

        return Query.query(criteria).with(pageable);
    }
}
