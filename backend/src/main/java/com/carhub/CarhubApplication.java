package com.carhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.carhub")
public class CarhubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarhubApplication.class, args);
    }

}
