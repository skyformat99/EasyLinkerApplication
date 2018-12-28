package com.easyiot.easylinker.service.proxy.service;


import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemInfoService {

    public Map ramInfo() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 操作系统
        String osName = System.getProperty("os.name");
        // 总的物理内存
        double totalSize = osmxb.getTotalPhysicalMemorySize();
        // 剩余的物理内存
        double freeSize = osmxb.getFreePhysicalMemorySize();
        double percentage = (totalSize-freeSize)/totalSize;
        Map ram = new HashMap(16);
        ram.put("osName", osName);
        ram.put("totalSize", totalSize);
        ram.put("freeSize", freeSize);
        ram.put("percentage", percentage);
        return ram;
    }
}
