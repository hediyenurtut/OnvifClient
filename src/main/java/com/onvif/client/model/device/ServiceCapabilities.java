package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Service Capabilities
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceCapabilities {
    
    @XmlElement(name = "Network")
    private NetworkCapabilities network;
    
    @XmlElement(name = "Security")
    private SecurityCapabilities security;
    
    @XmlElement(name = "System")
    private SystemCapabilities system;
}
