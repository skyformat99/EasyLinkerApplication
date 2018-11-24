package com.easylinker.proxy.server.app.config.coap;

import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.config.mvc.WebReturnResult;
import com.easylinker.proxy.server.app.model.mqtt.ClientDataEntry;
import com.easylinker.proxy.server.app.service.ClientDataEntryService;
import com.easylinker.proxy.server.app.service.MqttRemoteClientService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

@Component
public class EasyLinkerCOAPServer extends CoapServer implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClientDataEntryService clientDataEntryService;
    private final MqttRemoteClientService mqttRemoteClientService;

    @Autowired
    public EasyLinkerCOAPServer(ClientDataEntryService clientDataEntryService, MqttRemoteClientService mqttRemoteClientService) {
        this.clientDataEntryService = clientDataEntryService;
        this.mqttRemoteClientService = mqttRemoteClientService;
    }

    // easylinker.coap.server.host=0.0.0.0
    // easylinker.coap.server.path=api_coap_1_0
    @Value("${easylinker.coap.server.host}")
    private String host;
    @Value("${easylinker.coap.server.port}")
    private Integer port;
    @Value("${easylinker.coap.server.path}")
    private String path;


    @Override
    public void addEndpoint(Endpoint endpoint) {
        super.addEndpoint((new CoapEndpoint(new InetSocketAddress(host, port))));
    }

    /**
     * Executors.newSingleThreadExecutor().execute(() -> {
     * start();
     * });
     *
     * @param resources
     * @return
     */
    @Override
    public CoapServer add(Resource... resources) {
        return super.add(resources);


    }

    /**
     * {
     * "data":{
     * "data":{
     * "V1":"1",
     * "V2":"2"
     * },
     * "persistent":"true",
     * "info":"V"
     * },
     * "type":"data"
     * }
     */
    private void init() {
        super.add(new CoapResource(path) {
            @Override
            public void handlePOST(CoapExchange exchange) {
                logger.debug("From  client:[" + exchange.getSourceAddress() + "]RequestBody:" + exchange.getRequestText());
                System.out.println("请求文本：" + exchange.getRequestText());
                try {
                    JSONObject requestBody = JSONObject.parseObject(exchange.getRequestText());

                    if (StringUtils.hasLength(requestBody.getString("persistent"))
                            && StringUtils.hasLength(requestBody.getString("clientId"))
                            && StringUtils.hasLength(requestBody.getString("data"))
                            && StringUtils.hasLength(requestBody.getString("info"))) {
                        if (mqttRemoteClientService.findOneByClientId(requestBody.getString("clientId")) == null) {
                            exchange.respond(WebReturnResult.returnTipMessage(702, "Client  not exists!").toJSONString());
                        } else {
                            new Thread(() -> {
                                if (requestBody.getBooleanValue("persistent")) {
                                    ClientDataEntry clientDataEntry = new ClientDataEntry();
                                    clientDataEntry.setClientId(requestBody.getString("clientId"));
                                    clientDataEntry.setData(requestBody.getJSONObject("data"));
                                    clientDataEntry.setInfo(requestBody.getString("info"));
                                    clientDataEntryService.save(clientDataEntry);
                                }
                            }).start();
                            exchange.respond(WebReturnResult.returnTipMessage(700, "Post successful!").toJSONString());
                        }

                    } else {
                        exchange.respond(WebReturnResult.returnTipMessage(701, "POST failure!Lost param or JSON format error!").toJSONString());

                    }
                } catch (Exception e) {
                    exchange.respond(WebReturnResult.returnTipMessage(701, "POST failure!Lost param or JSON format error!").toJSONString());

                }


            }

            @Override
            public void handleGET(CoapExchange exchange) {
                logger.debug("From  client:[" + exchange.getSourceAddress() + "]RequestBody:" + exchange.getRequestText());
                if (StringUtils.hasLength(exchange.getQueryParameter("clientId"))
                        && StringUtils.hasLength(exchange.getQueryParameter("clientId"))
                        && StringUtils.hasLength(exchange.getQueryParameter("data"))
                        && StringUtils.hasLength(exchange.getQueryParameter("info"))) {
                    if (mqttRemoteClientService.findOneByClientId(exchange.getQueryParameter("clientId")) == null) {
                        exchange.respond(WebReturnResult.returnTipMessage(702, "Client  not exists!").toJSONString());
                    } else {
                        new Thread(() -> {
                            if (Boolean.valueOf(exchange.getQueryParameter("persistent"))) {
                                ClientDataEntry clientDataEntry = new ClientDataEntry();
                                clientDataEntry.setClientId(exchange.getQueryParameter("clientId"));
                                try {
                                    clientDataEntry.setData(JSONObject.parseObject(exchange.getQueryParameter("data")));

                                } catch (Exception e) {
                                    exchange.respond(WebReturnResult.returnTipMessage(701, "POST failure!Lost param or JSON format error!").toJSONString());

                                }
                                clientDataEntry.setInfo(exchange.getQueryParameter("info"));
                                clientDataEntryService.save(clientDataEntry);
                            }
                        }).start();
                        exchange.respond(WebReturnResult.returnTipMessage(700, "Post successful!").toJSONString());
                    }
                } else {
                    exchange.respond(WebReturnResult.returnTipMessage(702, "GET failure!Lost param or query string format error!").toJSONString());

                }


            }
        });
    }

    @Override
    public synchronized void start() {
        super.start();
        init();
    }

    @Override
    public void afterPropertiesSet() {
        this.start();

    }
}
