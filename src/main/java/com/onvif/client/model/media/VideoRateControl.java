package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Video Rate Control
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class VideoRateControl {
    
    @XmlElement(name = "FrameRateLimit")
    private int frameRateLimit;
    
    @XmlElement(name = "EncodingInterval")
    private int encodingInterval;
    
    @XmlElement(name = "BitrateLimit")
    private int bitrateLimit;
}
