package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.ptz.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
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
        log.debug("Parsing PTZ status from response");
        PTZStatus status = new PTZStatus();
        String normalized = normalizeXml(response);

        String positionBlock = extractBlock(normalized, "Position");
        if (!positionBlock.isEmpty()) {
            PTZVector position = new PTZVector();

            String panTiltBlock = extractBlock(positionBlock, "PanTilt");
            if (!panTiltBlock.isEmpty()) {
                Vector2D panTilt = new Vector2D();
                panTilt.setX(parseFloatAttr(panTiltBlock, "x"));
                panTilt.setY(parseFloatAttr(panTiltBlock, "y"));
                panTilt.setSpace(extractAttributeValue(panTiltBlock, "space"));
                position.setPanTilt(panTilt);
            } else {
                int ptIdx = normalized.indexOf("<PanTilt");
                if (ptIdx != -1) {
                    int ptEnd = normalized.indexOf(">", ptIdx);
                    if (ptEnd != -1) {
                        String ptTag = normalized.substring(ptIdx, ptEnd + 1);
                        Vector2D panTilt = new Vector2D();
                        panTilt.setX(parseFloatAttr(ptTag, "x"));
                        panTilt.setY(parseFloatAttr(ptTag, "y"));
                        panTilt.setSpace(extractAttributeValue(ptTag, "space"));
                        position.setPanTilt(panTilt);
                    }
                }
            }

            String zoomBlock = extractBlock(positionBlock, "Zoom");
            if (!zoomBlock.isEmpty()) {
                Vector1D zoom = new Vector1D();
                zoom.setX(parseFloatAttr(zoomBlock, "x"));
                zoom.setSpace(extractAttributeValue(zoomBlock, "space"));
                position.setZoom(zoom);
            } else {
                int zoomIdx = normalized.indexOf("<Zoom");
                if (zoomIdx != -1) {
                    int zoomEnd = normalized.indexOf(">", zoomIdx);
                    if (zoomEnd != -1) {
                        String zoomTag = normalized.substring(zoomIdx, zoomEnd + 1);
                        Vector1D zoom = new Vector1D();
                        zoom.setX(parseFloatAttr(zoomTag, "x"));
                        zoom.setSpace(extractAttributeValue(zoomTag, "space"));
                        position.setZoom(zoom);
                    }
                }
            }

            status.setPosition(position);
        }

        String moveStatusBlock = extractBlock(normalized, "MoveStatus");
        if (!moveStatusBlock.isEmpty()) {
            PTZMoveStatus moveStatus = new PTZMoveStatus();
            moveStatus.setPanTilt(extractValue(moveStatusBlock, "PanTilt"));
            moveStatus.setZoom(extractValue(moveStatusBlock, "Zoom"));
            status.setMoveStatus(moveStatus);
        }

        status.setError(extractValue(normalized, "Error"));
        return status;
    }

    /**
     * Parse Presets from response
     */
    private List<PTZPreset> parsePresets(String response) {
        log.debug("Parsing presets from response");
        List<PTZPreset> presets = new ArrayList<>();
        String normalized = normalizeXml(response);
        List<String> presetBlocks = extractAllBlocks(normalized, "Preset");
        for (String block : presetBlocks) {
            PTZPreset preset = new PTZPreset();
            preset.setToken(extractAttributeValue(block, "token"));
            preset.setName(extractValue(block, "Name"));

            String ptzPositionBlock = extractBlock(block, "PTZPosition");
            if (!ptzPositionBlock.isEmpty()) {
                PTZVector ptzPosition = new PTZVector();

                int ptIdx = ptzPositionBlock.indexOf("<PanTilt");
                if (ptIdx != -1) {
                    int ptEnd = ptzPositionBlock.indexOf(">", ptIdx);
                    if (ptEnd != -1) {
                        String ptTag = ptzPositionBlock.substring(ptIdx, ptEnd + 1);
                        Vector2D panTilt = new Vector2D();
                        panTilt.setX(parseFloatAttr(ptTag, "x"));
                        panTilt.setY(parseFloatAttr(ptTag, "y"));
                        panTilt.setSpace(extractAttributeValue(ptTag, "space"));
                        ptzPosition.setPanTilt(panTilt);
                    }
                }

                int zoomIdx = ptzPositionBlock.indexOf("<Zoom");
                if (zoomIdx != -1) {
                    int zoomEnd = ptzPositionBlock.indexOf(">", zoomIdx);
                    if (zoomEnd != -1) {
                        String zoomTag = ptzPositionBlock.substring(zoomIdx, zoomEnd + 1);
                        Vector1D zoom = new Vector1D();
                        zoom.setX(parseFloatAttr(zoomTag, "x"));
                        zoom.setSpace(extractAttributeValue(zoomTag, "space"));
                        ptzPosition.setZoom(zoom);
                    }
                }

                preset.setPtzPosition(ptzPosition);
            }

            presets.add(preset);
        }
        return presets;
    }

    /**
     * Parse Configuration Options from response
     */
    private PTZConfigurationOptions parseConfigurationOptions(String response) {
        log.debug("Parsing configuration options from response");
        PTZConfigurationOptions options = new PTZConfigurationOptions();
        String normalized = normalizeXml(response);

        String spacesBlock = extractBlock(normalized, "Spaces");
        if (!spacesBlock.isEmpty()) {
            PTZSpaces spaces = new PTZSpaces();

            String absPTBlock = extractBlock(spacesBlock, "AbsolutePanTiltPositionSpace");
            if (!absPTBlock.isEmpty()) {
                spaces.setAbsolutePanTiltPositionSpace(parseSpace2D(absPTBlock));
            }

            String absZoomBlock = extractBlock(spacesBlock, "AbsoluteZoomPositionSpace");
            if (!absZoomBlock.isEmpty()) {
                spaces.setAbsoluteZoomPositionSpace(parseSpace1D(absZoomBlock));
            }

            String relPTBlock = extractBlock(spacesBlock, "RelativePanTiltTranslationSpace");
            if (!relPTBlock.isEmpty()) {
                spaces.setRelativePanTiltTranslationSpace(parseSpace2D(relPTBlock));
            }

            String relZoomBlock = extractBlock(spacesBlock, "RelativeZoomTranslationSpace");
            if (!relZoomBlock.isEmpty()) {
                spaces.setRelativeZoomTranslationSpace(parseSpace1D(relZoomBlock));
            }

            String contPTBlock = extractBlock(spacesBlock, "ContinuousPanTiltVelocitySpace");
            if (!contPTBlock.isEmpty()) {
                spaces.setContinuousPanTiltVelocitySpace(parseSpace2D(contPTBlock));
            }

            String contZoomBlock = extractBlock(spacesBlock, "ContinuousZoomVelocitySpace");
            if (!contZoomBlock.isEmpty()) {
                spaces.setContinuousZoomVelocitySpace(parseSpace1D(contZoomBlock));
            }

            options.setSpaces(spaces);
        }

        String timeoutBlock = extractBlock(normalized, "PTZTimeout");
        if (!timeoutBlock.isEmpty()) {
            PTZTimeout timeout = new PTZTimeout();
            timeout.setMin(extractValue(timeoutBlock, "Min"));
            timeout.setMax(extractValue(timeoutBlock, "Max"));
            options.setPtzTimeout(timeout);
        }

        return options;
    }

    /**
     * Parse a 2D space description block
     */
    private Space2DDescription parseSpace2D(String block) {
        Space2DDescription space = new Space2DDescription();
        space.setUri(extractValue(block, "URI"));
        String xRangeBlock = extractBlock(block, "XRange");
        if (!xRangeBlock.isEmpty()) {
            space.setXRange(parseFloatRange(xRangeBlock));
        }
        String yRangeBlock = extractBlock(block, "YRange");
        if (!yRangeBlock.isEmpty()) {
            space.setYRange(parseFloatRange(yRangeBlock));
        }
        return space;
    }

    /**
     * Parse a 1D space description block
     */
    private Space1DDescription parseSpace1D(String block) {
        Space1DDescription space = new Space1DDescription();
        space.setUri(extractValue(block, "URI"));
        String xRangeBlock = extractBlock(block, "XRange");
        if (!xRangeBlock.isEmpty()) {
            space.setXRange(parseFloatRange(xRangeBlock));
        }
        return space;
    }

    /**
     * Parse a FloatRange block
     */
    private FloatRange parseFloatRange(String block) {
        FloatRange range = new FloatRange();
        range.setMin(parseFloat(block, "Min"));
        range.setMax(parseFloat(block, "Max"));
        return range;
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
     * Parse float value from an XML attribute
     */
    private float parseFloatAttr(String xml, String attrName) {
        String value = extractAttributeValue(xml, attrName);
        try {
            return value.isEmpty() ? 0.0f : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    /**
     * Parse float value from XML element
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
