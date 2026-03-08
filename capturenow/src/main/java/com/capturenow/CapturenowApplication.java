package com.capturenow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.capturenow")

public class CapturenowApplication {

    public static void main(String[] args) {

        SpringApplication.run(CapturenowApplication.class, args);
    }

}