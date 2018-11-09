package com.easylinker.proxy.server.app.controller.api.v1;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLEntry;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLGroupEntry;
import com.easylinker.proxy.server.app.model.mqtt.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import com.easylinker.proxy.server.app.service.ScheduleService;
import com.easylinker.proxy.server.app.utils.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 在V3里面，所有的连接进来的东西都是客户端
 * 不管你是C  Cpp 还是Java Python
 */
@RestController
//关于这里为何打破规则用了下划线：因为Spring的路径中出现数字以后会出问题
@RequestMapping(value = "/api/v_1_0")
public class ClientController {

    private MqttRemoteClientService mqttRemoteClientService;
    private CacheHelper cacheHelper;
    private ScheduleService scheduleService;


    @Autowired
    public ClientController(ScheduleService scheduleService, CacheHelper cacheHelper, MqttRemoteClientService mqttRemoteClientService) {
        this.scheduleService = scheduleService;
        this.cacheHelper = cacheHelper;
        this.mqttRemoteClientService = mqttRemoteClientService;
    }

    /**
     * {
     * "name":"GPS",
     * "info":"This is some info",
     * "location":[
     * "0",
     * "0"
     * ],
     * "aclEntry":[
     * {
     * "topic":"/test",
     * "acl":2,
     * "group":[
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

    public JSONObject add(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody) {
        if (StringUtils.hasText(requestBody.getString("name"))
                && StringUtils.hasText(requestBody.getString("info"))
                && StringUtils.hasText(requestBody.getString("topic"))
                && StringUtils.hasText(requestBody.getString("acl"))
                && StringUtils.hasText(requestBody.getString("group"))) {
            //从缓存中拿出用户ID
            Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
            if (userId == null) {
                return WebReturnResult.returnTipMessage(0, "Token已过期!");
            }
            //
            MqttRemoteClient mqttRemoteClient = new MqttRemoteClient();
            mqttRemoteClient.setName(requestBody.getString("name"));
            mqttRemoteClient.setInfo(requestBody.getString("info"));
            mqttRemoteClient.setUserId(userId);
            //配置默认的ACL
            ClientACLEntry defaultACLEntry = new ClientACLEntry();
            defaultACLEntry.setTopic("/" + userId + "/" + mqttRemoteClient.getClientId() + "/" + requestBody.getString("topic"));
            //ACL加入组
            List<ClientACLEntry> aclEntryList = new ArrayList<>();
            aclEntryList.add(defaultACLEntry);
            mqttRemoteClient.setAclEntries(aclEntryList);

            //分组
            List<ClientACLGroupEntry> clientACLGroupEntryList = new ArrayList<>();
            for (Object o : requestBody.getJSONArray("group")) {
                ClientACLGroupEntry clientACLGroupEntry = new ClientACLGroupEntry();
                clientACLGroupEntry.setName(((JSONObject) o).getString("name"));
                clientACLGroupEntry.setAcl(((JSONObject) o).getIntValue("acl"));
                clientACLGroupEntryList.add(clientACLGroupEntry);
            }

            mqttRemoteClient.setClientACLGroupEntries(clientACLGroupEntryList);
            mqttRemoteClientService.save(mqttRemoteClient);

            return WebReturnResult.returnTipMessage(1, "添加成功!");
        } else {
            return WebReturnResult.returnTipMessage(0, "参数不全!");

        }


    }

    /**
     * 花式删除
     *
     * @param httpServletRequest
     * @param id
     * @return
     */

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public JSONObject delete(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(0, "Token已过期!");
        }
        mqttRemoteClientService.delete(id);
        return WebReturnResult.returnTipMessage(1, "删除成功!");
    }

    /**
     * 花式删除
     *
     * @param httpServletRequest
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JSONObject delete(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(0, "Token已过期!");
        }
        for (Object o : requestBody.getJSONArray("ids")) {
            mqttRemoteClientService.delete(((JSONObject) o).getLongValue("id"));
        }

        return WebReturnResult.returnTipMessage(1, "删除成功!");
    }

    /**
     * 更新
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.PUT)

    public JSONObject update(@RequestBody JSONObject requestBody) {
        return null;
    }

    /**
     * 获取该用户所有的数据
     *
     * @param httpServletRequest
     * @return
     */

    @RequestMapping(value = "/list/{page}/{size}", method = RequestMethod.GET)
    public Object list(HttpServletRequest httpServletRequest,
                       @PathVariable int page,
                       @PathVariable int size) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(0, "Token已过期!");
        }
        Page<MqttRemoteClient> mqttRemoteClientPage = mqttRemoteClientService.findAllByUserId(
                userId,
                PageRequest.of(page,
                        size,
                        Sort.by(Sort.Direction.DESC, "_id")));
        return WebReturnResult.returnDataMessage(1, "查询成功!", mqttRemoteClientPage);

    }

}
