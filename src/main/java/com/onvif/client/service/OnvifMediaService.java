package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.media.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
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
     * Get all media profiles configured on the device.
     *
     * @return list of {@link Profile} objects, each containing at least a token and name
     * @throws RuntimeException if the SOAP request fails
     */
    public List<Profile> getProfiles() {
        log.info("Sending GetProfiles request");

        String soapRequest = buildSoapRequest("GetProfiles", "");
        String response = sendSoapRequest(soapRequest);

        log.debug("GetProfiles response: {}", response);
        return parseProfiles(response);
    }

    /**
     * Get the RTSP stream URI for the given media profile.
     * Builds a GetStreamUri SOAP request using RTP-Unicast over RTSP as the stream setup.
     *
     * @param profileToken the token of the media profile to retrieve the stream URI for; must not be null or empty
     * @return {@link StreamUriResponse} containing the URI string
     * @throws IllegalArgumentException if profileToken is null or empty
     * @throws RuntimeException if the SOAP request fails
     */
    public StreamUriResponse getStreamUri(String profileToken) {
        if (profileToken == null || profileToken.isBlank()) {
            throw new IllegalArgumentException("profileToken must not be null or empty");
        }
        log.info("Sending GetStreamUri request for profile: {}", profileToken);

        String body = String.format(
            "<trt:StreamSetup>" +
            "<tt:Stream>RTP-Unicast</tt:Stream>" +
            "<tt:Transport><tt:Protocol>RTSP</tt:Protocol></tt:Transport>" +
            "</trt:StreamSetup>" +
            "<trt:ProfileToken>%s</trt:ProfileToken>",
            profileToken
        );

        String soapRequest = buildSoapRequest("GetStreamUri", body);
        String response = sendSoapRequest(soapRequest);

        log.debug("GetStreamUri response: {}", response);
        return parseStreamUri(response);
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
        log.debug("Parsing video sources from response");
        List<VideoSource> sources = new ArrayList<>();
        String normalized = normalizeXml(response);
        List<String> sourceBlocks = extractAllBlocks(normalized, "VideoSources");
        for (String block : sourceBlocks) {
            VideoSource source = new VideoSource();
            source.setToken(extractAttributeValue(block, "token"));
            source.setFramerate(parseFloat(block, "Framerate"));

            String resBlock = extractBlock(block, "Resolution");
            if (!resBlock.isEmpty()) {
                VideoResolution resolution = new VideoResolution();
                resolution.setWidth(parseInt(resBlock, "Width"));
                resolution.setHeight(parseInt(resBlock, "Height"));
                source.setResolution(resolution);
            }

            String imagingBlock = extractBlock(block, "Imaging");
            if (!imagingBlock.isEmpty()) {
                ImagingSettings imaging = new ImagingSettings();
                imaging.setBrightness(parseFloat(imagingBlock, "Brightness"));
                imaging.setColorSaturation(parseFloat(imagingBlock, "ColorSaturation"));
                imaging.setContrast(parseFloat(imagingBlock, "Contrast"));
                imaging.setSharpness(parseFloat(imagingBlock, "Sharpness"));
                source.setImaging(imaging);
            }

            sources.add(source);
        }
        return sources;
    }

    /**
     * Parse Video Encoder Configurations from response
     */
    private List<VideoEncoderConfiguration> parseVideoEncoderConfigurations(String response) {
        log.debug("Parsing video encoder configurations from response");
        List<VideoEncoderConfiguration> configurations = new ArrayList<>();
        String normalized = normalizeXml(response);
        List<String> configBlocks = extractAllBlocks(normalized, "Configurations");
        for (String block : configBlocks) {
            VideoEncoderConfiguration config = new VideoEncoderConfiguration();
            config.setToken(extractAttributeValue(block, "token"));
            config.setName(extractValue(block, "Name"));
            config.setUseCount(parseInt(block, "UseCount"));
            config.setEncoding(extractValue(block, "Encoding"));
            config.setQuality(parseFloat(block, "Quality"));
            config.setSessionTimeout(extractValue(block, "SessionTimeout"));

            String resBlock = extractBlock(block, "Resolution");
            if (!resBlock.isEmpty()) {
                VideoResolution resolution = new VideoResolution();
                resolution.setWidth(parseInt(resBlock, "Width"));
                resolution.setHeight(parseInt(resBlock, "Height"));
                config.setResolution(resolution);
            }

            String rateBlock = extractBlock(block, "RateControl");
            if (!rateBlock.isEmpty()) {
                VideoRateControl rateControl = new VideoRateControl();
                rateControl.setFrameRateLimit(parseInt(rateBlock, "FrameRateLimit"));
                rateControl.setEncodingInterval(parseInt(rateBlock, "EncodingInterval"));
                rateControl.setBitrateLimit(parseInt(rateBlock, "BitrateLimit"));
                config.setRateControl(rateControl);
            }

            String h264Block = extractBlock(block, "H264");
            if (!h264Block.isEmpty()) {
                H264Configuration h264 = new H264Configuration();
                h264.setGovLength(parseInt(h264Block, "GovLength"));
                h264.setH264Profile(extractValue(h264Block, "H264Profile"));
                config.setH264(h264);
            }

            String h265Block = extractBlock(block, "H265");
            if (!h265Block.isEmpty()) {
                H265Configuration h265 = new H265Configuration();
                h265.setGovLength(parseInt(h265Block, "GovLength"));
                h265.setH265Profile(extractValue(h265Block, "H265Profile"));
                config.setH265(h265);
            }

            String multicastBlock = extractBlock(block, "Multicast");
            if (!multicastBlock.isEmpty()) {
                MulticastConfiguration multicast = new MulticastConfiguration();
                multicast.setPort(parseInt(multicastBlock, "Port"));
                multicast.setTtl(parseInt(multicastBlock, "TTL"));
                multicast.setAutoStart(Boolean.parseBoolean(extractValue(multicastBlock, "AutoStart")));
                String addressBlock = extractBlock(multicastBlock, "Address");
                if (!addressBlock.isEmpty()) {
                    IPAddress address = new IPAddress();
                    address.setType(extractValue(addressBlock, "Type"));
                    address.setIpv4Address(extractValue(addressBlock, "IPv4Address"));
                    address.setIpv6Address(extractValue(addressBlock, "IPv6Address"));
                    multicast.setAddress(address);
                }
                config.setMulticast(multicast);
            }

            configurations.add(config);
        }
        return configurations;
    }

    /**
     * Parse Profiles from response
     */
    private List<Profile> parseProfiles(String response) {
        log.debug("Parsing profiles from response");
        List<Profile> profiles = new ArrayList<>();
        String normalized = normalizeXml(response);
        List<String> profileBlocks = extractAllBlocks(normalized, "Profiles");
        for (String block : profileBlocks) {
            Profile profile = new Profile();
            profile.setToken(extractAttributeValue(block, "token"));
            profile.setName(extractValue(block, "Name"));

            String vsBlock = extractBlock(block, "VideoSourceConfiguration");
            if (!vsBlock.isEmpty()) {
                profile.setVideoSourceConfigurationToken(extractAttributeValue(vsBlock, "token"));
            }

            String vecBlock = extractBlock(block, "VideoEncoderConfiguration");
            if (!vecBlock.isEmpty()) {
                profile.setVideoEncoderConfigurationToken(extractAttributeValue(vecBlock, "token"));
            }

            profiles.add(profile);
        }
        return profiles;
    }

    /**
     * Parse StreamUri from response
     */
    private StreamUriResponse parseStreamUri(String response) {
        log.debug("Parsing stream URI from response");
        String normalized = normalizeXml(response);
        StreamUriResponse result = new StreamUriResponse();
        result.setUri(extractValue(normalized, "Uri"));
        return result;
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
