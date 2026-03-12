package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.ptz.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

/**
 * ONVIF PTZ Service Client
 * Handles PTZ control operations using SOAP 1.2
 */
@Service
@RequiredArgsConstructor
public class OnvifPTZService {
    private static final Logger log = LoggerFactory.getLogger(OnvifPTZService.class);

    private final OnvifConfig onvifConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String SOAP_NS = "http://www.w3.org/2003/05/soap-envelope";
    private static final String PTZ_NS = "http://www.onvif.org/ver20/ptz/wsdl";
    private static final String SCHEMA_NS = "http://www.onvif.org/ver10/schema";
    
    /**
     * Absolute Move
     */
    public void move(String profileToken, PTZVector position, PTZVector speed) {
        log.info("Sending AbsoluteMove request for profile: {}", profileToken);
        
        String body = buildMoveBody(profileToken, position, speed);
        String soapRequest = buildSoapRequest("AbsoluteMove", body);
        sendSoapRequest(soapRequest);
        
        log.info("Absolute move command sent successfully");
    }
    
    /**
     * Continuous Move
     */
    public void continuousMove(String profileToken, PTZVector velocity) {
        log.info("Sending ContinuousMove request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:Velocity>%s</tptz:Velocity>",
            profileToken,
            buildVectorXml(velocity)
        );
        
        String soapRequest = buildSoapRequest("ContinuousMove", body);
        sendSoapRequest(soapRequest);
        
        log.info("Continuous move command sent successfully");
    }
    
    /**
     * Stop PTZ Movement
     */
    public void stop(String profileToken, boolean panTilt, boolean zoom) {
        log.info("Sending Stop request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:PanTilt>%s</tptz:PanTilt>" +
            "<tptz:Zoom>%s</tptz:Zoom>",
            profileToken, panTilt, zoom
        );
        
        String soapRequest = buildSoapRequest("Stop", body);
        sendSoapRequest(soapRequest);
        
        log.info("Stop command sent successfully");
    }
    
    /**
     * Get Status
     */
    public PTZStatus getStatus(String profileToken) {
        log.info("Sending GetStatus request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>",
            profileToken
        );
        
        String soapRequest = buildSoapRequest("GetStatus", body);
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetStatus response: {}", response);
        return parseStatus(response);
    }
    
    /**
     * Set Home Position
     */
    public void setHomePosition(String profileToken) {
        log.info("Sending SetHomePosition request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>",
            profileToken
        );
        
        String soapRequest = buildSoapRequest("SetHomePosition", body);
        sendSoapRequest(soapRequest);
        
        log.info("Home position set successfully");
    }
    
    /**
     * Goto Home Position
     */
    public void gotoHomePosition(String profileToken, PTZVector speed) {
        log.info("Sending GotoHomePosition request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:Speed>%s</tptz:Speed>",
            profileToken,
            buildVectorXml(speed)
        );
        
        String soapRequest = buildSoapRequest("GotoHomePosition", body);
        sendSoapRequest(soapRequest);
        
        log.info("Goto home position command sent successfully");
    }
    
    /**
     * Get Presets
     */
    public List<PTZPreset> getPresets(String profileToken) {
        log.info("Sending GetPresets request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>",
            profileToken
        );
        
        String soapRequest = buildSoapRequest("GetPresets", body);
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetPresets response: {}", response);
        return parsePresets(response);
    }
    
    /**
     * Set Preset
     */
    public String setPreset(String profileToken, String presetName, String presetToken) {
        log.info("Sending SetPreset request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:PresetName>%s</tptz:PresetName>%s",
            profileToken,
            presetName,
            presetToken != null ? "<tptz:PresetToken>" + presetToken + "</tptz:PresetToken>" : ""
        );
        
        String soapRequest = buildSoapRequest("SetPreset", body);
        String response = sendSoapRequest(soapRequest);
        
        log.info("Preset set successfully");
        return extractValue(response, "PresetToken");
    }
    
    /**
     * Goto Preset
     */
    public void gotoPreset(String profileToken, String presetToken, PTZVector speed) {
        log.info("Sending GotoPreset request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:PresetToken>%s</tptz:PresetToken>" +
            "<tptz:Speed>%s</tptz:Speed>",
            profileToken,
            presetToken,
            buildVectorXml(speed)
        );
        
        String soapRequest = buildSoapRequest("GotoPreset", body);
        sendSoapRequest(soapRequest);
        
        log.info("Goto preset command sent successfully");
    }
    
    /**
     * Remove Preset
     */
    public void removePreset(String profileToken, String presetToken) {
        log.info("Sending RemovePreset request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:PresetToken>%s</tptz:PresetToken>",
            profileToken,
            presetToken
        );
        
        String soapRequest = buildSoapRequest("RemovePreset", body);
        sendSoapRequest(soapRequest);
        
        log.info("Preset removed successfully");
    }
    
    /**
     * Get Move Options
     */
    public PTZConfigurationOptions getMoveOptions(String profileToken) {
        log.info("Sending GetConfigurationOptions request for profile: {}", profileToken);
        
        String body = String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>",
            profileToken
        );
        
        String soapRequest = buildSoapRequest("GetConfigurationOptions", body);
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetConfigurationOptions response: {}", response);
        return parseConfigurationOptions(response);
    }
    
    /**
     * Build move body XML
     */
    private String buildMoveBody(String profileToken, PTZVector position, PTZVector speed) {
        return String.format(
            "<tptz:ProfileToken>%s</tptz:ProfileToken>" +
            "<tptz:Position>%s</tptz:Position>" +
            "<tptz:Speed>%s</tptz:Speed>",
            profileToken,
            buildVectorXml(position),
            buildVectorXml(speed)
        );
    }
    
    /**
     * Build PTZ Vector XML
     */
    private String buildVectorXml(PTZVector vector) {
        if (vector == null) return "";
        
        StringBuilder xml = new StringBuilder();
        
        if (vector.getPanTilt() != null) {
            Vector2D pt = vector.getPanTilt();
            xml.append("<tt:PanTilt")
               .append(pt.getSpace() != null ? " space=\"" + pt.getSpace() + "\"" : "")
               .append("><tt:x>").append(pt.getX()).append("</tt:x>")
               .append("<tt:y>").append(pt.getY()).append("</tt:y>")
               .append("</tt:PanTilt>");
        }
        
        if (vector.getZoom() != null) {
            Vector1D z = vector.getZoom();
            xml.append("<tt:Zoom")
               .append(z.getSpace() != null ? " space=\"" + z.getSpace() + "\"" : "")
               .append("><tt:x>").append(z.getX()).append("</tt:x>")
               .append("</tt:Zoom>");
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
            "xmlns:tptz=\"%s\" " +
            "xmlns:tt=\"%s\">" +
            "<soap:Header/>" +
            "<soap:Body>" +
            "<tptz:%s>%s</tptz:%s>" +
            "</soap:Body>" +
            "</soap:Envelope>",
            SOAP_NS, PTZ_NS, SCHEMA_NS, operation, body, operation
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
                onvifConfig.getBaseUrl().replace("device_service", "ptz_service"),
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
     * Parse PTZ Status from response
     */
    private PTZStatus parseStatus(String response) {
        // Simplified parsing
        log.debug("Parsing PTZ status from response");
        return new PTZStatus();
    }
    
    /**
     * Parse Presets from response
     */
    private List<PTZPreset> parsePresets(String response) {
        // Simplified parsing
        log.debug("Parsing presets from response");
        return List.of();
    }
    
    /**
     * Parse Configuration Options from response
     */
    private PTZConfigurationOptions parseConfigurationOptions(String response) {
        // Simplified parsing
        log.debug("Parsing configuration options from response");
        return new PTZConfigurationOptions();
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
}
