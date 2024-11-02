package com.denise.cleaningservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.denise")
public class CleaningServicesApplication {

    public static void main(String[] args) {

        SpringApplication.run(CleaningServicesApplication.class, args);
    }

}
