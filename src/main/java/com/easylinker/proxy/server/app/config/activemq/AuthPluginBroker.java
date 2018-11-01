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
    private static Logger log = LoggerFactory.getLogger(AuthPluginBroker.class);
    private MqttRemoteClientService service;
    private int authType;

    AuthPluginBroker(Broker next, MqttRemoteClientService service, int authType) {
        super(next);
        this.service = service;
        this.authType = authType;
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        System.out.println("addConnection:username:" + info.getUserName() + "_password:" + info.getPassword() + "_" + info.getClientId());

        SecurityContext securityContext = null;

        switch (authType) {
            case 1:
                securityContext = authenticateByUsernameAndPassword(info.getUserName(), info.getPassword());

                break;
            case 2:
                securityContext = authenticateByClientId(info.getClientId());
                break;
            case 3:
                authAnonymous();

                break;
            default:
                break;
        }

        try {
            context.setSecurityContext(securityContext);
            securityContexts.add(securityContext);
            addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(securityContext);
            context.setSecurityContext(null);
            throw e;
        }
    }


    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] peerCertificates) {
        MqttRemoteClient mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);

        return getSecurityContext(username, mqttRemoteClient);

    }

    public SecurityContext authenticateByUsernameAndPassword(String username, String password) {
        MqttRemoteClient mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
        if (mqttRemoteClient != null) {
            System.out.println("设备存在");
        } else {
            System.out.println("不存在");
        }

        return getSecurityContext(username, mqttRemoteClient);

    }

    private SecurityContext authenticateByClientId(String clientId) {
        MqttRemoteClient mqttRemoteClient = service.findOneByClientId(clientId);
        return getSecurityContext(clientId, mqttRemoteClient);

    }

    private SecurityContext getSecurityContext(String param, MqttRemoteClient mqttRemoteClient) {
        if (mqttRemoteClient != null) {
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


    private void authAnonymous() {

    }
}
