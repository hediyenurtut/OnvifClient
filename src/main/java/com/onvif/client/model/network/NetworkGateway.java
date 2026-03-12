package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Network Gateway
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkGateway {
    
    @XmlElement(name = "IPv4Address")
    private String ipv4Address;
    
    @XmlElement(name = "IPv6Address")
    private String ipv6Address;
}
