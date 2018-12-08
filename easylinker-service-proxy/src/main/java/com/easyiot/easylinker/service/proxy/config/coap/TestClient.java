package com.easyiot.easylinker.service.proxy.config.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.net.URI;
import java.net.URISyntaxException;

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
class TestClient {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("coap://localhost:5683/api_coap_1_0?clientId=XXX");
        CoapClient client = new CoapClient(uri);
        CoapResponse response1 = client.post("{\"data\":{\"data\":{\"V1\":\"1\",\"V2\":\"2\"},\"persistent\":\"true\",\"info\":\"V\"},\"clientId\":\"clientID\",\"type\":\"data\"}", MediaTypeRegistry.APPLICATION_JSON);
        System.out.println(Utils.prettyPrint(response1));
        CoapResponse response2 = client.get();
        System.out.println(Utils.prettyPrint(response2));

    }

}

