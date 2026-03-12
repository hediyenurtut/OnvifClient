package com.onvif.client.model.imaging;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF White Balance Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class WhiteBalanceSettings {
    
    @XmlElement(name = "Mode")
    private String mode;
    
    @XmlElement(name = "CrGain")
    private float crGain;
    
    @XmlElement(name = "CbGain")
    private float cbGain;
}
