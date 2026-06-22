package com.banco.digital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApiBancoDigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiBancoDigitalApplication.class, args);
    }
}
