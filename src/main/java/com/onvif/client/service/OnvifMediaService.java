package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.media.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

/**
 * ONVIF Media Service Client
 * Handles media stream and configuration operations using SOAP 1.2
 */
@Service
@RequiredArgsConstructor
public class OnvifMediaService {
    private static final Logger log = LoggerFactory.getLogger(OnvifMediaService.class);

    private final OnvifConfig onvifConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String SOAP_NS = "http://www.w3.org/2003/05/soap-envelope";
    private static final String MEDIA_NS = "http://www.onvif.org/ver10/media/wsdl";
    private static final String SCHEMA_NS = "http://www.onvif.org/ver10/schema";
    
    /**
     * Get Video Sources
     */
    public List<VideoSource> getVideoSources() {
        log.info("Sending GetVideoSources request");
        
        String soapRequest = buildSoapRequest("GetVideoSources", "");
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetVideoSources response: {}", response);
        return parseVideoSources(response);
    }
    
    /**
     * Get Video Encoder Configurations
     */
    public List<VideoEncoderConfiguration> getVideoEncoderConfigurations() {
        log.info("Sending GetVideoEncoderConfigurations request");
        
        String soapRequest = buildSoapRequest("GetVideoEncoderConfigurations", "");
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetVideoEncoderConfigurations response: {}", response);
        return parseVideoEncoderConfigurations(response);
    }
    
    /**
     * Add Configuration
     */
    public void addConfiguration(String profileToken, String name, String configurationToken) {
        log.info("Sending AddConfiguration request for profile: {}", profileToken);
        
        String body = String.format(
            "<trt:ProfileToken>%s</trt:ProfileToken>" +
            "<trt:Name>%s</trt:Name>" +
            "<trt:ConfigurationToken>%s</trt:ConfigurationToken>",
            profileToken, name, configurationToken
        );
        
        String soapRequest = buildSoapRequest("AddConfiguration", body);
        sendSoapRequest(soapRequest);
        
        log.info("Configuration added successfully");
    }
    
    /**
     * Build SOAP 1.2 request
     */
    private String buildSoapRequest(String operation, String body) {
        return String.format(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soap:Envelope " +
            "xmlns:soap=\"%s\" " +
            "xmlns:trt=\"%s\" " +
            "xmlns:tt=\"%s\">" +
            "<soap:Header/>" +
            "<soap:Body>" +
            "<trt:%s>%s</trt:%s>" +
            "</soap:Body>" +
            "</soap:Envelope>",
            SOAP_NS, MEDIA_NS, SCHEMA_NS, operation, body, operation
        );
    }
    
    /**
     * Send SOAP request
     */
    private String sendSoapRequest(String soapRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/soap+xml; charset=utf-8"));
        headers.set("SOAPAction", "");
        
        HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                onvifConfig.getBaseUrl().replace("device_service", "media_service"),
                request,
                String.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error sending SOAP request", e);
            throw new RuntimeException("SOAP request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse Video Sources from response
     */
    private List<VideoSource> parseVideoSources(String response) {
        // Simplified parsing
        log.debug("Parsing video sources from response");
        return List.of();
    }
    
    /**
     * Parse Video Encoder Configurations from response
     */
    private List<VideoEncoderConfiguration> parseVideoEncoderConfigurations(String response) {
        // Simplified parsing
        log.debug("Parsing video encoder configurations from response");
        return List.of();
    }
}
