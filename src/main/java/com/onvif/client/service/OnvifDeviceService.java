package com.onvif.client.service;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.device.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

/**
 * ONVIF Device Management Service Client
 * Handles device-level operations using SOAP 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnvifDeviceService {

    private final OnvifConfig onvifConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String SOAP_NS = "http://www.w3.org/2003/05/soap-envelope";
    private static final String DEVICE_NS = "http://www.onvif.org/ver10/device/wsdl";
    
    /**
     * Get Device Information
     */
    public DeviceInformation getDeviceInformation() {
        log.info("Sending GetDeviceInformation request");
        
        String soapRequest = buildSoapRequest(
            "GetDeviceInformation",
            ""
        );
        
        String response = sendSoapRequest(soapRequest);
        log.debug("GetDeviceInformation response: {}", response);
        
        return parseDeviceInformation(response);
    }
    
    /**
     * Get Services
     */
    public List<Service> getServices(boolean includeCapability) {
        log.info("Sending GetServices request");
        
        String body = String.format(
            "<tds:IncludeCapability>%s</tds:IncludeCapability>",
            includeCapability
        );
        
        String soapRequest = buildSoapRequest("GetServices", body);
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetServices response: {}", response);
        return parseServices(response);
    }
    
    /**
     * Get Service Capabilities
     */
    public ServiceCapabilities getServiceCapabilities() {
        log.info("Sending GetServiceCapabilities request");
        
        String soapRequest = buildSoapRequest("GetServiceCapabilities", "");
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetServiceCapabilities response: {}", response);
        return parseServiceCapabilities(response);
    }
    
    /**
     * Get Hostname
     */
    public HostnameInformation getHostname() {
        log.info("Sending GetHostname request");
        
        String soapRequest = buildSoapRequest("GetHostname", "");
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetHostname response: {}", response);
        return parseHostname(response);
    }
    
    /**
     * Set Hostname
     */
    public void setHostname(String hostname) {
        log.info("Sending SetHostname request for: {}", hostname);
        
        String body = String.format(
            "<tds:Name>%s</tds:Name>",
            hostname
        );
        
        String soapRequest = buildSoapRequest("SetHostname", body);
        sendSoapRequest(soapRequest);
        
        log.info("Hostname set successfully");
    }
    
    /**
     * Build SOAP 1.2 request
     */
    private String buildSoapRequest(String operation, String body) {
        return String.format(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soap:Envelope " +
            "xmlns:soap=\"%s\" " +
            "xmlns:tds=\"%s\">" +
            "<soap:Header/>" +
            "<soap:Body>" +
            "<tds:%s>%s</tds:%s>" +
            "</soap:Body>" +
            "</soap:Envelope>",
            SOAP_NS, DEVICE_NS, operation, body, operation
        );
    }
    
    /**
     * Send SOAP request to ONVIF device
     */
    private String sendSoapRequest(String soapRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/soap+xml; charset=utf-8"));
        headers.set("SOAPAction", "");
        
        HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                onvifConfig.getBaseUrl(),
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
     * Parse Device Information from SOAP response
     */
    private DeviceInformation parseDeviceInformation(String response) {
        DeviceInformation info = new DeviceInformation();
        
        info.setManufacturer(extractValue(response, "Manufacturer"));
        info.setModel(extractValue(response, "Model"));
        info.setFirmwareVersion(extractValue(response, "FirmwareVersion"));
        info.setSerialNumber(extractValue(response, "SerialNumber"));
        info.setHardwareId(extractValue(response, "HardwareId"));
        
        return info;
    }
    
    /**
     * Parse Services from SOAP response
     */
    private List<Service> parseServices(String response) {
        // Simplified parsing - in production, use proper XML parsing
        log.debug("Parsing services from response");
        return List.of();
    }
    
    /**
     * Parse Service Capabilities from SOAP response
     */
    private ServiceCapabilities parseServiceCapabilities(String response) {
        // Simplified parsing - in production, use proper XML parsing
        log.debug("Parsing service capabilities from response");
        return new ServiceCapabilities();
    }
    
    /**
     * Parse Hostname from SOAP response
     */
    private HostnameInformation parseHostname(String response) {
        HostnameInformation hostname = new HostnameInformation();
        hostname.setName(extractValue(response, "Name"));
        hostname.setFromDHCP(Boolean.parseBoolean(extractValue(response, "FromDHCP")));
        return hostname;
    }
    
    /**
     * Extract value from XML response
     */
    private String extractValue(String xml, String tagName) {
        String startTag = "<" + tagName + ">";
        String endTag = "</" + tagName + ">";
        
        int start = xml.indexOf(startTag);
        if (start == -1) return "";
        
        start += startTag.length();
        int end = xml.indexOf(endTag, start);
        if (end == -1) return "";
        
        return xml.substring(start, end).trim();
    }
}
