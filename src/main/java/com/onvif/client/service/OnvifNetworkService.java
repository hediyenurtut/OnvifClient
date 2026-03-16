package com.onvif.client.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.config.OnvifConfig;
import com.onvif.client.model.network.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONVIF Network Service Client
 * Handles network configuration operations using SOAP 1.2
 */
@Service
@RequiredArgsConstructor
public class OnvifNetworkService {
    private static final Logger log = LoggerFactory.getLogger(OnvifNetworkService.class);

    private final OnvifConfig onvifConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String SOAP_NS = "http://www.w3.org/2003/05/soap-envelope";
    private static final String DEVICE_NS = "http://www.onvif.org/ver10/device/wsdl";
    
    /**
     * Get Network Interfaces
     */
    public List<NetworkInterface> getNetworkInterfaces() {
        log.info("Sending GetNetworkInterfaces request");
        
        String soapRequest = buildSoapRequest("GetNetworkInterfaces", "");
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetNetworkInterfaces response: {}", response);
        return parseNetworkInterfaces(response);
    }
    
    /**
     * Set Network Interfaces
     */
    public void setNetworkInterfaces(String token, NetworkInterface networkInterface) {
        log.info("Sending SetNetworkInterfaces request for token: {}", token);
        
        String body = buildNetworkInterfaceXml(token, networkInterface);
        String soapRequest = buildSoapRequest("SetNetworkInterfaces", body);
        sendSoapRequest(soapRequest);
        
        log.info("Network interface set successfully");
    }
    
    /**
     * Get Network Default Gateway
     */
    public NetworkGateway getNetworkDefaultGateway() {
        log.info("Sending GetNetworkDefaultGateway request");
        
        String soapRequest = buildSoapRequest("GetNetworkDefaultGateway", "");
        String response = sendSoapRequest(soapRequest);
        
        log.debug("GetNetworkDefaultGateway response: {}", response);
        return parseNetworkGateway(response);
    }
    
    /**
     * Set Network Default Gateway
     */
    public void setNetworkDefaultGateway(String ipv4Address, String ipv6Address) {
        log.info("Sending SetNetworkDefaultGateway request");
        
        String body = String.format(
            "<tds:IPv4Address>%s</tds:IPv4Address>" +
            "<tds:IPv6Address>%s</tds:IPv6Address>",
            ipv4Address != null ? ipv4Address : "",
            ipv6Address != null ? ipv6Address : ""
        );
        
        String soapRequest = buildSoapRequest("SetNetworkDefaultGateway", body);
        sendSoapRequest(soapRequest);
        
        log.info("Network default gateway set successfully");
    }
    
    /**
     * Build network interface XML
     */
    private String buildNetworkInterfaceXml(String token, NetworkInterface networkInterface) {
        StringBuilder xml = new StringBuilder();
        xml.append("<tds:InterfaceToken>").append(token).append("</tds:InterfaceToken>");
        xml.append("<tds:NetworkInterface>");
        xml.append("<tt:Enabled>").append(networkInterface.isEnabled()).append("</tt:Enabled>");
        
        if (networkInterface.getIpv4() != null && networkInterface.getIpv4().getConfig() != null) {
            xml.append("<tt:IPv4>");
            xml.append("<tt:Enabled>").append(networkInterface.getIpv4().isEnabled()).append("</tt:Enabled>");
            xml.append("<tt:DHCP>").append(networkInterface.getIpv4().getConfig().isDhcp()).append("</tt:DHCP>");
            xml.append("</tt:IPv4>");
        }
        
        xml.append("</tds:NetworkInterface>");
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
            "xmlns:tds=\"%s\" " +
            "xmlns:tt=\"http://www.onvif.org/ver10/schema\">" +
            "<soap:Header/>" +
            "<soap:Body>" +
            "<tds:%s>%s</tds:%s>" +
            "</soap:Body>" +
            "</soap:Envelope>",
            SOAP_NS, DEVICE_NS, operation, body, operation
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
     * Parse Network Interfaces from response
     */
    private List<NetworkInterface> parseNetworkInterfaces(String response) {
        log.debug("Parsing network interfaces from response");
        List<NetworkInterface> interfaces = new ArrayList<>();
        String normalized = normalizeXml(response);
        List<String> ifaceBlocks = extractAllBlocks(normalized, "NetworkInterfaces");
        for (String block : ifaceBlocks) {
            NetworkInterface iface = new NetworkInterface();
            iface.setToken(extractAttributeValue(block, "token"));
            iface.setEnabled(Boolean.parseBoolean(extractValue(block, "Enabled")));

            String infoBlock = extractBlock(block, "Info");
            if (!infoBlock.isEmpty()) {
                NetworkInterfaceInfo info = new NetworkInterfaceInfo();
                info.setName(extractValue(infoBlock, "Name"));
                info.setHwAddress(extractValue(infoBlock, "HwAddress"));
                info.setMtu(parseInt(infoBlock, "MTU"));
                iface.setInfo(info);
            }

            String ipv4Block = extractBlock(block, "IPv4");
            if (!ipv4Block.isEmpty()) {
                IPv4NetworkInterface ipv4Iface = new IPv4NetworkInterface();
                ipv4Iface.setEnabled(Boolean.parseBoolean(extractValue(ipv4Block, "Enabled")));

                String configBlock = extractBlock(ipv4Block, "Config");
                if (!configBlock.isEmpty()) {
                    IPv4Configuration config = new IPv4Configuration();
                    config.setDhcp(Boolean.parseBoolean(extractValue(configBlock, "DHCP")));

                    List<String> manualBlocks = extractAllBlocks(configBlock, "Manual");
                    List<PrefixedIPv4Address> manualAddresses = new ArrayList<>();
                    for (String manualBlock : manualBlocks) {
                        PrefixedIPv4Address addr = new PrefixedIPv4Address();
                        addr.setAddress(extractValue(manualBlock, "Address"));
                        addr.setPrefixLength(parseInt(manualBlock, "PrefixLength"));
                        manualAddresses.add(addr);
                    }
                    config.setManual(manualAddresses);

                    String fromDhcpBlock = extractBlock(configBlock, "FromDHCP");
                    if (!fromDhcpBlock.isEmpty()) {
                        PrefixedIPv4Address fromDhcp = new PrefixedIPv4Address();
                        fromDhcp.setAddress(extractValue(fromDhcpBlock, "Address"));
                        fromDhcp.setPrefixLength(parseInt(fromDhcpBlock, "PrefixLength"));
                        config.setFromDHCP(fromDhcp);
                    }

                    ipv4Iface.setConfig(config);
                }
                iface.setIpv4(ipv4Iface);
            }

            String ipv6Block = extractBlock(block, "IPv6");
            if (!ipv6Block.isEmpty()) {
                IPv6NetworkInterface ipv6Iface = new IPv6NetworkInterface();
                ipv6Iface.setEnabled(Boolean.parseBoolean(extractValue(ipv6Block, "Enabled")));

                String configBlock = extractBlock(ipv6Block, "Config");
                if (!configBlock.isEmpty()) {
                    IPv6Configuration config = new IPv6Configuration();
                    config.setDhcp(Boolean.parseBoolean(extractValue(configBlock, "DHCP")));
                    ipv6Iface.setConfig(config);
                }
                iface.setIpv6(ipv6Iface);
            }

            interfaces.add(iface);
        }
        return interfaces;
    }
    
    /**
     * Parse Network Gateway from response
     */
    private NetworkGateway parseNetworkGateway(String response) {
        NetworkGateway gateway = new NetworkGateway();
        gateway.setIpv4Address(extractValue(response, "IPv4Address"));
        gateway.setIpv6Address(extractValue(response, "IPv6Address"));
        return gateway;
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
