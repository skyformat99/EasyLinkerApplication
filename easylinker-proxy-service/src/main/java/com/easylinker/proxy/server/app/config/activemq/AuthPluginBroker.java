package com.easylinker.proxy.server.app.config.activemq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLEntry;
import com.easylinker.proxy.server.app.model.mqtt.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.*;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证插件
 * 插件开发看这个帖子
 * https://www.cnblogs.com/huangzhex/p/6339761.html
 * 官网文档:http://activemq.apache.org/developing-plugins.html
 */
class AuthPluginBroker extends AbstractAuthenticationBroker {
    private static Logger logger = LoggerFactory.getLogger(AuthPluginBroker.class);
    private MqttRemoteClientService service;
    private int authType;
    private static final int SUB_PERMISSION = 1;
    private static final int PUB_PERMISSION = 2;
    private static final int PUB_AND_SUB_PERMISSION = 3;
    private StringRedisTemplate stringRedisTemplate;
    private final String MESSAGE_PUSHER_USERNAME = "MESSAGE_PUSHER";
    private final String MESSAGE_PUSHER_PASSWORD = "MESSAGE_PUSHER";


    AuthPluginBroker(Broker next, MqttRemoteClientService service, int authType, StringRedisTemplate stringRedisTemplate) {
        super(next);
        this.service = service;
        this.authType = authType;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        System.out.println("客户端请求连接: " + info.toString());
        //放行推送的客户端
        if (info.getUserName().equals(MESSAGE_PUSHER_USERNAME) || info.getPassword().equals(MESSAGE_PUSHER_PASSWORD)) {
            logger.info("内部推送客户端连接");
            super.addConnection(context, info);

        } else {

            SecurityContext securityContext;
            switch (authType) {
                case 1:
                    securityContext = authenticateByUsernameAndPassword(info.getUserName(), info.getPassword());
                    break;
                case 2:
                    securityContext = authenticateByClientId(info.getClientId());
                    break;
                case 3:
                    securityContext = authAnonymous();

                    break;
                default:
                    securityContext = null;
                    break;
            }

            try {
                context.setSecurityContext(securityContext);
                securityContexts.add(securityContext);
                super.addConnection(context, info);

            } catch (Exception e) {
                securityContexts.remove(securityContext);
                context.setSecurityContext(null);
                throw e;
            }
        }

    }

    /**
     * 这个是默认的认证方式，
     * 满足不了我们自己的认证过程所以仅仅实现而已
     *
     * @param username
     * @param password
     * @param x509Certificates
     * @return
     * @throws SecurityException
     */
    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] x509Certificates) throws SecurityException {
        return null;
    }

    /**
     * 通过用户密码来认证客户端
     *
     * @param username
     * @param password
     * @return
     */
    private SecurityContext authenticateByUsernameAndPassword(String username, String password) {
        logger.info("认证方式为:1");
        MqttRemoteClient mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
        return getSecurityContext(username, mqttRemoteClient);

    }

    /**
     * 通过ClientId来认证客户端
     *
     * @param clientId
     * @return
     * @throws SecurityException
     */
    private SecurityContext authenticateByClientId(String clientId) throws SecurityException {
        logger.info("认证方式为:2");
        MqttRemoteClient mqttRemoteClient = service.findOneByClientId(clientId);
        return getSecurityContext(clientId, mqttRemoteClient);

    }

    /**
     * 通过客户端的授权情况来获取安全上下文
     *
     * @param param
     * @param mqttRemoteClient
     * @return {
     */

    private SecurityContext getSecurityContext(String param, MqttRemoteClient mqttRemoteClient) {
        if (mqttRemoteClient != null) {
            logger.info("客户端连接授权成功");
            mqttRemoteClient.setOnLine(true);
            service.save(mqttRemoteClient);

            try {
                switch (authType) {
                    case 1://username 认证
                        cacheClientInfo(mqttRemoteClient.getUsername(), clientInfoToCacheJson(mqttRemoteClient));
                        break;
                    case 2://clientId认证
                        cacheClientInfo(mqttRemoteClient.getClientId(), clientInfoToCacheJson(mqttRemoteClient));
                        break;
                    default:
                        break;
                }


            } catch (Exception e) {
                System.out.println("Redis 缓存出错");
                e.printStackTrace();
                //e.printStackTrace();
            }
            //至此数据库显示上线成功
            return new SecurityContext(param) {
                @Override
                public Set<Principal> getPrincipals() {
                    Set<Principal> groups = new HashSet<>();
                    groups.add(() -> "CLIENT_GROUP");
                    return groups;
                }
            };


        } else {
            throw new SecurityException("Client auth failure!");
        }
    }

    /**
     * 允许匿名连接的函数
     *
     * @return
     */
    private SecurityContext authAnonymous() {
        logger.info("允许匿名连接!");
        return null;
    }

    /**
     * 到这里的时候，客户端确认已经连接成功
     * 所以这里可以直接查找
     *
     * @param context
     * @param info
     * @return
     * @throws Exception
     */

    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        String subscribeTopic = replaceWildcardCharacter(info.getDestination().getPhysicalName());
        String username = context.getConnectionState().getInfo().getUserName();
        String password = context.getConnectionState().getInfo().getPassword();
        String clientId = context.getConnectionState().getInfo().getClientId();

        if (username.equals(MESSAGE_PUSHER_USERNAME) || password.equals(MESSAGE_PUSHER_PASSWORD)) {
            logger.info("内部推送客户端消费");
            super.addConsumer(context, info);

        } else {
            MqttRemoteClient mqttRemoteClient;
            switch (authType) {
                case 1://username 认证
                    mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);

                    if (checkSubscribeAcl(mqttRemoteClient, subscribeTopic)) {
                        super.addConsumer(context, info);
                    } else {
                        throw new SecurityException("禁止订阅Topic:" + subscribeTopic);
                    }
                    break;
                case 2://clientId认证
                    mqttRemoteClient = service.findOneByClientId(clientId);
                    if (checkSubscribeAcl(mqttRemoteClient, subscribeTopic)) {
                        super.addConsumer(context, info);
                    } else {
                        throw new SecurityException("禁止订阅Topic:" + subscribeTopic);
                    }
                    break;
                case 3://匿名模式
                    authAnonymous();
                    break;
                default:
                    break;
            }
        }


        return super.addConsumer(context, info);
    }

    /**
     * 消息拦截器
     *
     * @param producerExchange
     * @param messageSend
     * @throws Exception
     */

    @Override
    //思路:根据发送的消息的topic的ACL值来判断是否有发送权限，如果是1 sub 则不允许发送
    public void send(ProducerBrokerExchange producerExchange,
                     Message messageSend) throws Exception {
        System.out.println("拦截的Topic:" + messageSend.getDestination().getQualifiedName() + " 拦截的消息内容:" + new String(messageSend.getContent().getData()).trim());
        String toTopic = replaceWildcardCharacter(messageSend.getDestination().getPhysicalName());
        String username = producerExchange.getConnectionContext().getConnectionState().getInfo().getUserName();
        String clientId = producerExchange.getConnectionContext().getConnectionState().getInfo().getClientId();
        String password = producerExchange.getConnectionContext().getConnectionState().getInfo().getPassword();
        if (username.equals(MESSAGE_PUSHER_USERNAME) || password.equals(MESSAGE_PUSHER_PASSWORD)) {

            super.send(producerExchange, messageSend);
            logger.info("内部客户端推送:" + new String(messageSend.getContent().getData()).trim());

        } else {

            switch (authType) {
                case 1://username 认证

                    if (checkPubSubAcl(getCachedClientInfo(username), toTopic)) {
                        System.out.println("通过ACL审核");
                        super.send(producerExchange, messageSend);
                    } else {
                        throw new SecurityException("ACL拒绝:[" + toTopic + "]因为该Topic不在ACL允许的范围之内!");
                    }
                    break;
                case 2://clientId认证
                    if (checkPubSubAcl(getCachedClientInfo(clientId), toTopic)) {
                        System.out.println("通过ACL审核");
                        super.send(producerExchange, messageSend);
                    } else {
                        throw new SecurityException("ACL拒绝:[" + toTopic + "]因为该Topic不在ACL允许的范围之内!");
                    }

                    break;
                case 3://匿名模式
                    authAnonymous();
                    super.send(producerExchange, messageSend);
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 替换Topic的路径符号
     *
     * @param topic
     * @return
     */

    private String replaceWildcardCharacter(String topic) {
        return topic.replace(".", "/")
                .replace(">", "#")
                .replace("*", "+");
    }

    /**
     * 检查订阅事件
     *
     * @param mqttRemoteClient
     * @return
     */
    private boolean checkSubscribeAcl(MqttRemoteClient mqttRemoteClient, String subscribeTopic) {
        if (mqttRemoteClient != null) {
            List<ClientACLEntry> clientACLEntries = mqttRemoteClient.getAclEntries();
            //遍历数据库里面的ACL权限
            if (clientACLEntries.size() > 0) {
                for (ClientACLEntry aClientACLEntry : clientACLEntries) {
                    //如果请求订阅的Topic在数据库则说明可以订阅
                    return aClientACLEntry.getTopic().equals(subscribeTopic);
                }
            }

        }
        return false;
    }

    /**
     * 检查消息发布属性
     * 1:sub
     * 2:pub
     * 3:sub&pub
     * 重写的ACL鉴权方法
     * 这个是针对Redis的
     * {
     * "acls": [
     * {
     * "topic": "/test",
     * "acl": 1,
     * "group": [
     * "DEFAULT_GROUP"
     * ]
     * }
     * ],
     * "clientKey": "username"
     * }
     *
     * @param jsonObject
     * @param toTopic
     * @return
     */

    private boolean checkPubSubAcl(JSONObject jsonObject, String toTopic) {


        JSONArray aclsArray = jsonObject.getJSONArray("acls");
        for (Object o : aclsArray) {
            if (toTopic.equals(((JSONObject) o).getString("topic"))) {
                int acl = ((JSONObject) o).getInteger("acl");
                System.out.println("toTopic:" + toTopic + "|Acl:" + acl);
                if ((acl == PUB_PERMISSION) || (acl == PUB_AND_SUB_PERMISSION)) return true;
                if (acl == SUB_PERMISSION) return false;
            }
        }
        return false;
    }

    @Override
    public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error) throws Exception {

//
//        ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
//        producerExchange.setConnectionContext(this.getNext().getAdminConnectionContext());
//        Message message = new ActiveMQMessage();
//
//        /*
//        *  case 1:
//                return new ActiveMQQueue(name);
//            case 2:
//                return new ActiveMQTopic(name);
//            case 3:
//         */
//        message.setDestination(ActiveMQDestination.createDestination(
//                ".test",
//                new Byte("2")));
//        ByteSequence byteSequence = new ByteSequence();
//        byteSequence.setData("Bye Bye".getBytes());
//        message.setContent(byteSequence);
//        super.send(producerExchange, message);

        String username = info.getUserName();
        String password = info.getPassword();
        String clientId = info.getClientId();

        if (username.equals(MESSAGE_PUSHER_USERNAME) || password.equals(MESSAGE_PUSHER_PASSWORD)) {
            logger.info("内部推送客户断开端连接");
            super.removeConnection(context, info, error);

        } else {


            MqttRemoteClient mqttRemoteClient;

            switch (authType) {
                case 1://username 认证
                    mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
                    if (mqttRemoteClient != null) {
                        mqttRemoteClient.setOnLine(false);
                        service.save(mqttRemoteClient);
                    }
                    System.out.println("客户端断开连接:" + info.toString());
                    super.removeConnection(context, info, error);
                    deleteCacheClientInfo(mqttRemoteClient.getUsername());

                    break;
                case 2://clientId认证
                    mqttRemoteClient = service.findOneByClientId(clientId);
                    if (mqttRemoteClient != null) {
                        mqttRemoteClient.setOnLine(false);
                        service.save(mqttRemoteClient);
                    }
                    System.out.println("客户端断开连接:" + info.toString());
                    super.removeConnection(context, info, error);

                    deleteCacheClientInfo(mqttRemoteClient.getClientId());

                    break;
                case 3://匿名模式
                    authAnonymous();
                    super.removeConnection(context, info, error);
                    break;
                default:
                    System.out.println("客户端断开连接:" + info.toString());
                    super.removeConnection(context, info, error);
                    break;
            }
        }

    }

    /**
     * 使用redis缓存连接信息
     *
     * @param param
     * @throws Exception 缓存进去的数据格式
     *                   clientID: topic,acl,group
     */

    private void cacheClientInfo(String param, JSONObject clientInfo) {
        stringRedisTemplate.opsForValue().set(param, clientInfo.toJSONString());
    }

    /**
     * 删除redis缓存
     *
     * @param clientKey
     */
    private void deleteCacheClientInfo(String clientKey) {
        stringRedisTemplate.delete(clientKey);
    }

    /**
     * 获取Redis缓存的Info
     *
     * @param clientKey
     * @return
     * @throws Exception
     */
    private JSONObject getCachedClientInfo(String clientKey) throws Exception {

        return (JSONObject) JSONObject.parse(stringRedisTemplate.opsForValue().get(clientKey));

    }


    /**
     * 转化成redis可以存储的JSON格式
     *
     * @param mqttRemoteClient
     * @return
     */
    private JSONObject clientInfoToCacheJson(MqttRemoteClient mqttRemoteClient) {
        JSONObject returnJson = new JSONObject();
        JSONArray aclArrays = new JSONArray();
        switch (authType) {
            case 1://username
                returnJson.put("clientKey", mqttRemoteClient.getUsername());
                clientACLEntryToJson(mqttRemoteClient, aclArrays);
                returnJson.put("acls", aclArrays);
                return returnJson;

            case 2://ClientID
                returnJson.put("clientKey", mqttRemoteClient.getClientId());
                clientACLEntryToJson(mqttRemoteClient, aclArrays);
                returnJson.put("acls", aclArrays);
                return returnJson;

            default:
                return returnJson;
        }

    }

    /**
     * ACL 描述转换成JSON便于查询
     *
     * @param mqttRemoteClient
     * @param aclArrays
     */
    private void clientACLEntryToJson(MqttRemoteClient mqttRemoteClient, JSONArray aclArrays) {
        for (ClientACLEntry clientACLEntry : mqttRemoteClient.getAclEntries()) {
            JSONObject clientACLEntryJson = new JSONObject();
            clientACLEntryJson.put("topic", clientACLEntry.getTopic());
            clientACLEntryJson.put("acl", clientACLEntry.getAcl());
            //clientACLEntryJson.put("group", clientACLEntry.getGroup());
            aclArrays.add(clientACLEntryJson);

        }
    }

    /**
     * 在AMQ添加目的地的时候调用，通常用来拦截
     * 不过已经在send里面拦截了，这里仅仅做个打印
     *
     * @param context
     * @param destination
     * @param createIfTemporary
     * @return
     * @throws Exception
     */
    @Override
    public Destination addDestination(ConnectionContext context, ActiveMQDestination destination, boolean createIfTemporary) throws Exception {
        System.out.println("添加一个目标:" + destination.getPhysicalName());
        return super.addDestination(context, destination, createIfTemporary);
    }

    /**
     * 监控接受消息的客户端
     *
     * @param consumerExchange
     * @param ack
     * @throws Exception
     */
    @Override
    public void acknowledge(ConsumerBrokerExchange consumerExchange, MessageAck ack) throws Exception {
        System.out.println("客户端已经收到消息:" + ack.isDeliveredAck());
        super.acknowledge(consumerExchange, ack);
    }
}
