package com.easylinker.proxy.server.app.vertx.vertxmqtt;

import com.easylinker.proxy.server.app.vertx.vertxmqtt.client.model.VertXMqttRemoteClient;
import com.easylinker.proxy.server.app.vertx.vertxmqtt.client.service.VertXMqttRemoteClientService;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Vertx 实现的mqtt服务器
 * 连接步骤
 * 1 鉴权
 * 2 添加ACL
 * 3 判断是否连接进来
 *  增加两个选项：通过Id 或者通过Username鉴权
 */
@Component
public class VertXMqttServer extends AbstractVerticle {
    private Logger logger = LoggerFactory.getLogger(VertXMqttServer.class);
    @Autowired
    VertXMqttRemoteClientService vertXMqttRemoteClientService;
    @Autowired
    VertXMqttConfig vertXMqttConfig;


    @Override
    public void start() {
        MqttServer mqttServer = MqttServer.create(vertx);
        String auth = vertXMqttConfig.getAuth();

        //重启恢复现场
        logger.info("MQTT服务器启动中,开始恢复现场...");
        List<VertXMqttRemoteClient> vertXMqttRemoteClientList = vertXMqttRemoteClientService.findAllByOnLine(true);
        for (VertXMqttRemoteClient client : vertXMqttRemoteClientList) {
            client.setOnLine(false);
            vertXMqttRemoteClientService.save(client);

        }
        logger.info("恢复完毕！");


        mqttServer
                .endpointHandler(endpoint -> {
                    String username = endpoint.auth().userName();
                    String password = endpoint.auth().password();
                    String clientId = endpoint.clientIdentifier();

                    // shows main connect info
                    logger.info("有客户端连接 [" + endpoint.auth().toJson() + "] clientId = " + endpoint.clientIdentifier() + "]");
                    //1 鉴权 ,初步采用MongoDb进行查库
                    //首先实现通过username

                    if (endpoint.auth() != null) {
//
                        //默认开启用户名认证¬
                        switch (auth) {
                            //开启用户名密码认证
                            case "username":
                                //如果没有账户密码直接拒绝
                                if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                                    logger.info("客户端连接失败！用户名或者密码空");
                                    endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
                                } else {
                                    VertXMqttRemoteClient vertXMqttRemoteClient = vertXMqttRemoteClientService.findTopByUsernameAndPassword(username, password);

                                    if (vertXMqttRemoteClient == null) {
                                        logger.info("客户端username在数据库不存在!连接失败!");
                                        endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);

                                    } else {

                                        // accept connection from the remote client
                                        endpoint.accept(true);
                                        vertXMqttRemoteClient.setOnLine(true);
                                        vertXMqttRemoteClientService.save(vertXMqttRemoteClient);
                                        logger.info("客户端鉴权成功！连接成功！");

                                    }
                                }
                                break;
                            //开启客户端ID认证
                            case "clientId":
                                //如果没有账户密码直接拒绝
                                if (StringUtils.isEmpty(clientId)) {
                                    logger.info("clientId空");
                                    endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
                                } else {
                                    VertXMqttRemoteClient vertXMqttRemoteClient = vertXMqttRemoteClientService.findTopByClientId(clientId);

                                    if (vertXMqttRemoteClient == null) {
                                        logger.info("客户端不存在");
                                        endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);

                                    } else {
                                        logger.info("客户端存在");
                                        endpoint.accept(true);
                                        vertXMqttRemoteClient.setOnLine(true);
                                        vertXMqttRemoteClientService.save(vertXMqttRemoteClient);
                                        logger.info("客户端:" + vertXMqttRemoteClient.getClientId() + " 连接成功!");

                                    }
                                }
                                break;
                            default:
                                break;
                        }


                    } else {
                        logger.info("开启匿名连接");
                    }
                    /**
                     * 处理订阅事件
                     */
                    if (endpoint.isConnected()) {
                        endpoint.subscribeHandler(subscribe -> {

                            VertXMqttRemoteClient vertXMqttRemoteClient = vertXMqttRemoteClientService.findTopByUsernameAndPassword(username, password);
                            if (vertXMqttRemoteClient != null) {
                                String topics[] = vertXMqttRemoteClient.getTopics();
                                //获取客户端的所有订阅主题
                                List<MqttTopicSubscription> topicSubscriptions = subscribe.topicSubscriptions();
                                for (MqttTopicSubscription mqttTopicSubscription : topicSubscriptions) {
                                    if (Arrays.asList(topics).contains(mqttTopicSubscription.topicName())) {
                                        logger.info("通过订阅Topic!" + mqttTopicSubscription.topicName());
                                        List<MqttQoS> grantedQosLevels = new ArrayList<>();
                                        grantedQosLevels.add(mqttTopicSubscription.qualityOfService());
                                        // 确认订阅请求
                                        endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

                                    } else {
                                        logger.info("ACL拒绝订阅Topic!" + mqttTopicSubscription.topicName());

                                    }


                                }
                            }


                        });

                        endpoint.publishHandler(message -> {
                            System.out.println(message.payload());
                        });

                        endpoint.disconnectHandler(v -> {

                            handler(auth, endpoint, username, password, clientId);
                        });
                        endpoint.closeHandler(v -> {
                            handler(auth, endpoint, username, password, clientId);
                        });
                    }


                })
                .listen(vertXMqttConfig.getPort(), vertXMqttConfig.getHost(), ar -> {

                    if (ar.succeeded()) {
                        logger.info("MQTT服务器启动成功,端口：" + mqttServer.actualPort());
                    } else {
                        logger.error("MQTT服务器启动失败:" + ar.cause().getMessage());
                    }
                });

    }

    /**
     * 处理离线事件
     *
     * @param auth
     * @param endpoint
     * @param username
     * @param password
     * @param clientId
     */
    private void handler(String auth, MqttEndpoint endpoint, String username, String password, String clientId) {
        logger.info("客户端连接断开，客户端信息:" + endpoint.auth().toJson() + "|ID " + endpoint.clientIdentifier());
        VertXMqttRemoteClient vertXMqttRemoteClient;
        switch (auth) {
            case "username":
                if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                    vertXMqttRemoteClient = vertXMqttRemoteClientService.findTopByUsernameAndPassword(username, password);

                    if (vertXMqttRemoteClient != null) {
                        logger.info("客户端离线！" + endpoint.auth().toJson());
                        vertXMqttRemoteClient.setOnLine(false);
                        vertXMqttRemoteClientService.save(vertXMqttRemoteClient);

                    }
                }

                break;
            case "clientId":
                vertXMqttRemoteClient = vertXMqttRemoteClientService.findTopByClientId(clientId);
                if (!StringUtils.isEmpty(clientId)) {

                    if (vertXMqttRemoteClient != null) {
                        logger.info("客户端离线！" + endpoint.auth().toJson());
                        vertXMqttRemoteClient.setOnLine(false);
                        vertXMqttRemoteClientService.save(vertXMqttRemoteClient);

                    }
                }

                break;
            default:
                break;
        }

    }


}
