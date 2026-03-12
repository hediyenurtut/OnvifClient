package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF PTZ Status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class PTZStatus {
    
    @XmlElement(name = "Position")
    private PTZVector position;
    
    @XmlElement(name = "MoveStatus")
    private PTZMoveStatus moveStatus;
    
    @XmlElement(name = "Error")
    private String error;
}
