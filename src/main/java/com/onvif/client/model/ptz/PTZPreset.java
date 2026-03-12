package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF PTZ Preset
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PTZPreset {
    
    @XmlAttribute(name = "token")
    private String token;
    
    @XmlElement(name = "Name")
    private String name;
    
    @XmlElement(name = "PTZPosition")
    private PTZVector ptzPosition;
}
