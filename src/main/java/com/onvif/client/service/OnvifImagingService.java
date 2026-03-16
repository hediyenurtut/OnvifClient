package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.imaging.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

/**
 * ONVIF Imaging Service Client
 * Handles imaging settings and options using SOAP 1.2
 */
@Service
@RequiredArgsConstructor
public class OnvifImagingService {
    private static final Logger log = LoggerFactory.getLogger(OnvifImagingService.class);

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
        log.debug("Parsing imaging options from response");
        ImagingOptions options = new ImagingOptions();
        String normalized = normalizeXml(response);

        String brightnessBlock = extractBlock(normalized, "Brightness");
        if (!brightnessBlock.isEmpty()) {
            FloatRange brightness = new FloatRange();
            brightness.setMin(parseFloatElement(brightnessBlock, "Min"));
            brightness.setMax(parseFloatElement(brightnessBlock, "Max"));
            options.setBrightness(brightness);
        }

        String colorSaturationBlock = extractBlock(normalized, "ColorSaturation");
        if (!colorSaturationBlock.isEmpty()) {
            FloatRange colorSaturation = new FloatRange();
            colorSaturation.setMin(parseFloatElement(colorSaturationBlock, "Min"));
            colorSaturation.setMax(parseFloatElement(colorSaturationBlock, "Max"));
            options.setColorSaturation(colorSaturation);
        }

        String contrastBlock = extractBlock(normalized, "Contrast");
        if (!contrastBlock.isEmpty()) {
            FloatRange contrast = new FloatRange();
            contrast.setMin(parseFloatElement(contrastBlock, "Min"));
            contrast.setMax(parseFloatElement(contrastBlock, "Max"));
            options.setContrast(contrast);
        }

        String sharpnessBlock = extractBlock(normalized, "Sharpness");
        if (!sharpnessBlock.isEmpty()) {
            FloatRange sharpness = new FloatRange();
            sharpness.setMin(parseFloatElement(sharpnessBlock, "Min"));
            sharpness.setMax(parseFloatElement(sharpnessBlock, "Max"));
            options.setSharpness(sharpness);
        }

        return options;
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
     * Remove XML namespace prefixes for simplified parsing
     */
    private String normalizeXml(String xml) {
        return xml
            .replaceAll("<([a-zA-Z][a-zA-Z0-9]*):", "<")
            .replaceAll("</([a-zA-Z][a-zA-Z0-9]*):", "</");
    }
    
    /**
     * Parse float value from XML element (used by parseOptions)
     */
    private float parseFloatElement(String xml, String tagName) {
        String value = extractValue(xml, tagName);
        try {
            return value.isEmpty() ? 0.0f : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
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
