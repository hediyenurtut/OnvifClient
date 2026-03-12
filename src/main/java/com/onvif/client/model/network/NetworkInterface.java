package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Network Interface
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkInterface {
    
    @XmlAttribute(name = "token")
    private String token;
    
    @XmlElement(name = "Enabled")
    private boolean enabled;
    
    @XmlElement(name = "Info")
    private NetworkInterfaceInfo info;
    
    @XmlElement(name = "IPv4")
    private IPv4NetworkInterface ipv4;
    
    @XmlElement(name = "IPv6")
    private IPv6NetworkInterface ipv6;
}
