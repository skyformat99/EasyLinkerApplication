package com.easylinker.proxy.server.app.config.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.net.URI;
import java.net.URISyntaxException;

public class Client {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("localhost:5683/api_coap_1_0?key=aaa");  //创建一个资源请求hello资源，注意默认端口为5683
        CoapClient client = new CoapClient(uri);
        CoapResponse response = client.post("hahaha", MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(Utils.prettyPrint(response));


    }


}

