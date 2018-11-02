package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.model.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
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

    private SecurityContext authenticateByUsernameAndPassword(String username, String password) {
        logger.info("认证方式为:1");
        MqttRemoteClient mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
        return getSecurityContext(username, mqttRemoteClient);

    }

    private SecurityContext authenticateByClientId(String clientId)  throws SecurityException{
        logger.info("认证方式为:2");
        MqttRemoteClient mqttRemoteClient = service.findOneByClientId(clientId);
        return getSecurityContext(clientId, mqttRemoteClient);

    }

    private SecurityContext getSecurityContext(String param, MqttRemoteClient mqttRemoteClient) {
        if (mqttRemoteClient != null) {
            logger.info("客户端连接授权成功");
            return new SecurityContext(param) {
                @Override
                public Set<Principal> getPrincipals() {
                    Set<Principal> groups = new HashSet<>();
                    groups.add(() -> "Clients");
                    return groups;
                }

            };

        } else {
            throw new SecurityException("Client auth failure!");
        }
    }


    private SecurityContext authAnonymous() {
        logger.info("允许匿名连接!");

        return null;
    }
}
