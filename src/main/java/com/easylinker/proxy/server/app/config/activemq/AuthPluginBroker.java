package com.easylinker.proxy.server.app.config.activemq;

import com.easylinker.proxy.server.app.model.MqttRemoteClient;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.*;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.acl.GroupImpl;

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
        log.debug("addConnection");
        SecurityContext securityContext = context.getSecurityContext();
        if (securityContext == null) {
            securityContext = authenticate(info.getUserName(), info.getPassword(), null);
            context.setSecurityContext(securityContext);
            securityContexts.add(securityContext);
        }

        try {
            super.addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(securityContext);
            context.setSecurityContext(null);
            throw e;
        }
    }


    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] peerCertificates) throws SecurityException {
        SecurityContext securityContext;
        MqttRemoteClient mqttRemoteClient = service.findOneByUsernameAndPassword(username, password);
        if (mqttRemoteClient != null) {
            System.out.println("设备不存在");
            securityContext = new SecurityContext(username) {
                @Override
                public Set<Principal> getPrincipals() {
                    Set<Principal> groups = new HashSet<>();
                    groups.add(new GroupImpl("clients"));
                    return groups;
                }

            };
        } else {
            throw new SecurityException("验证失败");

        }
        switch (authType) {
            case 1:
                authByClientId();
                break;
            case 2:
                authByUsernameAndPassword();
                break;
            case 3:
                authAnonymous();
                break;
            default:
                break;
        }


        return securityContext;
    }

    /**
     * 根据ID认证
     */
    private void authByClientId() {

    }

    /**
     * 用户账号密码认证
     */

    private void authByUsernameAndPassword() {

    }

    /**
     * 匿名
     */

    private void authAnonymous() {

    }
}
