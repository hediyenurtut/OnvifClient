package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Imaging Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ImagingSettings {
    
    @XmlElement(name = "Brightness")
    private float brightness;
    
    @XmlElement(name = "ColorSaturation")
    private float colorSaturation;
    
    @XmlElement(name = "Contrast")
    private float contrast;
    
    @XmlElement(name = "Sharpness")
    private float sharpness;
}
