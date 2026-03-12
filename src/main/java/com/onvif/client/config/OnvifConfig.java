package com.onvif.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ONVIF Camera Configuration Properties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "onvif.camera")
public class OnvifConfig {
    
    private String host = "localhost";
    private int port = 8080;
    private String username = "admin";
    private String password = "admin";
    private String servicePath = "/onvif/device_service";
    
    /**
     * Get the full base URL for ONVIF services
     */
    public String getBaseUrl() {
        return String.format("http://%s:%d%s", host, port, servicePath);
    }
}
