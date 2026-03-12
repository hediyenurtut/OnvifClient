package com.onvif.client.model.imaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Focus Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class FocusSettings {
    
    @XmlElement(name = "AutoFocusMode")
    private String autoFocusMode;
    
    @XmlElement(name = "DefaultSpeed")
    private float defaultSpeed;
    
    @XmlElement(name = "NearLimit")
    private float nearLimit;
    
    @XmlElement(name = "FarLimit")
    private float farLimit;
}
