package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class SecurityCapabilities {
    
    @XmlElement(name = "TLS1.1")
    private boolean tls11;
    
    @XmlElement(name = "TLS1.2")
    private boolean tls12;
    
    @XmlElement(name = "OnboardKeyGeneration")
    private boolean onboardKeyGeneration;
}
