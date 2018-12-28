package com.easyiot.easylinker.service.proxy;


import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;
import java.util.UUID;


public class SystemUtil {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(UUID.randomUUID().toString().split("-")[4]);
        }
    }

}

