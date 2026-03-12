package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF H.264 Configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class H264Configuration {
    
    @XmlElement(name = "GovLength")
    private int govLength;
    
    @XmlElement(name = "H264Profile")
    private String h264Profile;
}
