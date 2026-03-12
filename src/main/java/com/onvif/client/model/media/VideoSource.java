package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Video Source
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class VideoSource {
    
    @XmlAttribute(name = "token")
    private String token;
    
    @XmlElement(name = "Framerate")
    private float framerate;
    
    @XmlElement(name = "Resolution")
    private VideoResolution resolution;
    
    @XmlElement(name = "Imaging")
    private ImagingSettings imaging;
}
