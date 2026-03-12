package com.onvif.client.model.imaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Exposure Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ExposureSettings {
    
    @XmlElement(name = "Mode")
    private String mode;
    
    @XmlElement(name = "Priority")
    private String priority;
    
    @XmlElement(name = "MinExposureTime")
    private float minExposureTime;
    
    @XmlElement(name = "MaxExposureTime")
    private float maxExposureTime;
    
    @XmlElement(name = "MinGain")
    private float minGain;
    
    @XmlElement(name = "MaxGain")
    private float maxGain;
    
    @XmlElement(name = "MinIris")
    private float minIris;
    
    @XmlElement(name = "MaxIris")
    private float maxIris;
    
    @XmlElement(name = "ExposureTime")
    private float exposureTime;
    
    @XmlElement(name = "Gain")
    private float gain;
    
    @XmlElement(name = "Iris")
    private float iris;
}
