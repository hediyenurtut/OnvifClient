package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Service Version
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceVersion {
    
    @XmlElement(name = "Major")
    private int major;
    
    @XmlElement(name = "Minor")
    private int minor;
}
