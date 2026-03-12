package com.onvif.client.model.imaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Wide Dynamic Range Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class WideDynamicRangeSettings {
    
    @XmlElement(name = "Mode")
    private String mode;
    
    @XmlElement(name = "Level")
    private float level;
}
