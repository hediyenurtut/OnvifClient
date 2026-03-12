package com.onvif.client.model.imaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Imaging Options
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ImagingOptions {
    
    @XmlElement(name = "Brightness")
    private FloatRange brightness;
    
    @XmlElement(name = "ColorSaturation")
    private FloatRange colorSaturation;
    
    @XmlElement(name = "Contrast")
    private FloatRange contrast;
    
    @XmlElement(name = "Sharpness")
    private FloatRange sharpness;
}
