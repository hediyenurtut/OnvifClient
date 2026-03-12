package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF H.265 Configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class H265Configuration {
    
    @XmlElement(name = "GovLength")
    private int govLength;
    
    @XmlElement(name = "H265Profile")
    private String h265Profile;
}
