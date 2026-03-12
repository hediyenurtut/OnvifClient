package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * ONVIF Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Service {
    
    @XmlElement(name = "Namespace")
    private String namespace;
    
    @XmlElement(name = "XAddr")
    private String xAddr;
    
    @XmlElement(name = "Version")
    private ServiceVersion version;
    
    @XmlElement(name = "Capabilities")
    private ServiceCapabilities capabilities;
}
