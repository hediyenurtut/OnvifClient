package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF IPv6 Network Interface
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IPv6NetworkInterface {
    
    @XmlElement(name = "Enabled")
    private boolean enabled;
    
    @XmlElement(name = "Config")
    private IPv6Configuration config;
}
