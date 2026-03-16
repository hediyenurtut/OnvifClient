package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.device.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONVIF Device Management Service Client
 * Handles device-level operations using SOAP 1.2
 */
@Service
@RequiredArgsConstructor
public class OnvifDeviceService {
    private static final Logger log = LoggerFactory.getLogger(OnvifDeviceService.class);

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
    public List<com.onvif.client.model.device.Service> getServices(boolean includeCapability) {
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
    private List<com.onvif.client.model.device.Service> parseServices(String response) {
        log.debug("Parsing services from response");
        List<com.onvif.client.model.device.Service> services = new ArrayList<>();
        String normalized = normalizeXml(response);
        List<String> serviceBlocks = extractAllBlocks(normalized, "Service");
        for (String block : serviceBlocks) {
            com.onvif.client.model.device.Service service = new com.onvif.client.model.device.Service();
            service.setNamespace(extractValue(block, "Namespace"));
            service.setXAddr(extractValue(block, "XAddr"));

            String versionBlock = extractBlock(block, "Version");
            if (!versionBlock.isEmpty()) {
                ServiceVersion version = new ServiceVersion();
                version.setMajor(parseInt(versionBlock, "Major"));
                version.setMinor(parseInt(versionBlock, "Minor"));
                service.setVersion(version);
            }

            services.add(service);
        }
        return services;
    }

    /**
     * Parse Service Capabilities from SOAP response
     */
    private ServiceCapabilities parseServiceCapabilities(String response) {
        log.debug("Parsing service capabilities from response");
        ServiceCapabilities capabilities = new ServiceCapabilities();
        String normalized = normalizeXml(response);

        int networkIdx = normalized.indexOf("<Network");
        if (networkIdx != -1) {
            int networkEnd = normalized.indexOf(">", networkIdx);
            if (networkEnd != -1) {
                String networkTag = normalized.substring(networkIdx, networkEnd + 1);
                NetworkCapabilities network = new NetworkCapabilities();
                network.setIpFilter(Boolean.parseBoolean(extractAttributeValue(networkTag, "IPFilter")));
                network.setZeroConfiguration(Boolean.parseBoolean(extractAttributeValue(networkTag, "ZeroConfiguration")));
                network.setIpVersion6(Boolean.parseBoolean(extractAttributeValue(networkTag, "IPVersion6")));
                network.setDynDNS(Boolean.parseBoolean(extractAttributeValue(networkTag, "DynDNS")));
                capabilities.setNetwork(network);
            }
        }

        int securityIdx = normalized.indexOf("<Security");
        if (securityIdx != -1) {
            int securityEnd = normalized.indexOf(">", securityIdx);
            if (securityEnd != -1) {
                String securityTag = normalized.substring(securityIdx, securityEnd + 1);
                SecurityCapabilities security = new SecurityCapabilities();
                security.setTls11(Boolean.parseBoolean(extractAttributeValue(securityTag, "TLS1.1")));
                security.setTls12(Boolean.parseBoolean(extractAttributeValue(securityTag, "TLS1.2")));
                security.setOnboardKeyGeneration(Boolean.parseBoolean(extractAttributeValue(securityTag, "OnboardKeyGeneration")));
                capabilities.setSecurity(security);
            }
        }

        int systemIdx = normalized.indexOf("<System");
        if (systemIdx != -1) {
            int systemEnd = normalized.indexOf(">", systemIdx);
            if (systemEnd != -1) {
                String systemTag = normalized.substring(systemIdx, systemEnd + 1);
                SystemCapabilities system = new SystemCapabilities();
                system.setDiscoveryResolve(Boolean.parseBoolean(extractAttributeValue(systemTag, "DiscoveryResolve")));
                system.setDiscoveryBye(Boolean.parseBoolean(extractAttributeValue(systemTag, "DiscoveryBye")));
                system.setRemoteDiscovery(Boolean.parseBoolean(extractAttributeValue(systemTag, "RemoteDiscovery")));
                system.setSystemBackup(Boolean.parseBoolean(extractAttributeValue(systemTag, "SystemBackup")));
                capabilities.setSystem(system);
            }
        }

        return capabilities;
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

    /**
     * Extract inner content of the first matching block for tagName
     */
    private String extractBlock(String xml, String tagName) {
        String startTag = "<" + tagName;
        String endTag = "</" + tagName + ">";

        int start = xml.indexOf(startTag);
        if (start == -1) return "";

        int afterName = start + startTag.length();
        if (afterName < xml.length()) {
            char next = xml.charAt(afterName);
            if (next != '>' && next != ' ' && next != '/') return "";
        }

        int tagClose = xml.indexOf(">", start);
        if (tagClose == -1) return "";

        if (tagClose > start + 1 && xml.charAt(tagClose - 1) == '/') return "";

        int end = xml.indexOf(endTag, tagClose);
        if (end == -1) return "";

        return xml.substring(tagClose + 1, end).trim();
    }

    /**
     * Extract all blocks matching tagName, returning each full element (start tag + content + end tag)
     */
    private List<String> extractAllBlocks(String xml, String tagName) {
        List<String> blocks = new ArrayList<>();
        String startTag = "<" + tagName;
        String endTag = "</" + tagName + ">";

        int pos = 0;
        while (pos < xml.length()) {
            int start = xml.indexOf(startTag, pos);
            if (start == -1) break;

            int afterName = start + startTag.length();
            if (afterName < xml.length()) {
                char next = xml.charAt(afterName);
                if (next != '>' && next != ' ' && next != '/') {
                    pos = start + 1;
                    continue;
                }
            }

            int tagClose = xml.indexOf(">", start);
            if (tagClose == -1) break;

            if (tagClose > start + 1 && xml.charAt(tagClose - 1) == '/') {
                pos = tagClose + 1;
                continue;
            }

            int end = xml.indexOf(endTag, tagClose);
            if (end == -1) break;

            blocks.add(xml.substring(start, end + endTag.length()));
            pos = end + endTag.length();
        }
        return blocks;
    }

    /**
     * Extract attribute value from a single XML tag string
     */
    private String extractAttributeValue(String xml, String attrName) {
        String pattern = attrName + "=\"";
        int start = xml.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        int end = xml.indexOf("\"", start);
        if (end == -1) return "";
        return xml.substring(start, end);
    }

    /**
     * Remove XML namespace prefixes for simplified parsing
     */
    private String normalizeXml(String xml) {
        return xml
            .replaceAll("<([a-zA-Z][a-zA-Z0-9]*):", "<")
            .replaceAll("</([a-zA-Z][a-zA-Z0-9]*):", "</");
    }

    /**
     * Parse integer value from XML
     */
    private int parseInt(String xml, String tagName) {
        String value = extractValue(xml, tagName);
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
