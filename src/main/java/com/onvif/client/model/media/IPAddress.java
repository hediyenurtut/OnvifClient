package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF IP Address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IPAddress {
    
    @XmlElement(name = "Type")
    private String type;
    
    @XmlElement(name = "IPv4Address")
    private String ipv4Address;
    
    @XmlElement(name = "IPv6Address")
    private String ipv6Address;
}
