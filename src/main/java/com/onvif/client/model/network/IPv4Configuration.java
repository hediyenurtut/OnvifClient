package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * ONVIF IPv4 Configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IPv4Configuration {
    
    @XmlElement(name = "Manual")
    private List<PrefixedIPv4Address> manual;
    
    @XmlElement(name = "DHCP")
    private boolean dhcp;
    
    @XmlElement(name = "FromDHCP")
    private PrefixedIPv4Address fromDHCP;
}
