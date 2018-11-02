package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.model.ClientACLEntry;
import com.easylinker.proxy.server.app.model.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * 认证插件
 */
class AuthPluginBroker extends AbstractAuthenticationBroker {
    private static Logger logger = LoggerFactory.getLogger(AuthPluginBroker.class);
    private MqttRemoteClientService service;
    private int authType;
    private static final int SUB_PERMISSION = 0x01;
    private static final int PUB_PERMISSION = 0x10;
    private static final int PUB_AND_SUB_PERMISSION = 0x11;

    AuthPluginBroker(Broker next, MqttRemoteClientService service, int authType) {
        super(next);
        this.service = service;
        this.authType = authType;
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        System.out.println("客户端请求连接: " + info.toString());

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
     * @return
     */

    private SecurityContext getSecurityContext(String param, MqttRemoteClient mqttRemoteClient) {
        if (mqttRemoteClient != null) {
            logger.info("客户端连接授权成功");
            mqttRemoteClient.setOnLine(true);
            service.save(mqttRemoteClient);
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
    public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
        System.out.println("测试拦截器,消息内容:" + producerExchange.getConnectionContext().getConnectionState().getInfo().getUserName());

        String toTopic = replaceWildcardCharacter(messageSend.getDestination().getPhysicalName());
        String username = producerExchange.getConnectionContext().getConnectionState().getInfo().getUserName();
        String password = producerExchange.getConnectionContext().getConnectionState().getInfo().getPassword();
        String clientId = producerExchange.getConnectionContext().getConnectionState().getInfo().getClientId();
        MqttRemoteClient mqttRemoteClient;
        switch (authType) {
            case 1://username 认证
                mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);

                if (checkPubSubAcl(mqttRemoteClient, toTopic)) {
                    System.out.println("通过审核");
                    super.send(producerExchange, messageSend);
                } else {
                    throw new SecurityException("ACL拒绝:[" + toTopic + "]因为该Topic不在ACL允许的范围之内!");
                }
                break;
            case 2://clientId认证
                mqttRemoteClient = service.findOneByClientId(clientId);
                if (checkPubSubAcl(mqttRemoteClient, toTopic)) {
                    System.out.println("通过审核");
                    super.send(producerExchange, messageSend);
                } else {
                    throw new SecurityException("ACL拒绝:[" + toTopic + "]因为该Topic不在ACL允许的范围之内!");
                }
                break;
            case 3://匿名模式
                authAnonymous();
                //throw new SecurityException("匿名模式:" + toTopic);
                break;
            default:
                break;
        }
        //每次到这里多要查库  判断这个Topic的 ACL


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
            ClientACLEntry clientACLEntries[] = mqttRemoteClient.getAclEntry();
            //遍历数据库里面的ACL权限
            if (clientACLEntries.length > 0) {
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
     *
     * @return
     */
    private boolean checkPubSubAcl(MqttRemoteClient mqttRemoteClient, String toTopic) {
        if (mqttRemoteClient != null) {
            ClientACLEntry clientACLEntries[] = mqttRemoteClient.getAclEntry();
            //遍历数据库里面的ACL权限
            if (clientACLEntries.length > 0) {
                for (ClientACLEntry aClientACLEntry : clientACLEntries) {
                    if (toTopic.equals(aClientACLEntry.getTopic())) {
                        int acl = aClientACLEntry.getAcl();
                        System.out.println("toTopic:" + toTopic + "|Acl:" + acl);
                        if ((acl == 1) || (acl == 3)) {
                            return true;
                        }
                    }
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
        MqttRemoteClient mqttRemoteClient;
        switch (authType) {
            case 1://username 认证
                mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
                if (mqttRemoteClient != null) {
                    mqttRemoteClient.setOnLine(false);
                    service.save(mqttRemoteClient);
                }

                break;
            case 2://clientId认证
                mqttRemoteClient = service.findOneByClientId(clientId);
                if (mqttRemoteClient != null) {
                    mqttRemoteClient.setOnLine(false);
                    service.save(mqttRemoteClient);
                }
                break;
            case 3://匿名模式
                authAnonymous();
                break;
            default:
                break;
        }
        System.out.println("客户端断开连接:" + info.toString());
        super.removeConnection(context, info, error);
    }
}
