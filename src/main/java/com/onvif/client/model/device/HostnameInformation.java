package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Hostname Information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class HostnameInformation {
    
    @XmlElement(name = "FromDHCP")
    private boolean fromDHCP;
    
    @XmlElement(name = "Name")
    private String name;
}
