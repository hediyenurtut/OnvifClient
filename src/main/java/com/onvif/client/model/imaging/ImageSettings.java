package com.onvif.client.model.imaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Image Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ImageSettings {
    
    @XmlElement(name = "Brightness")
    private float brightness;
    
    @XmlElement(name = "ColorSaturation")
    private float colorSaturation;
    
    @XmlElement(name = "Contrast")
    private float contrast;
    
    @XmlElement(name = "Exposure")
    private ExposureSettings exposure;
    
    @XmlElement(name = "Focus")
    private FocusSettings focus;
    
    @XmlElement(name = "IrCutFilter")
    private String irCutFilter;
    
    @XmlElement(name = "Sharpness")
    private float sharpness;
    
    @XmlElement(name = "WideDynamicRange")
    private WideDynamicRangeSettings wideDynamicRange;
    
    @XmlElement(name = "WhiteBalance")
    private WhiteBalanceSettings whiteBalance;
}
