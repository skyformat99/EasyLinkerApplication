package com.easylinker.proxy.server.app.config.thread;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;

/**
 * @author wwhai
 */
@Component
public class EasyThreadFactory implements ThreadFactory {
//    /**
//     * 线程池的基本大小
//     */
//    private static int corePoolSize = 10;
//    /**
//     * 线程池最大数量
//     */
//    private static int maximumPoolSizeSize = 100;
//    /**
//     * 线程活动保持时间
//     */
//    private static long keepAliveTime = 1;
//
//
//    public COAPRequestThreadFactory() {
//
//    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable);
    }

}


