package com.onvif.client.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Network Interface Info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkInterfaceInfo {
    
    @XmlElement(name = "Name")
    private String name;
    
    @XmlElement(name = "HwAddress")
    private String hwAddress;
    
    @XmlElement(name = "MTU")
    private int mtu;
}
