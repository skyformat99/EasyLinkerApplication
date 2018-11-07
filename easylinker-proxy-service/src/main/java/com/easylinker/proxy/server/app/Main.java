package com.easylinker.proxy.server.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import schedule.TestScheduleController;

@SpringBootApplication
public class Main {
    //@Qualifier("Scheduler")
    //Scheduler scheduler;

    public static void main(String[] args) {
         SpringApplication.run(Main.class, args);

    }

}
