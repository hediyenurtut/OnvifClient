package com.onvif.client.service;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.imaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

/**
 * ONVIF Imaging Service Client
 * Handles imaging settings and options using SOAP 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnvifImagingService {

    private final OnvifConfig onvifConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String SOAP_NS = "http://www.w3.org/2003/05/soap-envelope";
    private static final String IMAGING_NS = "http://www.onvif.org/ver20/imaging/wsdl";
    private static final String SCHEMA_NS = "http://www.onvif.org/ver10/schema";
    
    /**
     * Get Image Settings
     */
    public ImageSettings getImageSettings(String videoSourceToken) {
        log.info("Sending GetImagingSettings request for video source: {}", videoSourceToken);
        
        String body = String.format(
            "<timg:VideoSourceToken>%s</timg:VideoSourceToken>",
            videoSourceToken
        );
        
        String soapRequest = buildSoapRequest("GetImagingSettings", body);
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetImagingSettings response: {}", response);
        return parseImageSettings(response);
    }
    
    /**
     * Set Image Settings
     */
    public void setImageSettings(String videoSourceToken, ImageSettings settings) {
        log.info("Sending SetImagingSettings request for video source: {}", videoSourceToken);
        
        String body = String.format(
            "<timg:VideoSourceToken>%s</timg:VideoSourceToken>" +
            "<timg:ImagingSettings>%s</timg:ImagingSettings>",
            videoSourceToken,
            buildImageSettingsXml(settings)
        );
        
        String soapRequest = buildSoapRequest("SetImagingSettings", body);
        sendSoapRequest(soapRequest);
        
        log.info("Image settings set successfully");
    }
    
    /**
     * Get Options
     */
    public ImagingOptions getOptions(String videoSourceToken) {
        log.info("Sending GetOptions request for video source: {}", videoSourceToken);
        
        String body = String.format(
            "<timg:VideoSourceToken>%s</timg:VideoSourceToken>",
            videoSourceToken
        );
        
        String soapRequest = buildSoapRequest("GetOptions", body);
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetOptions response: {}", response);
        return parseOptions(response);
    }
    
    /**
     * Build Image Settings XML
     */
    private String buildImageSettingsXml(ImageSettings settings) {
        StringBuilder xml = new StringBuilder();
        
        if (settings.getBrightness() != 0) {
            xml.append("<tt:Brightness>").append(settings.getBrightness()).append("</tt:Brightness>");
        }
        if (settings.getColorSaturation() != 0) {
            xml.append("<tt:ColorSaturation>").append(settings.getColorSaturation()).append("</tt:ColorSaturation>");
        }
        if (settings.getContrast() != 0) {
            xml.append("<tt:Contrast>").append(settings.getContrast()).append("</tt:Contrast>");
        }
        if (settings.getSharpness() != 0) {
            xml.append("<tt:Sharpness>").append(settings.getSharpness()).append("</tt:Sharpness>");
        }
        if (settings.getIrCutFilter() != null) {
            xml.append("<tt:IrCutFilter>").append(settings.getIrCutFilter()).append("</tt:IrCutFilter>");
        }
        
        return xml.toString();
    }
    
    /**
     * Build SOAP 1.2 request
     */
    private String buildSoapRequest(String operation, String body) {
        return String.format(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soap:Envelope " +
            "xmlns:soap=\"%s\" " +
            "xmlns:timg=\"%s\" " +
            "xmlns:tt=\"%s\">" +
            "<soap:Header/>" +
            "<soap:Body>" +
            "<timg:%s>%s</timg:%s>" +
            "</soap:Body>" +
            "</soap:Envelope>",
            SOAP_NS, IMAGING_NS, SCHEMA_NS, operation, body, operation
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
                onvifConfig.getBaseUrl().replace("device_service", "imaging_service"),
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
     * Parse Image Settings from response
     */
    private ImageSettings parseImageSettings(String response) {
        ImageSettings settings = new ImageSettings();
        settings.setBrightness(parseFloat(response, "Brightness"));
        settings.setColorSaturation(parseFloat(response, "ColorSaturation"));
        settings.setContrast(parseFloat(response, "Contrast"));
        settings.setSharpness(parseFloat(response, "Sharpness"));
        settings.setIrCutFilter(extractValue(response, "IrCutFilter"));
        return settings;
    }
    
    /**
     * Parse Options from response
     */
    private ImagingOptions parseOptions(String response) {
        // Simplified parsing
        log.debug("Parsing imaging options from response");
        return new ImagingOptions();
    }
    
    /**
     * Extract value from XML
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
     * Parse float value from XML
     */
    private float parseFloat(String xml, String tagName) {
        String value = extractValue(xml, tagName);
        try {
            return value.isEmpty() ? 0.0f : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }
}
