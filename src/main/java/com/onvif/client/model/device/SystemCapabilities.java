package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class SystemCapabilities {
    
    @XmlElement(name = "DiscoveryResolve")
    private boolean discoveryResolve;
    
    @XmlElement(name = "DiscoveryBye")
    private boolean discoveryBye;
    
    @XmlElement(name = "RemoteDiscovery")
    private boolean remoteDiscovery;
    
    @XmlElement(name = "SystemBackup")
    private boolean systemBackup;
}
