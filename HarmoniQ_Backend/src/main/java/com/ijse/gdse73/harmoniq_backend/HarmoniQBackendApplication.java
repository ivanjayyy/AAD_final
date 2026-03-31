package com.ijse.gdse73.harmoniq_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class HarmoniQBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarmoniQBackendApplication.class, args);
    }

}
