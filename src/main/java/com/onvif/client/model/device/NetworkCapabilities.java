package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkCapabilities {
    
    @XmlElement(name = "IPFilter")
    private boolean ipFilter;
    
    @XmlElement(name = "ZeroConfiguration")
    private boolean zeroConfiguration;
    
    @XmlElement(name = "IPVersion6")
    private boolean ipVersion6;
    
    @XmlElement(name = "DynDNS")
    private boolean dynDNS;
}
