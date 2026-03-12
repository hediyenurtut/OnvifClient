package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF 2D Vector (for Pan/Tilt)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Vector2D {
    
    @XmlElement(name = "x")
    private float x;
    
    @XmlElement(name = "y")
    private float y;
    
    @XmlAttribute(name = "space")
    private String space;
}
