package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Prefixed IPv4 Address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PrefixedIPv4Address {
    
    @XmlElement(name = "Address")
    private String address;
    
    @XmlElement(name = "PrefixLength")
    private int prefixLength;
}
