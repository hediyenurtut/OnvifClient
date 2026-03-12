package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF PTZ Configuration Options
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PTZConfigurationOptions {
    
    @XmlElement(name = "Spaces")
    private PTZSpaces spaces;
    
    @XmlElement(name = "PTZTimeout")
    private PTZTimeout ptzTimeout;
}
