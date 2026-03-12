package com.onvif.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ONVIF Profile T Client Application
 * 
 * Spring Boot application for communicating with ONVIF-compliant IP cameras
 * using SOAP 1.2 protocol and Profile T specifications.
 */
@SpringBootApplication
public class OnvifClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnvifClientApplication.class, args);
    }
}
