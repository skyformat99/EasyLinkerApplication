package com.easylinker.proxy.server.app.controller.api.v1;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLEntry;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLGroupEntry;
import com.easylinker.proxy.server.app.model.mqtt.ClientDataEntry;
import com.easylinker.proxy.server.app.model.mqtt.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.ClientDataEntryService;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
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
import java.util.UUID;

/**
 * 在V3里面，所有的连接进来的东西都是客户端
 * 不管你是C  Cpp 还是Java Python
 */
@RestController
//关于这里为何打破规则用了下划线：因为Spring的路径中出现数字以后会出问题
@RequestMapping(value = "/api/v_1_0/client")
public class ClientController {

    private final MqttRemoteClientService mqttRemoteClientService;
    private final CacheHelper cacheHelper;
    private final ClientDataEntryService clientDataEntryService;


    @Autowired
    public ClientController(CacheHelper cacheHelper, MqttRemoteClientService mqttRemoteClientService, ClientDataEntryService clientDataEntryService) {

        this.cacheHelper = cacheHelper;
        this.mqttRemoteClientService = mqttRemoteClientService;
        this.clientDataEntryService = clientDataEntryService;
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
    @RequestMapping(value = "/", method = RequestMethod.POST)

    public JSONObject add(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody) {
        if (StringUtils.hasText(requestBody.getString("name"))
                && StringUtils.hasText(requestBody.getString("info"))
                && StringUtils.hasText(requestBody.getString("topic"))
                && StringUtils.hasText(requestBody.getString("acl"))
                && StringUtils.hasText(requestBody.getString("group"))) {
            //从缓存中拿出用户ID
            Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
            if (userId == null) {
                return WebReturnResult.returnTipMessage(401, "Token已过期!");
            }
            //
            MqttRemoteClient mqttRemoteClient = new MqttRemoteClient();
            mqttRemoteClient.setName(requestBody.getString("name"));
            mqttRemoteClient.setInfo(requestBody.getString("info"));
            mqttRemoteClient.setUserId(userId);
            //配置默认的ACL
            ClientACLEntry defaultACLEntry = new ClientACLEntry();
            defaultACLEntry.setTopic("/" + userId + "/" + mqttRemoteClient.getClientId() + "/" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
            //ACL加入组
            List<ClientACLEntry> aclEntryList = new ArrayList<>();
            aclEntryList.add(defaultACLEntry);
            mqttRemoteClient.setAclEntries(aclEntryList);

            //分组
            List<ClientACLGroupEntry> clientACLGroupEntryList = new ArrayList<>();
            setACL(requestBody, clientACLGroupEntryList);

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
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public JSONObject delete(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        if (StringUtils.hasText(requestBody.getString("ids"))) {
            for (Object o : requestBody.getJSONArray("ids")) {
                mqttRemoteClientService.delete(((JSONObject) o).getLongValue("id"));
            }

            return WebReturnResult.returnTipMessage(1, "删除成功!");
        } else {
            return WebReturnResult.returnTipMessage(1, "参数缺少!");

        }


    }

    /**
     * 更新
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)

    public JSONObject update(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody) {

        if (StringUtils.hasText(requestBody.getString("id"))
                && StringUtils.hasText(requestBody.getString("name"))
                && StringUtils.hasText(requestBody.getString("info"))
                && StringUtils.hasText(requestBody.getString("topic"))
                && StringUtils.hasText(requestBody.getString("acl"))
                && StringUtils.hasText(requestBody.getString("group"))) {
            //从缓存中拿出用户ID
            Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
            if (userId == null) {
                return WebReturnResult.returnTipMessage(401, "Token已过期!");
            }
            //
            MqttRemoteClient mqttRemoteClient = mqttRemoteClientService.findOneById(requestBody.getLongValue("id"));
            if (mqttRemoteClient == null) {
                return WebReturnResult.returnTipMessage(0, "客户端ID不存在!");
            }
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
            setACL(requestBody, clientACLGroupEntryList);

            mqttRemoteClient.setClientACLGroupEntries(clientACLGroupEntryList);
            mqttRemoteClientService.save(mqttRemoteClient);

            return WebReturnResult.returnTipMessage(1, "更新成功!");
        } else {
            return WebReturnResult.returnTipMessage(0, "参数不全!");

        }

    }

    /**
     * 给客户端设置ACLø
     *
     * @param requestBody
     * @param clientACLGroupEntryList
     */
    private void setACL(JSONObject requestBody, List<ClientACLGroupEntry> clientACLGroupEntryList) {
        for (Object o : requestBody.getJSONArray("group")) {
            ClientACLGroupEntry clientACLGroupEntry = new ClientACLGroupEntry();
            clientACLGroupEntry.setName(((JSONObject) o).getString("name"));
            clientACLGroupEntry.setAcl(((JSONObject) o).getIntValue("acl"));
            clientACLGroupEntryList.add(clientACLGroupEntry);
        }
    }

    /**
     * 获取该用户所有的数据
     *
     * @param httpServletRequest
     * @return
     */

    @RequestMapping(value = "/{page}/{size}", method = RequestMethod.GET)
    public Object list(HttpServletRequest httpServletRequest,
                       @PathVariable int page,
                       @PathVariable int size) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        Page<MqttRemoteClient> mqttRemoteClientPage = mqttRemoteClientService.findAllByUserId(
                userId,
                PageRequest.of(page,
                        size,
                        Sort.by(Sort.Direction.DESC, "id")));
        return WebReturnResult.returnDataMessage(1, "查询成功!", mqttRemoteClientPage);

    }

    /**
     * 获取单个数据
     *
     * @param httpServletRequest
     * @return
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getById(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);
        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        MqttRemoteClient mqttRemoteClient = mqttRemoteClientService.findOneById(id);
        if (mqttRemoteClient == null) {
            return WebReturnResult.returnTipMessage(0, "客户端不存在!");
        }
        return WebReturnResult.returnDataMessage(1, "查询成功!", mqttRemoteClient);

    }

    /**
     * 根据客户端的ID来查找数据
     *
     * @param httpServletRequest
     * @param id
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/data/{id}/{page}/{size}", method = RequestMethod.GET)
    public Object data(HttpServletRequest httpServletRequest,
                       @PathVariable Long id,
                       @PathVariable int page,
                       @PathVariable int size) {
        Long userId = cacheHelper.getCurrentUserIdFromRedisCache(httpServletRequest);

        if (userId == null) {
            return WebReturnResult.returnTipMessage(401, "Token已过期!");
        }
        MqttRemoteClient mqttRemoteClient = mqttRemoteClientService.findOneById(id);
        if (mqttRemoteClient == null) {
            return WebReturnResult.returnTipMessage(0, "客户端不存在!");
        }
        //查找数据
        Page<ClientDataEntry> mqttRemoteClientPage = clientDataEntryService.getByClientId(mqttRemoteClient.getId(), PageRequest.of(page,
                size,
                Sort.by(Sort.Direction.DESC, "id")));
        return WebReturnResult.returnDataMessage(1, "查询成功!", mqttRemoteClientPage);

    }

}
