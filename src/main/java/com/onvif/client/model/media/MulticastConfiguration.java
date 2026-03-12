package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Multicast Configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class MulticastConfiguration {
    
    @XmlElement(name = "Address")
    private IPAddress address;
    
    @XmlElement(name = "Port")
    private int port;
    
    @XmlElement(name = "TTL")
    private int ttl;
    
    @XmlElement(name = "AutoStart")
    private boolean autoStart;
}
