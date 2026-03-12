package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF PTZ Vector
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PTZVector {
    
    @XmlElement(name = "PanTilt")
    private Vector2D panTilt;
    
    @XmlElement(name = "Zoom")
    private Vector1D zoom;
}
