package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * ONVIF IPv4 Network Interface
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IPv4NetworkInterface {
    
    @XmlElement(name = "Enabled")
    private boolean enabled;
    
    @XmlElement(name = "Config")
    private IPv4Configuration config;
}
