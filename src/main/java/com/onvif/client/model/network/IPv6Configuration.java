package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * ONVIF IPv6 Configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IPv6Configuration {
    
    @XmlElement(name = "Manual")
    private List<String> manual;
    
    @XmlElement(name = "DHCP")
    private boolean dhcp;
}
