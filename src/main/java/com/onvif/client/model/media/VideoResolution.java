package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Video Resolution
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class VideoResolution {
    
    @XmlElement(name = "Width")
    private int width;
    
    @XmlElement(name = "Height")
    private int height;
}
