package com.raf.asmi.karte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import com.raf.asmi.karte.security.WebSecurity;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class RestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }

}
