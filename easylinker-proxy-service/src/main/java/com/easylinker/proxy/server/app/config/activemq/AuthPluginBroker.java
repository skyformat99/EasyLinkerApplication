package com.easylinker.proxy.server.app.config.activemq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.thread.EasyThreadFactory;
import com.easylinker.proxy.server.app.model.mqtt.ClientACLEntry;
import com.easylinker.proxy.server.app.model.mqtt.ClientDataEntry;
import com.easylinker.proxy.server.app.model.mqtt.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.ClientDataEntryService;
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
import org.springframework.util.StringUtils;

import javax.jms.MessageNotWriteableException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    /**
     * 内部消息推送组件，防止被拦截器拦截
     */
    private final String INTERNAL_MESSAGE_PUSHER_USERNAME = "MESSAGE_PUSHER";
    private final String INTERNAL_MESSAGE_PUSHER_PASSWORD = "MESSAGE_PUSHER";

    /**
     * 为了支持WebSocket mqtt组件,再开一个推送器
     */
    private final String WEB_CONSOLE_PUSHER_USERNAME = "WEB_CONSOLE_MESSAGE_PUSHER";
    private final String WEB_CONSOLE_PUSHER_PASSWORD = "WEB_CONSOLE_MESSAGE_PUSHER";
    private final ClientDataEntryService clientDataEntryService;

    /**
     * 线程池
     */
    private static final ExecutorService executorService = new ThreadPoolExecutor(
            10,
            100,
            1L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new EasyThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());


    AuthPluginBroker(Broker next, MqttRemoteClientService service, int authType, StringRedisTemplate stringRedisTemplate, ClientDataEntryService clientDataEntryService) {
        super(next);
        this.service = service;
        this.authType = authType;
        this.stringRedisTemplate = stringRedisTemplate;
        this.clientDataEntryService = clientDataEntryService;
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        System.out.println("客户端请求连接: " + info.toString());
        //放行推送的客户端
        if (info.getUserName().equals(INTERNAL_MESSAGE_PUSHER_USERNAME) &&
                info.getPassword().equals(INTERNAL_MESSAGE_PUSHER_PASSWORD)) {
            logger.info("内部推送客户端连接");
            super.addConnection(context, info);

        } else if (info.getUserName().equals(WEB_CONSOLE_PUSHER_USERNAME) &&
                info.getPassword().equals(WEB_CONSOLE_PUSHER_PASSWORD)) {
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
     * 连接成功以后缓存到redis
     * KEY:username-VALUE:
     * {
     *     "acls": [
     *         {
     *             "topic": "/system/echo",
     *             "acl": 3
     *         },
     *         {
     *             "topic": "/1542359901679/fe6f6b2081994054978586c5eb42b71f/test",
     *             "acl": 3
     *         }
     *     ],
     *     "clientKey": "6532460fe1734a5e9b7b86eb6deb5a91"
     * }
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
                    case 1:
                        //username 认证
                        cacheClientInfo(mqttRemoteClient.getUsername(), clientInfoToCacheJson(mqttRemoteClient));
                        break;
                    case 2:
                        //clientId认证
                        cacheClientInfo(mqttRemoteClient.getClientId(), clientInfoToCacheJson(mqttRemoteClient));
                        break;
                    default:
                        break;
                }


            } catch (Exception e) {
                System.out.println("Redis 缓存出错");
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
        System.out.println("客户端:" + username + " 订阅了 " + subscribeTopic);

        if (username.equals(INTERNAL_MESSAGE_PUSHER_USERNAME) &&
                password.equals(INTERNAL_MESSAGE_PUSHER_PASSWORD)
                || username.equals(WEB_CONSOLE_PUSHER_USERNAME) &&
                username.equals(WEB_CONSOLE_PUSHER_PASSWORD)
        ) {
            logger.info("内部推送客户端消费");
            super.addConsumer(context, info);

        } else {
            MqttRemoteClient mqttRemoteClient;
            switch (authType) {
                case 1:
                    //username 认证
                    mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
                    checkSubscribeAcl(context, info, mqttRemoteClient, subscribeTopic);
                    break;
                case 2:
                    //clientId认证
                    mqttRemoteClient = service.findOneByClientId(clientId);
                    checkSubscribeAcl(context, info, mqttRemoteClient, subscribeTopic);

                    break;
                case 3:
                    //匿名模式
                    authAnonymous();
                    break;
                default:
                    break;
            }
        }


        return super.addConsumer(context, info);
    }

    /**
     * 检查订阅事件
     * 发现这里有个BUG
     * 每次只检查一个ACL
     *
     * @param mqttRemoteClient
     * @return
     */
    private void checkSubscribeAcl(ConnectionContext context, ConsumerInfo info, MqttRemoteClient mqttRemoteClient, String subscribeTopic) throws Exception {
        if (mqttRemoteClient != null) {
            List<ClientACLEntry> clientACLEntries = mqttRemoteClient.getAclEntries();
            //遍历数据库里面的ACL权限
            if (clientACLEntries.size() > 0) {
                List<String> topics = new ArrayList<>();
                for (ClientACLEntry aClientACLEntry : clientACLEntries) {
                    topics.add(aClientACLEntry.getTopic());
                }
                //这里应该是包含而不是全等
                if (topics.contains(subscribeTopic)) {
                    super.addConsumer(context, info);
                } else {
                    throw new SecurityException("禁止订阅Topic:" + subscribeTopic);
                }
            }

        }
    }

    /**
     * 消息拦截器
     * 思路:根据发送的消息的topic的ACL值来判断是否有发送权限，如果是1 sub 则不允许发送
     *
     * @param producerExchange
     * @param messageSend
     * @throws Exception
     */

    @Override
    public void send(ProducerBrokerExchange producerExchange,
                     Message messageSend) throws Exception {
        System.out.println("send:来自消息Topic:" + messageSend.getDestination().getQualifiedName() +
                " 消息内容:" + new String(messageSend.getContent().getData()).trim());
        String toTopic = replaceWildcardCharacter(messageSend.getDestination().getPhysicalName());
        String username = producerExchange.getConnectionContext().getConnectionState().getInfo().getUserName();
        String clientId = producerExchange.getConnectionContext().getConnectionState().getInfo().getClientId();
        String password = producerExchange.getConnectionContext().getConnectionState().getInfo().getPassword();
        if (username.equals(INTERNAL_MESSAGE_PUSHER_USERNAME) &&
                password.equals(INTERNAL_MESSAGE_PUSHER_PASSWORD)
                || username.equals(WEB_CONSOLE_PUSHER_USERNAME) &&
                username.equals(WEB_CONSOLE_PUSHER_PASSWORD)
        ) {
            System.out.println("MESSAGE_PUSHER");
            super.send(producerExchange, messageSend);
        } else {
            //接受数据
            try {
                JSONObject dataJson = JSONObject.parseObject(new String(messageSend.getContent().getData()).trim());
                switch (authType) {
                    case 1:
                        //username 认证

                        handleSend(producerExchange, messageSend, toTopic, username, clientId, dataJson);
                        break;
                    case 2:
                        //clientId认证
                        handleSend(producerExchange, messageSend, toTopic, clientId, clientId, dataJson);

                        break;
                    case 3:
                        //匿名模式
                        authAnonymous();
                        super.send(producerExchange, messageSend);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                throw new SecurityException("非JSON数据不予处理!");
            }


        }
    }

    /**
     * 用来处理消息的发送和是否持久化
     * {"persistent":"true","data":{"V1":"1","V2":"2"},"info":"V"}
     *
     * @param producerExchange
     * @param messageSend
     * @param toTopic
     * @param username
     * @param clientId
     * @param dataJson
     * @throws Exception
     */
    private synchronized void handleSend(ProducerBrokerExchange producerExchange, Message messageSend, String toTopic, String username, String clientId, JSONObject dataJson) throws Exception {
        if (checkPubSubAcl(getCachedClientInfo(username), toTopic)) {
            if (StringUtils.hasText(dataJson.getString("type"))) {
                System.out.println("消息类型:" + dataJson.getString("type"));

                //
                switch (dataJson.getString("type")) {
                    case "data":
                        //是否是持久化数据
                        super.send(producerExchange, messageSend);
                        //考虑到数据库持久化会浪费时间，所以开启多线程去保存数据，同时多线程又面临着上下文的问题，所以需要同步
                        synchronized (this) {

                            executorService.execute(() -> {
                                if (dataJson.getBooleanValue("persistent")) {
                                    ClientDataEntry clientDataEntry = new ClientDataEntry();
                                    clientDataEntry.setClientId(clientId);
                                    clientDataEntry.setData(dataJson.getJSONObject("data"));
                                    clientDataEntry.setInfo(dataJson.getString("info"));
                                    clientDataEntryService.save(clientDataEntry);
                                    System.out.println("持久化成功!");
                                }
                            });


                        }

                        break;
                    case "echo":
                        // Echo 专门发给一个通道 /system/echo/
                        synchronized (this) {
                            executorService.execute(() -> {
                                //
                                JSONObject echoMessageJson = new JSONObject();
                                echoMessageJson.put("message", new String(messageSend.getContent().getData()));
                                echoMessageJson.put("clientId", clientId);
                                ActiveMQTextMessage echoMessage = new ActiveMQTextMessage();
                                try {
                                    echoMessage.setText(echoMessageJson.toJSONString());
                                } catch (MessageNotWriteableException e) {
                                    //e.printStackTrace();
                                }
                                ActiveMQTopic echoTopic = new ActiveMQTopic();
                                echoTopic.setPhysicalName(".system.echo");
                                //半路拦截 然后修改目标地址
                                messageSend.setDestination(echoTopic);
                                messageSend.setContent(echoMessage.getContent());
                            });
                        }

                        super.send(producerExchange, messageSend);

                        break;
                    case "cmd":
                        //Echo 专门发给一个通道 /system/cmd/
                        synchronized (this) {
                            executorService.execute(() -> {
                                JSONObject cmdMessageJson = new JSONObject();

                                ActiveMQTextMessage cmdMessage = new ActiveMQTextMessage();
                                ActiveMQTopic cmdTopic = new ActiveMQTopic();
                                cmdTopic.setPhysicalName(toTopic.replace("/", "."));
                                System.out.println("CMD:" + cmdTopic);

                                try {
                                    cmdMessage.setText(cmdMessageJson.toJSONString());
                                } catch (MessageNotWriteableException e) {
                                    // e.printStackTrace();

                                }
                                JSONObject cmdJson = dataJson.getJSONObject("data").getJSONObject("data");
                                System.out.println(cmdJson.toJSONString());
                                // 这里默认仅仅支持两个命令：time，返回时间戳，version：返回版本
                                switch (cmdJson.getString("cmd")) {
                                    case "time":
                                        cmdMessageJson.put("message", System.currentTimeMillis());
                                        cmdMessageJson.put("clientId", clientId);
                                        messageSend.setContent(cmdMessage.getContent());

                                        break;
                                    case "version":
                                        cmdMessageJson.put("message", "V3.0.0.0");
                                        cmdMessageJson.put("clientId", clientId);
                                        messageSend.setContent(cmdMessage.getContent());

                                        break;
                                    default:
                                        break;

                                }

                            });
                        }

                        super.send(producerExchange, messageSend);
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("handlerSend：没有定义类型的消息直接忽略!");
            }


        } else {
            throw new SecurityException("ACL拒绝:[" + toTopic + "]因为该Topic不在ACL允许的范围之内!");
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
                System.out.println("checkPubSubAcl:toTopic:" + toTopic + "|Acl:" + acl);
                if ((acl == PUB_PERMISSION) || (acl == PUB_AND_SUB_PERMISSION)) {
                    return true;
                }
                if (acl == SUB_PERMISSION) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error) throws Exception {

        String username = info.getUserName();
        String password = info.getPassword();
        String clientId = info.getClientId();

        if (username.equals(INTERNAL_MESSAGE_PUSHER_USERNAME) &&
                password.equals(INTERNAL_MESSAGE_PUSHER_PASSWORD)
                || username.equals(WEB_CONSOLE_PUSHER_USERNAME) &&
                username.equals(WEB_CONSOLE_PUSHER_PASSWORD)
        ) {
            logger.info("内部推送客户断开端连接");
            super.removeConnection(context, info, error);

        } else {


            MqttRemoteClient mqttRemoteClient;

            switch (authType) {
                case 1:
                    //username 认证
                    mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
                    if (mqttRemoteClient != null) {
                        mqttRemoteClient.setOnLine(false);
                        service.save(mqttRemoteClient);
                        System.out.println("客户端断开连接:" + info.toString());
                        super.removeConnection(context, info, error);
                        deleteCacheClientInfo(mqttRemoteClient.getUsername());
                    }


                    break;
                case 2:
                    //clientId认证
                    mqttRemoteClient = service.findOneByClientId(clientId);
                    if (mqttRemoteClient != null) {
                        mqttRemoteClient.setOnLine(false);
                        service.save(mqttRemoteClient);
                        System.out.println("客户端断开连接:" + info.toString());
                        super.removeConnection(context, info, error);
                        deleteCacheClientInfo(mqttRemoteClient.getClientId());
                    }


                    break;
                case 3:
                    //匿名模式
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
     *{
     *     "acls": [
     *         {
     *             "topic": "/system/echo",
     *             "acl": 3
     *         },
     *         {
     *             "topic": "/1542359901679/fe6f6b2081994054978586c5eb42b71f/test",
     *             "acl": 3
     *         }
     *     ],
     *     "clientKey": "6532460fe1734a5e9b7b86eb6deb5a91"
     * }
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
            case 1:
                //username
                returnJson.put("clientKey", mqttRemoteClient.getUsername());
                clientACLEntryToJson(mqttRemoteClient, aclArrays);
                returnJson.put("acls", aclArrays);
                return returnJson;

            case 2:
                //ClientID
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
    public Destination addDestination(ConnectionContext context, ActiveMQDestination destination,
                                      boolean createIfTemporary) throws Exception {
        return super.addDestination(context, destination, createIfTemporary);
    }

    @Override
    public void acknowledge(ConsumerBrokerExchange consumerExchange, MessageAck ack) throws Exception {
        super.acknowledge(consumerExchange, ack);
    }
}
