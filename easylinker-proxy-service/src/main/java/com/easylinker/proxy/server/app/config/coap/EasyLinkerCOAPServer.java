package com.easylinker.proxy.server.app.config.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;

@Component
public class EasyLinkerCOAPServer extends CoapServer {
    // easylinker.coap.server.host=0.0.0.0
    // easylinker.coap.server.path=api_coap_1_0
//    @Value("${easylinker.coap.server.host}")
//    private String host;
//    @Value("${easylinker.coap.server.port}")
//    private Integer port;
//    @Value("${easylinker.coap.server.path}")
//    private String path;
//

    @Override
    public void addEndpoint(Endpoint endpoint) {
        super.addEndpoint((new CoapEndpoint(new InetSocketAddress("127.0.0.1", 5683))));
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

    private void init() {
        super.add(new CoapResource("api_coap_1_0") {
            @Override
            public void handlePOST(CoapExchange exchange) {
                System.out.println("请求文本：" + exchange.getRequestText());
                System.out.println("URL查询字符串" + exchange.getRequestOptions().getUriQueryString());
                System.out.println("请求长度：" + exchange.getRequestText().length());
                System.out.println("请求体：" + Arrays.toString(exchange.getRequestPayload()));
                exchange.respond("Hi I am server");

            }

            @Override
            public void handleGET(CoapExchange exchange) {
                super.handleGET(exchange);
            }

            @Override
            public void handleDELETE(CoapExchange exchange) {
                super.handleDELETE(exchange);
            }

            @Override
            public void handlePUT(CoapExchange exchange) {
                super.handlePUT(exchange);
            }

            @Override
            public void handleRequest(Exchange exchange) {
                super.handleRequest(exchange);
            }
        });
    }

    @Override
    public synchronized void start() {
        super.start();
        init();
        System.out.println("COAP Server Started ...");
    }

    public static void main(String[] args) {
        EasyLinkerCOAPServer easyLinkerCOAPServer = new EasyLinkerCOAPServer();
        easyLinkerCOAPServer.start();
    }

}
