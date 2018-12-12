package com.easyiot.easylinker.service.proxy;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.easylinker.service.proxy.model.client.ClientDataEntry;
import com.easyiot.easylinker.service.proxy.service.ClientDataEntryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    ClientDataEntryService clientDataEntryService;


    @Test
    public void test() {
        System.out.println("HelloWorld");
    }

    @Test
    public void addData() {
        for (int i = 0; i < 128; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("h", Math.random() * 100);
            jsonObject.put("t", Math.random() * 100);
            ClientDataEntry clientDataEntry = new ClientDataEntry();
            clientDataEntry.setClientId(1543584878778L);
            clientDataEntry.setData(jsonObject);
            clientDataEntry.setInfo("温湿度数据");
            clientDataEntryService.save(clientDataEntry);

        }
    }


}
