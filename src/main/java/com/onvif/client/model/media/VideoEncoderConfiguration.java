package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Video Encoder Configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class VideoEncoderConfiguration {
    
    @XmlAttribute(name = "token")
    private String token;
    
    @XmlElement(name = "Name")
    private String name;
    
    @XmlElement(name = "UseCount")
    private int useCount;
    
    @XmlElement(name = "Encoding")
    private String encoding;
    
    @XmlElement(name = "Resolution")
    private VideoResolution resolution;
    
    @XmlElement(name = "Quality")
    private float quality;
    
    @XmlElement(name = "RateControl")
    private VideoRateControl rateControl;
    
    @XmlElement(name = "H264")
    private H264Configuration h264;
    
    @XmlElement(name = "H265")
    private H265Configuration h265;
    
    @XmlElement(name = "Multicast")
    private MulticastConfiguration multicast;
    
    @XmlElement(name = "SessionTimeout")
    private String sessionTimeout;
}
