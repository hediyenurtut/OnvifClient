package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF PTZ Spaces
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PTZSpaces {
    
    @XmlElement(name = "AbsolutePanTiltPositionSpace")
    private Space2DDescription absolutePanTiltPositionSpace;
    
    @XmlElement(name = "AbsoluteZoomPositionSpace")
    private Space1DDescription absoluteZoomPositionSpace;
    
    @XmlElement(name = "RelativePanTiltTranslationSpace")
    private Space2DDescription relativePanTiltTranslationSpace;
    
    @XmlElement(name = "RelativeZoomTranslationSpace")
    private Space1DDescription relativeZoomTranslationSpace;
    
    @XmlElement(name = "ContinuousPanTiltVelocitySpace")
    private Space2DDescription continuousPanTiltVelocitySpace;
    
    @XmlElement(name = "ContinuousZoomVelocitySpace")
    private Space1DDescription continuousZoomVelocitySpace;
}
