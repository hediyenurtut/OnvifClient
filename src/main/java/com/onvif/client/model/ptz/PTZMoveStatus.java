package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF PTZ Move Status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PTZMoveStatus {
    
    @XmlElement(name = "PanTilt")
    private String panTilt;
    
    @XmlElement(name = "Zoom")
    private String zoom;
}
