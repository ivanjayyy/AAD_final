package com.ijse.gdse73.harmoniq_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HarmoniQBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarmoniQBackendApplication.class, args);
    }

}
